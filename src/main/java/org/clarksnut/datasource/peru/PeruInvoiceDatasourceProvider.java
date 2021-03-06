package org.clarksnut.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.LineBean;
import org.clarksnut.datasource.peru.types.TipoDocumento;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.models.utils.ClarksnutModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PeruInvoiceDatasourceProvider implements DatasourceProvider {

    @Override
    public String getName() {
        return "PeruInvoiceDS";
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public Datasource getDatasource(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        InvoiceType invoiceType = read(file);

        PeruInvoiceDatasource bean = new PeruInvoiceDatasource();

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

        bean.setTotalVenta(legalMonetaryTotalType.getPayableAmount().getValue().doubleValue());
        if (allowanceTotalAmountType != null) {
            bean.setTotalDescuentoGlobal(allowanceTotalAmountType.getValue().doubleValue());
        }
        if (chargeTotalAmountType != null) {
            bean.setTotalOtrosCargos(chargeTotalAmountType.getValue().doubleValue());
        }

        // Taxs
        bean.setTributos(BeanUtils.toTributos(invoiceType.getTaxTotal()));

        // Informacion adicional
        bean.setInformacionAdicional(BeanUtils.toInformacionAdicional(invoiceType.getUBLExtensions()));

        // Lines
        bean.setDetalle(getLines(invoiceType.getInvoiceLine()));

        return bean;
    }

    private InvoiceType read(XmlFileModel file) throws ImpossibleToUnmarshallException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), InvoiceType.class);
        } catch (Exception e) {
            throw new ImpossibleToUnmarshallException("Could not unmarshall to:" + InvoiceType.class.getName());
        }
    }

    private List<LineBean> getLines(List<InvoiceLineType> invoiceLineTypes) {
        List<LineBean> result = new ArrayList<>();

        for (InvoiceLineType invoiceLineType : invoiceLineTypes) {
            LineBean lineBean = new LineBean();

            lineBean.setCantidad(invoiceLineType.getInvoicedQuantity().getValue().doubleValue());
            lineBean.setUnidadMedida(invoiceLineType.getInvoicedQuantity().getUnitCode());
            lineBean.setPrecioUnitario(invoiceLineType.getPrice().getPriceAmount().getValue().doubleValue());
            lineBean.setTotalValorVenta(invoiceLineType.getLineExtensionAmount().getValue().doubleValue());

            // Precio de venta unitario
            BeanUtils.agregarPrecioUnitario(lineBean, Optional.ofNullable(invoiceLineType.getPricingReference().getAlternativeConditionPrice()));

            // Descuentos por item
            List<AllowanceChargeType> allowanceChargeTypes = invoiceLineType.getAllowanceCharge();
            if (allowanceChargeTypes != null && !allowanceChargeTypes.isEmpty()) {
                lineBean.setTotalDescuento(allowanceChargeTypes.get(0).getAmount().getValue().doubleValue());
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
