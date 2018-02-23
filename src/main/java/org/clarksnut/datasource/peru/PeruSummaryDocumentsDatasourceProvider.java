package org.clarksnut.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AllowanceChargeType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.InformacionAdicionalBean;
import org.clarksnut.datasource.peru.beans.SummaryLineBean;
import org.clarksnut.datasource.peru.types.TipoDocumento;
import org.clarksnut.datasource.peru.types.TipoPagoResumen;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;
import sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.SummaryDocumentsLineType;

import java.util.ArrayList;
import java.util.List;

public class PeruSummaryDocumentsDatasourceProvider implements DatasourceProvider {

    @Override
    public String getName() {
        return "PeruSummaryDocumentsDS";
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public Datasource getDatasource(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        SummaryDocumentsType summaryDocumentsType = read(file);

        PeruSummaryDocumentsDatasource bean = new PeruSummaryDocumentsDatasource();

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

    private SummaryDocumentsType read(XmlFileModel file) throws ImpossibleToUnmarshallException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), SummaryDocumentsType.class);
        } catch (Exception e) {
            throw new ImpossibleToUnmarshallException("Could not unmarshall to:" + SummaryDocumentsType.class.getName());
        }
    }

}
