package org.clarksnut.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.LineBean;
import org.clarksnut.datasource.peru.types.TipoDocumento;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
@DatasourceType(datasource = "PeruInvoiceDS")
public class PeruInvoiceDatasourceProvider implements DatasourceProvider {

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        InvoiceType invoiceType = read(file);
        if (invoiceType == null) {
            return null;
        }

        InvoiceDatasource bean = new InvoiceDatasource();

        bean.setIdAsignado(invoiceType.getID().getValue());
        TipoDocumento.getFromCode(invoiceType.getInvoiceTypeCode().getValue()).ifPresent(c -> bean.setTipoDocumento(c.getDenominacion()));
        bean.setMoneda(invoiceType.getDocumentCurrencyCode().getValue());
        bean.setProveedor(BeanUtils.toSupplier(invoiceType.getAccountingSupplierParty()));
        bean.setCliente(BeanUtils.toCustomer(invoiceType.getAccountingCustomerParty()));

        // Fecha emision
        bean.setFechaEmision(BeanUtils.toDate(invoiceType.getIssueDate(), Optional.ofNullable(invoiceType.getIssueTime())));

        // Fecha vencimiento
        List<PaymentMeansType> paymentMeans = invoiceType.getPaymentMeans();
        if (paymentMeans != null && !paymentMeans.isEmpty()) {
            bean.setFechaVencimiento(paymentMeans.get(0).getPaymentDueDate().getValue().toGregorianCalendar().getTime());
        }

        // Delivery location
        DeliveryTermsType deliveryTermsType = invoiceType.getDeliveryTerms();
        if (deliveryTermsType != null) {
            LocationType locationType = deliveryTermsType.getDeliveryLocation();
            if (locationType != null) {
                bean.setDireccionEnvio(BeanUtils.toAddress(locationType.getAddress()));
            }
        }

        // Tipo y numero de remision
        List<DocumentReferenceType> documentReferenceTypes = invoiceType.getDespatchDocumentReference();
        if (documentReferenceTypes != null && !documentReferenceTypes.isEmpty()) {
            for (DocumentReferenceType documentReferenceType : documentReferenceTypes) {
                bean.setNumeroGuiaRemisionRelacionada(documentReferenceType.getID().getValue());
                break;
            }
        }

        // Otro documento relacionado
        List<DocumentReferenceType> documentReferenceTypes1 = invoiceType.getAdditionalDocumentReference();
        if (documentReferenceTypes1 != null && !documentReferenceTypes1.isEmpty()) {
            for (DocumentReferenceType documentReferenceType : documentReferenceTypes1) {
                bean.setOtroDocumentoRelacionadoId(documentReferenceType.getID().getValue());
                break;
            }
        }

        // Payable amount
        MonetaryTotalType legalMonetaryTotalType = invoiceType.getLegalMonetaryTotal();
        AllowanceTotalAmountType allowanceTotalAmountType = legalMonetaryTotalType.getAllowanceTotalAmount();
        ChargeTotalAmountType chargeTotalAmountType = legalMonetaryTotalType.getChargeTotalAmount();

        bean.setTotalVenta(legalMonetaryTotalType.getPayableAmount().getValue().floatValue());
        if (allowanceTotalAmountType != null) {
            bean.setTotalDescuentoGlobal(allowanceTotalAmountType.getValue().floatValue());
        }
        if (chargeTotalAmountType != null) {
            bean.setTotalOtrosCargos(chargeTotalAmountType.getValue().floatValue());
        }

        // Taxs
        bean.setTributos(BeanUtils.toTributos(invoiceType.getTaxTotal()));

        // Informacion adicional
        bean.setInformacionAdicional(BeanUtils.toInformacionAdicional(invoiceType.getUBLExtensions()));

        // Lines
        bean.setDetalle(getLines(invoiceType.getInvoiceLine()));

        return bean;
    }

    private InvoiceType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), InvoiceType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

    private List<LineBean> getLines(List<InvoiceLineType> invoiceLineTypes) {
        List<LineBean> result = new ArrayList<>();

        for (InvoiceLineType invoiceLineType : invoiceLineTypes) {
            LineBean lineBean = new LineBean();

            lineBean.setCantidad(invoiceLineType.getInvoicedQuantity().getValue().floatValue());
            lineBean.setUnidadMedida(invoiceLineType.getInvoicedQuantity().getUnitCode());
            lineBean.setPrecioUnitario(invoiceLineType.getPrice().getPriceAmount().getValue().floatValue());
            lineBean.setTotalValorVenta(invoiceLineType.getLineExtensionAmount().getValue().floatValue());

            // Precio de venta unitario
            BeanUtils.agregarPrecioUnitario(lineBean, Optional.ofNullable(invoiceLineType.getPricingReference().getAlternativeConditionPrice()));

            // Descuentos por item
            List<AllowanceChargeType> allowanceChargeTypes = invoiceLineType.getAllowanceCharge();
            if (allowanceChargeTypes != null && !allowanceChargeTypes.isEmpty()) {
                lineBean.setTotalDescuento(allowanceChargeTypes.get(0).getAmount().getValue().floatValue());
            }

            // Descripcion y codigo de producto
            BeanUtils.agregarDescripcionYCodigo(lineBean, Optional.ofNullable(invoiceLineType.getItem()));

            // Impuestos
            BeanUtils.agregarImpuestosEnLine(lineBean, Optional.ofNullable(invoiceLineType.getTaxTotal()));

            result.add(lineBean);
        }

        return result;
    }
}
