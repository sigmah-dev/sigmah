package org.sigmah.client.ui.view.project.export;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.export.ExportProjectsSettingPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.SimpleComboBoxData;
import org.sigmah.client.ui.widget.ToggleAnchor;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ElementTypeEnum;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
public class ExportProjectsSettingView extends AbstractPopupView<PopupWidget> implements ExportProjectsSettingPresenter.View {

	private FormPanel panel;
	private ListStore<FlexibleElementDTO> fieldsStore;
	private ListStore<ProjectModelDTO> modelsStore;
	private final Map<Integer, Boolean> fieldsMap;
	private ListStore<SimpleComboBoxData> deleteScheduleStore;
	private ComboBox<SimpleComboBoxData> deleteSchedulesBox;
	private List<SimpleComboBoxData> autoExportSchedules;
	private List<SimpleComboBoxData> autoDeleteSchedules;
	private List<SimpleComboBoxData> autoExportMonthlySchedules;
	private List<SimpleComboBoxData> autoExportWeeklySchedules;
	public static Map<Integer, SimpleComboBoxData> exportScheduleMap;
	public static Map<Integer, SimpleComboBoxData> deleteScheduleMap;
	public static Map<Integer, SimpleComboBoxData> monthScheduleMap;
	public static Map<Integer, SimpleComboBoxData> weekScheduleMap;
	private Radio calcChoice;
	private Radio excelChoice;
	private Button saveButton;
	private ListStore<SimpleComboBoxData> exportScheduleStore;
	private ComboBox<SimpleComboBoxData> exportSchedulesBox;
	private ListStore<SimpleComboBoxData> exportMonthlyScheduleStore;
	private ComboBox<SimpleComboBoxData> exportMonthlySchedulesBox;
	private ListStore<SimpleComboBoxData> exportWeeklyScheduleStore;
	private ComboBox<SimpleComboBoxData> exportWeeklySchedulesBox;

	public ExportProjectsSettingView() {
		super(new PopupWidget(true), 550);

		fieldsMap = new HashMap<Integer, Boolean>();
		modelsStore = new ListStore<ProjectModelDTO>();
		fieldsStore = new ListStore<FlexibleElementDTO>();
	}

