package org.openfact.report;

import java.io.IOException;
import java.util.Set;

public interface ReportThemeProvider {

    int getProviderPriority();

    ReportTheme getTheme(String name, String type) throws IOException;

    Set<String> nameSet(String type);

    boolean hasTheme(String name, String type);

}