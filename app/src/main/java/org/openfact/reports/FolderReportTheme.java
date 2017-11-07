package org.openfact.reports;

import org.openfact.utils.PropertiesUtil;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;

public class FolderReportTheme implements ReportTheme {

    private String parentName;
    private String importName;
    private String datasource;
    private File themeDir;
    private File resourcesDir;
    private String type;
    private String name;
    private final Properties properties;

    public FolderReportTheme(File themeDir, String type, String name) throws IOException {
        this.themeDir = themeDir;
        this.type = type;
        this.name = name;
        this.properties = new Properties();

        File propertiesFile = new File(themeDir, "theme.properties");
        if (propertiesFile.isFile()) {
            Charset encoding = PropertiesUtil.detectEncoding(new FileInputStream(propertiesFile));
            try (Reader reader = new InputStreamReader(new FileInputStream(propertiesFile), encoding)) {
                properties.load(reader);
            }
            parentName = properties.getProperty("parent");
            importName = properties.getProperty("import");
            datasource = properties.getProperty("datasource");
        }

        resourcesDir = new File(themeDir, "resources");
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
    public URL getTemplate(String name) throws IOException {
        File file = new File(themeDir, name);
        return file.isFile() ? file.toURI().toURL() : null;
    }

    @Override
    public InputStream getTemplateAsStream(String name) throws IOException {
        URL url = getTemplate(name);
        return url != null ? url.openStream() : null;
    }

    @Override
    public URL getResource(String path) throws IOException {
        if (File.separatorChar != '/') {
            path = path.replace('/', File.separatorChar);
        }

        File file = new File(resourcesDir, path);
        if (!file.isFile() || !file.getCanonicalPath().startsWith(resourcesDir.getCanonicalPath())) {
            return null;
        } else {
            return file.toURI().toURL();
        }
    }

    @Override
    public InputStream getResourceAsStream(String path) throws IOException {
        URL url = getResource(path);
        return url != null ? url.openStream() : null;
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

        File file = new File(themeDir, "messages" + File.separator + baseBundlename + "_" + locale.toString() + ".properties");
        if (file.isFile()) {
            Charset encoding = PropertiesUtil.detectEncoding(new FileInputStream(file));
            try (Reader reader = new InputStreamReader(new FileInputStream(file), encoding)) {
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
