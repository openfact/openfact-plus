package org.openfact.config;

public class ReportThemeConfig {

    private final String defaultTheme;
    private final Boolean cacheTemplates;
    private final Boolean cacheReports;
    private final String folderDir;

    private ReportThemeConfig(Builder builder) {
        this.defaultTheme = builder.defaulTheme;
        this.cacheTemplates = builder.cacheTemplates;
        this.cacheReports = builder.cacheReports;
        this.folderDir = builder.folderDir;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getDefaultTheme() {
        return defaultTheme;
    }

    public String getDefaultTheme(String defaultValue) {
        return defaultTheme != null ? defaultTheme : defaultValue;
    }

    public Boolean getCacheTemplates() {
        return cacheTemplates;
    }

    public Boolean getCacheTemplates(Boolean defaultValue) {
        return cacheTemplates != null ? cacheTemplates : defaultValue;
    }

    public Boolean getCacheReports() {
        return cacheReports;
    }

    public Boolean getCacheReports(boolean defaultValue) {
        return cacheReports != null ? cacheReports : defaultValue;
    }

    public String getFolderDir() {
        return folderDir;
    }

    public String getFolderDir(String defaultValue) {
        return folderDir != null ? folderDir : defaultValue;
    }

    public static class Builder {
        private String defaulTheme;
        private Boolean cacheTemplates;
        private Boolean cacheReports;
        private String folderDir;

        public Builder defaultTheme(String defaulTheme) {
            this.defaulTheme = defaulTheme;
            return this;
        }

        public Builder cacheTemplates(Boolean cacheTemplates) {
            this.cacheTemplates = cacheTemplates;
            return this;
        }

        public Builder cacheReports(Boolean cacheReports) {
            this.cacheReports = cacheReports;
            return this;
        }

        public Builder folderDir(String folderDir) {
            this.folderDir = folderDir;
            return this;
        }

        public ReportThemeConfig build() {
            return new ReportThemeConfig(this);
        }
    }

}
