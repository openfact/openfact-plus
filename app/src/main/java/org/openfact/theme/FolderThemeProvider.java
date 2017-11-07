package org.openfact.theme;

import org.openfact.config.ThemeConfig;
import org.openfact.theme.*;
import org.openfact.theme.ThemeProviderType.Type;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Stateless
@ThemeManagerSelector
@ThemeProviderType(type = Type.FOLDER)
public class FolderThemeProvider implements ThemeProvider {

    private final ThemeConfig config;
    private File themesDir;

    @Inject
    public FolderThemeProvider(ThemeConfig config) {
        this.config = config;
    }

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
    public Theme getTheme(String name, Theme.Type type) throws IOException {
        File themeDir = getThemeDir(name, type);
        return themeDir.isDirectory() ? new FolderTheme(themeDir, name, type) : null;
    }

    @Override
    public Set<String> nameSet(Theme.Type type) {
        final String typeName = type.name().toLowerCase();
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
    public boolean hasTheme(String name, Theme.Type type) {
        return getThemeDir(name, type).isDirectory();
    }

    private File getThemeDir(String name, Theme.Type type) {
        return new File(themesDir, name + File.separator + type.name().toLowerCase());
    }

}
