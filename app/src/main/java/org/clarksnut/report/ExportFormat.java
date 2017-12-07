package org.clarksnut.report;

public enum ExportFormat {
    HTML("html"), PDF("pdf");

    private String extension;

    ExportFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
