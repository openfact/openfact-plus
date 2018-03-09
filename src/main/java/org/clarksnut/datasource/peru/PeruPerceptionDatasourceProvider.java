package org.clarksnut.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ExchangeRateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.PerceptionLineBean;
import org.clarksnut.datasource.peru.types.TipoDocumento;
import org.clarksnut.datasource.peru.types.TipoRegimenPercepcion;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.openfact.perception.PerceptionType;
import org.openfact.perception.SUNATPerceptionDocumentReferenceType;

import java.util.ArrayList;
import java.util.List;

public class PeruPerceptionDatasourceProvider implements DatasourceProvider {

    @Override
    public String getName() {
        return "PeruPerceptionDS";
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public Datasource getDatasource(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        PerceptionType perceptionType = read(file);

        PeruPerceptionDatasource bean = new PeruPerceptionDatasource();

        bean.setIdAsignado(perceptionType.getId().getValue());
        bean.setFechaEmision(perceptionType.getIssueDate().getValue().toGregorianCalendar().getTime());
        bean.setEmisor(BeanUtils.toSupplier(perceptionType.getAgentParty()));
        bean.setCliente(BeanUtils.toCustomer(perceptionType.getReceiverParty()));
        TipoRegimenPercepcion.getFromCode(perceptionType.getSunatPerceptionSystemCode().getValue()).ifPresent(c -> {
            bean.setRegimen(c.getDenominacion());
        });
        bean.setTasa(perceptionType.getSunatPerceptionPercent().getValue().doubleValue());

        List<NoteType> noteTypes = perceptionType.getNote();
        if (noteTypes != null && !noteTypes.isEmpty()) {
            bean.setObservaciones(noteTypes.get(0).getValue());
        }

        bean.setImporteTotalPercibido(perceptionType.getTotalInvoiceAmount().getValue().doubleValue());
        bean.setMonedaImporteTotalPercibido(perceptionType.getTotalInvoiceAmount().getCurrencyID());
        bean.setImporteTotalCobrado(perceptionType.getSunatTotalCashed().getValue().doubleValue());
        bean.setMonedaImporteTotalCobrado(perceptionType.getSunatTotalCashed().getCurrencyID());

        // Detalle
        List<SUNATPerceptionDocumentReferenceType> sunatPerceptionDocumentReferenceTypes = perceptionType.getSunatPerceptionDocumentReference();

        List<PerceptionLineBean> detalle = new ArrayList<>();

        for (SUNATPerceptionDocumentReferenceType sunatPerceptionDocumentReferenceType : sunatPerceptionDocumentReferenceTypes) {
            PerceptionLineBean lineBean = new PerceptionLineBean();

            lineBean.setDocumentoRelacionado(sunatPerceptionDocumentReferenceType.getId().getValue());
            TipoDocumento.getFromCode(sunatPerceptionDocumentReferenceType.getId().getSchemeID()).ifPresent(c -> {
                lineBean.setTipoDocumentoRelacionado(c.getDenominacion());
            });
            lineBean.setFechaEmisionDocumentoRelacionado(sunatPerceptionDocumentReferenceType.getIssueDate().getValue().toGregorianCalendar().getTime());
            lineBean.setImporteTotalDocumentoRelacionado(sunatPerceptionDocumentReferenceType.getTotalInvoiceAmount().getValue().doubleValue());
            lineBean.setMonedaDocumentoRelacionado(sunatPerceptionDocumentReferenceType.getTotalInvoiceAmount().getCurrencyID());

            lineBean.setFechaCobro(sunatPerceptionDocumentReferenceType.getPayment().getPaidDate().getValue().toGregorianCalendar().getTime());
            lineBean.setImporteCobro(sunatPerceptionDocumentReferenceType.getPayment().getPaidAmount().getValue().doubleValue());
            lineBean.setMonedaCobro(sunatPerceptionDocumentReferenceType.getPayment().getPaidAmount().getCurrencyID());

            lineBean.setImportePercibido(sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatPerceptionAmount().getValue().doubleValue());
            lineBean.setMonedaImportePercibido(sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatPerceptionAmount().getCurrencyID());
            lineBean.setFechaPercepcion(sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatPerceptionDate().getValue().toGregorianCalendar().getTime());
            lineBean.setImporteTotalACobrar(sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatNetTotalCashed().getValue().doubleValue());
            lineBean.setMonedaImporteTotalACobrar(sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatNetTotalCashed().getCurrencyID());

            ExchangeRateType exchangeRateType = sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getExchangeRate();
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

    private PerceptionType read(XmlFileModel file) throws ImpossibleToUnmarshallException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), PerceptionType.class);
        } catch (Exception e) {
            throw new ImpossibleToUnmarshallException("Could not unmarshall to:" + PerceptionType.class.getName());
        }
    }

}
