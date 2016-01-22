package org.sigmah.client.ui.widget;

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
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;
import org.sigmah.shared.dto.history.HistoryTokenManager;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.sigmah.shared.dto.history.HistoryTokenDTO;

/**
 * A simple window to show the history of a field.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class HistoryWindow {

	/**
	 * Singleton.
	 */
	private static HistoryWindow instance;

	/**
	 * Show the history.
	 * 
	 * @param tokens
	 *          The history.
	 * @param manager
	 *          The history manager.
	 */
	public static void show(final List<HistoryTokenListDTO> tokens, final HistoryTokenManager manager) {

		if (instance == null) {
			instance = new HistoryWindow();
		}

		instance.showHistory(tokens, manager);
	}

	/**
	 * Returns the display name of the user in a history token.
	 * 
	 * @param token
	 *          The history token.
	 * @return The display name;
	 */
	private static String getUserDisplayName(final HistoryTokenListDTO token) {
		return token.getUserFirstName() != null ? token.getUserFirstName() + ' ' + token.getUserName() : token.getUserName();
	}

	private final Window window;
	private final ListStore<HistoryTokenListDTO> store;
	private final Grid<HistoryTokenListDTO> grid;
	private HistoryTokenManager manager;
	private final Label noHistoryLabel;

	/**
	 * Builds the window.
	 */
	private HistoryWindow() {

		// Store.
		store = new ListStore<HistoryTokenListDTO>();
		store.setStoreSorter(new StoreSorter<HistoryTokenListDTO>() {

			@Override
			public int compare(Store<HistoryTokenListDTO> store, HistoryTokenListDTO m1, HistoryTokenListDTO m2, String property) {

				if ("user".equals(property)) {
					return getUserDisplayName(m1).compareToIgnoreCase(getUserDisplayName(m2));
				} else {
					return super.compare(store, m1, m2, property);
				}
			}
		});

		// Plugins.
		final RowNumberer countColumn = new RowNumberer();

		// Grid.
		grid = new Grid<HistoryTokenListDTO>(store, new ColumnModel(Arrays.asList(getColumnModel(countColumn))));
		grid.getView().setForceFit(true);
		grid.setBorders(false);
		grid.setAutoExpandColumn("value");
		grid.addPlugin(countColumn);

		// Builds the window.
		window = new Window();
		window.setWidth(800);
		window.setHeight(400);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setLayout(new FitLayout());

		// Builds the no history label.
		noHistoryLabel = new Label(I18N.CONSTANTS.historyNoHistory());

		window.add(grid);
	}

	/**
	 * Builds the columns model.
	 * 
	 * @param countColumn
	 *          The row numberer plugin.
	 * @return The columns model.
	 */
	private ColumnConfig[] getColumnModel(RowNumberer countColumn) {

		final DateTimeFormat format = DateUtils.DATE_TIME_SHORT;

		// Core version name.
		final ColumnConfig coreVersionColumn = new ColumnConfig();
		coreVersionColumn.setId("coreVersionName");
		coreVersionColumn.setHeaderText(I18N.CONSTANTS.projectCoreBoxTitle());
		coreVersionColumn.setWidth(100);
		coreVersionColumn.setRenderer(new GridCellRenderer<HistoryTokenListDTO>() {

			@Override
			public Object render(HistoryTokenListDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				String coreVersion = "--";
				
				for(final HistoryTokenDTO token : model.getTokens()) {
					if(token.getCoreVersionName() != null) {
						coreVersion = token.getCoreVersionName();
					}
				}
				return coreVersion;
			}
		});

		// Date.
		final ColumnConfig dateColumn = new ColumnConfig();
		dateColumn.setId("date");
		dateColumn.setHeaderText(I18N.CONSTANTS.historyDate());
		dateColumn.setWidth(100);
		dateColumn.setDateTimeFormat(format);

		// User.
		final ColumnConfig userColumn = new ColumnConfig();
		userColumn.setId("user");
		userColumn.setHeaderText(I18N.CONSTANTS.historyUser());
		userColumn.setWidth(135);
		userColumn.setRenderer(new GridCellRenderer<HistoryTokenListDTO>() {

			@Override
			public Object render(HistoryTokenListDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<HistoryTokenListDTO> store,
					Grid<HistoryTokenListDTO> grid) {
				return getUserDisplayName(model);
			}
		});

		// Value.
		final ColumnConfig valueColumn = new ColumnConfig();
		valueColumn.setId("tokens");
		valueColumn.setHeaderText(I18N.CONSTANTS.historyValue());
		valueColumn.setSortable(false);
		valueColumn.setWidth(350);
		valueColumn.setRenderer(new GridCellRenderer<HistoryTokenListDTO>() {

			@Override
			public Object render(HistoryTokenListDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<HistoryTokenListDTO> store,
					Grid<HistoryTokenListDTO> grid) {

				if (manager != null) {
					return manager.renderHistoryToken(model);
				} else {
					return null;
				}
			}
		});

		// Value.
		final ColumnConfig commentColumn = new ColumnConfig();
		commentColumn.setId("comment");
		commentColumn.setHeaderText(I18N.CONSTANTS.comments());
		commentColumn.setSortable(false);
		commentColumn.setWidth(200);
		commentColumn.setRenderer(new GridCellRenderer<HistoryTokenListDTO>() {

			@Override
			public Object render(HistoryTokenListDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<HistoryTokenListDTO> store,
					Grid<HistoryTokenListDTO> grid) {

				final StringBuilder stringBuilder = new StringBuilder();
				for(final HistoryTokenDTO token : model.getTokens()) {
					stringBuilder.append(token.getComment() != null ? token.getComment() : "").append("<br>");
				}
				return stringBuilder.toString();
			}
		});

		return new ColumnConfig[] {
			countColumn,
			coreVersionColumn,
			dateColumn,
			userColumn,
			valueColumn,
			commentColumn
		};
	}

	/**
	 * Show the history.
	 * 
	 * @param tokens
	 *          The history.
	 * @param manager
	 *          The history manager.
	 */
	private void showHistory(List<HistoryTokenListDTO> tokens, HistoryTokenManager manager) {

		// Hides if shown.
		window.hide();

		// Sets the current manager.
		this.manager = manager;

		// Reset window.
		window.removeAll();

		// Reloads store.
		store.removeAll();
		store.sort("date", SortDir.DESC);

		// Adds the tokens grid if there is a history.
		if (tokens != null && !tokens.isEmpty()) {
			store.add(tokens);
			window.add(grid);
		}
		// Adds the no-history label if there isn't a history.
		else {
			window.add(noHistoryLabel);
		}

		window.layout();

		// Shows window.
		window.setHeadingText(I18N.CONSTANTS.history() + ": " + manager.getElementLabel());
		window.show();
	}
}
