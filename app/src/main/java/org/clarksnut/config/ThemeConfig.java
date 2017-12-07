package org.clarksnut.config;

public class ThemeConfig {

    private final String defaultTheme;
    private final Long staticMaxAge;
    private final Boolean cacheTemplates;
    private final Boolean cacheThemes;
    private final String folderDir;

    private ThemeConfig(Builder builder) {
        this.defaultTheme = builder.defaultTheme;
        this.staticMaxAge = builder.staticMaxAge;
        this.cacheTemplates = builder.cacheTemplates;
        this.cacheThemes = builder.cacheThemes;
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

    public Long getStaticMaxAge() {
        return staticMaxAge;
    }

    public Long getStaticMaxAge(Long defaultValue) {
        return staticMaxAge != null ? staticMaxAge : defaultValue;
    }

    public Boolean getCacheTemplates() {
        return cacheTemplates;
    }

    public Boolean getCacheTemplates(Boolean defaultValue) {
        return cacheTemplates != null ? cacheTemplates : defaultValue;
    }

    public Boolean getCacheThemes() {
        return cacheThemes;
    }

    public Boolean getCacheThemes(boolean defaultValue) {
        return cacheThemes != null ? cacheThemes : defaultValue;
    }

    public String getFolderDir() {
        return folderDir;
    }

    public String getFolderDir(String defaultValue) {
        return folderDir != null ? folderDir : defaultValue;
    }

    public static class Builder {
        private String defaultTheme;
        private Long staticMaxAge;
        private Boolean cacheTemplates;
        private Boolean cacheThemes;
        private String folderDir;

        public Builder defaultTheme(String defaultTheme) {
            this.defaultTheme = defaultTheme;
            return this;
        }

        public Builder staticMaxAge(Long staticMaxAge) {
            this.staticMaxAge = staticMaxAge;
            return this;
        }

        public Builder cacheTemplates(Boolean cacheTemplates) {
            this.cacheTemplates = cacheTemplates;
            return this;
        }

        public Builder cacheThemes(Boolean cacheTheems) {
            this.cacheThemes = cacheTheems;
            return this;
        }

        public Builder folderDir(String folderDir) {
            this.folderDir = folderDir;
            return this;
        }

        public ThemeConfig build() {
            return new ThemeConfig(this);
        }
    }

}
