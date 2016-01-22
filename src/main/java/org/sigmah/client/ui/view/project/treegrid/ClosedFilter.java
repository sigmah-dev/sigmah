package org.sigmah.client.ui.view.project.treegrid;

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
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.shared.dto.ProjectDTO;

import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.DateMenu;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * Closed filter implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see com.extjs.gxt.ui.client.widget.grid.filters.Filter
 */
final class ClosedFilter extends Filter {

	private static final String RADIOS_GROUP_NAME = "radios";

	private final CheckMenuItem noneFilter;
	private final CheckMenuItem sixMonthsFilter;
	private final CheckMenuItem twelveMonthsFilter;
	private final CheckMenuItem customFilter;
	private final DateMenu dateMenu;

	private CheckMenuItem currentItem;

	/**
	 * Selection listener implementation.
	 */
	private final Listener<MenuEvent> handler = new Listener<MenuEvent>() {

		@Override
		public void handleEvent(final MenuEvent be) {

			currentItem = (CheckMenuItem) be.getItem();
			fireUpdate();
		}
	};

	/**
	 * Menu listener implementation.
	 */
	private final Listener<MenuEvent> menuListener = new Listener<MenuEvent>() {

		@Override
		public void handleEvent(final MenuEvent be) {

			if (be.getType() == Events.Select) {
				onMenuSelect(be);
			}
		}
	};

	public ClosedFilter(String dataIndex) {

		super(dataIndex);

		menu = new Menu();

		noneFilter = new CheckMenuItem(I18N.CONSTANTS.noneFilter());
		noneFilter.setGroup(RADIOS_GROUP_NAME);
		noneFilter.setChecked(true);
		menu.add(noneFilter);

		sixMonthsFilter = new CheckMenuItem(I18N.CONSTANTS.sixMonthsFilter());
		sixMonthsFilter.setGroup(RADIOS_GROUP_NAME);
		menu.add(sixMonthsFilter);

		twelveMonthsFilter = new CheckMenuItem(I18N.CONSTANTS.twelveMonthsFilter());
		twelveMonthsFilter.setGroup(RADIOS_GROUP_NAME);
		menu.add(twelveMonthsFilter);

		customFilter = new CheckMenuItem(I18N.CONSTANTS.customFilter());
		customFilter.setGroup(RADIOS_GROUP_NAME);
		menu.add(customFilter);

		dateMenu = new DateMenu();

		dateMenu.setDate(new Date());
		dateMenu.addListener(Events.Select, menuListener);

		customFilter.setSubMenu(dateMenu);

		customFilter.addListener(Events.Select, handler);
		sixMonthsFilter.addListener(Events.Select, handler);
		twelveMonthsFilter.addListener(Events.Select, handler);
		noneFilter.addListener(Events.Select, handler);
		currentItem = noneFilter;
	}

	/**
	 * Method called on menu selection event.
	 * 
	 * @param be
	 *          The menu event.
	 */
	protected void onMenuSelect(final MenuEvent be) {

		if (currentItem != null) {
			currentItem.setChecked(false, true);

		} else {
			N10N.warn("Null", "current item null");
		}

		currentItem = customFilter;
		customFilter.setChecked(true, true);

		if (be.getMenu() == dateMenu) {
			final DateMenu dateMenu = (DateMenu) be.getMenu();
			dateMenu.hide(true);

			fireUpdate();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FilterConfig> getSerialArgs() {
		// No need for implementation.
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue() {
		// No need for implementation.
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(Object value) {
		// No need for implementation.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isActivatable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validateModel(final ModelData model) {

		final Date closeDate = model.get(ProjectDTO.CLOSE_DATE);
		if (closeDate == null) {
			return true;
		}

		if (currentItem == customFilter) {
			return !closeDate.before(dateMenu.getDate());
		}

		if (currentItem == sixMonthsFilter) {
			final Date dateTemp = new Date();
			CalendarUtil.addMonthsToDate(dateTemp, -6);
			return !closeDate.before(dateTemp);
		}

		if (currentItem == noneFilter) {
			return false;
		}

		if (currentItem == twelveMonthsFilter) {
			final Date dateTemp = new Date();
			CalendarUtil.addMonthsToDate(dateTemp, -12);
			return !closeDate.before(dateTemp);
		}

		return false;
	}

}
