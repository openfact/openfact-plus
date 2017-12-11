package org.clarksnut.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.LineBean;
import org.clarksnut.datasource.peru.types.TipoInvoice;
import org.clarksnut.datasource.peru.types.TipoNotaDebito;
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
@DatasourceType(datasource = "PeruDebitNoteDS")
public class PeruDebitNoteBeanProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        DebitNoteType debitNoteType = read(file);
        if (debitNoteType == null) {
            return null;
        }

        DebitNoteDatasource bean = new DebitNoteDatasource();
       
        bean.setIdAsignado(debitNoteType.getID().getValue());
        TipoNotaDebito.getFromCode(debitNoteType.getDiscrepancyResponse().get(0).getResponseCode().getValue()).ifPresent(c -> bean.setTipoNotaDebito(c.getDenominacion()));
        bean.setMotivoSustento(debitNoteType.getDiscrepancyResponse().get(0).getDescription().get(0).getValue());
        bean.setMoneda(debitNoteType.getDocumentCurrencyCode().getValue());
        bean.setProveedor(BeanUtils.toSupplier(debitNoteType.getAccountingSupplierParty()));
        bean.setCliente(BeanUtils.toCustomer(debitNoteType.getAccountingCustomerParty()));

        // Fecha emision
        bean.setFechaEmision(BeanUtils.toDate(debitNoteType.getIssueDate(), Optional.ofNullable(debitNoteType.getIssueTime())));

        // Documento que modifica
        List<BillingReferenceType> billingReferenceTypes = debitNoteType.getBillingReference();
        if (billingReferenceTypes != null && !billingReferenceTypes.isEmpty()) {
            DocumentReferenceType documentReferenceType = billingReferenceTypes.get(0).getInvoiceDocumentReference();
            bean.setDocumentoModifica(documentReferenceType.getID().getValue());
            TipoInvoice.getFromCode(documentReferenceType.getDocumentTypeCode().getValue()).ifPresent(c -> {
                bean.setTipoDocumentoModifica(c.getDenominacion());
            });
        }

        // Tipo y numero de remision
        List<DocumentReferenceType> documentReferenceTypes = debitNoteType.getDespatchDocumentReference();
        if (documentReferenceTypes != null && !documentReferenceTypes.isEmpty()) {
            for (DocumentReferenceType documentReferenceType : documentReferenceTypes) {
                bean.setNumeroGuiaRemisionRelacionada(documentReferenceType.getID().getValue());
                break;
            }
        }

        // Otro documento relacionado
        List<DocumentReferenceType> documentReferenceTypes1 = debitNoteType.getAdditionalDocumentReference();
        if (documentReferenceTypes1 != null && !documentReferenceTypes1.isEmpty()) {
            for (DocumentReferenceType documentReferenceType : documentReferenceTypes1) {
                bean.setOtroDocumentoRelacionadoId(documentReferenceType.getID().getValue());
                break;
            }
        }

        // Payable amount
        MonetaryTotalType legalMonetaryTotalType = debitNoteType.getRequestedMonetaryTotal();
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
        bean.setTributos(BeanUtils.toTributos(debitNoteType.getTaxTotal()));

        // Informacion adicional
        bean.setInformacionAdicional(BeanUtils.toInformacionAdicional(debitNoteType.getUBLExtensions()));

        // Lines
        bean.setDetalle(getLines(debitNoteType.getDebitNoteLine()));

        return bean;
    }

    private DebitNoteType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), DebitNoteType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

    private List<LineBean> getLines(List<DebitNoteLineType> debitNoteLineTypes) {
        List<LineBean> result = new ArrayList<>();

        for (DebitNoteLineType debitNoteLineType : debitNoteLineTypes) {
            LineBean lineBean = new LineBean();

            lineBean.setCantidad(debitNoteLineType.getDebitedQuantity().getValue().floatValue());
            lineBean.setUnidadMedida(debitNoteLineType.getDebitedQuantity().getUnitCode());
            lineBean.setPrecioUnitario(debitNoteLineType.getPrice().getPriceAmount().getValue().floatValue());
            lineBean.setTotalValorVenta(debitNoteLineType.getLineExtensionAmount().getValue().floatValue());

            // Precio de venta unitario
            BeanUtils.agregarPrecioUnitario(lineBean, Optional.ofNullable(debitNoteLineType.getPricingReference().getAlternativeConditionPrice()));

            // Descripcion y codigo de producto
            BeanUtils.agregarDescripcionYCodigo(lineBean, Optional.ofNullable(debitNoteLineType.getItem()));

            // Impuestos
            BeanUtils.agregarImpuestosEnLine(lineBean, Optional.ofNullable(debitNoteLineType.getTaxTotal()));

            result.add(lineBean);
        }

        return result;
    }

}
