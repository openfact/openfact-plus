package org.clarksnut.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.datasource.peru.beans.*;
import org.clarksnut.datasource.peru.types.TipoDocumento;
import org.clarksnut.datasource.peru.types.TipoPagoResumen;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;
import sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.SummaryDocumentsLineType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

@Stateless
@DatasourceType(datasource = "PeruSummaryDocumentsDS")
public class PeruSummaryDocumentsBeanProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Datasource getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        SummaryDocumentsType summaryDocumentsType = read(file);
        if (summaryDocumentsType == null) {
            return null;
        }

        SummaryDocumentsDatasource bean = new SummaryDocumentsDatasource();

        bean.setIdAsignado(summaryDocumentsType.getID().getValue());
        bean.setFechaEmision(summaryDocumentsType.getIssueDate().getValue().toGregorianCalendar().getTime());
        bean.setFechaGeneracion(summaryDocumentsType.getReferenceDate().getValue().toGregorianCalendar().getTime());
        bean.setProveedor(BeanUtils.toSupplier(summaryDocumentsType.getAccountingSupplierParty()));

        List<SummaryLineBean> lines = new ArrayList<>();

        List<SummaryDocumentsLineType> summaryDocumentsLineTypes = summaryDocumentsType.getSummaryDocumentsLine();
        for (SummaryDocumentsLineType summaryDocumentsLineType : summaryDocumentsLineTypes) {
            SummaryLineBean lineBean = new SummaryLineBean();

            lineBean.setDocumentoSerie(summaryDocumentsLineType.getDocumentSerialID().getValue());
            lineBean.setDocumentoNumeroInicio(summaryDocumentsLineType.getStartDocumentNumberID().getValue());
            lineBean.setDocumentoNumeroFin(summaryDocumentsLineType.getEndDocumentNumberID().getValue());
            TipoDocumento.getFromCode(summaryDocumentsLineType.getDocumentTypeCode().getValue()).ifPresent(c -> {
                lineBean.setTipoDocumento(c.getDenominacion());
            });
            lineBean.setTotalVenta(summaryDocumentsLineType.getTotalAmount().getValue().floatValue());
            lineBean.setMoneda(summaryDocumentsLineType.getTotalAmount().getCurrencyID().value());

            List<AllowanceChargeType> allowanceChargeTypes = summaryDocumentsLineType.getAllowanceCharge();
            if (allowanceChargeTypes != null && !allowanceChargeTypes.isEmpty()) {
                lineBean.setTotalOtrosCargos(allowanceChargeTypes.get(0).getAmount().getValue().floatValue());
            }

            // Tributos
            lineBean.setTributos(BeanUtils.toTributos(summaryDocumentsLineType.getTaxTotal()));

            // Informacion adicional
            List<PaymentType> paymentTypes = summaryDocumentsLineType.getBillingPayment();
            if (paymentTypes != null) {
                InformacionAdicionalBean informacionAdicionalBean = new InformacionAdicionalBean();
                for (PaymentType paymentType : paymentTypes) {
                    TipoPagoResumen.getFromCode(paymentType.getInstructionID().getValue()).ifPresent(c -> {
                        switch (c) {
                            case TOTAL_VENTA_OPERACIONES_GRAVADAS:
                                informacionAdicionalBean.setTotalGravada(paymentType.getPaidAmount().getValue().floatValue());
                                break;
                            case TOTAL_VENTA_OPERACIONES_EXONERADAS:
                                informacionAdicionalBean.setTotalExonerada(paymentType.getPaidAmount().getValue().floatValue());
                                break;
                            case TOTAL_VENTA_OPERACIONES_INAFECTAS:
                                informacionAdicionalBean.setTotalInafecta(paymentType.getPaidAmount().getValue().floatValue());
                                break;
                        }
                    });
                }
                lineBean.setInformacionAdicional(informacionAdicionalBean);
            }

            lines.add(lineBean);
        }

        bean.setDetalle(lines);

        return bean;
    }

    private SummaryDocumentsType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), SummaryDocumentsType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
