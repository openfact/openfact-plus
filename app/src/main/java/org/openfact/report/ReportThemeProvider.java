package org.openfact.report;

import java.io.IOException;
import java.util.Set;

public interface ReportThemeProvider {

    int getProviderPriority();

    ReportTheme getTheme(String type, String name) throws IOException;

    Set<String> nameSet(String type);

    boolean hasTheme(String type, String name);

}