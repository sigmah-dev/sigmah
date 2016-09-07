package org.sigmah.shared.file;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
import org.sigmah.shared.util.FileType;

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
	public void isCached(FileVersionDTO fileVersionDTO, AsyncCallback<Boolean> callback) {
		callback.onSuccess(Boolean.FALSE);
	}

	@Override
	public void upload(final FormPanel formPanel, final ProgressListener progressListener) {
		upload(formPanel, progressListener, ServletConstants.ServletMethod.UPLOAD);
	}

	@Override
	public void uploadAvatar(final FormPanel formPanel, final ProgressListener progressListener) {
		upload(formPanel, progressListener, ServletConstants.ServletMethod.UPLOAD_AVATAR);
	}

	private void upload(final FormPanel formPanel, final ProgressListener progressListener, ServletConstants.ServletMethod servletMethod) {
		ServletUrlBuilder urlBuilder = new ServletUrlBuilder(authenticationProvider, pageManager, ServletConstants.Servlet.FILE, servletMethod);

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
		callback.onSuccess(state == ApplicationState.ONLINE && fileVersionDTO.isAvailable());
	}

	@Override
	public boolean canUpload() {
		return state == ApplicationState.ONLINE;
	}
}
