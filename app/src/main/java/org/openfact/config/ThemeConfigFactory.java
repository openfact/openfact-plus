package org.openfact.config;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class ThemeConfigFactory {

    private ThemeConfig config;

    @Inject
    public ThemeConfigFactory(
            @ConfigurationValue("openfact.theme.default") String defaultTheme,
            @ConfigurationValue("openfact.theme.staticMaxAge") Long staticMaxAge,
            @ConfigurationValue("openfact.theme.cacheTemplates") Boolean cacheTemplates,
            @ConfigurationValue("openfact.theme.cacheThemes") Boolean cacheThemes,
            @ConfigurationValue("openfact.theme.folder.dir") String folderDir) {
        this.config = ThemeConfig.builder()
                .defaultTheme(defaultTheme)
                .staticMaxAge(staticMaxAge)
                .cacheTemplates(cacheTemplates)
                .cacheThemes(cacheThemes)
                .folderDir(folderDir)
                .build();
    }

    @Produces
    public ThemeConfig produce() {
        return config;
    }

}
