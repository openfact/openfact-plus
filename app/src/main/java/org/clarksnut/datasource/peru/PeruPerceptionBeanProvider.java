package org.clarksnut.datasource.peru;


import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ExchangeRateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.PerceptionLineBean;
import org.clarksnut.datasource.peru.types.TipoDocumento;
import org.clarksnut.datasource.peru.types.TipoRegimenPercepcion;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.documents.reader.pe.common.jaxb.perception.PerceptionType;
import org.clarksnut.documents.reader.pe.common.jaxb.perception.SUNATPerceptionDocumentReferenceType;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

@Stateless
@DatasourceType(datasource = "PeruPerceptionDS")
public class PeruPerceptionBeanProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        PerceptionType perceptionType = read(file);
        if (perceptionType == null) {
            return null;
        }

        PerceptionDatasource bean = new PerceptionDatasource();

        bean.setIdAsignado(perceptionType.getId().getValue());
        bean.setFechaEmision(perceptionType.getIssueDate().getValue().toGregorianCalendar().getTime());
        bean.setEmisor(BeanUtils.toSupplier(perceptionType.getAgentParty()));
        bean.setCliente(BeanUtils.toCustomer(perceptionType.getReceiverParty()));
        TipoRegimenPercepcion.getFromCode(perceptionType.getSunatPerceptionSystemCode().getValue()).ifPresent(c -> {
            bean.setRegimen(c.getDenominacion());
        });
        bean.setTasa(perceptionType.getSunatPerceptionPercent().getValue().floatValue());

        List<NoteType> noteTypes = perceptionType.getNote();
        if (noteTypes != null && !noteTypes.isEmpty()) {
            bean.setObservaciones(noteTypes.get(0).getValue());
        }

        bean.setImporteTotalPercibido(perceptionType.getTotalInvoiceAmount().getValue().floatValue());
        bean.setMonedaImporteTotalPercibido(perceptionType.getTotalInvoiceAmount().getCurrencyID());
        bean.setImporteTotalCobrado(perceptionType.getSunatTotalCashed().getValue().floatValue());
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
            lineBean.setImporteTotalDocumentoRelacionado(sunatPerceptionDocumentReferenceType.getTotalInvoiceAmount().getValue().floatValue());
            lineBean.setMonedaDocumentoRelacionado(sunatPerceptionDocumentReferenceType.getTotalInvoiceAmount().getCurrencyID());

            lineBean.setFechaCobro(sunatPerceptionDocumentReferenceType.getPayment().getPaidDate().getValue().toGregorianCalendar().getTime());
            lineBean.setImporteCobro(sunatPerceptionDocumentReferenceType.getPayment().getPaidAmount().getValue().floatValue());
            lineBean.setMonedaCobro(sunatPerceptionDocumentReferenceType.getPayment().getPaidAmount().getCurrencyID());

            lineBean.setImportePercibido(sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatPerceptionAmount().getValue().floatValue());
            lineBean.setMonedaImportePercibido(sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatPerceptionAmount().getCurrencyID());
            lineBean.setFechaPercepcion(sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatPerceptionDate().getValue().toGregorianCalendar().getTime());
            lineBean.setImporteTotalACobrar(sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatNetTotalCashed().getValue().floatValue());
            lineBean.setMonedaImporteTotalACobrar(sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getSunatNetTotalCashed().getCurrencyID());

            ExchangeRateType exchangeRateType = sunatPerceptionDocumentReferenceType.getSunatPerceptionInformation().getExchangeRate();
            if (exchangeRateType != null) {
                lineBean.setMonedaReferencia(exchangeRateType.getSourceCurrencyCodeValue());
                lineBean.setMonedaObjetivo(exchangeRateType.getTargetCurrencyCodeValue());
                lineBean.setTipoCambio(exchangeRateType.getCalculationRateValue().floatValue());
                lineBean.setFechaCambio(exchangeRateType.getDate().getValue().toGregorianCalendar().getTime());
            }

            detalle.add(lineBean);
        }

        bean.setDetalle(detalle);

        return bean;
    }

    private PerceptionType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), PerceptionType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
