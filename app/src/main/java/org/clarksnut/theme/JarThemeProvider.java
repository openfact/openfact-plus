package org.clarksnut.theme;

import org.keycloak.util.JsonSerialization;
import org.clarksnut.theme.ThemeProviderType.Type;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Singleton
@ThemeManagerSelector
@ThemeProviderType(type = Type.JAR)
public class JarThemeProvider implements ThemeProvider {

    protected static final String CLARKSNUT_THEMES_JSON = "META-INF/clarksnut-themes.json";
    protected static Map<Theme.Type, Map<String, ClassLoaderTheme>> themes = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Enumeration<URL> resources = classLoader.getResources(CLARKSNUT_THEMES_JSON);
            while (resources.hasMoreElements()) {
                loadThemes(classLoader, resources.nextElement().openStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load themes", e);
        }
    }

    @Override
    @Lock(LockType.READ)
    public int getProviderPriority() {
        return 0;
    }

    @Override
    @Lock(LockType.READ)
    public Theme getTheme(String name, Theme.Type type) throws IOException {
        return hasTheme(name, type) ? themes.get(type).get(name) : null;
    }

    @Override
    @Lock(LockType.READ)
    public Set<String> nameSet(Theme.Type type) {
        if (themes.containsKey(type)) {
            return themes.get(type).keySet();
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    @Lock(LockType.READ)
    public boolean hasTheme(String name, Theme.Type type) {
        return themes.containsKey(type) && themes.get(type).containsKey(name);
    }

    protected void loadThemes(ClassLoader classLoader, InputStream themesInputStream) {
        try {
            ThemesRepresentation themesRep = JsonSerialization.readValue(themesInputStream, ThemesRepresentation.class);

            for (ThemeRepresentation themeRep : themesRep.getThemes()) {
                for (String t : themeRep.getTypes()) {
                    Theme.Type type = Theme.Type.valueOf(t.toUpperCase());
                    if (!themes.containsKey(type)) {
                        themes.put(type, new HashMap<>());
                    }
                    themes.get(type).put(themeRep.getName(), new ClassLoaderTheme(themeRep.getName(), type, classLoader));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load themes", e);
        }
    }

}
