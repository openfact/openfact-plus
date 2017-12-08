package org.clarksnut.report;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Stateless
@ReportThemeManagerSelector
@ReportProviderType(type = ReportProviderType.Type.FOLDER)
public class FolderReportThemeProvider implements ReportThemeProvider {

    @Inject
    @ConfigurationValue("clarksnut.report.folder.dir")
    private Optional<String> clarksnutReportFolderDir;

    private File themesDir;

    @PostConstruct
    public void init() {
        File rootDir = null;
        if (clarksnutReportFolderDir.isPresent()) {
            rootDir = new File(clarksnutReportFolderDir.get());
        }
        themesDir = rootDir;
    }

    @Override
    public int getProviderPriority() {
        return 100;
    }

    @Override
    public ReportTheme getTheme(String name, String type) throws IOException {
        File themeDir = getThemeDir(name, type);
        return themeDir.isDirectory() ? new FolderReportTheme(themeDir, name, type) : null;
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
    public boolean hasTheme(String name, String type) {
        return getThemeDir(name, type).isDirectory();
    }

    private File getThemeDir(String name, String type) {
        return new File(themesDir, name + File.separator + type.toLowerCase());
    }


}
