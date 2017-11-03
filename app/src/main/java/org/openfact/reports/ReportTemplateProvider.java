package org.openfact.reports;

import org.openfact.documents.DocumentModel;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.reports.exceptions.ReportException;

import java.util.Set;

public interface ReportTemplateProvider {

    ReportTheme getTheme(String type, String name);

    Set<ReportTheme> getSupportedThemes(DocumentModel document) throws FileFetchException;

    byte[] getReport(ReportTemplateConfiguration configuration, DocumentModel document, ExportFormat format) throws FileFetchException, ReportException;

}
