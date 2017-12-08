package org.clarksnut.report;

import org.clarksnut.common.Version;
import org.jboss.logging.Logger;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@ReportProviderType(type = ReportProviderType.Type.EXTENDING)
public class ExtendingReportThemeManager implements ReportThemeProvider {

    private static final Logger log = Logger.getLogger(ExtendingReportThemeManager.class);

    @Inject
    @ConfigurationValue("clarksnut.report.default")
    private Optional<String> clarksnutReportDefaultTheme;

    @Inject
    @ConfigurationValue("clarksnut.report.cacheReports")
    private Optional<Boolean> clarksnutCacheReports;

    @Inject
    @Any
    @ReportThemeManagerSelector
    private Instance<ReportThemeProvider> themeProviders;

    private String defaultTheme;
    private ConcurrentHashMap<ReportThemeKey, ReportTheme> themeCache;
    private List<ReportThemeProvider> providers;

    @PostConstruct
    public void init() {
        defaultTheme = clarksnutReportDefaultTheme.orElse(Version.NAME.toLowerCase());
        if (clarksnutCacheReports.orElse(true)) {
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
    public ReportTheme getTheme(String name, String type) throws IOException {
        if (name == null) {
            name = defaultTheme;
        }

        if (themeCache != null) {
            ReportThemeKey key = ReportThemeKey.get(name, type);
            ReportTheme theme = themeCache.get(key);
            if (theme == null) {
                theme = loadTheme(name, type);
                if (theme == null) {
                    theme = loadTheme(name, "model");
                    if (theme == null) {
                        theme = loadTheme("clarksnut", type);
                        if (theme == null) {
                            theme = loadTheme("clarksnut", "model");
                        }
                        log.errorv("Failed to find {0} report report {1}, using built-in report themes", type, name);
                    }
                } else if (themeCache.putIfAbsent(key, theme) != null) {
                    theme = themeCache.get(key);
                }
            }
            return theme;
        } else {
            ReportTheme theme = loadTheme(name, type);
            if (theme == null) {
                theme = loadTheme(name, "model");
                if (theme == null) {
                    theme = loadTheme("clarksnut", type);
                    if (theme == null) {
                        theme = loadTheme("clarksnut", "model");
                    }
                    log.errorv("Failed to find {0} report report {1}, using built-in report themes", type, name);
                }
            }
            return theme;
        }
    }

    private ReportTheme loadTheme(String name, String type) throws IOException {
        ReportTheme theme = findTheme(name, type);
        if (theme != null && (theme.getParentName() != null || theme.getImportName() != null)) {
            List<ReportTheme> themes = new LinkedList<>();
            themes.add(theme);

            if (theme.getImportName() != null) {
                String[] s = theme.getImportName().split("/");
                themes.add(findTheme(s[1], type));
            }

            if (theme.getParentName() != null) {
                for (String parentName = theme.getParentName(); parentName != null; parentName = theme.getParentName()) {
                    theme = findTheme(parentName, type);
                    themes.add(theme);

                    if (theme.getImportName() != null) {
                        String[] s = theme.getImportName().split("/");
                        themes.add(findTheme(s[1], type));
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
    public boolean hasTheme(String name, String type) {
        for (ReportThemeProvider p : providers) {
            if (p.hasTheme(name, type)) {
                return true;
            }
        }
        return false;
    }

    private ReportTheme findTheme(String name, String type) {
        for (ReportThemeProvider p : providers) {
            if (p.hasTheme(name, type)) {
                try {
                    return p.getTheme(name, type);
                } catch (IOException e) {
                    log.errorv(e, p.getClass() + " failed to load report, type={0}, name={1}", type, name);
                }
            }
        }
        return null;
    }

}