	@Override
	public void initialize() {

		autoExportSchedules = new ArrayList<SimpleComboBoxData>();
		for (Integer value : exportScheduleMap.keySet()) {
			autoExportSchedules.add(exportScheduleMap.get(value));
		}
		autoDeleteSchedules = new ArrayList<SimpleComboBoxData>();
		for (Integer value : deleteScheduleMap.keySet()) {
			autoDeleteSchedules.add(deleteScheduleMap.get(value));
		}

		autoExportMonthlySchedules = new ArrayList<SimpleComboBoxData>();
		for (Integer value : monthScheduleMap.keySet()) {
			autoExportMonthlySchedules.add(monthScheduleMap.get(value));
		}
		autoExportWeeklySchedules = new ArrayList<SimpleComboBoxData>();
		for (Integer value : weekScheduleMap.keySet()) {
			autoExportWeeklySchedules.add(weekScheduleMap.get(value));
		}

		panel = new FormPanel();
		panel.setHeaderVisible(false);
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(150);
		panel.setAutoWidth(true);
		panel.setAutoHeight(true);

		panel.setLayout(layout);
		// file format
		calcChoice = new Radio();
		calcChoice.setBoxLabel(I18N.CONSTANTS.openDocumentSpreadsheet());
		calcChoice.setValue(true);
		calcChoice.setName("type");

		excelChoice = new Radio();
		excelChoice.setBoxLabel(I18N.CONSTANTS.msExcel());
		excelChoice.setName("type");

		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setOrientation(Orientation.VERTICAL);
		radioGroup.setFieldLabel(I18N.CONSTANTS.chooseFileType());
		radioGroup.add(calcChoice);
		radioGroup.add(excelChoice);
		panel.add(radioGroup);

		// configure fields
		// project model
		Grid<ProjectModelDTO> pModelsGrid = getModelsGrid();
		// flex elements
		Grid<FlexibleElementDTO> elementsGrid = getElementsGrid();

		FlexTable fieldsTable = new FlexTable();

		LabelField fildsTableLabel = new LabelField(I18N.CONSTANTS.configureFieldsToExport() + " :");
		fildsTableLabel.setWidth(150);
		fieldsTable.setWidget(0, 0, fildsTableLabel);

		fieldsTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

		fieldsTable.setWidget(0, 1, pModelsGrid);
		fieldsTable.setWidget(0, 2, elementsGrid);
		fieldsTable.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
		fieldsTable.getFlexCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
		panel.add(fieldsTable);

		// auto export schedule
		exportScheduleStore = new ListStore<SimpleComboBoxData>();
		exportSchedulesBox = new ComboBox<SimpleComboBoxData>();
		exportSchedulesBox.setFieldLabel(I18N.CONSTANTS.autoExportSchedule());
		exportSchedulesBox.setStore(exportScheduleStore);
		exportSchedulesBox.setDisplayField("label");
		exportSchedulesBox.setValueField("value");
		exportSchedulesBox.setEditable(false);
		exportSchedulesBox.setTriggerAction(TriggerAction.ALL);
		exportScheduleStore.add(autoExportSchedules);
		exportScheduleStore.commitChanges();
		exportSchedulesBox.setValue(exportScheduleMap.get(0));
		panel.add(exportSchedulesBox);

		// auto export monthly schedule
		exportMonthlyScheduleStore = new ListStore<SimpleComboBoxData>();
		exportMonthlySchedulesBox = new ComboBox<SimpleComboBoxData>();
		exportMonthlySchedulesBox.setFieldLabel(I18N.CONSTANTS.dayInMonth());
		exportMonthlySchedulesBox.setStore(exportMonthlyScheduleStore);
		exportMonthlySchedulesBox.setDisplayField("label");
		exportMonthlySchedulesBox.setValueField("value");
		exportMonthlySchedulesBox.setEditable(false);
		exportMonthlySchedulesBox.setTriggerAction(TriggerAction.ALL);
		exportMonthlyScheduleStore.add(autoExportMonthlySchedules);
		exportMonthlyScheduleStore.commitChanges();
		exportMonthlySchedulesBox.setValue(monthScheduleMap.get(31));
		exportMonthlySchedulesBox.hide();
		panel.add(exportMonthlySchedulesBox);

		// auto export Weekly schedule
		exportWeeklyScheduleStore = new ListStore<SimpleComboBoxData>();
		exportWeeklySchedulesBox = new ComboBox<SimpleComboBoxData>();
		exportWeeklySchedulesBox.setFieldLabel(I18N.CONSTANTS.dayInWeek());
		exportWeeklySchedulesBox.setStore(exportWeeklyScheduleStore);
		exportWeeklySchedulesBox.setDisplayField("label");
		exportWeeklySchedulesBox.setValueField("value");
		exportWeeklySchedulesBox.setEditable(false);
		exportWeeklySchedulesBox.setTriggerAction(TriggerAction.ALL);
		exportWeeklyScheduleStore.add(autoExportWeeklySchedules);
		exportWeeklyScheduleStore.commitChanges();
		exportWeeklySchedulesBox.setValue(weekScheduleMap.get(61));
		exportWeeklySchedulesBox.hide();
		panel.add(exportWeeklySchedulesBox);

		// auto delete schedule
		deleteScheduleStore = new ListStore<SimpleComboBoxData>();
		deleteSchedulesBox = new ComboBox<SimpleComboBoxData>();
		deleteSchedulesBox.setFieldLabel(I18N.CONSTANTS.autoCleanupSchedule());
		deleteSchedulesBox.setStore(deleteScheduleStore);
		deleteSchedulesBox.setDisplayField("label");
		deleteSchedulesBox.setValueField("value");
		deleteSchedulesBox.setEditable(false);
		deleteSchedulesBox.setTriggerAction(TriggerAction.ALL);
		deleteScheduleStore.add(autoDeleteSchedules);
		deleteScheduleStore.commitChanges();
		deleteSchedulesBox.setValue(deleteScheduleMap.get(0));
		panel.add(deleteSchedulesBox);

		// Add onChange handler for exportSchedulesBox
		exportSchedulesBox.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				exportMonthlySchedulesBox.hide();
				exportWeeklySchedulesBox.hide();

				if (exportSchedulesBox.getValue().getValue() == 31) { // Case of Monthly Schedule
					exportMonthlySchedulesBox.show();
				} else if (exportSchedulesBox.getValue().getValue() == 61) { // Case of Weekly Schedule
					exportWeeklySchedulesBox.show();
				} else {
					// Regular case of every N days
				}
			}
		});
		// onChange handler for exportSchedulesBox handled

		// button
		saveButton = new Button(I18N.CONSTANTS.saveExportConfiguration());
		panel.getButtonBar().add(saveButton);
		panel.setScrollMode(Scroll.AUTO);
		initPopup(panel);

	}

	private Grid<ProjectModelDTO> getModelsGrid() {

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig("name", I18N.CONSTANTS.adminProjectModelsName(), 150);
		column.setRenderer(new GridCellRenderer<ProjectModelDTO>() {

			@Override
			public Object render(final ProjectModelDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ProjectModelDTO> store,
					Grid<ProjectModelDTO> grid) {

				final ToggleAnchor anchor = new ToggleAnchor(model.getName());
				anchor.setAnchorMode(true);

				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						fieldsStore.removeAll();
						fieldsStore.add(model.getGlobalExportElements());
						fieldsStore.commitChanges();
					}

				});
				return anchor;
			}
		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<ProjectModelDTO> grid = new Grid<ProjectModelDTO>(modelsStore, cm);
		grid.setStyleName("global-export-fields-table");
		grid.setAutoHeight(true);
		grid.setWidth(150);
		grid.getView().setForceFit(true);
		return grid;
	}

	private Grid<FlexibleElementDTO> getElementsGrid() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// checkbox
		ColumnConfig column = new ColumnConfig("globallyExportable", I18N.CONSTANTS.export() + "?", 70);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FlexibleElementDTO> store,
					Grid<FlexibleElementDTO> grid) {
				final CheckBox gExportable = createCheckBox("globallyExportable", null);
				gExportable.setValue(model.getGloballyExportable());
				gExportable.addListener(Events.OnClick, new Listener<FieldEvent>() {

					@Override
					public void handleEvent(FieldEvent be) {
						fieldsMap.put(model.getId(), gExportable.getValue());
					}
				});
				return gExportable;
			}
		});
		configs.add(column);

		// name
		column = new ColumnConfig("label", I18N.CONSTANTS.adminFlexibleName(), 180);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(FlexibleElementDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FlexibleElementDTO> store,
					Grid<FlexibleElementDTO> grid) {
				String title = null;
				if (ElementTypeEnum.DEFAULT.equals(model.getElementType())) {
					title = DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO) model).getType());
				} else {
					title = model.getLabel();
				}
				final Text text = createGridText(title);
				text.setTitle(title);
				return text;
			}

		});
		configs.add(column);

		column = new ColumnConfig("container", I18N.CONSTANTS.adminFlexibleContainer(), 120);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(FlexibleElementDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FlexibleElementDTO> store,
					Grid<FlexibleElementDTO> grid) {

				BaseModelData container = model.getContainerModel();
				return createGridText((String) container.get("name"));

			}

		});
		configs.add(column);

		column = new ColumnConfig("group", I18N.CONSTANTS.adminFlexibleGroup(), 200);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FlexibleElementDTO> store,
					Grid<FlexibleElementDTO> grid) {

				LayoutGroupDTO group = model.getGroup();
				return createGridText((String) group.get("title"));
			}
		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);
		Grid<FlexibleElementDTO> grid = new Grid<FlexibleElementDTO>(fieldsStore, cm);
		grid.setStyleName("global-export-fields-table");
		grid.setAutoHeight(true);
		grid.setWidth(470);
		grid.getView().setForceFit(true);
		return grid;
	}

	@Override
	public void clearFrom() {

		this.modelsStore.removeAll();
		this.fieldsStore.removeAll();

		calcChoice.setValue(true);

		exportSchedulesBox.clearSelections();
		exportMonthlySchedulesBox.clearSelections();
		exportWeeklySchedulesBox.clearSelections();

		deleteSchedulesBox.clearSelections();
	}

	private Text createGridText(String content) {
		final Text label = new Text(content);
		label.addStyleName("label-small");
		return label;
	}

	private CheckBox createCheckBox(String property, String label) {
		CheckBox box = new CheckBox();
		box.setName(property);
		box.setBoxLabel(label);
		return box;
	}

	@Override
	public ListStore<FlexibleElementDTO> getFieldsStore() {
		return fieldsStore;
	}

	@Override
	public ListStore<ProjectModelDTO> getModelsStore() {
		return modelsStore;
	}

	@Override
	public Map<Integer, Boolean> getFieldsMap() {
		return fieldsMap;
	}

	@Override
	public List<SimpleComboBoxData> getAutoExportSchedules() {
		return autoExportSchedules;
	}

	@Override
	public List<SimpleComboBoxData> getAutoDeleteSchedules() {
		return autoDeleteSchedules;
	}

	@Override
	public List<SimpleComboBoxData> getAutoExportMonthlySchedules() {
		return autoExportMonthlySchedules;
	}

	@Override
	public List<SimpleComboBoxData> getAutoExportWeeklySchedules() {
		return autoExportWeeklySchedules;
	}

	@Override
	public ListStore<SimpleComboBoxData> getDeleteScheduleStore() {
		return deleteScheduleStore;
	}

	@Override
	public ComboBox<SimpleComboBoxData> getDeleteSchedulesBox() {
		return deleteSchedulesBox;
	}

	@Override
	public FormPanel getPanel() {
		return panel;
	}

	@Override
	public Radio getCalcChoice() {
		return calcChoice;
	}

	@Override
	public Radio getExcelChoice() {
		return excelChoice;
	}

	@Override
	public Button getSaveButton() {
		return saveButton;
	}

	@Override
	public ListStore<SimpleComboBoxData> getExportScheduleStore() {
		return exportScheduleStore;
	}

	@Override
	public ComboBox<SimpleComboBoxData> getExportSchedulesBox() {
		return exportSchedulesBox;
	}

	@Override
	public ListStore<SimpleComboBoxData> getExportMonthlyScheduleStore() {
		return exportMonthlyScheduleStore;
	}

	@Override
	public ComboBox<SimpleComboBoxData> getExportMonthlySchedulesBox() {
		return exportMonthlySchedulesBox;
	}

	@Override
	public ListStore<SimpleComboBoxData> getExportWeeklyScheduleStore() {
		return exportWeeklyScheduleStore;
	}

	@Override
	public ComboBox<SimpleComboBoxData> getExportWeeklySchedulesBox() {
		return exportWeeklySchedulesBox;
	}

	static {
		exportScheduleMap = new HashMap<Integer, SimpleComboBoxData>();
		exportScheduleMap.put(0, new SimpleComboBoxData(0, I18N.CONSTANTS.notScheduled()));
		exportScheduleMap.put(1, new SimpleComboBoxData(1, I18N.CONSTANTS.daily()));
		exportScheduleMap.put(3, new SimpleComboBoxData(3, I18N.MESSAGES.everyXDays("3")));
		exportScheduleMap.put(9, new SimpleComboBoxData(9, I18N.MESSAGES.everyXDays("9")));
		exportScheduleMap.put(15, new SimpleComboBoxData(15, I18N.MESSAGES.everyXDays("15")));
		exportScheduleMap.put(31, new SimpleComboBoxData(31, I18N.CONSTANTS.monthly())); // In this case take value from
		// monthMap
		exportScheduleMap.put(61, new SimpleComboBoxData(61, I18N.CONSTANTS.weekly())); // In this case take value from
		// weekMap

		// The number space of 31-58 is reserved for month dates in monthyExport settings.
		// 31 represents the first day of the month, 32 the second day and so on
		// Only 28 days as considered as they are common in every month of the year
		monthScheduleMap = new HashMap<Integer, SimpleComboBoxData>();
		monthScheduleMap.put(31, new SimpleComboBoxData(31, I18N.CONSTANTS.number_1()));
		monthScheduleMap.put(32, new SimpleComboBoxData(32, I18N.CONSTANTS.number_2()));
		monthScheduleMap.put(33, new SimpleComboBoxData(33, I18N.CONSTANTS.number_3()));
		monthScheduleMap.put(34, new SimpleComboBoxData(34, I18N.CONSTANTS.number_4()));
		monthScheduleMap.put(35, new SimpleComboBoxData(35, I18N.CONSTANTS.number_5()));
		monthScheduleMap.put(36, new SimpleComboBoxData(36, I18N.CONSTANTS.number_6()));
		monthScheduleMap.put(37, new SimpleComboBoxData(37, I18N.CONSTANTS.number_7()));
		monthScheduleMap.put(38, new SimpleComboBoxData(38, I18N.CONSTANTS.number_8()));
		monthScheduleMap.put(39, new SimpleComboBoxData(39, I18N.CONSTANTS.number_9()));
		monthScheduleMap.put(40, new SimpleComboBoxData(40, I18N.CONSTANTS.number_10()));
		monthScheduleMap.put(41, new SimpleComboBoxData(41, I18N.CONSTANTS.number_11()));
		monthScheduleMap.put(42, new SimpleComboBoxData(42, I18N.CONSTANTS.number_12()));
		monthScheduleMap.put(43, new SimpleComboBoxData(43, I18N.CONSTANTS.number_13()));
		monthScheduleMap.put(44, new SimpleComboBoxData(44, I18N.CONSTANTS.number_14()));
		monthScheduleMap.put(45, new SimpleComboBoxData(45, I18N.CONSTANTS.number_15()));
		monthScheduleMap.put(46, new SimpleComboBoxData(46, I18N.CONSTANTS.number_16()));
		monthScheduleMap.put(47, new SimpleComboBoxData(47, I18N.CONSTANTS.number_17()));
		monthScheduleMap.put(48, new SimpleComboBoxData(48, I18N.CONSTANTS.number_18()));
		monthScheduleMap.put(49, new SimpleComboBoxData(49, I18N.CONSTANTS.number_19()));
		monthScheduleMap.put(50, new SimpleComboBoxData(50, I18N.CONSTANTS.number_20()));
		monthScheduleMap.put(51, new SimpleComboBoxData(51, I18N.CONSTANTS.number_21()));
		monthScheduleMap.put(52, new SimpleComboBoxData(52, I18N.CONSTANTS.number_22()));
		monthScheduleMap.put(53, new SimpleComboBoxData(53, I18N.CONSTANTS.number_23()));
		monthScheduleMap.put(54, new SimpleComboBoxData(54, I18N.CONSTANTS.number_24()));
		monthScheduleMap.put(55, new SimpleComboBoxData(55, I18N.CONSTANTS.number_25()));
		monthScheduleMap.put(56, new SimpleComboBoxData(56, I18N.CONSTANTS.number_26()));
		monthScheduleMap.put(57, new SimpleComboBoxData(57, I18N.CONSTANTS.number_27()));
		monthScheduleMap.put(58, new SimpleComboBoxData(58, I18N.CONSTANTS.number_28()));

		// The number space 61-67 is reserved to denote weekly export schedule
		// 61 represents sunday, 62 monday and so on
		weekScheduleMap = new HashMap<Integer, SimpleComboBoxData>();
		weekScheduleMap.put(61, new SimpleComboBoxData(61, I18N.CONSTANTS.dayName_1()));
		weekScheduleMap.put(62, new SimpleComboBoxData(62, I18N.CONSTANTS.dayName_2()));
		weekScheduleMap.put(63, new SimpleComboBoxData(63, I18N.CONSTANTS.dayName_3()));
		weekScheduleMap.put(64, new SimpleComboBoxData(64, I18N.CONSTANTS.dayName_4()));
		weekScheduleMap.put(65, new SimpleComboBoxData(65, I18N.CONSTANTS.dayName_5()));
		weekScheduleMap.put(66, new SimpleComboBoxData(66, I18N.CONSTANTS.dayName_6()));
		weekScheduleMap.put(67, new SimpleComboBoxData(67, I18N.CONSTANTS.dayName_7()));

		deleteScheduleMap = new HashMap<Integer, SimpleComboBoxData>();
		deleteScheduleMap.put(0, new SimpleComboBoxData(0, I18N.CONSTANTS.notScheduled()));
		deleteScheduleMap.put(1, new SimpleComboBoxData(1, I18N.MESSAGES.olderThanXMonths("1")));
		deleteScheduleMap.put(3, new SimpleComboBoxData(3, I18N.MESSAGES.olderThanXMonths("3")));
		deleteScheduleMap.put(6, new SimpleComboBoxData(6, I18N.MESSAGES.olderThanXMonths("6")));
		deleteScheduleMap.put(12, new SimpleComboBoxData(12, I18N.MESSAGES.olderThanXMonths("12")));
	}
}
