package org.clarksnut.report;

import org.clarksnut.models.DocumentModel;

public interface DataSourceProvider {

    Object getDatasource(DocumentModel document, ReportTheme theme);

}
