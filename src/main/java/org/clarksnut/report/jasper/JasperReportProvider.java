package org.clarksnut.report.jasper;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.clarksnut.datasource.DatasourceFactory;
import org.clarksnut.datasource.DatasourceProvider;
import org.clarksnut.files.*;
import org.clarksnut.models.DocumentModel;
import org.clarksnut.models.DocumentVersionModel;
import org.clarksnut.models.exceptions.ImpossibleToUnmarshallException;
import org.clarksnut.report.*;
import org.clarksnut.report.exceptions.ReportException;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
public class JasperReportProvider implements ReportTemplateProvider {

    private static final Logger logger = Logger.getLogger(JasperReportProvider.class);

    @Inject
    private JasperReportUtil jasperReportUtil;

    @Inject
    @ReportProviderType(type = ReportProviderType.Type.EXTENDING)
    private ReportThemeProvider themeProvider;

    @Override
    public ReportTheme getTheme(String name, String type) {
        try {
            return themeProvider.getTheme(name, type);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public byte[] getReport(ReportTemplateConfiguration config, DocumentModel document, ExportFormat exportFormat) throws ReportException {
        try {
            String themeName = config.getThemeName() != null ? config.getThemeName().toLowerCase() : null;
            String themeType = document.getType().toLowerCase();
            ReportTheme theme = themeProvider.getTheme(themeName, themeType);

            XmlUBLFileModel ublFile = new FlyWeightXmlUBLFileModel(
                    new BasicXmlUBLFileModel(
                            new FlyWeightXmlFileModel(
                                    new BasicXmlFileModel(
                                            new FlyWeightFileModel(document.getCurrentVersion().getImportedDocument().getFile())
                                    )
                            )
                    )
            );

            DatasourceProvider datasourceProvider = DatasourceFactory.getInstance().getDatasourceProvider(theme.getDatasource());
            Object bean = datasourceProvider.getDatasource(ublFile);

            Map<String, Object> beanMap = toMap(bean, "bean");
            Map<String, Object> modelMap = toMap(toModelBean(document), "model");
            beanMap.putAll(modelMap);
            JRDataSource dataSource = new JRMapCollectionDataSource(Collections.singletonList(beanMap));

            JasperPrint jasperPrint = jasperReportUtil.processReport(theme, theme.getName(), config.getAttributes(), dataSource, config.getLocale());
            return export(jasperPrint, exportFormat);
        } catch (IOException e) {
            throw new ReportException("Could not read a resource on template", e);
        } catch (JRException e) {
            throw new ReportException("Failed to process jasper report", e);
        } catch (IllegalAccessException e) {
            throw new ReportException("Failed to map fields jasper report", e);
        } catch (ImpossibleToUnmarshallException e) {
            throw new ReportException(e.getMessage(), e);
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

    public ModelBean toModelBean(DocumentModel document) {
        DocumentVersionModel currentVersion = document.getCurrentVersion();

        ModelBean bean = new ModelBean();

        bean.setType(document.getType());
        bean.setAssignedId(document.getAssignedId());
        bean.setSupplierAssignedId(document.getSupplierAssignedId());
        bean.setCustomerAssignedId(document.getCustomerAssignedId());

        bean.setAmount(currentVersion.getAmount());
        bean.setTax(currentVersion.getTax());
        bean.setCurrency(currentVersion.getCurrency());
        bean.setIssueDate(currentVersion.getIssueDate());
        bean.setSupplierName(currentVersion.getSupplierName());
        bean.setCustomerName(currentVersion.getCustomerName());

        bean.setSupplierStreetAddress(currentVersion.getSupplierStreetAddress());
        bean.setSupplierCity(currentVersion.getSupplierCity());
        bean.setSupplierCountry(currentVersion.getSupplierCountry());

        bean.setCustomerStreetAddress(currentVersion.getCustomerStreetAddress());
        bean.setCustomerCity(currentVersion.getCustomerCity());
        bean.setCustomerCountry(currentVersion.getCustomerCountry());

        bean.setLocation("www.clakrsnut.com");

        return bean;
    }


    public Map<String, Object> toMap(Object obj, String... prefixes) throws IllegalAccessException {
        String prefix = Stream.of(prefixes).collect(Collectors.joining("_"));
        prefix = prefix.isEmpty() ? "" : prefix + "_";

        Map<String, Object> map = new HashMap<>();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(prefix + field.getName(), field.get(obj));
        }

        return map;
    }
}
