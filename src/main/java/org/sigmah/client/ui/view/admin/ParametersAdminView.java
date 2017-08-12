package org.sigmah.client.ui.view.admin;

import java.util.Arrays;
import java.util.Date;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.ParametersAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.BackupStatusWidget;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.password.ExpirationPolicy;
import org.sigmah.shared.dto.value.FileDTO.LoadingScope;
import org.sigmah.shared.util.ExportUtils.ExportFormat;
import org.sigmah.shared.util.Pair;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;

/**
 * {@link ParametersAdminPresenter}'s view implementation.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ParametersAdminView extends AbstractView implements ParametersAdminPresenter.View {

	// CSS styles.
	private static final String CSS_ADMIN_PARAMETERS = "admin-parameters";
	private static final String CSS_PREVIEW_WRAPPER = "preview-wrapper";
	private static final int LOGO_IMAGE_HEIGHT = 150;

	// General parameters.
	private FormPanel generalForm;
	private TextField<String> generalOrganizationNameTextField;
	private FileUploadField generalLogoFileField;
	private Image generalLogoPreview;
	private Button generalSaveButton;

	// Backup.
	private FormPanel backupManagementForm;
	private RadioGroup backupManagementRadioGroup;
	private Radio backupManagementAllVersionsRadio;
	private Radio backupManagementLastVersionRadio;
	private ComboBox<OrgUnitDTO> backupManagementOrgUnitsComboBox;
	private BackupStatusWidget backupManagementStatus;
	private Button backupManagementSaveButton;

	// Export.
	private FormPanel exportManagementForm;
	private RadioGroup exportManagementRadioGroup;
	private Radio exportManagementOdsRadio;
	private Radio exportManagementXlsRadio;
	private Button exportManagementSaveButton;

	// Export.
	private FormPanel passwordExpirationManagementForm;
	private CheckBox resetNewUserPasswordCheckBox;
	private SimpleComboBox<ExpirationPolicy> policyTypeCombo;
	private SpinnerField frequencyField;
	private DateField scheduledDateField;
	private Button passwordExpirationSaveButton;

	//Solr settings
	private FormPanel solrSettingsForm;
	private TextField<String> solrCoreUrlTextField;
	private Button solrSaveConfigButton;
	private Button manualIndexButton;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		final LayoutContainer topContainer = Layouts.hBox();
		topContainer.add(createGeneralParametersPanel(), Layouts.hBoxData(Margin.HALF_RIGHT));
		topContainer.add(createBackupParametersPanel(), Layouts.hBoxData(Margin.HALF_LEFT));

		// final LayoutContainer bottomContainer = Layouts.hBox();
		// bottomContainer.add(createExportManagementPanel(),
		// Layouts.hBoxData(Margin.HALF_RIGHT));
		// bottomContainer.add(createPasswordExpirationManagementPanel(),
		// Layouts.hBoxData(Margin.HALF_LEFT));

		add(topContainer, Layouts.borderLayoutData(LayoutRegion.NORTH, 0.5f, Margin.HALF_BOTTOM));

		final LayoutContainer bottomWestContainer = Layouts.hBox();
		bottomWestContainer.add(createExportManagementPanel(), Layouts.hBoxData(Margin.HALF_RIGHT));
		final LayoutContainer bottomEastContainer = Layouts.hBox();
		bottomEastContainer.add(createSolrSettingsPanel(), Layouts.hBoxData(Margin.HALF_LEFT));

		add(bottomWestContainer, Layouts.borderLayoutData(LayoutRegion.WEST, 0.5f, Margin.HALF_TOP));
		add(bottomEastContainer, Layouts.borderLayoutData(LayoutRegion.EAST, 0.5f, Margin.HALF_TOP));

	}

	/**
	 * Creates the general parameters panel.
	 * 
	 * @return The general parameters panel.
	 */
	private ContentPanel createGeneralParametersPanel() {

		final ContentPanel panel = Panels.content(I18N.CONSTANTS.organizationManagementTitle(), CSS_ADMIN_PARAMETERS);

		generalForm = Forms.panel(150);

		generalOrganizationNameTextField = Forms.text(I18N.CONSTANTS.organizationManagementOrganizationName(), true);
		generalForm.add(generalOrganizationNameTextField);

		generalLogoFileField = Forms.upload(I18N.CONSTANTS.organizationManagementLogoUpload());
		generalForm.add(generalLogoFileField);

		generalLogoPreview = new Image();
		final SimplePanel previewWrapper = new SimplePanel(generalLogoPreview);
		previewWrapper.setStyleName(CSS_PREVIEW_WRAPPER);
		final AdapterField logoImageField = new AdapterField(previewWrapper);
		logoImageField.setFieldLabel(I18N.CONSTANTS.organizationManagementActualLogo());
		logoImageField.setResizeWidget(false);
		generalForm.add(logoImageField);

		generalLogoPreview.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(final LoadEvent event) {

				final Pair<Integer, Integer> ratio = ClientUtils.ratio(generalLogoPreview.getWidth(),
						generalLogoPreview.getHeight(), generalForm.getFieldWidth(), LOGO_IMAGE_HEIGHT);

				generalLogoPreview.setPixelSize(ratio.left, ratio.right);
			}
		});

		generalSaveButton = Forms.button(I18N.CONSTANTS.organizationManagementSaveChanges(),
				IconImageBundle.ICONS.save());
		generalForm.addButton(generalSaveButton);

		panel.add(generalForm);

		return panel;
	}

	/**
	 * Creates the backup parameters panel.
	 * 
	 * @return The backup parameters panel.
	 */
	private ContentPanel createBackupParametersPanel() {

		final ContentPanel panel = Panels.content(I18N.CONSTANTS.backupManagementTitle());

		backupManagementForm = Forms.panel(300);

		backupManagementAllVersionsRadio = Forms.radio(I18N.CONSTANTS.backupManagementAllVersion());
		backupManagementLastVersionRadio = Forms.radio(I18N.CONSTANTS.backupManagementOneVersion());

		backupManagementRadioGroup = Forms.radioGroup(I18N.CONSTANTS.backupManagementDownload(), Orientation.VERTICAL,
				backupManagementAllVersionsRadio, backupManagementLastVersionRadio);
		backupManagementForm.add(backupManagementRadioGroup);

		backupManagementOrgUnitsComboBox = Forms.combobox(I18N.CONSTANTS.backupManagementRootOrganization(), true,
				OrgUnitDTO.ID, OrgUnitDTO.FULL_NAME, new ListStore<OrgUnitDTO>());
		backupManagementForm.add(backupManagementOrgUnitsComboBox);

		backupManagementStatus = new BackupStatusWidget();
		backupManagementForm
				.add(Forms.adapter(I18N.CONSTANTS.backupManagement_status_formLabel(), backupManagementStatus));

		backupManagementSaveButton = Forms.button(I18N.CONSTANTS.backupManagementBackupAllFiles(),
				IconImageBundle.ICONS.save());
		backupManagementForm.addButton(backupManagementSaveButton);

		panel.add(backupManagementForm);

		return panel;
	}

	/**
	 * Creates the export management panel.
	 * 
	 * @return The export management panel.
	 */
	private ContentPanel createExportManagementPanel() {

		final ContentPanel panel = Panels.content(I18N.CONSTANTS.defaultExportFormat());

		exportManagementForm = Forms.panel(200);

		// File format.
		exportManagementOdsRadio = Forms.radio(I18N.CONSTANTS.openDocumentSpreadsheet());
		exportManagementXlsRadio = Forms.radio(I18N.CONSTANTS.msExcel());

		exportManagementRadioGroup = Forms.radioGroup(I18N.CONSTANTS.chooseFileType(), Orientation.VERTICAL,
				exportManagementOdsRadio, exportManagementXlsRadio);
		exportManagementForm.add(exportManagementRadioGroup);

		// button
		exportManagementSaveButton = Forms.button(I18N.CONSTANTS.saveExportConfiguration(),
				IconImageBundle.ICONS.save());
		exportManagementForm.addButton(exportManagementSaveButton);

		panel.add(exportManagementForm);

		return panel;
	}

	/**
	 * Creates the solr settings management panel.
	 * 
	 * @return The solr settings management panel.
	 */
	private ContentPanel createSolrSettingsPanel() {

		final ContentPanel panel = Panels.content("Solr Search Settings", CSS_ADMIN_PARAMETERS);

		solrSettingsForm = Forms.panel(150);

		solrCoreUrlTextField = Forms.text("Solr Core Url", true);
		solrSettingsForm.add(solrCoreUrlTextField);

		solrSettingsForm.add(new Html("<br><br>"));
		manualIndexButton = Forms.button("Manual Index");

		FlexTable table = new FlexTable();
		
		table.setWidth("100%");
		
		table.setHTML(0, 0, "&nbsp;");
		
		Label l = new Label("Perform manual Solr indexing, consisting of Import of Data and Files :");
		l.getElement().setAttribute("font-family","tahoma");
		l.getElement().setAttribute("font-size","12px");
		table.setWidget(1, 0, l);
		table.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
		
		table.setHTML(2, 0, "&nbsp;");
		
		table.setWidget(3, 0, manualIndexButton);
		table.getCellFormatter().setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER);
		
		solrSettingsForm.add(table);
		
		solrSaveConfigButton = Forms.button(I18N.CONSTANTS.organizationManagementSaveChanges(),
				IconImageBundle.ICONS.save());
		solrSettingsForm.addButton(solrSaveConfigButton);

		panel.add(solrSettingsForm);

		return panel;
	}

	/**
	 * Creates the password expiration policy management panel.
	 * 
	 * @return The password expiration policy management panel.
	 */
	private ContentPanel createPasswordExpirationManagementPanel() {

		final ContentPanel panel = Panels.content(I18N.CONSTANTS.userPasswordSettings());

		passwordExpirationManagementForm = Forms.panel(300);

		resetNewUserPasswordCheckBox = Forms.checkbox(I18N.CONSTANTS.resetNewUserPasswords());
		resetNewUserPasswordCheckBox.setFieldLabel(I18N.CONSTANTS.resetNewUserPasswords());

		policyTypeCombo = new SimpleComboBox<ExpirationPolicy>();
		policyTypeCombo.add(Arrays.asList(ExpirationPolicy.values()));
		policyTypeCombo.setTriggerAction(ComboBox.TriggerAction.ALL);
		policyTypeCombo.setEditable(false);
		policyTypeCombo.setAllowBlank(false);
		policyTypeCombo.setFieldLabel(I18N.CONSTANTS.automaticExpirationPolicy());

		frequencyField = new SpinnerField();
		frequencyField.setMinValue(0);
		frequencyField.setValue(0);
		frequencyField.setWidth(40);
		frequencyField.setFormat(NumberFormat.getFormat("0"));
		frequencyField.setIncrement(1);
		frequencyField.setFieldLabel(I18N.CONSTANTS.every());

		scheduledDateField = new DateField();
		scheduledDateField.setMinValue(new Date());
		scheduledDateField.setFieldLabel(I18N.CONSTANTS.at());

		passwordExpirationManagementForm.add(resetNewUserPasswordCheckBox);
		passwordExpirationManagementForm.add(policyTypeCombo);
		passwordExpirationManagementForm.add(frequencyField);
		passwordExpirationManagementForm.add(scheduledDateField);

		// button
		passwordExpirationSaveButton = Forms.button(I18N.CONSTANTS.saveExportConfiguration(),
				IconImageBundle.ICONS.save());
		passwordExpirationManagementForm.addButton(passwordExpirationSaveButton);

		panel.add(passwordExpirationManagementForm);

		return panel;
	}

	// --
	// General.
	// --

	@Override
	public FormPanel getGeneralParametersForm() {
		return generalForm;
	}

	@Override
	public TextField<String> getOrganizationNameTextField() {
		return generalOrganizationNameTextField;
	}

	@Override
	public FileUploadField getLogoFileField() {
		return generalLogoFileField;
	}

	@Override
	public Image getLogoPreview() {
		return generalLogoPreview;
	}

	@Override
	public Button getGeneralParametersSaveButton() {
		return generalSaveButton;
	}

	// --
	// Backup.
	// --

	@Override
	public FormPanel getBackupForm() {
		return backupManagementForm;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectedBackupDownloadFormat(final LoadingScope downloadType) {

		backupManagementAllVersionsRadio.setValue(null);
		backupManagementLastVersionRadio.setValue(null);

		if (downloadType == null) {
			return;
		}

		switch (downloadType) {
		case ALL_VERSIONS:
			backupManagementAllVersionsRadio.setValue(true);
			break;

		case LAST_VERSION:
			backupManagementLastVersionRadio.setValue(true);
			break;

		default:
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoadingScope getSelectedBackupDownloadType() {

		if (backupManagementAllVersionsRadio.equals(backupManagementRadioGroup.getValue())) {
			return LoadingScope.ALL_VERSIONS;

		} else if (backupManagementLastVersionRadio.equals(backupManagementRadioGroup.getValue())) {
			return LoadingScope.LAST_VERSION;

		} else {
			return null;
		}
	}

	@Override
	public ComboBox<OrgUnitDTO> getBackupManagementOrgUnitsComboBox() {
		return backupManagementOrgUnitsComboBox;
	}

	@Override
	public Button getBackupSaveButton() {
		return backupManagementSaveButton;
	}

	@Override
	public BackupStatusWidget getBackupStatus() {
		return backupManagementStatus;
	}

	// --
	// Export.
	// --

	@Override
	public FormPanel getExportManagementForm() {
		return exportManagementForm;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectedExportFormat(final ExportFormat exportFormat) {

		exportManagementXlsRadio.setValue(null);
		exportManagementOdsRadio.setValue(null);

		if (exportFormat == null) {
			return;
		}

		switch (exportFormat) {
		case XLS:
			exportManagementXlsRadio.setValue(true);
			break;

		case ODS:
			exportManagementOdsRadio.setValue(true);
			break;

		default:
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExportFormat getSelectedExportFormat() {

		if (exportManagementOdsRadio.equals(exportManagementRadioGroup.getValue())) {
			return ExportFormat.ODS;

		} else if (exportManagementXlsRadio.equals(exportManagementRadioGroup.getValue())) {
			return ExportFormat.XLS;

		} else {
			return null;
		}
	}

	@Override
	public Button getExportManagementSaveButton() {
		return exportManagementSaveButton;
	}

	@Override
	public TextField<String> getSolrCoreUrlTextField() {
		return solrCoreUrlTextField;
	}

	public void setSolrCoreUrlTextField(TextField<String> solrCoreUrlTextField) {
		this.solrCoreUrlTextField = solrCoreUrlTextField;
	}

	@Override
	public Button getSolrSaveConfigButton() {
		return solrSaveConfigButton;
	}

	public void setSolrSaveConfigButton(Button solrSaveConfigButton) {
		this.solrSaveConfigButton = solrSaveConfigButton;
	}

	@Override
	public Button getManualIndexButton() {
		return manualIndexButton;
	}

	public void setManualIndexButton(Button manualIndexButton) {
		this.manualIndexButton = manualIndexButton;
	}

	public FormPanel getSolrSettingsForm() {
		return solrSettingsForm;
	}

	public void setSolrSettingsForm(FormPanel solrSettingsForm) {
		this.solrSettingsForm = solrSettingsForm;
	}

}
