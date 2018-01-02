package org.clarksnut.representations.idm;

import java.util.Map;

public class DocumentReportQueryRepresentation {

    private String theme;
    private String format;
    private Map<String, Object> attributes;

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

}
