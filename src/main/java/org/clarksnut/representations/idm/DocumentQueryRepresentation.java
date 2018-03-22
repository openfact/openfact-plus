package org.clarksnut.representations.idm;

import java.util.Date;
import java.util.Set;

public class DocumentQueryRepresentation {

    public static enum SpaceRole {
        SENDER, RECEIVER
    }

    private DocumentQueryData data;

    public DocumentQueryRepresentation() {
    }

    public DocumentQueryRepresentation(DocumentQueryData data) {
        this.data = data;
    }

    public DocumentQueryData getData() {
        return data;
    }

    public void setData(DocumentQueryData data) {
        this.data = data;
    }

    public static class DocumentQueryData {
        private String type;
        private DocumentQueryAttributes attributes;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public DocumentQueryAttributes getAttributes() {
            return attributes;
        }

        public void setAttributes(DocumentQueryAttributes attributes) {
            this.attributes = attributes;
        }
    }

    public static class DocumentQueryAttributes {
        private SpaceRole role;
        private String filterText;

        private Set<String> types;
        private Set<String> currencies;

        private Date after;
        private Date before;

        private Double lessThan;
        private Double greaterThan;

        private Set<String> tags;
        private Set<String> spaces;

        private String orderBy;
        private boolean asc;

        private Integer offset;
        private Integer limit;

        private Boolean starred;
        private Boolean viewed;
        private Boolean checked;

        public SpaceRole getRole() {
            return role;
        }

        public void setRole(SpaceRole role) {
            this.role = role;
        }

        public String getFilterText() {
            return filterText;
        }

        public void setFilterText(String filterText) {
            this.filterText = filterText;
        }

        public Set<String> getTypes() {
            return types;
        }

        public void setTypes(Set<String> types) {
            this.types = types;
        }

        public Set<String> getCurrencies() {
            return currencies;
        }

        public void setCurrencies(Set<String> currencies) {
            this.currencies = currencies;
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

        public Double getLessThan() {
            return lessThan;
        }

        public void setLessThan(Double lessThan) {
            this.lessThan = lessThan;
        }

        public Double getGreaterThan() {
            return greaterThan;
        }

        public void setGreaterThan(Double greaterThan) {
            this.greaterThan = greaterThan;
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

        public String getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }

        public boolean isAsc() {
            return asc;
        }

        public void setAsc(boolean asc) {
            this.asc = asc;
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public Boolean getStarred() {
            return starred;
        }

        public void setStarred(Boolean starred) {
            this.starred = starred;
        }

        public Boolean getViewed() {
            return viewed;
        }

        public void setViewed(Boolean viewed) {
            this.viewed = viewed;
        }

        public Boolean getChecked() {
            return checked;
        }

        public void setChecked(Boolean checked) {
            this.checked = checked;
        }
    }

}
