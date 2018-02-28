package org.clarksnut.mapper.document.basic.debitnote;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PayableAmountType;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.mapper.document.basic.BasicUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public class BasicDebitNoteBeanAdapter implements DocumentBean {

    private final DebitNoteType type;

    public BasicDebitNoteBeanAdapter(DebitNoteType type) {
        this.type = type;
    }

    private Optional<PayableAmountType> getRequestedPayableAmount() {
        MonetaryTotalType legalMonetaryTotal = type.getRequestedMonetaryTotal();
        if (legalMonetaryTotal != null) {
            return Optional.ofNullable(legalMonetaryTotal.getPayableAmount());
        }
        return Optional.empty();
    }

    private Optional<AddressType> getSupplierPostalAddress() {
        SupplierPartyType accountingSupplierParty = type.getAccountingSupplierParty();
        if (accountingSupplierParty != null) {
            PartyType party = accountingSupplierParty.getParty();
            if (party != null) {
                return Optional.ofNullable(party.getPostalAddress());
            }
        }
        return Optional.empty();
    }

    private Optional<AddressType> getCustomerPostalAddress() {
        CustomerPartyType accountingCustomerParty = type.getAccountingCustomerParty();
        if (accountingCustomerParty != null) {
            PartyType party = accountingCustomerParty.getParty();
            if (party != null) {
                return Optional.ofNullable(party.getPostalAddress());
            }
        }
        return Optional.empty();
    }

    @Override
    public String getAssignedId() {
        return type.getIDValue();
    }

    @Override
    public Float getAmount() {
        Optional<PayableAmountType> payableAmount = getRequestedPayableAmount();
        if (payableAmount.isPresent()) {
            PayableAmountType payableAmountType = payableAmount.get();
            BigDecimal value = payableAmountType.getValue();
            if (value != null) {
                return value.floatValue();
            }
        }
        return null;
    }

    @Override
    public Float getTax() {
        return type.getTaxTotal().stream()
                .map(TaxTotalType::getTaxAmountValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue();
    }

    @Override
    public String getCurrency() {
        Optional<PayableAmountType> payableAmount = getRequestedPayableAmount();
        if (payableAmount.isPresent()) {
            PayableAmountType payableAmountType = payableAmount.get();
            return payableAmountType.getCurrencyID();
        }
        return null;
    }

    @Override
    public Date getIssueDate() {
        return BasicUtils.toDate(type.getIssueDate(), Optional.ofNullable(type.getIssueTime()));
    }

    @Override
    public String getSupplierName() {
        return BasicUtils.getSupplierName(type.getAccountingSupplierParty());
    }

    @Override
    public String getSupplierAssignedId() {
        SupplierPartyType accountingSupplierParty = type.getAccountingSupplierParty();
        if (accountingSupplierParty != null) {
            return accountingSupplierParty.getCustomerAssignedAccountIDValue();
        }
        return null;
    }

    @Override
    public String getSupplierStreetAddress() {
        Optional<AddressType> supplierPostalAddress = getSupplierPostalAddress();
        if (supplierPostalAddress.isPresent()) {
            AddressType addressType = supplierPostalAddress.get();
            return addressType.getStreetNameValue();
        }
        return null;
    }

    @Override
    public String getSupplierCity() {
        Optional<AddressType> supplierPostalAddress = getSupplierPostalAddress();
        if (supplierPostalAddress.isPresent()) {
            AddressType addressType = supplierPostalAddress.get();
            return BasicUtils.toCityString(addressType);
        }
        return null;
    }

    @Override
    public String getSupplierCountry() {
        Optional<AddressType> supplierPostalAddress = getSupplierPostalAddress();
        if (supplierPostalAddress.isPresent()) {
            AddressType addressType = supplierPostalAddress.get();
            if (addressType.getCountry() != null) {
                return addressType.getCountry().getIdentificationCodeValue();
            }
        }
        return null;
    }

    @Override
    public String getCustomerName() {
        return BasicUtils.getCustomerName(type.getAccountingCustomerParty());
    }

    @Override
    public String getCustomerAssignedId() {
        CustomerPartyType accountingCustomerParty = type.getAccountingCustomerParty();
        if (accountingCustomerParty != null) {
            return accountingCustomerParty.getCustomerAssignedAccountIDValue();
        }
        return null;
    }

    @Override
    public String getCustomerStreetAddress() {
        Optional<AddressType> customerPostalAddress = getCustomerPostalAddress();
        if (customerPostalAddress.isPresent()) {
            AddressType addressType = customerPostalAddress.get();
            return addressType.getStreetNameValue();
        }
        return null;
    }

    @Override
    public String getCustomerCity() {
        Optional<AddressType> customerPostalAddress = getCustomerPostalAddress();
        if (customerPostalAddress.isPresent()) {
            AddressType addressType = customerPostalAddress.get();
            return BasicUtils.toCityString(addressType);
        }
        return null;
    }

    @Override
    public String getCustomerCountry() {
        Optional<AddressType> customerPostalAddress = getCustomerPostalAddress();
        if (customerPostalAddress.isPresent()) {
            AddressType addressType = customerPostalAddress.get();
            if (addressType.getCountry() != null) {
                return addressType.getCountry().getIdentificationCodeValue();
            }
        }
        return null;
    }
}
