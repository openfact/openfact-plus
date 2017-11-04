package org.openfact.reports;

import org.keycloak.util.JsonSerialization;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Startup
@Singleton(name = "JarReportThemeProvider")
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
@ReportThemeManagerSelector
@ReportProviderType(type = ReportProviderType.ProviderType.JAR)
public class JarReportThemeProvider implements ReportThemeProvider {

    protected static final String OPENFACT_REPORT_THEMES_JSON = "META-INF/openfact-reports.json";
    private Map<String, Map<String, ClassLoaderReportTheme>> themes = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Enumeration<URL> resources = classLoader.getResources(OPENFACT_REPORT_THEMES_JSON);
            while (resources.hasMoreElements()) {
                loadThemes(classLoader, resources.nextElement().openStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load report themes", e);
        }
    }

    @Override
    public int getProviderPriority() {
        return 0;
    }

    @Override
    public ReportTheme getTheme(String type, String name) throws IOException {
        return hasTheme(type, name) ? themes.get(type).get(name) : null;
    }

    @Override
    public Set<String> nameSet(String type) {
        if (themes.containsKey(type)) {
            return themes.get(type).keySet();
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean hasTheme(String type, String name) {
        return themes.containsKey(type) && themes.get(type).containsKey(name);
    }

    protected void loadThemes(ClassLoader classLoader, InputStream themesInputStream) {
        try {
            ReportThemesRepresentation themesRep = JsonSerialization.readValue(themesInputStream, ReportThemesRepresentation.class);

            for (ReportThemeRepresentation themeRep : themesRep.getThemes()) {
                String type = themeRep.getType();
                if (!themes.containsKey(type)) {
                    themes.put(type, new HashMap<>());
                }
                themes.get(type).put(themeRep.getName(), new ClassLoaderReportTheme(type, themeRep.getName(), classLoader));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load report themes", e);
        }
    }

}
