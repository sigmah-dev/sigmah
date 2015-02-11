package org.sigmah.shared.file;

import org.sigmah.client.page.PageManager;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.servlet.ServletConstants;
import org.sigmah.shared.servlet.ServletUrlBuilder;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.event.handler.OfflineHandler;

/**
 * Download and upload files with classic http requests.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
class DirectTransfertManager implements TransfertManager {

	private final AuthenticationProvider authenticationProvider;
	private final PageManager pageManager;
	private ApplicationState state;

	public DirectTransfertManager(AuthenticationProvider authenticationProvider, PageManager pageManager, EventBus eventBus) {
		this.authenticationProvider = authenticationProvider;
		this.pageManager = pageManager;
		
		eventBus.addHandler(OfflineEvent.getType(), new OfflineHandler() {

			@Override
			public void handleEvent(OfflineEvent event) {
				state = event.getState();
			}
		});
	}

	@Override
	public void download(FileVersionDTO fileVersionDTO, final ProgressListener progressListener) {
		final ServletUrlBuilder urlBuilder =
				new ServletUrlBuilder(authenticationProvider, pageManager, ServletConstants.Servlet.FILE, ServletConstants.ServletMethod.DOWNLOAD_FILE);
		urlBuilder.addParameter(RequestParameter.ID, fileVersionDTO.getId());
		ClientUtils.launchDownload(urlBuilder.toString());
	}

	@Override
	public void cache(FileVersionDTO fileVersionDTO) {
		// Not supported
	}

	@Override
	public void upload(final FormPanel formPanel, final ProgressListener progressListener) {
		final ServletUrlBuilder urlBuilder =
				new ServletUrlBuilder(authenticationProvider, pageManager, ServletConstants.Servlet.FILE, ServletConstants.ServletMethod.UPLOAD);

		formPanel.setAction(urlBuilder.toString());
		formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
		formPanel.setMethod(FormPanel.Method.POST);

		formPanel.addListener(Events.Submit, new Listener<FormEvent>() {

			@Override
			public void handleEvent(FormEvent formEvent) {
				formPanel.removeListener(Events.Submit, this);
				final String result = formEvent.getResultHtml();

				switch (ServletConstants.getErrorCode(result)) {
					case Response.SC_OK:
						progressListener.onLoad(result);
						break;

					case Response.SC_NO_CONTENT:
						progressListener.onFailure(Cause.EMPTY_FILE);
						break;

					case Response.SC_REQUEST_ENTITY_TOO_LARGE:
						progressListener.onFailure(Cause.FILE_TOO_LARGE);
						break;

					case Response.SC_PRECONDITION_FAILED:
						progressListener.onFailure(Cause.BAD_REQUEST);
						break;

					default:
						progressListener.onFailure(Cause.SERVER_ERROR);
						break;
				}
			}
		});

		formPanel.submit();
	}

	@Override
	public void canDownload(FileVersionDTO fileVersionDTO, AsyncCallback<Boolean> callback) {
		callback.onSuccess(state == ApplicationState.ONLINE);
	}

	@Override
	public boolean canUpload() {
		return state == ApplicationState.ONLINE;
	}
}
