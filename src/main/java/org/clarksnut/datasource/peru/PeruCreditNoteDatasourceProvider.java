package org.clarksnut.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.BillingReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CreditNoteLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.LineBean;
import org.clarksnut.datasource.peru.types.TipoDocumento;
import org.clarksnut.datasource.peru.types.TipoNotaCredito;
import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PeruCreditNoteDatasourceProvider implements DatasourceProvider {

    @Override
    public String getName() {
        return "PeruCreditNoteDS";
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public Datasource getDatasource(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        CreditNoteType creditNoteType = read(file);
        if (creditNoteType == null) {
            return null;
        }

        PeruCreditNoteDatasource bean = new PeruCreditNoteDatasource();

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
            TipoDocumento.getFromCode(documentReferenceType.getDocumentTypeCode().getValue()).ifPresent(c -> {
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

        // Lines
        bean.setDetalle(getLines(creditNoteType.getCreditNoteLine()));

        return bean;
    }

    private CreditNoteType read(XmlFileModel file) throws ImpossibleToUnmarshallException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), CreditNoteType.class);
        } catch (Exception e) {
            throw new ImpossibleToUnmarshallException("Could not unmarshall to:" + CreditNoteType.class.getName());
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
