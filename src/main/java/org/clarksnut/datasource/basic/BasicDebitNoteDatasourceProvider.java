package org.clarksnut.datasource.basic;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
import org.clarksnut.datasource.Datasource;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.datasource.basic.beans.LineBean;
import org.clarksnut.files.XmlFileModel;
import org.clarksnut.files.exceptions.FileFetchException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BasicDebitNoteDatasourceProvider implements DatasourceProvider {

    @Override
    public String getName() {
        return "BasicDebitNoteDS";
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    public Datasource getDatasource(XmlFileModel file) throws FileFetchException {
        DebitNoteType debitNoteType = UBL21Reader.debitNote().read(file.getDocument());
        if (debitNoteType == null) {
            return null;
        }

        BasicDebitNoteDatasource bean = new BasicDebitNoteDatasource();

        bean.setAssignedId(debitNoteType.getIDValue());
        bean.setCurrency(debitNoteType.getDocumentCurrencyCodeValue());
        bean.setSupplier(BasicDatasourceUtils.toSupplier(debitNoteType.getAccountingSupplierParty()));
        bean.setCustomer(BasicDatasourceUtils.toCustomer(debitNoteType.getAccountingCustomerParty()));

        // Invoice reference
        String invoiceReference = debitNoteType.getDiscrepancyResponse().stream()
                .filter(p -> p.getReferenceIDValue() != null)
                .map(ResponseType::getReferenceIDValue)
                .collect(Collectors.joining(", "));
        bean.setInvoiceReference(invoiceReference);

        // Issue date
        bean.setIssueDate(BasicDatasourceUtils.toDate(debitNoteType.getIssueDate(), Optional.ofNullable(debitNoteType.getIssueTime())));

        // Payable amount
        MonetaryTotalType legalMonetaryTotalType = debitNoteType.getRequestedMonetaryTotal();
        if (legalMonetaryTotalType != null) {
            if (legalMonetaryTotalType.getPayableAmountValue() != null) {
                bean.setPayableAmount(legalMonetaryTotalType.getPayableAmountValue().floatValue());
            }
            if (legalMonetaryTotalType.getAllowanceTotalAmountValue() != null) {
                bean.setAllowanceTotal(legalMonetaryTotalType.getAllowanceTotalAmountValue().floatValue());
            }
            if (legalMonetaryTotalType.getChargeTotalAmountValue() != null) {
                bean.setChargeTotal(legalMonetaryTotalType.getChargeTotalAmountValue().floatValue());
            }
        }

        // Total tax
        float totalTax = debitNoteType.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue();
        bean.setTotalTax(totalTax);

        // Lines
        bean.setLines(getLines(debitNoteType.getDebitNoteLine()));

        return bean;
    }

    private List<LineBean> getLines(List<DebitNoteLineType> debitNoteLineTypes) {
        List<LineBean> result = new ArrayList<>();

        for (DebitNoteLineType debitNoteLineType : debitNoteLineTypes) {
            LineBean lineBean = new LineBean();

            // Description and product code
            BasicDatasourceUtils.addDescriptionAndProductCode(lineBean, Optional.ofNullable(debitNoteLineType.getItem()));

            // Quantity and unit code
            if (debitNoteLineType.getDebitedQuantityValue() != null) {
                lineBean.setQuantity(debitNoteLineType.getDebitedQuantityValue().floatValue());
            }
            if (debitNoteLineType.getDebitedQuantity() != null) {
                lineBean.setUnitCode(debitNoteLineType.getDebitedQuantity().getUnitCode());
            }

            // Price amount
            if (debitNoteLineType.getPrice() != null) {
                if (debitNoteLineType.getPrice().getPriceAmountValue() != null) {
                    lineBean.setPriceAmount(debitNoteLineType.getPrice().getPriceAmountValue().floatValue());
                }
            }

            // Allowance charges
            List<AllowanceChargeType> allowanceChargeTypes = debitNoteLineType.getAllowanceCharge();
            float totalAllowanceCharge = allowanceChargeTypes.stream()
                    .map(AllowanceChargeType::getAmountValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .floatValue();
            lineBean.setTotalAllowanceCharge(totalAllowanceCharge);

            // Total Price
            if (debitNoteLineType.getLineExtensionAmountValue() != null) {
                lineBean.setExtensionAmount(debitNoteLineType.getLineExtensionAmountValue().floatValue());
            }

            // Total tax
            float totalTax = debitNoteLineType.getTaxTotal().stream()
                    .map(TaxTotalType::getTaxAmountValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .floatValue();
            lineBean.setTotalTax(totalTax);


            result.add(lineBean);
        }

        return result;
    }
}
