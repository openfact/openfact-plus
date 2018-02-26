package org.clarksnut.mapper.document.pe.voideddocuments;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyLegalEntityType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.SupplierPartyType;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.mapper.document.pe.PEUtils;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;
import un.unece.uncefact.data.specification.unqualifieddatatypesschemamodule._2.NameType;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PEVoidedDocumentsBeanAdapter implements DocumentBean {

    private final VoidedDocumentsType type;

    public PEVoidedDocumentsBeanAdapter(VoidedDocumentsType type) {
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
