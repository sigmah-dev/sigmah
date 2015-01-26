package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.Properties;

import org.sigmah.shared.command.GetApplicationInfo;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.ApplicationInfo;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetApplicationInfoHandler implements CommandHandler<GetApplicationInfo> {

    private final Properties configProperties;

    @Inject
    public GetApplicationInfoHandler(Properties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public CommandResult execute(GetApplicationInfo cmd, User user) throws CommandException {

        final ApplicationInfo info = new ApplicationInfo();

        // Version.
        info.setVersionName(configProperties.getProperty("version.name"));
        info.setVersionNumber(configProperties.getProperty("version.number"));
        info.setVersionReleaseDate(configProperties.getProperty("version.date"));
        info.setVersionRef(configProperties.getProperty("version.reference"));
        
        // Managers.
        info.setManagers(new ArrayList<ApplicationInfo.ApplicationManager>());
        final String managers = configProperties.getProperty("version.managers");
        if (managers != null) {
            for (final String manager : managers.split(";")) {
                if (manager != null) {
                    final String[] details = manager.split(",");
                    if (details != null && details.length >= 2) {
                        info.getManagers().add(new ApplicationInfo.ApplicationManager(details[0], details[1]));
                    }
                }
            }
        }

        // Partners.
        info.setPartners(new ArrayList<ApplicationInfo.ApplicationPartner>());
        final String partners = configProperties.getProperty("version.partners");
        if (partners != null) {
            for (final String partner : partners.split(";")) {
                if (partner != null) {
                    final String[] details = partner.split(",");
                    if (details != null && details.length >= 3) {
                        info.getPartners().add(
                                new ApplicationInfo.ApplicationPartner(details[0],
                                        ApplicationInfo.ApplicationPartnerRole.valueOf(details[1]), details[2]));
                    }
                }
            }
        }

        // Developers.
        info.setDeveloppers(new ArrayList<ApplicationInfo.ApplicationDeveloper>());
        final String developers = configProperties.getProperty("version.developpers");
        if (developers != null) {
            for (final String developer : developers.split(";")) {
                if (developer != null) {
                    final String[] details = developer.split(",");
                    if (details != null && details.length >= 2) {
                        info.getDeveloppers().add(new ApplicationInfo.ApplicationDeveloper(details[0], details[1]));
                    }
                }
            }
        }

        // Contributors.
        info.setContributors(new ArrayList<ApplicationInfo.ApplicationContributor>());
        final String contributors = configProperties.getProperty("version.contributors");
        if (contributors != null) {
            for (final String contributor : contributors.split(";")) {
                if (contributor != null) {
                    final String[] details = contributor.split(",");
                    if (details != null && details.length >= 2) {
                        info.getContributors().add(new ApplicationInfo.ApplicationContributor(details[0], details[1]));
                    }
                }
            }
        }

        return info;
    }
}
