package org.clarksnut.mapper.document.pe.perception;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.AddressType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyLegalEntityType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.AmountType;
import org.clarksnut.mapper.document.DocumentMapped.DocumentBean;
import org.clarksnut.mapper.document.pe.PEUtils;
import org.openfact.perception.PerceptionType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PEPerceptionBeanAdapter implements DocumentBean {

    private final PerceptionType type;

    public PEPerceptionBeanAdapter(PerceptionType type) {
        this.type = type;
    }

    private Optional<AddressType> getSupplierPostalAddress() {
        PartyType agentParty = type.getAgentParty();
        if (agentParty != null) {
            return Optional.ofNullable(agentParty.getPostalAddress());
        }
        return Optional.empty();
    }

    private Optional<AddressType> getCustomerPostalAddress() {
        PartyType receiverParty = type.getReceiverParty();
        if (receiverParty != null) {
            return Optional.ofNullable(receiverParty.getPostalAddress());
        }
        return Optional.empty();
    }

    @Override
    public String getAssignedId() {
        return type.getId().getValue();
    }

    @Override
    public Float getAmount() {
        AmountType sunatTotalCashed = type.getSunatTotalCashed();
        if (sunatTotalCashed != null) {
            BigDecimal value = sunatTotalCashed.getValue();
            if (value != null) {
                return value.floatValue();
            }
        }
        return null;
    }

    @Override
    public Float getTax() {
        return null;
    }

    @Override
    public String getCurrency() {
        AmountType sunatTotalCashed = type.getSunatTotalCashed();
        if (sunatTotalCashed != null) {
            return sunatTotalCashed.getCurrencyID();
        }
        return null;
    }

    @Override
    public Date getIssueDate() {
        return PEUtils.toDate(type.getIssueDate(), Optional.empty());
    }

    @Override
    public String getSupplierName() {
        PartyType agentParty = type.getAgentParty();
        if (agentParty != null) {
            return agentParty.getPartyLegalEntity().stream()
                    .map(PartyLegalEntityType::getRegistrationNameValue)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        }
        return null;
    }

    @Override
    public String getSupplierAssignedId() {
        PartyType agentParty = type.getAgentParty();
        if (agentParty != null) {
            return agentParty.getPartyIdentification().stream()
                    .map(PartyIdentificationType::getIDValue)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
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
                return addressType.getCountry().getIdentificationCodeValue();
            }
        }
        return null;
    }

    @Override
    public String getCustomerName() {
        PartyType agentParty = type.getReceiverParty();
        if (agentParty != null) {
            return agentParty.getPartyLegalEntity().stream()
                    .map(PartyLegalEntityType::getRegistrationNameValue)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        }
        return null;
    }

    @Override
    public String getCustomerAssignedId() {
        PartyType agentParty = type.getReceiverParty();
        if (agentParty != null) {
            return agentParty.getPartyIdentification().stream()
                    .map(PartyIdentificationType::getIDValue)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
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
                return addressType.getCountry().getIdentificationCodeValue();
            }
        }
        return null;
    }
}
