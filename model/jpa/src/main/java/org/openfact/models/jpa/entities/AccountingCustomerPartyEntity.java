package org.openfact.models.jpa.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "ACCOUNTING_CUSTOMER_PARTY")
@NamedQueries({

})
public class AccountingCustomerPartyEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Access(AccessType.PROPERTY)
    private String id;

    @Column(name = "ASSIGNED_ACCOUNT_ID")
    private String assignedAccountId;

    @Column(name = "ADDITIONAL_ACCOUNT_ID")
    private String additonalAccountId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssignedAccountId() {
        return assignedAccountId;
    }

    public void setAssignedAccountId(String assignedAccountId) {
        this.assignedAccountId = assignedAccountId;
    }

    public String getAdditonalAccountId() {
        return additonalAccountId;
    }

    public void setAdditonalAccountId(String additonalAccountId) {
        this.additonalAccountId = additonalAccountId;
    }
}
