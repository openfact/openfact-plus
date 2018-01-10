package org.clarksnut.mapper.document.pe.summarydocuments;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.mapper.document.pe.PEUtils;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;
import un.unece.uncefact.data.specification.unqualifieddatatypesschemamodule._2.NameType;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PESummaryDocumentsBeanAdapter implements DocumentBean {

    private final SummaryDocumentsType type;

    public PESummaryDocumentsBeanAdapter(SummaryDocumentsType type) {
        this.type = type;
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

    @Override
    public String getAssignedId() {
        return type.getID().getValue();
    }

    @Override
    public Float getAmount() {
        return null;
    }

    @Override
    public Float getTax() {
        return null;
    }

    @Override
    public String getCurrency() {
        return null;
    }

    @Override
    public Date getIssueDate() {
        return PEUtils.toDate(type.getIssueDate(), Optional.empty());
    }

    @Override
    public String getSupplierName() {
        SupplierPartyType accountingSupplierParty = type.getAccountingSupplierParty();
        if (accountingSupplierParty != null) {
            PartyType party = accountingSupplierParty.getParty();
            if (party != null) {
                return party.getPartyLegalEntity().stream()
                        .map(PartyLegalEntityType::getRegistrationName)
                        .filter(Objects::nonNull)
                        .map(NameType::getValue)
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
        return null;
    }

    @Override
    public String getCustomerAssignedId() {
        return null;
    }

    @Override
    public String getCustomerStreetAddress() {
        return null;
    }

    @Override
    public String getCustomerCity() {
        return null;
    }

    @Override
    public String getCustomerCountry() {
        return null;
    }
}
