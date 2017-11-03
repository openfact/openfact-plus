package org.openfact.reports;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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

    public ClassLoaderReportTheme(String type, String name, ClassLoader classLoader) throws IOException {
        init(type, name, classLoader);
    }

    public void init(String type, String name, ClassLoader classLoader) throws IOException {
        this.type = type;
        this.name = name;
        this.classLoader = classLoader;

        String themeRoot = "report/" + type.toLowerCase() + "/" + name.toLowerCase() + "/";

        this.templateRoot = themeRoot;
        this.resourceRoot = themeRoot + "resources/";
        this.messageRoot = themeRoot + "messages/";
        this.properties = new Properties();

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

}
