package org.clarksnut.report.jasper;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.clarksnut.config.ReportThemeConfig;
import org.clarksnut.report.ReportTheme;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class JasperReportUtil {

    @Inject
    private ReportThemeConfig config;

    private ConcurrentHashMap<String, JasperReport> cache;

    @PostConstruct
    private void init() {
        if (config.getCacheTemplates(true)) {
            cache = new ConcurrentHashMap<>();
        }
    }

    public JasperPrint processReport(ReportTheme theme, String templateName, Map<String, Object> parameters, JRDataSource dataSource, Locale locale) throws JRException, IOException {
        JasperReport jr;
        if (cache != null) {
            String key = theme.getName() + "/" + templateName;
            jr = cache.get(key);
            if (jr == null) {
                jr = getReport(templateName, theme);
                if (cache.putIfAbsent(key, jr) != null) {
                    jr = cache.get(key);
                }
            }
        } else {
            jr = getReport(templateName, theme);
        }

        // Location
        Properties messages = theme.getMessages(locale);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        messages.store(output, null);
        ByteArrayInputStream is = new ByteArrayInputStream(output.toByteArray());

        parameters.put(JRParameter.REPORT_LOCALE, locale);
        parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, new PropertyResourceBundle(is));

        // Image resources
        Properties paramResources = theme.getParamResources();
        for (Map.Entry<Object, Object> entry : paramResources.entrySet()) {
            String paramName = (String) entry.getKey();
            String resourceName = (String) entry.getValue();
            parameters.put(paramName, ImageIO.read(theme.getResource(resourceName)));
        }

        return JasperFillManager.fillReport(jr, parameters, dataSource);
    }

    public JasperReport getReport(String templateName, ReportTheme theme) throws IOException, JRException {
        URL url = theme.getTemplate(templateName + ".jasper");
        if (url != null) {
            return (JasperReport) JRLoader.loadObject(url);
        }

        url = theme.getTemplate(templateName + ".jrxml");
        if (url != null) {
            return JasperCompileManager.compileReport(url.openStream());
        }

        return null;
    }

}
