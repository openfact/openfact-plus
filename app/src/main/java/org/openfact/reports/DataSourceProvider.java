package org.openfact.reports;

import org.openfact.documents.DocumentModel;

public interface DataSourceProvider {

    Object getDatasource(DocumentModel document, ReportTheme theme);

}
