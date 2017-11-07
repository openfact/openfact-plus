package org.openfact.report.jasper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.jboss.logging.Logger;
import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceUtil;
import org.openfact.documents.DocumentModel;
import org.openfact.files.FileProvider;
import org.openfact.files.FlyWeightFileModel;
import org.openfact.files.FlyWeightXmlFileModel;
import org.openfact.files.XmlFileModel;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.report.*;
import org.openfact.report.ReportProviderType.Type;
import org.openfact.report.exceptions.ReportException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Stateless
public class JasperReportProvider implements ReportTemplateProvider {

    private static final Logger logger = Logger.getLogger(JasperReportProvider.class);

    @Inject
    private FileProvider fileProvider;

    @Inject
    private JasperReportUtil jasperReportUtil;

    @Inject
    @ReportProviderType(type = Type.EXTENDING)
    private ReportThemeProvider themeProvider;

    @Inject
    private DatasourceUtil datasourceUtil;

    @Override
    public ReportTheme getTheme(String type, String name) {
        try {
            return themeProvider.getTheme(type, name);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Set<ReportTheme> getSupportedThemes(DocumentModel document) throws FileFetchException {
        Set<ReportTheme> themes = new HashSet<>();

        for (String themeName : themeProvider.nameSet(document.getType())) {
            XmlFileModel file = new FlyWeightXmlFileModel(new FlyWeightFileModel(fileProvider.getFile(document.getFileId())));
            try {
                ReportTheme theme = themeProvider.getTheme(document.getType(), themeName);
                DatasourceProvider datasourceProvider = datasourceUtil.getDatasourceProvider(theme.getDatasource());
                if (datasourceProvider.support(document, file)) {
                    themes.add(theme);
                }
            } catch (IOException e) {
                logger.error("Could not read a resource on template", e);
            }
        }
        return themes;
    }

    @Override
    public byte[] getReport(ReportTemplateConfiguration config, DocumentModel document, ExportFormat exportFormat) throws FileFetchException, ReportException {
        try {
            String themeType = document.getType().toLowerCase();
            String themeName = config.getThemeName() != null ? config.getThemeName().toLowerCase() : null;
            ReportTheme theme = themeProvider.getTheme(themeType, themeName);

            XmlFileModel file = new FlyWeightXmlFileModel(new FlyWeightFileModel(fileProvider.getFile(document.getFileId())));
            DatasourceProvider datasourceProvider = datasourceUtil.getDatasourceProvider(theme.getDatasource());
            Object bean = datasourceProvider.getDatasource(document, file);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(bean));

            JasperPrint jasperPrint = jasperReportUtil.processReport(theme, theme.getName(), config.getAttributes(), dataSource);
            return export(jasperPrint, exportFormat);
        } catch (IOException e) {
            throw new ReportException("Could not read a resource on template", e);
        } catch (JRException e) {
            throw new ReportException("Failed to process jasper report", e);
        }
    }

    protected byte[] export(final JasperPrint print, ExportFormat format) throws JRException {
        final Exporter exporter;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean html = false;
        switch (format) {
            case HTML:
                exporter = new HtmlExporter();
                exporter.setExporterOutput(new SimpleHtmlExporterOutput(out));
                html = true;
                break;
            case PDF:
                exporter = new JRPdfExporter();
                break;
            default:
                throw new JRException("Unknown report format: " + format.toString());
        }

        if (!html) {
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
        }

        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.exportReport();

        return out.toByteArray();
    }

}
