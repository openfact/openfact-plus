package org.openfact.report;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ExtendingReportTheme implements ReportTheme {

    private List<ReportTheme> themes;

    private Properties properties;

    private Properties paramResources;

    private ConcurrentHashMap<String, ConcurrentHashMap<Locale, Properties>> messages = new ConcurrentHashMap<>();

    public ExtendingReportTheme(List<ReportTheme> themes) {
        this.themes = themes;
    }

    @Override
    public String getType() {
        return themes.get(0).getType();
    }

    @Override
    public String getName() {
        return themes.get(0).getName();
    }

    @Override
    public String getDatasource() {
        return themes.get(0).getDatasource();
    }

    @Override
    public String getParentName() {
        return themes.get(0).getParentName();
    }

    @Override
    public String getImportName() {
        return themes.get(0).getImportName();
    }

    @Override
    public URL getTemplate(String name) throws IOException {
        for (ReportTheme t : themes) {
            URL template = t.getTemplate(name);
            if (template != null) {
                return template;
            }
        }
        return null;
    }

    @Override
    public InputStream getTemplateAsStream(String name) throws IOException {
        for (ReportTheme t : themes) {
            InputStream template = t.getTemplateAsStream(name);
            if (template != null) {
                return template;
            }
        }
        return null;
    }


    @Override
    public URL getResource(String path) throws IOException {
        for (ReportTheme t : themes) {
            URL resource = t.getResource(path);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String path) throws IOException {
        for (ReportTheme t : themes) {
            InputStream resource = t.getResourceAsStream(path);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    @Override
    public URL getImagePreview() throws IOException {
        URL url = getResource(ReportTheme.PREVIEW_IMG_NAME);
        return url != null ? url : ExtendingReportTheme.class.getResource("/images/defaultReportThemePreview.png");
    }

    @Override
    public InputStream getImagePreviewAsStream() throws IOException {
        InputStream is = getResourceAsStream(ReportTheme.PREVIEW_IMG_NAME);
        return is != null ? is : ExtendingReportTheme.class.getResource("/images/defaultReportThemePreview.png").openStream();
    }

    @Override
    public Properties getMessages(Locale locale) throws IOException {
        return getMessages("messages", locale);
    }

    @Override
    public Properties getMessages(String baseBundlename, Locale locale) throws IOException {
        if (messages.get(baseBundlename) == null || messages.get(baseBundlename).get(locale) == null) {
            Properties messages = new Properties();

            if (!Locale.ENGLISH.equals(locale)) {
                messages.putAll(getMessages(baseBundlename, Locale.ENGLISH));
            }

            ListIterator<ReportTheme> itr = themes.listIterator(themes.size());
            while (itr.hasPrevious()) {
                Properties m = itr.previous().getMessages(baseBundlename, locale);
                if (m != null) {
                    messages.putAll(m);
                }
            }

            this.messages.putIfAbsent(baseBundlename, new ConcurrentHashMap<Locale, Properties>());
            this.messages.get(baseBundlename).putIfAbsent(locale, messages);

            return messages;
        } else {
            return messages.get(baseBundlename).get(locale);
        }
    }

    @Override
    public Properties getProperties() throws IOException {
        if (properties == null) {
            Properties properties = new Properties();
            ListIterator<ReportTheme> itr = themes.listIterator(themes.size());
            while (itr.hasPrevious()) {
                Properties p = itr.previous().getProperties();
                if (p != null) {
                    properties.putAll(p);
                }
            }
            this.properties = properties;
            return properties;
        } else {
            return properties;
        }
    }

    @Override
    public Properties getParamResources() throws IOException {
        if (paramResources == null) {
            Properties properties = new Properties();
            ListIterator<ReportTheme> itr = themes.listIterator(themes.size());
            while (itr.hasPrevious()) {
                Properties p = itr.previous().getParamResources();
                if (p != null) {
                    properties.putAll(p);
                }
            }
            this.paramResources = properties;
            return properties;
        } else {
            return paramResources;
        }
    }

}
