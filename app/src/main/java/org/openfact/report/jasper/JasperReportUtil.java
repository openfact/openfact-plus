package org.openfact.report.jasper;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.openfact.config.ReportThemeConfig;
import org.openfact.report.ReportTheme;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class JasperReportUtil {

    private final ReportThemeConfig config;
    private ConcurrentHashMap<String, JasperReport> cache;

    @Inject
    public JasperReportUtil(ReportThemeConfig config) {
        this.config = config;
    }

    @PostConstruct
    private void init() {
        if (config.getCacheTemplates(true)) {
            cache = new ConcurrentHashMap<>();
        }
    }

    public JasperPrint processReport(ReportTheme theme, String templateName, Map<String, Object> parameters, JRDataSource dataSource) throws JRException, IOException {
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

        JasperPrint jp = JasperFillManager.fillReport(jr, parameters, dataSource);
        return jp;
    }

    private JasperReport getReport(String templateName, ReportTheme theme) throws IOException, JRException {
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
