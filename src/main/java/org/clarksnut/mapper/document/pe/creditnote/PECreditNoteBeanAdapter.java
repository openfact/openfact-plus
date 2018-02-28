package org.clarksnut.mapper.document.pe.creditnote;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PayableAmountType;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.mapper.document.pe.PEUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public class PECreditNoteBeanAdapter implements DocumentBean {

    private final CreditNoteType type;

    public PECreditNoteBeanAdapter(CreditNoteType type) {
        this.type = type;
    }

    private Optional<PayableAmountType> getPayableAmount() {
        MonetaryTotalType legalMonetaryTotal = type.getLegalMonetaryTotal();
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
        return type.getID().getValue();
    }

    @Override
    public Float getAmount() {
        Optional<PayableAmountType> payableAmount = getPayableAmount();
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
                .map(f -> f.getTaxAmount().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue();
    }

    @Override
    public String getCurrency() {
        Optional<PayableAmountType> payableAmount = getPayableAmount();
        if (payableAmount.isPresent()) {
            PayableAmountType payableAmountType = payableAmount.get();
            return payableAmountType.getCurrencyID().value();
        }
        return null;
    }

    @Override
    public Date getIssueDate() {
        return PEUtils.toDate(type.getIssueDate(), Optional.ofNullable(type.getIssueTime()));
    }

    @Override
    public String getSupplierName() {
        return PEUtils.getSupplierName(type.getAccountingSupplierParty());
    }

    @Override
    public String getSupplierAssignedId() {
        SupplierPartyType accountingSupplierParty = type.getAccountingSupplierParty();
        if (accountingSupplierParty != null) {
            return accountingSupplierParty.getCustomerAssignedAccountID().getValue();
        }
        return null;
    }

    @Override
    public String getSupplierStreetAddress() {
        Optional<AddressType> supplierPostalAddress = getSupplierPostalAddress();
        if (supplierPostalAddress.isPresent()) {
            AddressType addressType = supplierPostalAddress.get();
            return addressType.getStreetName().getValue();
        }
        return null;
    }

    @Override
    public String getSupplierCity() {
        Optional<AddressType> supplierPostalAddress = getSupplierPostalAddress();
        if (supplierPostalAddress.isPresent()) {
            AddressType addressType = supplierPostalAddress.get();
            return PEUtils.toCityString(addressType);
        }
        return null;
    }

    @Override
    public String getSupplierCountry() {
        Optional<AddressType> supplierPostalAddress = getSupplierPostalAddress();
        if (supplierPostalAddress.isPresent()) {
            AddressType addressType = supplierPostalAddress.get();
            if (addressType.getCountry() != null) {
                return addressType.getCountry().getIdentificationCode().getValue();
            }
        }
        return null;
    }

    @Override
    public String getCustomerName() {
        return PEUtils.getCustomerName(type.getAccountingCustomerParty());
    }

    @Override
    public String getCustomerAssignedId() {
        CustomerPartyType accountingCustomerParty = type.getAccountingCustomerParty();
        if (accountingCustomerParty != null) {
            return accountingCustomerParty.getCustomerAssignedAccountID().getValue();
        }
        return null;
    }

    @Override
    public String getCustomerStreetAddress() {
        Optional<AddressType> customerPostalAddress = getCustomerPostalAddress();
        if (customerPostalAddress.isPresent()) {
            AddressType addressType = customerPostalAddress.get();
            return addressType.getStreetName().getValue();
        }
        return null;
    }

    @Override
    public String getCustomerCity() {
        Optional<AddressType> customerPostalAddress = getCustomerPostalAddress();
        if (customerPostalAddress.isPresent()) {
            AddressType addressType = customerPostalAddress.get();
            return PEUtils.toCityString(addressType);
        }
        return null;
    }

    @Override
    public String getCustomerCountry() {
        Optional<AddressType> customerPostalAddress = getCustomerPostalAddress();
        if (customerPostalAddress.isPresent()) {
            AddressType addressType = customerPostalAddress.get();
            if (addressType.getCountry() != null) {
                return addressType.getCountry().getIdentificationCode().getValue();
            }
        }
        return null;
    }
}
