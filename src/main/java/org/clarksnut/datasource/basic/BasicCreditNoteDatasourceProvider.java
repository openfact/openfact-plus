package org.clarksnut.datasource.basic;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.basic.beans.LineBean;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BasicCreditNoteDatasourceProvider implements DatasourceProvider {

    @Override
    public String getName() {
        return "BasicCreditNoteDS";
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    public Datasource getDatasource(XmlUBLFileModel file) throws ImpossibleToUnmarshallException {
        CreditNoteType creditNoteType = null;
        try {
            creditNoteType = UBL21Reader.creditNote().read(file.getDocument());
        } catch (Exception e) {
            // Nothing to do
        }
        if (creditNoteType == null) {
            throw new ImpossibleToUnmarshallException("Could not unmarshall to:" + CreditNoteType.class.getName());
        }

        BasicCreditNoteDatasource bean = new BasicCreditNoteDatasource();

        bean.setAssignedId(creditNoteType.getIDValue());
        bean.setCurrency(creditNoteType.getDocumentCurrencyCodeValue());
        bean.setSupplier(BasicDatasourceUtils.toSupplier(creditNoteType.getAccountingSupplierParty()));
        bean.setCustomer(BasicDatasourceUtils.toCustomer(creditNoteType.getAccountingCustomerParty()));

        // Invoice reference
        String invoiceReference = creditNoteType.getDiscrepancyResponse().stream()
                .filter(p -> p.getReferenceIDValue() != null)
                .map(ResponseType::getReferenceIDValue)
                .collect(Collectors.joining(", "));
        bean.setInvoiceReference(invoiceReference);

        // Issue date
        bean.setIssueDate(BasicDatasourceUtils.toDate(creditNoteType.getIssueDate(), Optional.ofNullable(creditNoteType.getIssueTime())));

        // Payable amount
        MonetaryTotalType legalMonetaryTotalType = creditNoteType.getLegalMonetaryTotal();
        if (legalMonetaryTotalType != null) {
            bean.setMonetaryTotal(BasicDatasourceUtils.toMonetaryTotal(legalMonetaryTotalType));
        }

        // Total tax
        double totalTax = creditNoteType.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
        bean.setTotalTax(totalTax);

        // Lines
        bean.setLines(getLines(creditNoteType.getCreditNoteLine()));

        return bean;
    }

    private List<LineBean> getLines(List<CreditNoteLineType> creditNoteLineTypes) {
        List<LineBean> result = new ArrayList<>();

        for (CreditNoteLineType creditNoteLineType : creditNoteLineTypes) {
            LineBean lineBean = new LineBean();

            // Description and product code
            BasicDatasourceUtils.addDescriptionAndProductCode(lineBean, Optional.ofNullable(creditNoteLineType.getItem()));

            // Quantity and unit code
            if (creditNoteLineType.getCreditedQuantityValue() != null) {
                lineBean.setQuantity(creditNoteLineType.getCreditedQuantityValue().doubleValue());
            }
            if (creditNoteLineType.getCreditedQuantity() != null) {
                lineBean.setUnitCode(creditNoteLineType.getCreditedQuantity().getUnitCode());
            }

            // Price amount
            if (creditNoteLineType.getPrice() != null) {
                if (creditNoteLineType.getPrice().getPriceAmountValue() != null) {
                    lineBean.setPriceAmount(creditNoteLineType.getPrice().getPriceAmountValue().doubleValue());
                }
            }

            // Allowance charges
            List<AllowanceChargeType> allowanceChargeTypes = creditNoteLineType.getAllowanceCharge();
            double totalAllowanceCharge = allowanceChargeTypes.stream()
                    .map(AllowanceChargeType::getAmountValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .doubleValue();
            lineBean.setTotalAllowanceCharge(totalAllowanceCharge);

            // Total Price
            if (creditNoteLineType.getLineExtensionAmountValue() != null) {
                lineBean.setExtensionAmount(creditNoteLineType.getLineExtensionAmountValue().doubleValue());
            }

            // Total tax
            double totalTax = creditNoteLineType.getTaxTotal().stream()
                    .map(TaxTotalType::getTaxAmountValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .doubleValue();
            lineBean.setTotalTax(totalTax);


            result.add(lineBean);
        }

        return result;
    }
}
