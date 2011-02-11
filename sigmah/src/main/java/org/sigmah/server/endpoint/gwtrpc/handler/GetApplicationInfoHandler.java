package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;

import org.sigmah.shared.command.GetApplicationInfo;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.ApplicationInfo;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

public class GetApplicationInfoHandler implements CommandHandler<GetApplicationInfo> {

    @Override
    public CommandResult execute(GetApplicationInfo cmd, User user) throws CommandException {
        final ApplicationInfo info = new ApplicationInfo();
        
        // TODO read info from an external file.
            
        info.setVersionName("Amazone");
        info.setVersionNumber("0.9");
        info.setVersionReleaseDate("02/2011");
        
        info.setManagers(new ArrayList<ApplicationInfo.ApplicationManager>());
        info.getManagers().add(new ApplicationInfo.ApplicationManager("Coopérative de pilotage", "http://www.sigmah.org"));
        info.getManagers().add(new ApplicationInfo.ApplicationManager("Groupe URD", "http://www.urd.org"));
        
        info.setPartners(new ArrayList<ApplicationInfo.ApplicationPartner>());
        info.getPartners().add(new ApplicationInfo.ApplicationPartner("IDEIA", ApplicationInfo.ApplicationPartnerRole.DEVELOPPER, "http://www.ideia.fr"));
        info.getPartners().add(new ApplicationInfo.ApplicationPartner("betadadriven", ApplicationInfo.ApplicationPartnerRole.DEVELOPPER, "http://www.betadadriven.com"));
        info.getPartners().add(new ApplicationInfo.ApplicationPartner("Adergo", ApplicationInfo.ApplicationPartnerRole.DESIGN, "http://www.adergo.com"));
        info.getPartners().add(new ApplicationInfo.ApplicationPartner("Philippe Rouanet", ApplicationInfo.ApplicationPartnerRole.GRAPHISM, "http://www.philipperouanet.com"));
        
        info.setDeveloppers(new ArrayList<ApplicationInfo.ApplicationDevelopper>());
        
        info.setContributors(new ArrayList<ApplicationInfo.ApplicationContributor>());

        return info;
    }
}
