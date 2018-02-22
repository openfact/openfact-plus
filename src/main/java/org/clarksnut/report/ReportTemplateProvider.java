package org.clarksnut.report;

import org.clarksnut.models.DocumentModel;
import org.clarksnut.report.exceptions.ReportException;

public interface ReportTemplateProvider {

    ReportTheme getTheme(String name, String type);

    byte[] getReport(ReportTemplateConfiguration configuration, DocumentModel document, ExportFormat format) throws ReportException;

}
