package org.sigmah.client.ui.presenter.project.export;

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

import java.util.List;
import java.util.Map;

import org.sigmah.client.ClientFactory;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.view.project.export.ExportProjectsSettingView;
import org.sigmah.client.ui.widget.SimpleComboBoxData;
import org.sigmah.shared.command.GetGlobalExportSettings;
import org.sigmah.shared.command.UpdateGlobalExportSettingsCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.GlobalExportSettingsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.util.ExportUtils;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
public class ExportProjectsSettingPresenter extends AbstractPagePresenter<ExportProjectsSettingPresenter.View> {

	public static interface View extends ViewPopupInterface {

		ListStore<FlexibleElementDTO> getFieldsStore();

		ListStore<ProjectModelDTO> getModelsStore();

		Map<Integer, Boolean> getFieldsMap();

		List<SimpleComboBoxData> getAutoExportSchedules();

		List<SimpleComboBoxData> getAutoDeleteSchedules();

		List<SimpleComboBoxData> getAutoExportMonthlySchedules();

		List<SimpleComboBoxData> getAutoExportWeeklySchedules();

		ListStore<SimpleComboBoxData> getDeleteScheduleStore();

		ComboBox<SimpleComboBoxData> getDeleteSchedulesBox();

		FormPanel getPanel();

		Radio getCalcChoice();

		Radio getExcelChoice();

		Button getSaveButton();

		ListStore<SimpleComboBoxData> getExportScheduleStore();

		ComboBox<SimpleComboBoxData> getExportSchedulesBox();

		ListStore<SimpleComboBoxData> getExportMonthlyScheduleStore();

		ComboBox<SimpleComboBoxData> getExportMonthlySchedulesBox();

		ListStore<SimpleComboBoxData> getExportWeeklyScheduleStore();

		ComboBox<SimpleComboBoxData> getExportWeeklySchedulesBox();

		void clearFrom();

	}

	public ExportProjectsSettingPresenter(View view, ClientFactory factory) {
		super(view, factory);
	}

	@Override
	public void onBind() {
		saveButtonListener();
	}

	@Override
	public Page getPage() {
		return Page.PROJECT_EXPORTS_SETTING;
	}

	@Override
	public void onPageRequest(PageRequest request) {

		view.clearFrom();

		loadElement();

		setPageTitle(I18N.CONSTANTS.globalProjectsExportConfiguration());
	}

	private void loadElement() {

		GetGlobalExportSettings settingsCommand = new GetGlobalExportSettings(auth().getOrganizationId(), true);

		dispatch.execute(settingsCommand, new CommandResultHandler<GlobalExportSettingsDTO>() {

			@Override
			public void onCommandFailure(Throwable caught) {
				N10N.warn(I18N.CONSTANTS.globalProjectsExportConfiguration(), I18N.CONSTANTS.serverError());
			}

			@Override
			public void onCommandSuccess(GlobalExportSettingsDTO result) {
				// set export format
				if (result.getExportFormat() != null) {

					switch (result.getExportFormat()) {
						case XLS:
							view.getExcelChoice().setValue(true);
							break;

						case ODS:
							view.getCalcChoice().setValue(true);
							break;
					}

				}

				// set pmodels
				view.getModelsStore().add(result.getProjectModelsDTO());
				view.getModelsStore().commitChanges();

				// auto export schdule
				if (((ExportProjectsSettingView) view).exportScheduleMap.get(result.getAutoExportFrequency()) != null
					|| ((ExportProjectsSettingView) view).monthScheduleMap.get(result.getAutoExportFrequency()) != null
					|| ((ExportProjectsSettingView) view).weekScheduleMap.get(result.getAutoExportFrequency()) != null) {

					if (result.getAutoExportFrequency() >= 31 && result.getAutoExportFrequency() <= 58) {// Case of Monthly Update
						view.getExportSchedulesBox().setValue(((ExportProjectsSettingView) view).exportScheduleMap.get(31));
						view.getExportMonthlySchedulesBox().show();

						view.getExportMonthlySchedulesBox().setValue(((ExportProjectsSettingView) view).monthScheduleMap.get(result.getAutoExportFrequency()));
					} else if (result.getAutoExportFrequency() >= 61 && result.getAutoExportFrequency() <= 67) {// Case of Weekly
																																																			// Update
						view.getExportSchedulesBox().setValue(((ExportProjectsSettingView) view).exportScheduleMap.get(61));
						view.getExportWeeklySchedulesBox().show();

						view.getExportWeeklySchedulesBox().setValue(((ExportProjectsSettingView) view).weekScheduleMap.get(result.getAutoExportFrequency()));
					} else {
						// Regular case of every N-days
						view.getExportSchedulesBox().setValue(((ExportProjectsSettingView) view).exportScheduleMap.get(result.getAutoExportFrequency()));
					}

				}

				// auto delete schedule
				if (((ExportProjectsSettingView) view).deleteScheduleMap.get(result.getAutoDeleteFrequency()) != null) {
					view.getDeleteSchedulesBox().setValue(((ExportProjectsSettingView) view).deleteScheduleMap.get(result.getAutoDeleteFrequency()));
				}
			}
		}, new LoadingMask(view.getPanel(), I18N.CONSTANTS.loading()));

	}

	private void saveButtonListener() {

		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				UpdateGlobalExportSettingsCommand settings = new UpdateGlobalExportSettingsCommand(view.getFieldsMap());

				if (view.getExcelChoice().getValue()) {
					settings.setExportFormat(ExportUtils.ExportFormat.XLS);
				} else {
					settings.setExportFormat(ExportUtils.ExportFormat.ODS);
				}
				if (view.getExportSchedulesBox().getValue() == null || view.getExportSchedulesBox().getValue().getValue() == 0) {
					settings.setAutoExportFrequency(null);
				} else {

					if (view.getExportSchedulesBox().getValue().getValue() == 31) { // Case of Monthly Schedule
						// Push the value selected in the Monthly export schedule box
						// Value between 31-38 (both inclusive)
						settings.setAutoExportFrequency(view.getExportMonthlySchedulesBox().getValue().getValue());
					} else if (view.getExportSchedulesBox().getValue().getValue() == 61) { // Case of Weekly Schedule
						// Value between 61-67 (both inclusive)
						// Push the value selected in the Weekly export schedule box
						settings.setAutoExportFrequency(view.getExportWeeklySchedulesBox().getValue().getValue());
					} else {
						// Regular case of every N days
						settings.setAutoExportFrequency(view.getExportSchedulesBox().getValue().getValue());
					}

				}
				if (view.getDeleteSchedulesBox().getValue() == null || view.getDeleteSchedulesBox().getValue().getValue() == 0) {
					settings.setAutoDeleteFrequency(null);
				} else {
					settings.setAutoDeleteFrequency(view.getDeleteSchedulesBox().getValue().getValue());
				}

				settings.setOrganizationId(auth().getOrganizationId());
				dispatch.execute(settings, new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandFailure(Throwable caught) {
						N10N.warn(I18N.CONSTANTS.saveExportConfiguration(), I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.globalProjectsExportConfiguration()));
					}

					@Override
					public void onCommandSuccess(VoidResult result) {
						view.hide();
						N10N.infoNotif(I18N.CONSTANTS.saveExportConfiguration(), I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.globalProjectsExportConfiguration()));
					}
				});

			}
		});
	}

}
