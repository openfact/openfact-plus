package org.openfact.representations.idm;

import java.util.Date;
import java.util.Set;

public class DocumentQueryRepresentation {

    private String filterText;
    private String spacePosition;
    private Date after;
    private Date before;
    private Float greater;
    private Float less;
    private String currency;
    private String documentType;
    private Set<String> tags;
    private Set<String> spaces;

    public String getSpacePosition() {
        return spacePosition;
    }

    public void setSpacePosition(String spacePosition) {
        this.spacePosition = spacePosition;
    }

    public Date getAfter() {
        return after;
    }

    public void setAfter(Date after) {
        this.after = after;
    }

    public Date getBefore() {
        return before;
    }

    public void setBefore(Date before) {
        this.before = before;
    }

    public Float getGreater() {
        return greater;
    }

    public void setGreater(Float greater) {
        this.greater = greater;
    }

    public Float getLess() {
        return less;
    }

    public void setLess(Float less) {
        this.less = less;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<String> getSpaces() {
        return spaces;
    }

    public void setSpaces(Set<String> spaces) {
        this.spaces = spaces;
    }

    public String getFilterText() {
        return filterText;
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }
}
