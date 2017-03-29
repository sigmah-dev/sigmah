package org.sigmah.client.ui.presenter.reports;

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


import java.util.Date;

import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.view.reports.AttachFileView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.ButtonFileUploadField;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.file.Cause;
import org.sigmah.shared.file.ProgressListener;
import org.sigmah.shared.servlet.FileUploadResponse;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * Attach file presenter which manages the {@link AttachFileView}.
 * </p>
 * <p>
 * It allows users to attach a file to a Project or a OrgUnit.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class AttachFilePresenter extends AbstractPagePresenter<AttachFilePresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(AttachFileView.class)
	public static interface View extends ViewPopupInterface {

		FormPanel getForm();

		Button getCancelButton();

		// UI fields.

		ButtonFileUploadField getFileUploadButtonField();

		Field<Object> getElementField();

		Field<Object> getPhaseField();

		// Hidden fields sent within file form.

		Field<String> getElementIdField();

		Field<String> getContainerIdField();

		Field<String> getNameField();

		Field<String> getAuthorField();

	}

	/**
	 * The container id.
	 */
	private Integer containerId;

	/**
	 * The flexible element.
	 */
	private FlexibleElementDTO flexibleElement;

	/**
	 * The phase name.
	 */
	private String phaseName;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public AttachFilePresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ATTACH_FILE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// FileUploadButton change listener submitting automatically the form.
		// --

		view.getFileUploadButtonField().addListener(Events.OnChange, new Listener<DomEvent>() {

			@Override
			public void handleEvent(final DomEvent event) {
				event.getEvent().stopPropagation(); // Important!
				onFileChange();
			}
		});

		// --
		// Cancel button listener.
		// --

		view.getCancelButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				hideView();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// --
		// View reset.
		// --

		view.getForm().clearAll();

		// --
		// Retrieving required parameters from request.
		// --

		containerId = request.getParameterInteger(RequestParameter.ID);
		if (containerId == null) {
			hideView();
			throw new IllegalArgumentException("Invalid container id.");
		}

		phaseName = request.getParameter(RequestParameter.NAME);
		if (ClientUtils.isBlank(phaseName)) {
			hideView();
			throw new IllegalArgumentException("Invalid phase name '" + phaseName + "'.");
		}

		flexibleElement = request.getData(RequestParameter.DTO);
		if (flexibleElement == null) {
			hideView();
			throw new IllegalArgumentException("Invalid flexible element.");
		}

		// --
		// Page title.
		// --

		setPageTitle(I18N.CONSTANTS.flexibleElementFilesListAddDocumentDetails());

		// --
		// Form fields.
		// --

		view.getPhaseField().setValue(phaseName);
		view.getElementField().setValue(flexibleElement.getLabel());
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Method executed on file change event.
	 */
	private void onFileChange() {

		if (!injector.getTransfertManager().canUpload()) {
			N10N.warn(I18N.CONSTANTS.flexibleElementFilesListUploadUnable());
			return;
		}

		// --
		// Preparing upload.
		// --

		// Set hidden fields values.
		view.getElementIdField().setValue(String.valueOf(flexibleElement.getId()));
		view.getContainerIdField().setValue(String.valueOf(containerId));
		view.getNameField().setValue(view.getFileUploadButtonField().getValue());
		view.getAuthorField().setValue(String.valueOf(auth().getUserId()));

		// Debug form hidden values.
		if (Log.isDebugEnabled()) {
			final StringBuilder sb = new StringBuilder();
			sb.append("Upload a new file with parameters: ");
			sb.append("name=");
			sb.append(view.getNameField().getValue());
			sb.append(" ; author id=");
			sb.append(view.getAuthorField().getValue());
			sb.append(" ; orgUnit id=");
			sb.append(view.getContainerIdField().getValue());
			sb.append(" ; element id=");
			sb.append(view.getElementIdField().getValue());

			Log.debug(sb.toString());
		}

		// --
		// Starting upload.
		// --

		view.setLoading(true);

		injector.getTransfertManager().upload(view.getForm(), new ProgressListener() {

			@Override
			public void onProgress(final double progress, final double speed) {
			}

			@Override
			public void onFailure(final Cause cause) {
				try {

					onUploadFailure(cause);

				} finally {
					onComplete();
				}
			}

			@Override
			public void onLoad(final String result) {
				try {

					onUploadSuccess(result);

				} finally {
					onComplete();
				}
			}

		});
	}

	/**
	 * Callback executed on file upload failure.
	 * 
	 * @param cause
	 *          The failure cause.
	 */
	private void onUploadFailure(final Cause cause) {

		// If an error occurred, informs the user.
		final StringBuilder sb = new StringBuilder();
		sb.append(I18N.CONSTANTS.flexibleElementFilesListUploadErrorDetails());

		switch (cause) {

			case EMPTY_FILE:
				sb.append("\n");
				sb.append(I18N.CONSTANTS.flexibleElementFilesListUploadErrorEmpty());
				break;

			case FILE_TOO_LARGE:

				String fileSize = null; // FIXME (v2.0) String.valueOf(Integer.parseInt(FROM_RESPONSE[0]) / (1024 * 1024));

				if (ClientUtils.isNotBlank(fileSize)) {
					final String maxFileSize = String.valueOf(FileUploadUtils.MAX_UPLOAD_FILE_SIZE);
					sb.append(I18N.MESSAGES.flexibleElementFilesListUploadErrorTooBig(fileSize, maxFileSize));

				} else {
					sb.append(I18N.CONSTANTS.flexibleElementFilesListUploadErrorTooBig());
				}

				sb.append("\n");

				break;

			default:
				break;
		}

		N10N.error(I18N.CONSTANTS.flexibleElementFilesListUploadError(), sb.toString());
	}

	/**
	 * Callback executed on file upload success.
	 * 
	 * @param result
	 *          The upload result that contains the uploaded file version.
	 */
	private void onUploadSuccess(final String result) {

		final FileUploadResponse response = FileUploadResponse.parse(result);
		final FileVersionDTO fileVersion = response.getFileVersion();
		final MonitoredPointDTO point = response.getMonitoredPoint();

		if (fileVersion == null) {
			throw new UnsupportedOperationException("Invalid required file version.");
		}
		
		// Mark the file as available
		fileVersion.setAvailable(true);

		// Create the report reference.
		final ReportReference reportReference = new ReportReference(fileVersion);
		reportReference.setId(fileVersion.getId());
		reportReference.setName(view.getFileUploadButtonField().getValue());
		reportReference.setLastEditDate(new Date());
		reportReference.setEditorName(auth().getUserShortName());
		reportReference.setFlexibleElementLabel((String)view.getElementField().getValue());
		reportReference.setPhaseName((String)view.getPhaseField().getValue());

		N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.flexibleElementFilesListUploadFileConfirm());

		if (point != null) {

			// Adds the monitored point.
			if (Log.isDebugEnabled()) {
				Log.debug("Adds a monitored point '" + point.getLabel() + "' to container with id #" + containerId + ".");
			}

			N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.monitoredPointAddConfirm());

			// TODO (v1.3) orgUnit.addMonitoredPoint(point);
			// TODO (v2.0) project.addMonitoredPoint(point); // Still necessary?
		}

		// Sends an event to update reports store.
		eventBus.fireEvent(new UpdateEvent(UpdateEvent.REPORT_DOCUMENTS_UPDATE, reportReference));

		hideView();
	}

	/**
	 * Callback executed on complete event (after failure or success).
	 */
	private void onComplete() {

		// Loading state update.
		view.setLoading(false);

		// Reset upload fields.
		view.getFileUploadButtonField().reset();
	}

}
