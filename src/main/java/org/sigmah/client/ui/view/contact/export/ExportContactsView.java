package org.sigmah.client.ui.view.contact.export;

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

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.inject.Singleton;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.contact.export.ExportContactsPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.dto.GlobalContactExportDTO;
import org.sigmah.shared.dto.GlobalExportDTO;

@Singleton
public class ExportContactsView extends AbstractPopupView<PopupWidget> implements ExportContactsPresenter.View {

	private Button settingsButton;
	private Button exportButton;
	private Button searchButton;

	private DateField fromDate;
	private DateField toDate;

	private ListStore<GlobalContactExportDTO> periodsStore;
	private ComboBox<GlobalContactExportDTO> periods;

	private Radio liveChoice;
	private Radio backupChoice;

	public ExportContactsView() {
		super(new PopupWidget(true), 550);
	}

	@Override
	public void initialize() {

		final FormPanel panel = Forms.panel(I18N.CONSTANTS.GLOBAL_EXPORT());
		panel.setHeaderVisible(false);
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(220);
		panel.setLayout(layout);
		panel.setScrollMode(Scroll.AUTOY);

		// version
		liveChoice = new Radio();
		liveChoice.setBoxLabel(I18N.CONSTANTS.liveData());
		liveChoice.setName("version");
		liveChoice.setValue(true);

		backupChoice = new Radio();
		backupChoice.setBoxLabel(I18N.CONSTANTS.backedUpData());
		backupChoice.setName("version");

		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setOrientation(Orientation.VERTICAL);
		radioGroup.setFieldLabel(I18N.CONSTANTS.versionOfDataToExport());
		radioGroup.add(liveChoice);
		radioGroup.add(backupChoice);
		panel.add(radioGroup);

		// period
		fromDate = getDateField();
		toDate = getDateField();
		toDate.setValue(new Date());
		searchButton = new Button(I18N.CONSTANTS.search());

		periodsStore = new ListStore<GlobalContactExportDTO>();
		periods = new ComboBox<GlobalContactExportDTO>();
		periods.setWidth(180);
		periods.setStore(periodsStore);
		periods.setDisplayField("date");
		periods.setValueField("id");
		periods.setEditable(false);
		periods.setTriggerAction(TriggerAction.ALL);
		periods.setHideLabel(false);
		periods.setEnabled(false);

		final FlexTable periodTable = new FlexTable();
		periodTable.setHTML(0, 0, "<b>" + I18N.CONSTANTS.exportBackSelection() + "</b>");
		periodTable.getFlexCellFormatter().setWidth(0, 0, "220px");
		periodTable.setHTML(1, 0, I18N.CONSTANTS.specifyPeriodForBackup());
		periodTable.setWidget(1, 1, fromDate);
		periodTable.setWidget(1, 2, new LabelField(" -"));
		periodTable.setWidget(1, 3, toDate);
		periodTable.setWidget(1, 4, searchButton);
		periodTable.getFlexCellFormatter().setHeight(1, 0, "30px");

		periodTable.setHTML(2, 0, I18N.CONSTANTS.selectBackupToExport());
		periodTable.setWidget(2, 1, periods);
		periodTable.getFlexCellFormatter().setColSpan(2, 1, 4);
		panel.add(periodTable);
		periodTable.setVisible(false);

		backupChoice.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent fe) {
				periods.setAllowBlank(false);
				periodTable.setVisible(true);

			}
		});

		liveChoice.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent fe) {
				fromDate.clear();
				toDate.clear();
				periods.setAllowBlank(true);
				periods.clear();
				periodTable.setVisible(false);
			}
		});

		exportButton = new Button(I18N.CONSTANTS.export());
		settingsButton = new Button(I18N.CONSTANTS.changeConfiguration());
		panel.getButtonBar().add(exportButton);
		panel.getButtonBar().add(settingsButton);

		initPopup(panel);

	}

	private DateField getDateField() {
		final DateTimeFormat DATE_FORMAT = DateUtils.DATE_SHORT;
		final DateField dateField = new DateField();
		dateField.setWidth(85);
		dateField.getPropertyEditor().setFormat(DATE_FORMAT);
		dateField.setAllowBlank(false);
		return dateField;
	}

	@Override
	public Button getSettingsButton() {
		return settingsButton;
	}

	@Override
	public Button getExportButton() {
		return exportButton;
	}

	@Override
	public Button getSearchButton() {
		return searchButton;
	}

	@Override
	public DateField getFromDate() {
		return fromDate;
	}

	@Override
	public DateField getToDate() {
		return toDate;
	}

	@Override
	public ListStore<GlobalContactExportDTO> getPeriodsStore() {
		return periodsStore;
	}

	@Override
	public ComboBox<GlobalContactExportDTO> getPeriods() {
		return periods;
	}

	@Override
	public Radio getLiveChoice() {
		return liveChoice;
	}

	@Override
	public Radio getBackupChoice() {
		return backupChoice;
	}

}
