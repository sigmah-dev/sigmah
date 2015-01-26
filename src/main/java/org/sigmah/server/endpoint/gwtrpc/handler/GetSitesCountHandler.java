/**
 * 
 */
package org.sigmah.server.endpoint.gwtrpc.handler;

import org.sigmah.shared.command.GetSitesCount;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.dao.SiteTableDAO;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * 
 * Handler to the command GetAllSites
 * 
 * @author HUZHE
 *
 */
public class GetSitesCountHandler implements CommandHandler<GetSitesCount> {

	
	 private final SiteTableDAO siteDAO;
	 
	 @Inject
	 public GetSitesCountHandler(SiteTableDAO siteDAO)
	 {
		 this.siteDAO = siteDAO;
	 }

	@Override
	public CommandResult execute(GetSitesCount cmd, User user)
			throws CommandException {
		
		
		return new SiteResult( siteDAO.queryCount(user, cmd.getFilter()));
		
	}
	
	
	
}
