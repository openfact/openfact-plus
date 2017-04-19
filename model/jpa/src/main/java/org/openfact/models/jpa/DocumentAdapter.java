package org.openfact.models.jpa;

import org.jboss.logging.Logger;
import org.openfact.models.DocumentLineModel;
import org.openfact.models.DocumentModel;
import org.openfact.models.jpa.entities.DocumentAttributeEntity;
import org.openfact.models.jpa.entities.DocumentEntity;
import org.openfact.models.jpa.entities.DocumentLineEntity;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocumentAdapter implements DocumentModel, JpaModel<DocumentEntity> {

    private static final Logger logger = Logger.getLogger(DocumentAdapter.class);

    private DocumentEntity document;
    private EntityManager em;

    public DocumentAdapter(EntityManager em, DocumentEntity document) {
        this.em = em;
        this.document = document;
    }

    public static DocumentEntity toEntity(DocumentModel model, EntityManager em) {
        if (model instanceof DocumentAdapter) {
            return ((DocumentAdapter) model).getEntity();
        }
        return em.getReference(DocumentEntity.class, model.getId());
    }

    @Override
    public DocumentEntity getEntity() {
        return document;
    }


    @Override
    public String getId() {
        return document.getId();
    }

    @Override
    public String getDocumentId() {
        return document.getDocumentId();
    }

    @Override
    public String getDocumentType() {
        return document.getDocumentType();
    }

    @Override
    public String getOriginUuid() {
        return document.getOriginUuid();
    }

    @Override
    public LocalDateTime getIssueDate() {
        return document.getIssueDate();
    }

    @Override
    public void setIssueDate(LocalDateTime issueDate) {
        document.setIssueDate(issueDate);
    }

    @Override
    public String getDocumentCurrencyCode() {
        return document.getDocumentCurrencyCode();
    }

    @Override
    public void setDocumentCurrencyCode(String documentCurrencyCode) {
        document.setDocumentCurrencyCode(documentCurrencyCode);
    }

    @Override
    public String getSupplierAssignedAccountId() {
        return document.getSupplierPartyAssignedAccountId();
    }

    @Override
    public void setSupplierAssignedAccountId(String supplierAssignedAccountId) {
        document.setSupplierPartyAssignedAccountId(supplierAssignedAccountId);
    }

    @Override
    public String getCustomerAssignedAccountId() {
        return document.getCustomerPartyAssignedAccountId();
    }

    @Override
    public void setCustomerAssignedAccountId(String customerAssignedAccountId) {
        document.setCustomerPartyAssignedAccountId(customerAssignedAccountId);
    }

    /*
    * Attributes*/
    public void setAttribute(String name, String value, String className) {
        for (DocumentAttributeEntity attr : document.getAttributes()) {
            if (attr.getName().equals(name)) {
                attr.setValue(value);
                attr.setClassName(className);
                return;
            }
        }
        DocumentAttributeEntity attr = new DocumentAttributeEntity();
        attr.setName(name);
        attr.setValue(value);
        attr.setClassName(className);
        attr.setDocument(document);
        em.persist(attr);
        document.getAttributes().add(attr);
    }

    @Override
    public void setAttribute(String name, String value) {
        setAttribute(name, value.toString(), String.class.getName());
    }

    @Override
    public void setAttribute(String name, Boolean value) {
        setAttribute(name, value.toString(), Boolean.class.getName());
    }

    @Override
    public void setAttribute(String name, Integer value) {
        setAttribute(name, value.toString(), Integer.class.getName());
    }

    @Override
    public void setAttribute(String name, Long value) {
        setAttribute(name, value.toString(), Long.class.getName());
    }

    @Override
    public void setAttribute(String name, BigDecimal value) {
        setAttribute(name, value.toString());
    }

    @Override
    public void removeAttribute(String name) {
        Iterator<DocumentAttributeEntity> it = document.getAttributes().iterator();
        while (it.hasNext()) {
            DocumentAttributeEntity attr = it.next();
            if (attr.getName().equals(name)) {
                it.remove();
                em.remove(attr);
            }
        }
    }

    public String getAttribute(String name) {
        for (DocumentAttributeEntity attr : document.getAttributes()) {
            if (attr.getName().equals(name)) {
                return attr.getValue();
            }
        }
        return null;
    }

    @Override
    public String getAttribute(String name, String defaultValue) {
        String v = getAttribute(name);
        return v != null ? v : defaultValue;
    }

    @Override
    public Integer getAttribute(String name, Integer defaultValue) {
        String v = getAttribute(name);
        return v != null ? Integer.parseInt(v) : defaultValue;
    }

    @Override
    public BigDecimal getAttribute(String name, BigDecimal defaultValue) {
        String v = getAttribute(name);
        return v != null ? new BigDecimal(v) : defaultValue;
    }

    @Override
    public Long getAttribute(String name, Long defaultValue) {
        String v = getAttribute(name);
        return v != null ? Long.parseLong(v) : defaultValue;
    }

    @Override
    public Boolean getAttribute(String name, Boolean defaultValue) {
        String v = getAttribute(name);
        return v != null ? Boolean.parseBoolean(v) : defaultValue;
    }

    @Override
    public Map<String, String> getAttributes() {
        // should always return a copy
        Map<String, String> result = new HashMap<>();
        for (DocumentAttributeEntity attr : document.getAttributes()) {
            result.put(attr.getName(), attr.getValue());
        }
        return result;
    }

    @Override
    public DocumentLineModel addDocumentLine() {
        DocumentLineEntity entity = new DocumentLineEntity();
        entity.setDocument(document);
        em.persist(entity);
        document.getLines().add(entity);
        return new DocumentLineAdapter(this, em, entity);
    }

    @Override
    public List<DocumentLineModel> getDocumentLines() {
        return document.getLines().stream()
                .map(f -> new DocumentLineAdapter(this, em, f))
                .collect(Collectors.toList());
    }

    @Override
    public boolean removeDocumentLine(DocumentLineModel documentLine) {
        DocumentLineEntity entity = em.find(DocumentLineEntity.class, documentLine.getId());
        if (entity == null) return false;
        em.remove(entity);
        return true;
    }

}
