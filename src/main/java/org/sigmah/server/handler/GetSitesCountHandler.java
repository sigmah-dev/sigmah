/**
 * 
 */
package org.sigmah.server.handler;

import org.sigmah.shared.command.GetSitesCount;
import org.sigmah.shared.command.result.SiteResult;

import com.google.inject.Inject;
import org.sigmah.server.dao.SiteTableDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler to the command GetAllSites.
 * 
 * @author HUZHE
 */
public class GetSitesCountHandler extends AbstractCommandHandler<GetSitesCount, SiteResult> {

	private final SiteTableDAO siteDAO;
	 
	@Inject
	public GetSitesCountHandler(SiteTableDAO siteDAO) {
		this.siteDAO = siteDAO;
	}

	@Override
	protected SiteResult execute(GetSitesCount command, UserDispatch.UserExecutionContext context) throws CommandException {
		return new SiteResult(siteDAO.queryCount(context.getUser(), command.getFilter()));
	}
	
}
