package org.sigmah.shared.file;

import org.sigmah.client.page.PageManager;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.offline.status.ConnectionStatus;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.servlet.ServletConstants;
import org.sigmah.shared.servlet.ServletUrlBuilder;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
class DirectTransfertManager implements TransfertManager {

	private final AuthenticationProvider authenticationProvider;
	private final PageManager pageManager;
	private final ConnectionStatus connectionStatus;

	public DirectTransfertManager(AuthenticationProvider authenticationProvider, PageManager pageManager, ConnectionStatus connectionStatus) {
		this.authenticationProvider = authenticationProvider;
		this.pageManager = pageManager;
		this.connectionStatus = connectionStatus;
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
		callback.onSuccess(connectionStatus.getState() == ApplicationState.ONLINE);
	}

	@Override
	public boolean canUpload() {
		return connectionStatus.getState() == ApplicationState.ONLINE;
	}
}
