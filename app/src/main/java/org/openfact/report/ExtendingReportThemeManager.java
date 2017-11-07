package org.openfact.report;

import org.jboss.logging.Logger;
import org.openfact.common.Version;
import org.openfact.config.ReportThemeConfig;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@ReportProviderType(type = ReportProviderType.Type.EXTENDING)
public class ExtendingReportThemeManager implements ReportThemeProvider {

    private static final Logger log = Logger.getLogger(ExtendingReportThemeManager.class);

    private final ReportThemeConfig config;
    private final Instance<ReportThemeProvider> themeProviders;

    private String defaultTheme;
    private ConcurrentHashMap<ReportThemeKey, ReportTheme> themeCache;
    private List<ReportThemeProvider> providers;

    @Inject
    public ExtendingReportThemeManager(ReportThemeConfig config, @Any @ReportThemeManagerSelector Instance<ReportThemeProvider> themeProviders) {
        this.config = config;
        this.themeProviders = themeProviders;
    }

    @PostConstruct
    public void init() {
        defaultTheme = config.getDefaultTheme(Version.NAME.toLowerCase());
        if (config.getCacheReports(true)) {
            themeCache = new ConcurrentHashMap<>();
        }
        loadProviders();
    }

    private void loadProviders() {
        providers = new LinkedList<>();
        for (ReportThemeProvider themeProvider : themeProviders) {
            providers.add(themeProvider);
        }
        providers.sort((o1, o2) -> o2.getProviderPriority() - o1.getProviderPriority());
    }

    @Override
    @Lock(LockType.READ)
    public int getProviderPriority() {
        return 0;
    }

    @Override
    @Lock(LockType.READ)
    public ReportTheme getTheme(String type, String name) throws IOException {
        if (name == null) {
            name = defaultTheme;
        }

        if (themeCache != null) {
            ReportThemeKey key = ReportThemeKey.get(name);
            ReportTheme theme = themeCache.get(key);
            if (theme == null) {
                theme = loadTheme(type, name);
                if (theme == null) {
                    theme = loadTheme("model", "openfact");
                    log.errorv("Failed to find {0} report theme {1}, using built-in report themes", type, name);
                } else if (themeCache.putIfAbsent(key, theme) != null) {
                    theme = themeCache.get(key);
                }
            }
            return theme;
        } else {
            return loadTheme(type, name);
        }
    }

    private ReportTheme loadTheme(String type, String name) throws IOException {
        ReportTheme theme = findTheme(type, name);
        if (theme != null && (theme.getParentName() != null || theme.getImportName() != null)) {
            List<ReportTheme> themes = new LinkedList<>();
            themes.add(theme);

            if (theme.getImportName() != null) {
                String[] s = theme.getImportName().split("/");
                themes.add(findTheme(s[0], s[1]));
            }

            if (theme.getParentName() != null) {
                for (String parentName = theme.getParentName(); parentName != null; parentName = theme.getParentName()) {
                    theme = findTheme(parentName, type);
                    themes.add(theme);

                    if (theme.getImportName() != null) {
                        String[] s = theme.getImportName().split("/");
                        themes.add(findTheme(s[0], s[1]));
                    }
                }
            }

            return new ExtendingReportTheme(themes);
        } else {
            return theme;
        }
    }

    @Override
    @Lock(LockType.READ)
    public Set<String> nameSet(String type) {
        Set<String> themes = new HashSet<>();
        for (ReportThemeProvider p : providers) {
            themes.addAll(p.nameSet(type));
        }
        return themes;
    }

    @Override
    @Lock(LockType.READ)
    public boolean hasTheme(String type, String name) {
        for (ReportThemeProvider p : providers) {
            if (p.hasTheme(type, name)) {
                return true;
            }
        }
        return false;
    }

    private ReportTheme findTheme(String type, String name) {
        for (ReportThemeProvider p : providers) {
            if (p.hasTheme(type, name)) {
                try {
                    return p.getTheme(type, name);
                } catch (IOException e) {
                    log.errorv(e, p.getClass() + " failed to load report theme, type={0}, name={1}", type, name);
                }
            }
        }
        return null;
    }

}
