package org.clarksnut.report;

import org.clarksnut.documents.DocumentModel;

public interface DataSourceProvider {

    Object getDatasource(DocumentModel document, ReportTheme theme);

}
