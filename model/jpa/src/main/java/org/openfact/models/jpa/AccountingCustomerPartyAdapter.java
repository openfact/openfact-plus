package org.openfact.models.jpa;

import org.openfact.models.AccountingCustomerPartyModel;
import org.openfact.models.jpa.entities.AccountingCustomerPartyEntity;

import javax.persistence.EntityManager;

public class AccountingCustomerPartyAdapter implements AccountingCustomerPartyModel, JpaModel<AccountingCustomerPartyEntity> {

    private EntityManager em;
    private AccountingCustomerPartyEntity accountingCustomerParty;

    public AccountingCustomerPartyAdapter(EntityManager em, AccountingCustomerPartyEntity accountingCustomerParty) {
        this.em = em;
        this.accountingCustomerParty = accountingCustomerParty;
    }

    @Override
    public AccountingCustomerPartyEntity getEntity() {
        return accountingCustomerParty;
    }

    @Override
    public String getId() {
        return accountingCustomerParty.getId();
    }

    @Override
    public String getAssignedAccountId() {
        return accountingCustomerParty.getAssignedAccountId();
    }

    @Override
    public String getAdditionalAccountId() {
        return accountingCustomerParty.getAdditonalAccountId();
    }

}
