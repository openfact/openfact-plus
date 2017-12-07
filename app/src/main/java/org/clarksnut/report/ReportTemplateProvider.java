package org.clarksnut.report;

import org.clarksnut.files.exceptions.FileFetchException;
import org.clarksnut.documents.DocumentModel;
import org.clarksnut.report.exceptions.ReportException;

public interface ReportTemplateProvider {

    ReportTheme getTheme(String name, String type);

    byte[] getReport(ReportTemplateConfiguration configuration, DocumentModel document, ExportFormat format) throws FileFetchException, ReportException;

}
