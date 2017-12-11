package org.clarksnut.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.LineBean;
import org.clarksnut.datasource.peru.types.*;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.util.*;

@Stateless
@DatasourceType(datasource = "PeruCreditNoteDS")
public class PeruCreditNoteBeanProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        CreditNoteType creditNoteType = read(file);
        if (creditNoteType == null) {
            return null;
        }

        CreditNoteDatasource bean = new CreditNoteDatasource();

        bean.setIdAsignado(creditNoteType.getID().getValue());
        TipoNotaCredito.getFromCode(creditNoteType.getDiscrepancyResponse().get(0).getResponseCode().getValue()).ifPresent(c -> bean.setTipoNotaCredito(c.getDenominacion()));
        bean.setMotivoSustento(creditNoteType.getDiscrepancyResponse().get(0).getDescription().get(0).getValue());
        bean.setMoneda(creditNoteType.getDocumentCurrencyCode().getValue());
        bean.setProveedor(BeanUtils.toSupplier(creditNoteType.getAccountingSupplierParty()));
        bean.setCliente(BeanUtils.toCustomer(creditNoteType.getAccountingCustomerParty()));

        // Fecha emision
        bean.setFechaEmision(BeanUtils.toDate(creditNoteType.getIssueDate(), Optional.ofNullable(creditNoteType.getIssueTime())));

        // Documento que modifica
        List<BillingReferenceType> billingReferenceTypes = creditNoteType.getBillingReference();
        if (billingReferenceTypes != null && !billingReferenceTypes.isEmpty()) {
            DocumentReferenceType documentReferenceType = billingReferenceTypes.get(0).getInvoiceDocumentReference();
            bean.setDocumentoModifica(documentReferenceType.getID().getValue());
            TipoInvoice.getFromCode(documentReferenceType.getDocumentTypeCode().getValue()).ifPresent(c -> {
                bean.setTipoDocumentoModifica(c.getDenominacion());
            });
        }

        // Tipo y numero de remision
        List<DocumentReferenceType> documentReferenceTypes = creditNoteType.getDespatchDocumentReference();
        if (documentReferenceTypes != null && !documentReferenceTypes.isEmpty()) {
            for (DocumentReferenceType documentReferenceType : documentReferenceTypes) {
                bean.setNumeroGuiaRemisionRelacionada(documentReferenceType.getID().getValue());
                break;
            }
        }

        // Otro documento relacionado
        List<DocumentReferenceType> documentReferenceTypes1 = creditNoteType.getAdditionalDocumentReference();
        if (documentReferenceTypes1 != null && !documentReferenceTypes1.isEmpty()) {
            for (DocumentReferenceType documentReferenceType : documentReferenceTypes1) {
                bean.setOtroDocumentoRelacionadoId(documentReferenceType.getID().getValue());
                break;
            }
        }

        // Payable amount
        MonetaryTotalType legalMonetaryTotalType = creditNoteType.getLegalMonetaryTotal();
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
        bean.setTributos(BeanUtils.toTributos(creditNoteType.getTaxTotal()));

        // Informacion adicional
        bean.setInformacionAdicional(BeanUtils.toInformacionAdicional(creditNoteType.getUBLExtensions()));

//        // Lines
        bean.setDetalle(getLines(creditNoteType.getCreditNoteLine()));

        return bean;
    }

    private CreditNoteType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), CreditNoteType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

    private List<LineBean> getLines(List<CreditNoteLineType> creditNoteLineTypes) {
        List<LineBean> result = new ArrayList<>();

        for (CreditNoteLineType creditNoteLineType : creditNoteLineTypes) {
            LineBean lineBean = new LineBean();

            lineBean.setCantidad(creditNoteLineType.getCreditedQuantity().getValue().floatValue());
            lineBean.setUnidadMedida(creditNoteLineType.getCreditedQuantity().getUnitCode());
            lineBean.setPrecioUnitario(creditNoteLineType.getPrice().getPriceAmount().getValue().floatValue());
            lineBean.setTotalValorVenta(creditNoteLineType.getLineExtensionAmount().getValue().floatValue());

            // Precio de venta unitario
            BeanUtils.agregarPrecioUnitario(lineBean, Optional.ofNullable(creditNoteLineType.getPricingReference().getAlternativeConditionPrice()));

            // Descripcion y codigo de producto
            BeanUtils.agregarDescripcionYCodigo(lineBean, Optional.ofNullable(creditNoteLineType.getItem()));

            // Impuestos
            BeanUtils.agregarImpuestosEnLine(lineBean, Optional.ofNullable(creditNoteLineType.getTaxTotal()));

            result.add(lineBean);
        }

        return result;
    }

}
