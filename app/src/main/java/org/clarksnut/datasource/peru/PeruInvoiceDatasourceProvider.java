package org.clarksnut.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.MonetaryTotalType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PaymentMeansType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.TaxTotalType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AllowanceTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ChargeTotalAmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueTimeType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.ExtensionContentType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.DatasourceType;
import org.clarksnut.datasource.peru.beans.BeanUtils;
import org.clarksnut.datasource.peru.beans.InvoiceBean;
import org.clarksnut.datasource.peru.types.ConceptosTributarios;
import org.clarksnut.datasource.peru.types.TipoInvoice;
import org.clarksnut.datasource.peru.types.TipoTributo;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.w3c.dom.Document;
import sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.AdditionalInformationType;
import sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.AdditionalMonetaryTotalType;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
@DatasourceType(datasource = "PeruInvoiceDS")
public class PeruInvoiceDatasourceProvider implements DatasourceProvider {

    @Override
    public boolean support(DocumentModel document, XmlFileModel file) throws FileFetchException {
        return read(file) != null;
    }

    @Override
    public Object getDatasource(DocumentModel document, XmlFileModel file) throws FileFetchException {
        InvoiceType invoiceType = read(file);
        if (invoiceType == null) {
            return null;
        }

        InvoiceBean bean = new InvoiceBean();

        //
        bean.setAssignedId(invoiceType.getID().getValue());
        bean.setInvoiceType(TipoInvoice.getFromCode(invoiceType.getInvoiceTypeCode().getValue()).get().getDenominacion());
        bean.setCurrency(invoiceType.getDocumentCurrencyCode().getValue());
        bean.setSupplier(BeanUtils.toSupplier(invoiceType.getAccountingSupplierParty()));
        bean.setCustomer(BeanUtils.toCustomer(invoiceType.getAccountingCustomerParty()));

        // Issue date
        Date issueDate = invoiceType.getIssueDate().getValue().toGregorianCalendar().getTime();

        IssueTimeType issueTimeType = invoiceType.getIssueTime();
        if (issueTimeType != null) {
            Date issueTime = issueTimeType.getValue().toGregorianCalendar().getTime();

            Calendar issueDateCalendar = Calendar.getInstance();
            issueDateCalendar.setTime(issueDate);

            Calendar issueTimeCalendar = Calendar.getInstance();
            issueTimeCalendar.setTime(issueTime);

            issueDateCalendar.set(Calendar.HOUR_OF_DAY, issueTimeCalendar.get(Calendar.HOUR_OF_DAY));
            issueDateCalendar.set(Calendar.MINUTE, issueTimeCalendar.get(Calendar.MINUTE));
            issueDateCalendar.set(Calendar.SECOND, issueTimeCalendar.get(Calendar.SECOND));

            issueDate = issueDateCalendar.getTime();
        }
        bean.setIssueDate(issueDate);

        // Payment due
        List<PaymentMeansType> paymentMeans = invoiceType.getPaymentMeans();
        if (paymentMeans != null && !paymentMeans.isEmpty()) {
            bean.setPaymentDue(paymentMeans.get(0).getPaymentDueDate().getValue().toGregorianCalendar().getTime());
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

        // Tax
        for (TaxTotalType taxTotalType : invoiceType.getTaxTotal()) {
            TipoTributo.getFromCode(taxTotalType.getTaxSubtotal().get(0).getTaxCategory().getTaxScheme().getID().getValue()).ifPresent(c -> {
                switch (c) {
                    case IGV:
                        bean.setTotalIgv(taxTotalType.getTaxAmount().getValue().floatValue());
                        break;
                    case ISC:
                        bean.setTotalIsc(taxTotalType.getTaxAmount().getValue().floatValue());
                        break;
                    case OTROS:
                        bean.setTotalOtrosTributos(taxTotalType.getTaxAmount().getValue().floatValue());
                        break;
                }
            });
        }

        // Additional information
        for (UBLExtensionType ublExtensionType : invoiceType.getUBLExtensions().getUBLExtension()) {
            ExtensionContentType extensionContent = ublExtensionType.getExtensionContent();
            Document extensionContentDocument = extensionContent.getAny().getOwnerDocument();
            try {
                AdditionalInformationType additionalInformationType = ClarksnutModelUtils.unmarshall(extensionContentDocument, AdditionalInformationType.class);
                for (AdditionalMonetaryTotalType additionalMonetaryTotalType : additionalInformationType.getAdditionalMonetaryTotal()) {
                    Optional<ConceptosTributarios> optional = ConceptosTributarios.getByCode(additionalMonetaryTotalType.getID().getValue());
                    optional.ifPresent(c -> {
                        switch (c) {
                            case TOTAL_VALOR_VENTA_OPERACIONES_GRAVADAS:
                                bean.setTotalGravada(additionalMonetaryTotalType.getPayableAmount().getValue().floatValue());
                                break;
                            case TOTAL_VALOR_VENTA_OPERACIONES_INAFECTAS:
                                bean.setTotalInafecta(additionalMonetaryTotalType.getPayableAmount().getValue().floatValue());
                                break;
                            case TOTAL_VALOR_VENTA_OPERACIONES_EXONERADAS:
                                bean.setTotalExonerada(additionalMonetaryTotalType.getPayableAmount().getValue().floatValue());
                                break;
                            case TOTAL_VALOR_VENTA_OPERACIONES_GRATUITAS:
                                bean.setTotalGratuita(additionalMonetaryTotalType.getPayableAmount().getValue().floatValue());
                                break;
                            case TOTAL_DESCUENTOS:
                                bean.setTotalDescuentos(additionalMonetaryTotalType.getPayableAmount().getValue().floatValue());
                                break;
                        }
                    });
                }
                break;
            } catch (JAXBException e) {
                // No problem
            }
        }

        return bean;
    }

    private InvoiceType read(XmlFileModel file) throws FileFetchException {
        try {
            return ClarksnutModelUtils.unmarshall(file.getDocument(), InvoiceType.class);
        } catch (JAXBException e) {
            return null;
        }
    }

}
