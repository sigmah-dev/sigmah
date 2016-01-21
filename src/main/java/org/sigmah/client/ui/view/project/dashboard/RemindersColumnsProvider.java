package org.sigmah.client.ui.view.project.dashboard;

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

import java.util.Arrays;
import java.util.Date;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Provides reminders/monitored points columns configuration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class RemindersColumnsProvider {

	// CSS style names.
	private static final String STYLE_POINTS_DATE_EXCEEDED = "points-date-exceeded";

	/**
	 * The project dashboard view.
	 */
	private final ProjectDashboardView view;

	public RemindersColumnsProvider(final ProjectDashboardView view) {
		this.view = view;
	}

	/**
	 * Gets the columns model for the reminders grid.
	 * 
	 * @return The columns model for the reminders grid.
	 */
	public ColumnModel getRemindersColumnModel() {

		// Completed ?
		final CheckColumnConfig completedColumn = new CheckColumnConfig() {

			/**
			 * Returns the CSS style name which contains a background image representing the checkbox. This implementation
			 * returns "-on" or "" based on a boolean model property. "-disabled" can be returned to render a disabled
			 * checkbox.
			 * 
			 * @param model
			 *          the model
			 * @param property
			 *          the model property
			 * @param rowIndex
			 *          the row index
			 * @param colIndex
			 *          the cell index
			 * @return the CSS style name
			 */
			@Override
			protected String getCheckState(final ModelData model, final String property, final int rowIndex, final int colIndex) {

				final ReminderDTO reminder = (ReminderDTO) model;

				if (view.getPresenterHandler().isAuthor(reminder)) {
					return ClientUtils.isTrue(model.get(property)) ? "-on" : "";

				} else {
					return "-disabled";
				}
			}
		};
		completedColumn.setId(ReminderDTO.COMPLETED);
		completedColumn.setHeaderText(I18N.CONSTANTS.monitoredPointClose() + "?");
		completedColumn.setWidth(20);
		completedColumn.setSortable(false);
		completedColumn.setEditor(new CellEditor(new CheckBox()));

		// Icon
		final ColumnConfig iconColumn = new ColumnConfig();
		iconColumn.setId("icon");
		iconColumn.setHeaderText("");
		iconColumn.setWidth(16);
		iconColumn.setRenderer(new GridCellRenderer<ReminderDTO>() {

			@Override
			public Object render(final ReminderDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ReminderDTO> store, final Grid<ReminderDTO> grid) {

				if (model.isCompleted()) {
					return IconImageBundle.ICONS.closedReminder().createImage();

				} else if (DateUtils.DAY_COMPARATOR.compare(new Date(), model.getExpectedDate()) > 0) {
					return IconImageBundle.ICONS.overdueReminder().createImage();

				} else {
					return IconImageBundle.ICONS.openedReminder().createImage();
				}
			}
		});

		// Label.
		final ColumnConfig labelColumn = new ColumnConfig();
		labelColumn.setId(ReminderDTO.LABEL);
		labelColumn.setHeaderText(I18N.CONSTANTS.monitoredPointLabel());
		labelColumn.setWidth(60);
		labelColumn.setRenderer(new RemindersLabelCellRenderer<ReminderDTO>(view));

		// Expected date.
		final ColumnConfig expectedDateColumn = new ColumnConfig();
		expectedDateColumn.setId(ReminderDTO.EXPECTED_DATE);
		expectedDateColumn.setHeaderText(I18N.CONSTANTS.monitoredPointExpectedDate());
		expectedDateColumn.setWidth(60);
		expectedDateColumn.setDateTimeFormat(DateUtils.DATE_SHORT);
		expectedDateColumn.setRenderer(new GridCellRenderer<ReminderDTO>() {

			@Override
			public Object render(final ReminderDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ReminderDTO> store, final Grid<ReminderDTO> grid) {

				final Label expectedDateLabel = new Label(DateUtils.DATE_SHORT.format(model.getExpectedDate()));

				if (!model.isCompleted() && DateUtils.DAY_COMPARATOR.compare(new Date(), model.getExpectedDate()) > 0) {
					expectedDateLabel.addStyleName(STYLE_POINTS_DATE_EXCEEDED);
				}

				return expectedDateLabel;
			}
		});

		// Completion date.
		final ColumnConfig completionDateColumn = new ColumnConfig();
		completionDateColumn.setId(ReminderDTO.COMPLETION_DATE);
		completionDateColumn.setHeaderText(I18N.CONSTANTS.monitoredPointCompletionDate());
		completionDateColumn.setWidth(60);
		completionDateColumn.setDateTimeFormat(DateUtils.DATE_SHORT);

		return new ColumnModel(Arrays.asList(completedColumn, iconColumn, labelColumn, expectedDateColumn, completionDateColumn));
	}

	/**
	 * Gets the columns model for the monitored points grid.
	 * 
	 * @return The columns model for the monitored points grid.
	 */
	public ColumnModel getMonitoredPointsColumnModel() {

		final DateTimeFormat format = DateUtils.DATE_SHORT;

		// Completed ?
		final CheckColumnConfig completedColumn = new CheckColumnConfig() {

			/**
			 * Returns the CSS style name which contains a background image representing the checkbox. This implementation
			 * returns "-on" or "" based on a boolean model property. "-disabled" can be returned to render a disabled
			 * checkbox.
			 * 
			 * @param model
			 *          the model
			 * @param property
			 *          the model property
			 * @param rowIndex
			 *          the row index
			 * @param colIndex
			 *          the cell index
			 * @return the CC style name.
			 */
			@Override
			protected String getCheckState(final ModelData model, final String property, final int rowIndex, final int colIndex) {

				final MonitoredPointDTO point = (MonitoredPointDTO) model;

				if (view.getPresenterHandler().isAuthor(point)) {
					return ClientUtils.isTrue(model.get(property)) ? "-on" : "";

				} else {
					return "-disabled";
				}
			}
		};
		completedColumn.setId(MonitoredPointDTO.COMPLETED);
		completedColumn.setHeaderText(I18N.CONSTANTS.monitoredPointClose() + "?");
		completedColumn.setWidth(20);
		completedColumn.setSortable(false);
		completedColumn.setEditor(new CellEditor(new CheckBox()));

		// Icon
		final ColumnConfig iconColumn = new ColumnConfig();
		iconColumn.setId("icon");
		iconColumn.setHeaderText("");
		iconColumn.setWidth(16);
		iconColumn.setRenderer(new GridCellRenderer<MonitoredPointDTO>() {

			@Override
			public Object render(final MonitoredPointDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<MonitoredPointDTO> store, final Grid<MonitoredPointDTO> grid) {

				if (model.isCompleted()) {
					return IconImageBundle.ICONS.closedPoint().createImage();

				} else if (DateUtils.DAY_COMPARATOR.compare(new Date(), model.getExpectedDate()) > 0) {
					return IconImageBundle.ICONS.overduePoint().createImage();

				} else {
					return IconImageBundle.ICONS.openedPoint().createImage();
				}
			}
		});

		// Label.
		final ColumnConfig labelColumn = new ColumnConfig();
		labelColumn.setId(MonitoredPointDTO.LABEL);
		labelColumn.setHeaderText(I18N.CONSTANTS.monitoredPointLabel());
		labelColumn.setWidth(60);
		labelColumn.setRenderer(new RemindersLabelCellRenderer<MonitoredPointDTO>(view));

		// Expected date.
		final ColumnConfig expectedDateColumn = new ColumnConfig();
		expectedDateColumn.setId(MonitoredPointDTO.EXPECTED_DATE);
		expectedDateColumn.setHeaderText(I18N.CONSTANTS.monitoredPointExpectedDate());
		expectedDateColumn.setWidth(60);
		expectedDateColumn.setDateTimeFormat(format);
		expectedDateColumn.setRenderer(new GridCellRenderer<MonitoredPointDTO>() {

			@Override
			public Object render(final MonitoredPointDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<MonitoredPointDTO> store, final Grid<MonitoredPointDTO> grid) {

				final Label expectedDateLabel = new Label(format.format(model.getExpectedDate()));

				if (!model.isCompleted() && DateUtils.DAY_COMPARATOR.compare(new Date(), model.getExpectedDate()) > 0) {
					expectedDateLabel.addStyleName(STYLE_POINTS_DATE_EXCEEDED);
				}

				return expectedDateLabel;
			}
		});

		// Completion date.
		final ColumnConfig completionDateColumn = new ColumnConfig();
		completionDateColumn.setId(MonitoredPointDTO.COMPLETION_DATE);
		completionDateColumn.setHeaderText(I18N.CONSTANTS.monitoredPointCompletionDate());
		completionDateColumn.setWidth(60);
		completionDateColumn.setDateTimeFormat(format);

		return new ColumnModel(Arrays.asList(completedColumn, iconColumn, labelColumn, expectedDateColumn, completionDateColumn));
	}

}
