package org.openfact.report;

import org.keycloak.util.JsonSerialization;
import org.openfact.report.ReportProviderType.Type;
import org.openfact.theme.ClassLoaderTheme;
import org.openfact.theme.Theme;
import org.openfact.theme.ThemeRepresentation;
import org.openfact.theme.ThemesRepresentation;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Singleton
@ReportThemeManagerSelector
@ReportProviderType(type = Type.JAR)
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
    @Lock(LockType.READ)
    public int getProviderPriority() {
        return 0;
    }

    @Override
    @Lock(LockType.READ)
    public ReportTheme getTheme(String name, String type) throws IOException {
        return hasTheme(name, type) ? themes.get(type).get(name) : null;
    }

    @Override
    @Lock(LockType.READ)
    public Set<String> nameSet(String type) {
        if (themes.containsKey(type)) {
            return themes.get(type).keySet();
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    @Lock(LockType.READ)
    public boolean hasTheme(String name, String type) {
        return themes.containsKey(type) && themes.get(type).containsKey(name);
    }

    protected void loadThemes(ClassLoader classLoader, InputStream themesInputStream) {
        try {
            ReportThemesRepresentation themesRep = JsonSerialization.readValue(themesInputStream, ReportThemesRepresentation.class);

            for (ReportThemeRepresentation themeRep : themesRep.getThemes()) {
                for (String type : themeRep.getTypes()) {
                    if (!themes.containsKey(type)) {
                        themes.put(type, new HashMap<>());
                    }
                    themes.get(type).put(themeRep.getName(), new ClassLoaderReportTheme(themeRep.getName(), type, classLoader));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load report themes", e);
        }
    }

}
