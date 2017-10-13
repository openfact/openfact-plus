package org.openfact.documents.jpa.entity;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.openfact.models.db.CreatableEntity;
import org.openfact.models.db.CreatedAtListener;
import org.openfact.models.db.UpdatableEntity;
import org.openfact.models.db.UpdatedAtListener;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Indexed
@Table(name = "document")
@EntityListeners({CreatedAtListener.class, UpdatedAtListener.class})
//@AnalyzerDefs(value = {
//        @AnalyzerDef(
//                name = "customanalyzer",
//                tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
//                filters = {
//                        @TokenFilterDef(factory = LowerCaseFilterFactory.class)
//                })
//})
public class DocumentEntity implements CreatableEntity, UpdatableEntity, Serializable {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @Field
//    @Analyzer(definition = "customanalyzer")
    @NotNull
    @Column(name = "type")
    private String type;

    @Field
    @NotNull
    @Column(name = "assigned_id")
    private String assignedId;

    @NotNull
    @Column(name = "file_id")
    private String fileId;

    @Field
    @Digits(integer = 10, fraction = 5)
    @Type(type = "org.hibernate.type.BigDecimalType")
    @Column(name = "amount")
    private BigDecimal amount;

    @Field
//    @Analyzer(definition = "customanalyzer")
    @Column(name = "currency")
    private String currency;

    @Field
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "issue_date")
    private Date issueDate;

    @Field
//    @Analyzer(definition = "customanalyzer")
    @Column(name = "supplier_name")
    private String supplierName;

    @Field
    @Column(name = "supplier_assigned_id")
    private String supplierAssignedId;

    @Field
//    @Analyzer(definition = "customanalyzer")
    @Column(name = "customer_name")
    private String customerName;

    @Field
    @Column(name = "customer_assigned_id")
    private String customerAssignedId;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @IndexedEmbedded
    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "document_tags", joinColumns = {@JoinColumn(name = "document_id")})
    private Map<String, String> tags = new HashMap<>();

    @IndexedEmbedded
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<DocumentSpaceEntity> spaces = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierAssignedId() {
        return supplierAssignedId;
    }

    public void setSupplierAssignedId(String supplierAssignedId) {
        this.supplierAssignedId = supplierAssignedId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAssignedId() {
        return customerAssignedId;
    }

    public void setCustomerAssignedId(String customerAssignedId) {
        this.customerAssignedId = customerAssignedId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public Set<DocumentSpaceEntity> getSpaces() {
        return spaces;
    }

    public void setSpaces(Set<DocumentSpaceEntity> spaces) {
        this.spaces = spaces;
    }

}
