package org.openfact.report;

import org.openfact.documents.DocumentModel;

public interface DataSourceProvider {

    Object getDatasource(DocumentModel document, ReportTheme theme);

}
