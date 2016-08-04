package org.sigmah.client.ui.presenter.admin;

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

import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.HasForm;
import org.sigmah.client.ui.view.admin.ParametersAdminView;
import org.sigmah.client.ui.widget.BackupStatusWidget;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.BackupArchiveManagementCommand;
import org.sigmah.shared.command.GetGlobalExportSettings;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.UpdateGlobalExportSettingsCommand;
import org.sigmah.shared.command.UpdateOrganization;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.BackupDTO;
import org.sigmah.shared.dto.GlobalExportSettingsDTO;
import org.sigmah.shared.dto.organization.OrganizationDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.value.FileDTO.LoadingScope;
import org.sigmah.shared.servlet.ServletConstants;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletRequestBuilder;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.util.ExportUtils.ExportFormat;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Admin Parameters Presenter which manages {@link ParametersAdminView}.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ParametersAdminPresenter extends AbstractAdminPresenter<ParametersAdminPresenter.View> implements HasForm {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ParametersAdminView.class)
	public static interface View extends AbstractAdminPresenter.View {

		// --
		// General parameters.
		// --

		FormPanel getGeneralParametersForm();

		TextField<String> getOrganizationNameTextField();

		FileUploadField getLogoFileField();

		Image getLogoPreview();

		Button getGeneralParametersSaveButton();

		// --
		// Backup.
		// --

		FormPanel getBackupForm();

		/**
		 * Sets the selected backup download type.
		 * 
		 * @param downloadType
		 *          The backup download type.
		 */
		void setSelectedBackupDownloadFormat(LoadingScope downloadType);

		/**
		 * Returns the selected backup download type.
		 * 
		 * @return The selected backup download type, or {@code null} if no option is selected. </ul>
		 */
		LoadingScope getSelectedBackupDownloadType();

		ComboBox<OrgUnitDTO> getBackupManagementOrgUnitsComboBox();

		Button getBackupSaveButton();

		BackupStatusWidget getBackupStatus();

		// --
		// Export management.
		// --

		FormPanel getExportManagementForm();

		/**
		 * Sets the selected export management format.
		 * 
		 * @param exportFormat
		 *          The export management format:
		 *          <ul>
		 *          <li>{@link ExportFormat#XLS}</li>
		 *          <li>{@link ExportFormat#ODS}</li>
		 *          </ul>
		 */
		void setSelectedExportFormat(ExportFormat exportFormat);

		/**
		 * Returns the selected export management format.
		 * 
		 * @return The selected export management format:
		 *         <ul>
		 *         <li>{@link ExportFormat#XLS}</li>
		 *         <li>{@link ExportFormat#ODS}</li>
		 *         <li>{@code null} if no option is selected.</li>
		 *         </ul>
		 */
		ExportFormat getSelectedExportFormat();

		Button getExportManagementSaveButton();

	}

	/**
	 * Backup status update timer (in ms).
	 */
	private static final int BACKUP_STATUS_UPDATE_TIMER_DELAY = 5000;

	/**
	 * Timer used to regularly update the backup status once a backup process has been started.
	 */
	private Timer updateBackupStatusTimer;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected ParametersAdminPresenter(View view, Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_PARAMETERS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// General parameters handlers.
		// --

		// Logo form submit handler.
		view.getGeneralParametersForm().addListener(Events.Submit, new Listener<FormEvent>() {

			@Override
			public void handleEvent(final FormEvent be) {

				final String newLogoFileName = ClientUtils.deletePreTags(be.getResultHtml());

				if (ServletConstants.isErrorResponse(newLogoFileName)) {
					if (Log.isErrorEnabled()) {
						Log.error("Upload process failed. Received error code '" + newLogoFileName + "'.");
					}
					view.getGeneralParametersSaveButton().setLoading(false);
					throw new IllegalStateException();
				}

				// Updates logo preview with uploaded file.
				displayLogo(newLogoFileName);

				N10N.validNotif(I18N.CONSTANTS.organizationManagementLogoNotificationTitle(), I18N.CONSTANTS.organizationManagementLogoNotificationMessage());

				// Launches general form save (for organization name).
				saveGeneralParametersForm(newLogoFileName);
			}
		});

		// Save button handler.
		view.getGeneralParametersSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {

				if (ClientUtils.isBlank(view.getOrganizationNameTextField().getValue())) {
					N10N.warn(I18N.CONSTANTS.organizationManagementBlankNameNotificationError());
					return;
				}

				final String logoFileName = view.getLogoFileField().getValue();
				view.getGeneralParametersSaveButton().setLoading(true);

				if (ClientUtils.isNotBlank(logoFileName)) {
					// Submit logo file upload form.
					final ServletUrlBuilder urlBuilder =
							new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), Servlet.FILE, ServletMethod.UPLOAD_ORGANIZATION_LOGO);
					urlBuilder.addParameter(RequestParameter.ID, auth().getOrganizationId());

					view.getGeneralParametersForm().setAction(urlBuilder.toString());
					view.getGeneralParametersForm().setEncoding(Encoding.MULTIPART);
					view.getGeneralParametersForm().setMethod(Method.POST);

					view.getGeneralParametersForm().submit();

				} else {
					// Directly executed action to update org name.
					saveGeneralParametersForm(auth().getOrganizationLogo());
				}
			}
		});

		// --
		// Backup files handlers.
		// --

		// Org unit models store listeners.
		view.getBackupManagementOrgUnitsComboBox().getStore().addStoreListener(new StoreListener<OrgUnitDTO>() {

			@Override
			public void storeClear(final StoreEvent<OrgUnitDTO> se) {
				view.getBackupManagementOrgUnitsComboBox().setEnabled(false);
			}

			@Override
			public void storeAdd(final StoreEvent<OrgUnitDTO> se) {
				view.getBackupManagementOrgUnitsComboBox().setEnabled(true);
			}

		});

		view.getBackupSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {

				saveBackupFilesForm();

			}
		});

		updateBackupStatusTimer = new Timer() {

			@Override
			public void run() {
				updateBackupStatus(false);
			}
		};

		// --
		// Export management handlers.
		// --

		view.getExportManagementSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {

				saveExportManagementForm();

			}
		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// 'General Parameters' form reset is processed in 'onViewRevealed()' due to 'FileUploadField' constraints.
		view.getBackupForm().reset();
		view.getBackupForm().clearAll();
		view.getExportManagementForm().reset();
		view.getExportManagementForm().clearAll();

		// Loads organization data.
		view.getOrganizationNameTextField().setValue(auth().getOrganizationName());
		displayLogo(auth().getOrganizationLogo());

		// Retrieves OrgUnits and populates store.
		dispatch.execute(new GetOrgUnits(OrgUnitDTO.Mode.WITH_TREE), new CommandResultHandler<ListResult<OrgUnitDTO>>() {

			@Override
			public void onCommandSuccess(final ListResult<OrgUnitDTO> result) {
				for (OrgUnitDTO orgUnitDTO : result.getData()) {
					// Recursily fills the store.
					fillOrgUnitsCombobox(orgUnitDTO, view.getBackupManagementOrgUnitsComboBox().getStore());
				}
			}

		});

		// Updates backup status.
		updateBackupStatus(true);
		updateBackupStatusTimer.scheduleRepeating(BACKUP_STATUS_UPDATE_TIMER_DELAY);

		// Retrieves configured export format.
		dispatch.execute(new GetGlobalExportSettings(auth().getOrganizationId(), false), new CommandResultHandler<GlobalExportSettingsDTO>() {

			@Override
			public void onCommandSuccess(final GlobalExportSettingsDTO result) {
				view.setSelectedExportFormat(result.getDefaultOrganizationExportFormat());
				view.getExportManagementForm().resetValueHasChanged();
			}

		}, view.getExportManagementSaveButton());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onViewRevealed() {
		// 'FileUploadField' reset needs to be processed once view has been rendered.
		view.getLogoFileField().reset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel[] getForms() {
		// Backup form does not not manage data to be saved.
		return new FormPanel[] {
														view.getGeneralParametersForm(),
														view.getExportManagementForm()
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onLeavingOk() {
		updateBackupStatusTimer.cancel();
	}

	// -------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------------

	/**
	 * Function to display the organization logo
	 * 
	 * @param organisationLogo
	 *          File id of the logo
	 */
	private void displayLogo(final String organisationLogo) {

		final ServletRequestBuilder builder = new ServletRequestBuilder(injector, RequestBuilder.GET, Servlet.FILE, ServletMethod.DOWNLOAD_LOGO);
		builder.addParameter(RequestParameter.ID, organisationLogo);

		builder.send(new ServletRequestBuilder.RequestCallbackAdapter() {

			@Override
			public void onResponseReceived(final Request request, final Response response) {

				final String logoUrl;
				if (response.getStatusCode() == Response.SC_OK) {
					// Existing logo.
					logoUrl = builder.toString();

				} else {
					// Non existing logo.
					logoUrl = ""; // Cannot be null.
				}

				view.getLogoPreview().setUrl(logoUrl);
			}
		});
	}

	/**
	 * Updates the backup status.
	 * 
	 * @param saveButtonLoading
	 *          {@code true} to set the save button in loading mode during update, {@code false} to ignore it.
	 */
	private void updateBackupStatus(final boolean saveButtonLoading) {

		dispatch.execute(new BackupArchiveManagementCommand(auth().getOrganizationId()), new CommandResultHandler<BackupDTO>() {

			@Override
			public void onCommandSuccess(final BackupDTO result) {
				view.getBackupStatus().update(result, new ClickHandler() {

					@Override
					public void onClick(final ClickEvent event) {

						final ServletUrlBuilder builder =
								new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), Servlet.FILE, ServletMethod.DOWNLOAD_ARCHIVE);
						builder.addParameter(RequestParameter.ID, result.getArchiveFileName());

						ClientUtils.launchDownload(builder.toString());
					}
				});
			}

		}, saveButtonLoading ? view.getBackupSaveButton() : null);
	}

	/**
	 * Fills combobox with given the children of the given root org units.
	 * 
	 * @param unit
	 *          The root org unit.
	 * @param store
	 *          The field store.
	 */
	private static void fillOrgUnitsCombobox(final OrgUnitDTO unit, final ListStore<OrgUnitDTO> store) {

		store.add(unit);

		final Set<OrgUnitDTO> children = unit.getChildrenOrgUnits();
		if (ClientUtils.isNotEmpty(children)) {
			for (final OrgUnitDTO child : children) {
				fillOrgUnitsCombobox(child, store);
			}
		}
	}

	/**
	 * Validates and saves the general parameters form.
	 * 
	 * @param organizationLogoFileName
	 *          The organization logo file name (should never be {@code null}).
	 */
	private void saveGeneralParametersForm(final String organizationLogoFileName) {

		if (!view.getGeneralParametersForm().isValid()) {
			view.getGeneralParametersSaveButton().setLoading(false);
			return;
		}

		final OrganizationDTO organizationDTO = new OrganizationDTO();
		organizationDTO.setId(auth().getOrganizationId());
		organizationDTO.setName(view.getOrganizationNameTextField().getValue());
		organizationDTO.setLogo(organizationLogoFileName);

		// Saves new organization name.
		dispatch.execute(new UpdateOrganization(organizationDTO), new CommandResultHandler<OrganizationDTO>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				N10N.error(I18N.CONSTANTS.organizationManagementWebServiceNotificationError());
			}

			@Override
			public void onCommandSuccess(final OrganizationDTO result) {

				N10N.validNotif(I18N.CONSTANTS.organizationManagementSaveChangesNotificationTitle(),
					I18N.CONSTANTS.organizationManagementSaveChangesNotificationMessage());

				view.getGeneralParametersForm().resetValueHasChanged();

			}
		}, view.getGeneralParametersSaveButton());
	}

	/**
	 * Validates and saves the backup files form.
	 */
	private void saveBackupFilesForm() {

		if (!view.getBackupForm().isValid()) {
			return;
		}

		if (view.getSelectedBackupDownloadType() == null || view.getBackupManagementOrgUnitsComboBox().getValue() == null) {
			N10N.warn(I18N.CONSTANTS.form_validation_ko());
			return;
		}

		final BackupDTO backupConf = new BackupDTO();
		backupConf.setOrganizationId(auth().getOrganizationId());
		backupConf.setOrgUnitId(view.getBackupManagementOrgUnitsComboBox().getValue().getId());
		backupConf.setLoadingScope(view.getSelectedBackupDownloadType());

		dispatch.execute(new BackupArchiveManagementCommand(backupConf), new CommandResultHandler<BackupDTO>() {

			@Override
			public void onCommandSuccess(final BackupDTO result) {

				if (result == null) {
					N10N.valid(I18N.CONSTANTS.backupManagement_process_started());

				} else {
					N10N.warn(I18N.CONSTANTS.backupManagement_process_alreadyRunning());
				}

				updateBackupStatus(true);
			}

		}, view.getBackupSaveButton());
	}

	/**
	 * Validates and saves the export management form.
	 */
	private void saveExportManagementForm() {

		if (!view.getExportManagementForm().isValid()) {
			return;
		}

		final UpdateGlobalExportSettingsCommand command = new UpdateGlobalExportSettingsCommand();
		command.setUpdateDefaultExportFormat(true);
		command.setDefaultOrganizationExportFormat(view.getSelectedExportFormat());
		command.setOrganizationId(auth().getOrganizationId());

		dispatch.execute(command, new CommandResultHandler<VoidResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.warn(I18N.CONSTANTS.saveExportConfiguration(), I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.defaultExportFormat()));
			}

			@Override
			public void onCommandSuccess(final VoidResult result) {
				N10N.validNotif(I18N.CONSTANTS.exportManagementSaveChangesNotificationTitle(), I18N.CONSTANTS.exportManagementSaveChangesNotificationMessage());
				view.getExportManagementForm().resetValueHasChanged();
			}

		}, view.getExportManagementSaveButton());
	}

}
