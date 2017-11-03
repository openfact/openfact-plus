package org.openfact.reports;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Stateless
@ReportThemeManagerSelector
@ReportProviderType(type = ReportProviderType.ProviderType.FOLDER)
public class FolderReportThemeProvider implements ReportThemeProvider {

    private File themesDir;

    @PostConstruct
    public void init() {
        themesDir = new File("reports");
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
