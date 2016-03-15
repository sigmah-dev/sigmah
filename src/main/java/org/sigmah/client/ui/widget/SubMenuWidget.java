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


import com.google.gwt.dom.client.Style;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;

/**
 * Sub-menu widget.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SubMenuWidget implements IsWidget {

	/**
	 * id of agenda sub menu.
	 */
	private final static String AGENDA_SUB_MENU_ID="agendaSubMenuId"; 
	/**
	 * Sub-menu orientation.
	 */
	public static enum Orientation {

		/**
		 * Vertical sub-menu layout.
		 */
		VERTICAL,

		/**
		 * Horizontal sub-menu layout.
		 */
		HORIZONTAL;

		private static final String CSS_SUBMENU = "sub-menu";
		private static final String CSS_HORIZONTAL = "horizontal";
		private static final String CSS_VERTICAL = "vertical";

		/**
		 * Return the given {@code orientation} corresponding {@link Panel} widget.
		 * 
		 * @param orientation
		 *          The {@link Orientation} instance.
		 * @return The given {@code orientation} corresponding {@link Panel} widget.
		 * @throws IllegalArgumentException
		 *           If given {@code orientation} is {@code null}.
		 */
		private static Panel asPanel(final Orientation orientation) {

			if (orientation == null) {
				throw new IllegalArgumentException("Invalid orientation value.");
			}

			final Panel panel;

			switch (orientation) {

				case VERTICAL:
					panel = new VerticalPanel();
					panel.setStyleName(CSS_SUBMENU);
					panel.addStyleName(CSS_VERTICAL);
					break;

				case HORIZONTAL:
					panel = new HorizontalPanel();
					panel.setStyleName(CSS_SUBMENU);
					panel.addStyleName(CSS_HORIZONTAL);
					break;

				default:
					throw new IllegalArgumentException("Invalid orientation value.");
			}

			return panel;
		}
	}

	/**
	 * Sub-menu listener interface.
	 */
	public static interface SubMenuListener {

		/**
		 * Method executed on sub-menu click.
		 * 
		 * @param menuItem
		 *          The selected sub-menu item.
		 */
		void onSubMenuClick(final SubMenuItem menuItem);

	}

	// Widget CSS style name.
	private static final String CSS_SUBMENU_ACTIVE = "active";

	// Widget containers.
	private final Panel menuWrapper;
	private final Panel menuPanel;

	// Stores the MenuItem.
	private final List<SubMenuItem> menuItems;

	// Listeners.
	private final List<SubMenuListener> listeners;

	public SubMenuWidget(final Orientation orientation, final Map<Page, String> linksMap) {

		menuItems = new ArrayList<SubMenuItem>();
		listeners = new ArrayList<SubMenuListener>();

		menuPanel = Orientation.asPanel(orientation);
		menuWrapper = new AbsolutePanel();
		menuWrapper.add(menuPanel);

		for (final Page linkedPage : linksMap.keySet()) {
			addMenu(linkedPage, linksMap.get(linkedPage));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return menuWrapper;
	}

	/**
	 * Registers the given {@code listener} to the widget.
	 * 
	 * @param listener
	 *          The {@link SubMenuListener} instance. Does nothing if {@code null}.
	 */
	public void addListener(final SubMenuListener listener) {

		if (listener == null) {
			return;
		}

		listeners.add(listener);
	}

	/**
	 * Initialize the menu widget regarding the given {@code currentPage}.
	 * 
	 * @param currentPage
	 *          The current active page, may be {@code null}.
	 */
	public void initializeMenu(final Page currentPage, final Authentication authentication) {

		if (currentPage == null) {
			return;
		}

		for (final SubMenuItem menuItem : menuItems) {
			final PageRequest request = menuItem.getRequest();
			
			if (request != null && currentPage == request.getPage()) {
				activeMenu(menuItem);
			}
			
			menuItem.setVisible(menuItem.hasRequiredPermissions(authentication));
		}
	}
	
	/**
	 * Changes the required permissions to view the menu item associated to
	 * the given <code>page</code>.
	 * 
	 * @param page 
	 *			Page associated to the menu item to edit.
	 * @param requiredPermissions 
	 *			Required permissions to access this menu item.
	 */
	public void setRequiredPermissions(Page page, GlobalPermissionEnum... requiredPermissions) {
		for (final SubMenuItem menuItem : menuItems) {
			final PageRequest request = menuItem.getRequest();
			if (request != null && page == request.getPage()) {
				menuItem.setRequiredPermissions(requiredPermissions);
			}
		}
	}

	/**
	 * Method to add menu item to the sub-menu
	 * 
	 * @param linkedPage
	 * @param title
	 */
	private void addMenu(final Page linkedPage, final String title) {

		final SubMenuItem menuItem = new SubMenuItem(linkedPage);
		menuItem.setMenuItemTitle(title);
		if(Page.PROJECT_CALENDAR==linkedPage){
			menuItem.getElement().setId(AGENDA_SUB_MENU_ID);
		}
		menuItem.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (isActive(menuItem)) {
					return;
				}
				// Should not activate the menu here.
				// UI update should be done once target page has been loaded.
				fireEvent(menuItem);
			}

		});

		menuItems.add(menuItem);
		menuPanel.add(menuItem);
	}

	/**
	 * Checks if the given menu item is active.
	 * 
	 * @param menuItem
	 *          The menu item.
	 * @return <code>true</code> if the menu item is active, <code>false</code> otherwise.
	 */
	private boolean isActive(final SubMenuItem menuItem) {

		if (menuItem == null) {
			return false;
		}

		return menuItem.getStyleName().contains(CSS_SUBMENU_ACTIVE);

	}

	/**
	 * Fires the "menuActivated" event for the given menuItem.
	 * 
	 * @param menuItem
	 *          The sub-menu item.
	 */
	private void fireEvent(final SubMenuItem menuItem) {
		for (final SubMenuListener listener : listeners) {
			listener.onSubMenuClick(menuItem);
		}
	}

	/**
	 * Activates the given tab.
	 * 
	 * @param tab
	 *          The tab.
	 */
	private void activeMenu(final SubMenuItem menuItem) {

		if (menuItem == null) {
			return;
		}

		for (final SubMenuItem item : menuItems) {
			item.removeStyleName(CSS_SUBMENU_ACTIVE);
		}

		menuItem.addStyleName(CSS_SUBMENU_ACTIVE);
	}
	
	/**
	 * Show or hide the given <code>menuItem</code>.
	 * 
	 * @param menuItem
	 *			Menu item to show or hide.
	 * @param visible 
	 *			<code>true</code> to show the item, <code>false</code> to hide it.
	 */
	private void setMenuVisibility(final SubMenuItem menuItem, final boolean visible) {
		if(visible) {
			menuItem.getElement().getStyle().clearDisplay();
		} else {
			menuItem.getElement().getStyle().setDisplay(Style.Display.NONE);
		}
	}

}
