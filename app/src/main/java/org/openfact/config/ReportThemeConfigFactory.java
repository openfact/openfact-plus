package org.openfact.config;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class ReportThemeConfigFactory {

    private ReportThemeConfig config;

    @Inject
    public ReportThemeConfigFactory(
            @ConfigurationValue("openfact.report.default") String defaultTheme,
            @ConfigurationValue("openfact.report.cacheTemplates") Boolean cacheTemplates,
            @ConfigurationValue("openfact.report.cacheReports") Boolean cacheReports,
            @ConfigurationValue("openfact.report.folder.dir") String folderDir) {
        this.config = ReportThemeConfig.builder()
                .defaultTheme(defaultTheme)
                .cacheTemplates(cacheTemplates)
                .cacheReports(cacheReports)
                .folderDir(folderDir)
                .build();
    }

    @Produces
    public ReportThemeConfig produce() {
        return config;
    }

}
