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

import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * A cell renderer for label column in a Reminder/MonitoredPoint grid.
 * 
 * @author HUZHE
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class RemindersLabelCellRenderer<D extends EntityDTO<?> & ModelData> implements GridCellRenderer<D> {

	private static final String STYLE_POINTS_COMPLETED = "points-completed";
	private static final String STYLE_HYPERLINK_LABEL = "hyperlink-label";

	private final ProjectDashboardView view;

	public RemindersLabelCellRenderer(final ProjectDashboardView view) {
		this.view = view;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object render(final D model, final String property, final ColumnData config, final int rowIndex, final int colIndex, final ListStore<D> store,
			final Grid<D> grid) {

		if (!view.getPresenterHandler().isAuthorizedToEditReminder()) {
			return null;
		}

		final String entityLabel;
		final boolean entityCompleted;

		if (model instanceof ReminderDTO) {
			final ReminderDTO reminder = (ReminderDTO) model;
			entityLabel = reminder.getLabel();
			entityCompleted = reminder.isCompleted();

		} else if (model instanceof MonitoredPointDTO) {
			final MonitoredPointDTO monitoredPoint = (MonitoredPointDTO) model;
			entityLabel = monitoredPoint.getLabel();
			entityCompleted = monitoredPoint.isCompleted();

		} else {
			throw new UnsupportedOperationException("Only types 'ReminderDTO' and 'MonitoredPointDTO' are supported.");
		}

		// Create a label with a hyperlink style.
		com.google.gwt.user.client.ui.Label label = new com.google.gwt.user.client.ui.Label(entityLabel);

		if (entityCompleted) {
			// When the monitored point is completed, change the label style.
			label.addStyleName(STYLE_POINTS_COMPLETED);
		}

		if (view.getPresenterHandler().isAuthor(model)) {
			label.addStyleName(STYLE_HYPERLINK_LABEL);

		} else {
			return label;
		}

		// Add a click handler to response a click event.
		label.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {

				view.getPresenterHandler().onLabelClickEvent(model);

			}
		});

		return label;
	}
}
