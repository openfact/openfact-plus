package org.openfact.report;

import org.openfact.utils.PropertiesUtil;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;

public class ClassLoaderReportTheme implements ReportTheme {

    private String type;

    private String name;

    private String parentName;

    private String importName;

    private String datasource;

    private ClassLoader classLoader;

    private String templateRoot;

    private String resourceRoot;

    private String messageRoot;

    private Properties properties;

    private Properties paramResources;

    public ClassLoaderReportTheme(String type, String name, ClassLoader classLoader) throws IOException {
        init(type, name, classLoader);
    }

    public void init(String name, String type, ClassLoader classLoader) throws IOException {
        this.type = name;
        this.name = type;
        this.classLoader = classLoader;

        String themeRoot = "report/" + name.toLowerCase() + "/" + type.toLowerCase() + "/";

        this.templateRoot = themeRoot;
        this.resourceRoot = themeRoot + "resources/";
        this.messageRoot = themeRoot + "messages/";
        this.properties = new Properties();
        this.paramResources = new Properties();

        URL p = classLoader.getResource(themeRoot + "theme.properties");
        if (p != null) {
            Charset encoding = PropertiesUtil.detectEncoding(p.openStream());
            try (Reader reader = new InputStreamReader(p.openStream(), encoding)) {
                properties.load(reader);
            }
            this.parentName = properties.getProperty("parent");
            this.importName = properties.getProperty("import");
            this.datasource = properties.getProperty("datasource");
        } else {
            this.parentName = null;
            this.importName = null;
        }

        URL resourcesMapperURL = classLoader.getResource(themeRoot + "resources.properties");
        if (resourcesMapperURL != null) {
            try (Reader reader = new InputStreamReader(resourcesMapperURL.openStream())) {
                paramResources.load(reader);
            }
        }

    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDatasource() {
        return datasource;
    }

    @Override
    public String getParentName() {
        return parentName;
    }

    @Override
    public String getImportName() {
        return importName;
    }

    @Override
    public URL getTemplate(String name) {
        return classLoader.getResource(templateRoot + name);
    }

    @Override
    public InputStream getTemplateAsStream(String name) {
        return classLoader.getResourceAsStream(templateRoot + name);
    }

    @Override
    public URL getResource(String path) {
        return classLoader.getResource(resourceRoot + path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return classLoader.getResourceAsStream(resourceRoot + path);
    }

    @Override
    public URL getImagePreview() throws IOException {
        return getResource(ReportTheme.PREVIEW_IMG_NAME);
    }

    @Override
    public InputStream getImagePreviewAsStream() throws IOException {
        return getResourceAsStream(ReportTheme.PREVIEW_IMG_NAME);
    }

    @Override
    public Properties getMessages(Locale locale) throws IOException {
        return getMessages("messages", locale);
    }

    @Override
    public Properties getMessages(String baseBundlename, Locale locale) throws IOException {
        if (locale == null) {
            return null;
        }
        Properties m = new Properties();

        URL url = classLoader.getResource(this.messageRoot + baseBundlename + "_" + locale.toString() + ".properties");
        if (url != null) {
            Charset encoding = PropertiesUtil.detectEncoding(url.openStream());
            try (Reader reader = new InputStreamReader(url.openStream(), encoding)) {
                m.load(reader);
            }
        }
        return m;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public Properties getParamResources() throws IOException {
        return paramResources;
    }

}
