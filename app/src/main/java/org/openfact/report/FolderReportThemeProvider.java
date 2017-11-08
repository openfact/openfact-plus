package org.openfact.report;

import org.openfact.config.ReportThemeConfig;
import org.openfact.report.ReportProviderType.Type;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Stateless
@ReportThemeManagerSelector
@ReportProviderType(type = Type.FOLDER)
public class FolderReportThemeProvider implements ReportThemeProvider {

    @Inject
    private ReportThemeConfig config;

    private File themesDir;

    @PostConstruct
    public void init() {
        String d = config.getFolderDir();
        File rootDir = null;
        if (d != null) {
            rootDir = new File(d);
        }
        themesDir = rootDir;
    }

    @Override
    public int getProviderPriority() {
        return 100;
    }

    @Override
    public ReportTheme getTheme(String type, String name) throws IOException {
        File themeDir = getThemeDir(type, name);
        return themeDir.isDirectory() ? new FolderReportTheme(themeDir, type, name) : null;
    }

    @Override
    public Set<String> nameSet(String type) {
        final String typeName = type.toLowerCase();
        File[] themeDirs = themesDir.listFiles(pathname -> pathname.isDirectory() && new File(pathname, typeName).isDirectory());
        if (themeDirs != null) {
            Set<String> names = new HashSet<>();
            for (File themeDir : themeDirs) {
                names.add(themeDir.getName());
            }
            return names;
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean hasTheme(String type, String name) {
        return getThemeDir(type, name).isDirectory();
    }

    private File getThemeDir(String type, String name) {
        return new File(themesDir, type.toLowerCase() + File.separator + name.toLowerCase());
    }

}
