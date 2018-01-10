package org.clarksnut.mapper.document.pe.debitnote;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.PayableAmountType;
import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.mapper.document.pe.PEUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PEDebitNoteBeanAdapter implements DocumentBean {

    private final DebitNoteType type;

    public PEDebitNoteBeanAdapter(DebitNoteType type) {
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
        return type.getID().getValue();
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
                .map(f -> f.getTaxAmount().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .floatValue();
    }

    @Override
    public String getCurrency() {
        Optional<PayableAmountType> payableAmount = getRequestedPayableAmount();
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
        SupplierPartyType accountingSupplierParty = type.getAccountingSupplierParty();
        if (accountingSupplierParty != null) {
            PartyType party = accountingSupplierParty.getParty();
            if (party != null) {
                return party.getPartyLegalEntity().stream()
                        .map(f -> f.getRegistrationName().getValue())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", "));
            }
        }
        return null;
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
        CustomerPartyType accountingCustomerParty = type.getAccountingCustomerParty();
        if (accountingCustomerParty != null) {
            PartyType party = accountingCustomerParty.getParty();
            if (party != null) {
                return party.getPartyLegalEntity().stream()
                        .map(f -> f.getRegistrationName().getValue())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", "));
            }
        }
        return null;
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
