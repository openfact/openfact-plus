package org.openfact.report;

import org.openfact.documents.DocumentModel;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.report.exceptions.ReportException;

import java.util.Set;

public interface ReportTemplateProvider {

    ReportTheme getTheme(String name, String type);

    byte[] getReport(ReportTemplateConfiguration configuration, DocumentModel document, ExportFormat format) throws FileFetchException, ReportException;

}
