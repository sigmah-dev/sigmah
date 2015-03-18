package org.sigmah.offline.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetSitesCount;
import org.sigmah.shared.command.result.SiteResult;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetSitesCountAsyncHandler implements AsyncCommandHandler<GetSitesCount, SiteResult> {

	@Override
	public void execute(GetSitesCount command, OfflineExecutionContext executionContext, AsyncCallback<SiteResult> callback) {
		callback.onSuccess(new SiteResult(0));
	}
	
}
