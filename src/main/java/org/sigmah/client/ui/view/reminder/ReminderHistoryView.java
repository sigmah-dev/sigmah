package org.sigmah.client.ui.view.reminder;

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
import java.util.List;

import org.sigmah.client.ui.presenter.reminder.ReminderHistoryPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.reminder.ReminderHistoryDTO;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.inject.Singleton;

/**
 * Reminder frame view used to show a reminder or a monitored point's history.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReminderHistoryView extends AbstractPopupView<PopupWidget> implements ReminderHistoryPresenter.View {

	private Grid<AbstractModelDataEntityDTO<?>> grid;

	/**
	 * Builds the view.
	 */
	public ReminderHistoryView() {
		super(new PopupWidget(true, Layouts.fitLayout()), 750, 400);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId(ReminderHistoryDTO.ID);
		column.setHeaderText("Token ID"); // TODO i18n
		column.setWidth(100);
		configs.add(column);

		column = new ColumnConfig(ReminderHistoryDTO.DATE, "DATE", 150); // TODO i18n
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		column = new ColumnConfig(ReminderHistoryDTO.VALUE, "NOTE", 150); // TODO i18n
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		column = new ColumnConfig(ReminderHistoryDTO.TYPE, "ACTION", 150); // TODO i18n
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		grid = new Grid<AbstractModelDataEntityDTO<?>>(new ListStore<AbstractModelDataEntityDTO<?>>(), new ColumnModel(configs));
		grid.getView().setForceFit(true);
		grid.setBorders(false);
		grid.setAutoExpandColumn(ReminderHistoryDTO.VALUE);

		initPopup(grid);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(final List<? extends AbstractModelDataEntityDTO<?>> dataList) {
		grid.getStore().removeAll();
		grid.getStore().add(dataList);
		grid.getStore().commitChanges();
	}

}
