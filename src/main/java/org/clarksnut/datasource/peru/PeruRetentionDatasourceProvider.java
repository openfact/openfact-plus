package org.clarksnut.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ExchangeRateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.RetentionLineBean;
import org.clarksnut.datasource.peru.types.TipoDocumento;
import org.clarksnut.datasource.peru.types.TipoRegimenRetencion;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.openfact.retention.RetentionType;
import org.openfact.retention.SUNATRetentionDocumentReferenceType;

import java.util.ArrayList;
import java.util.List;

public class PeruRetentionDatasourceProvider implements DatasourceProvider {

    @Override
    public String getName() {
        return "PeruRetentionDS";
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public Datasource getDatasource(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        RetentionType retentionType = read(file);

        PeruRetentionDatasource bean = new PeruRetentionDatasource();

        bean.setIdAsignado(retentionType.getId().getValue());
        bean.setFechaEmision(retentionType.getIssueDate().getValue().toGregorianCalendar().getTime());
        bean.setEmisor(BeanUtils.toSupplier(retentionType.getAgentParty()));
        bean.setProveedor(BeanUtils.toCustomer(retentionType.getReceiverParty()));
        TipoRegimenRetencion.getFromCode(retentionType.getSunatRetentionSystemCode().getValue()).ifPresent(c -> {
            bean.setRegimen(c.getDenominacion());
        });
        bean.setTasa(retentionType.getSunatRetentionPercent().getValue().doubleValue());

        List<NoteType> noteTypes = retentionType.getNote();
        if (noteTypes != null && !noteTypes.isEmpty()) {
            bean.setObservaciones(noteTypes.get(0).getValue());
        }

        bean.setImporteTotalRetenido(retentionType.getTotalInvoiceAmount().getValue().doubleValue());
        bean.setMonedaImporteTotalRetenido(retentionType.getTotalInvoiceAmount().getCurrencyID());
        bean.setImporteTotalPagado(retentionType.getSunatTotalPaid().getValue().doubleValue());
        bean.setMonedaImporteTotalPagado(retentionType.getSunatTotalPaid().getCurrencyID());

        // Detalle
        List<SUNATRetentionDocumentReferenceType> sunatRetentionDocumentReferenceTypes = retentionType.getSunatRetentionDocumentReference();

        List<RetentionLineBean> detalle = new ArrayList<>();

        for (SUNATRetentionDocumentReferenceType sunatRetentionDocumentReferenceType : sunatRetentionDocumentReferenceTypes) {
            RetentionLineBean lineBean = new RetentionLineBean();

            lineBean.setDocumentoRelacionado(sunatRetentionDocumentReferenceType.getID().getValue());
            TipoDocumento.getFromCode(sunatRetentionDocumentReferenceType.getID().getSchemeID()).ifPresent(c -> {
                lineBean.setTipoDocumentoRelacionado(c.getDenominacion());
            });
            lineBean.setFechaEmisionDocumentoRelacionado(sunatRetentionDocumentReferenceType.getIssueDate().getValue().toGregorianCalendar().getTime());
            lineBean.setImporteTotalDocumentoRelacionado(sunatRetentionDocumentReferenceType.getTotalInvoiceAmount().getValue().doubleValue());
            lineBean.setMonedaDocumentoRelacionado(sunatRetentionDocumentReferenceType.getTotalInvoiceAmount().getCurrencyID());

            lineBean.setFechaPago(sunatRetentionDocumentReferenceType.getPayment().getPaidDate().getValue().toGregorianCalendar().getTime());
            lineBean.setImportePagoSinRetencion(sunatRetentionDocumentReferenceType.getPayment().getPaidAmount().getValue().doubleValue());
            lineBean.setMonedaPagoSinRetencion(sunatRetentionDocumentReferenceType.getPayment().getPaidAmount().getCurrencyID());

            lineBean.setImporteRetenido(sunatRetentionDocumentReferenceType.getSUNATRetentionInformation().getSUNATRetentionAmount().getValue().doubleValue());
            lineBean.setMonedaImporteRetenido(sunatRetentionDocumentReferenceType.getSUNATRetentionInformation().getSUNATRetentionAmount().getCurrencyID());
            lineBean.setFechaRetencion(sunatRetentionDocumentReferenceType.getSUNATRetentionInformation().getSUNATRetentionDate().getValue().toGregorianCalendar().getTime());
            lineBean.setImporteTotalAPagar(sunatRetentionDocumentReferenceType.getSUNATRetentionInformation().getSUNATNetTotalPaid().getValue().doubleValue());
            lineBean.setMonedaImporteTotalAPagar(sunatRetentionDocumentReferenceType.getSUNATRetentionInformation().getSUNATNetTotalPaid().getCurrencyID());

            ExchangeRateType exchangeRateType = sunatRetentionDocumentReferenceType.getSUNATRetentionInformation().getExchangeRate();
            if (exchangeRateType != null) {
                lineBean.setMonedaReferencia(exchangeRateType.getSourceCurrencyCodeValue());
                lineBean.setMonedaObjetivo(exchangeRateType.getTargetCurrencyCodeValue());
                lineBean.setTipoCambio(exchangeRateType.getCalculationRateValue().doubleValue());
                lineBean.setFechaCambio(exchangeRateType.getDate().getValue().toGregorianCalendar().getTime());
            }

            detalle.add(lineBean);
        }

        bean.setDetalle(detalle);

        return bean;
    }

    private RetentionType read(XmlFileModel file) throws ImpossibleToUnmarshallException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), RetentionType.class);
        } catch (Exception e) {
            throw new ImpossibleToUnmarshallException("Could not unmarshall to:" + RetentionType.class.getName());
        }
    }

}
