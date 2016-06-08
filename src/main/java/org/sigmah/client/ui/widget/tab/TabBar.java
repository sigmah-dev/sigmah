package org.sigmah.client.ui.widget.tab;

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

import com.google.gwt.animation.client.Animation;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;
import java.util.Map;

/**
 * This widget displays a bar of {@link Tab}.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class TabBar implements IsWidget {

	// Widgets.
	private final FlowPanel tabContainer;
	private final AbsolutePanel scrollPanel;

	// Stores the tabs.
	private final List<Tab> tabs;
	private final Map<TabId, Tab> tabsIds;
	
	/**
	 * Tab currently displayed.
	 */
	private Tab activeTab;
	private Tab previousTab;

	// Animation.
	private static final int ANIMATION_DURATION = 200;
	private final Animation scrollAnimation;
	private int direction;
	private int initialPosition;
	private double distance;
	private int leftTabIndex;

	// Listeners.

	private final List<TabBarListener> listeners;

	/**
	 * Tab bar events listener.
	 * 
	 * @author Tom Miette (tmiette@ideia.fr)
	 */
	public static interface TabBarListener {

		void tabShownOrHiddden(TabBar bar);

		void tabAdded(TabBar bar, Tab tab);

		void tabRemoved(TabBar bar, Tab tab);

		void tabActivated(TabBar bar, Tab tab);

	}

	/**
	 * Creates a new TabBar using the given TabModel.
	 */
	public TabBar() {

		// Widgets.

		tabContainer = new FlowPanel();
		tabContainer.setHeight("100%");

		scrollPanel = new AbsolutePanel();
		scrollPanel.setSize("100%", "100%");

		scrollPanel.add(tabContainer, 0, 0);

		// To store the tabs.

		tabs = new ArrayList<Tab>();
		tabsIds = new HashMap<TabId, Tab>();

		// Animation.

		scrollAnimation = new Animation() {

			@Override
			protected void onUpdate(double progress) {

				int x = (int) (distance * progress) * direction + initialPosition;

				if (x > 0) {
					x = 0;
				}

				scrollPanel.setWidgetPosition(tabContainer, x, 0);

			}

			@Override
			protected void onComplete() {

				int x = (int) distance * direction + initialPosition;

				if (x > 0) {
					x = 0;
				}

				scrollPanel.setWidgetPosition(tabContainer, x, 0);

				// Fires event.
				fireTabShownOrHiddden(TabBar.this);

			}

		};

		direction = 1;
		initialPosition = 0;
		distance = 0.0;
		leftTabIndex = 0;

		// Listeners.
		listeners = new ArrayList<TabBarListener>();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return scrollPanel;
	}

	/**
	 * Gets the total number of tabs (hidden or not).
	 * 
	 * @return the total number of tabs.
	 */
	public int getNumberOfTabs() {
		return tabs.size();
	}

	/**
	 * Gets the numbers of tabs currently visible.
	 * 
	 * @return the numbers of tabs currently visible.
	 */
	public int getNumberOfVisibleTabs() {
		return getNumberOfTabs() - getNumberOfHiddenTabs();
	}

	/**
	 * Gets the number of tabs currently hidden.
	 * 
	 * @return the number of tabs currently hidden.
	 */
	public int getNumberOfHiddenTabs() {
		return leftTabIndex;
	}

	/**
	 * Hides the first displayed tab on the left.
	 */
	public void hideTab() {

		int currentPosition = scrollPanel.getWidgetLeft(tabContainer);
		distance = initialPosition + distance * direction - currentPosition;
		if (distance < 0) {
			distance = -distance;
		}

		if (leftTabIndex < tabs.size() - 1) {
			distance += getTabWidth(tabs.get(leftTabIndex));
			leftTabIndex++;
		}

		direction = -1;

		initialPosition = currentPosition;
		scrollAnimation.run(ANIMATION_DURATION);

	}

	/**
	 * Shows the first hidden tab on the left.
	 */
	public void showTab() {

		int currentPosition = scrollPanel.getWidgetLeft(tabContainer);
		distance = initialPosition + distance * direction - currentPosition;
		if (distance < 0) {
			distance = -distance;
		}

		if (leftTabIndex > 0) {
			leftTabIndex--;
			distance += getTabWidth(tabs.get(leftTabIndex));
		}

		direction = 1;

		initialPosition = currentPosition;
		scrollAnimation.run(ANIMATION_DURATION);

	}

	/**
	 * Adds and activates the given tab. If the tab already exists (based on its unique id), it is only activated.
	 * 
	 * @param tab
	 *          The tab.
	 */
	public void addTab(final Tab tab) {
		
		// Nothing to do.
		if (tab == null || tab.getId() == null) {
			return;
		}

		// Builds a new tab if necessary.
		Tab t = null;
		if ((t = tabsIds.get(tab.getId())) == null) {

			tab.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (isActive(tab)) {
						// Active tab click action: nothing to do.
						return;
					}
					activateTab(tab, false, true);
				}

			});

			if (tab.isCloseable()) {
				tab.addCloseHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						removeTab(tab);
					}
				});
			}

			// Stores the new tab.
			t = tab;
			tabContainer.add(t);
			tabs.add(t);
			tabsIds.put(t.getId(), t);

			// Fires event.
			fireTabAdded(tab);

		}
		// Or just update its title.
		else {
			updateTitle(t.getId(), tab.getTitle());
		}

		// Activates the tab
		activateTab(t, true, false);

	}

	/**
	 * Updates the title of the tab with the given id.
	 * 
	 * @param id
	 *          The id.
	 * @param title
	 *          The new title.
	 */
	public void updateTitle(TabId id, String title) {

		final Tab tab = tabsIds.get(id);
		if (tab != null) {
			tab.setTabTitle(title);
		}

	}
	
	/**
	 * Updates the title of the tab with the given id.
	 * 
	 * @param id
	 *          The id.
	 * @param title
	 *          The new title.
	 */
	/**
	 * Updates the dom of the tab with the given id.
	 * @param id
	 * @param domId 
	 */
	public void updateClosableTabDomId(TabId id, String domId) {

		final Tab tab = tabsIds.get(id);
		if (tab != null) {			
			if(tab.getClosePanel()!=null){
				tab.getClosePanel().getElement().setId(domId);
			}
		}

	}

	/**
	 * Removes the given {@code id} corresponding tab.
	 * 
	 * @param id
	 *          The tab id.
	 */
	public void removeTab(final TabId id) {

		removeTab(tabsIds.get(id));

	}
	
	/**
	 * Removes the tab currently displayed and activates to the previous tab
	 * (if not null).
	 */
	public void removeActiveTab() {
		final Tab tabToRemove = this.activeTab;
		
		activateTab(previousTab, true, true);
		removeTab(tabToRemove);
		
		this.previousTab = null;
	}

	/**
	 * Activates the given tab.
	 * 
	 * @param tab
	 *          The tab.
	 * @param updateStyle
	 *          {@code true} to update tabs style (active), {@code false} to leave current styles.
	 * @param fireEvent
	 *          If the {@link TabBarListener#tabActivated(TabBar, Tab)} event must be fired.
	 */
	private void activateTab(Tab tab, boolean updateStyle, boolean fireEvent) {

		if (tab == null) {
			return;
		}
		
		this.previousTab = activeTab;
		this.activeTab = tab;

		if (updateStyle) {
			for (final Tab t : tabs) {
				t.removeStyleName(Tab.CSS_TAB_ACTIVE);
			}

			tab.addStyleName(Tab.CSS_TAB_ACTIVE);
		}

		// Fires event.
		if (fireEvent) {
			fireTabActivated(tab);
		}

	}

	/**
	 * Checks if the given tab is active.
	 * 
	 * @param tab
	 *          The tab.
	 * @return <code>true</code> if the tab is active, <code>false</code> otherwise.
	 */
	private boolean isActive(Tab tab) {

		if (tab == null) {
			return false;
		}

		return tab.getStyleName().contains(Tab.CSS_TAB_ACTIVE);

	}

	/**
	 * Removes the given tab.
	 * 
	 * @param tab
	 *          The tab
	 */
	private void removeTab(final Tab tab) {

		// Nothing to do.
		if (tab == null || tab.getId() == null) {
			return;
		}

		// Is the tab active ?
		final boolean active = isActive(tab);

		// Removes the tab.
		final int tabIndex = tabs.indexOf(tab);
		tabContainer.remove(tab);
		tabs.remove(tab);
		tabsIds.remove(tab.getId());

		// Fires event.
		fireTabRemoved(tab);

		// Activates the first tab ?
		if (active) {
			activateTab(tabs.get(tabIndex == tabs.size() ? tabIndex - 1 : tabIndex), true, true);
		}

	}

	/**
	 * Gets the given tab total width (including borders, margins, paddings, etc.).
	 * 
	 * @param tab
	 *          The tab.
	 * @return The width.
	 */
	private int getTabWidth(Tab tab) {
		return getTabWidth(tab.getElement());
	}

	private static final native int getTabWidth(com.google.gwt.dom.client.Element element) /*-{
		var width = 0;
		var style = $wnd.getComputedStyle(element, null);
		width += parseInt(style.width) + parseInt(style.borderLeftWidth)
				+ parseInt(style.borderRightWidth) + parseInt(style.marginLeft)
				+ parseInt(style.marginRight) + parseInt(style.paddingLeft)
				+ parseInt(style.paddingRight);

		return width;
	}-*/;

	public void addListener(TabBarListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public void removeListener(TabBarListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}

	protected void fireTabAdded(Tab tab) {
		for (final TabBarListener listener : listeners) {
			listener.tabAdded(this, tab);
		}
	}

	protected void fireTabRemoved(Tab tab) {
		for (final TabBarListener listener : listeners) {
			listener.tabRemoved(this, tab);
		}
	}

	protected void fireTabActivated(Tab tab) {
		for (final TabBarListener listener : listeners) {
			listener.tabActivated(this, tab);
		}
	}

	protected void fireTabShownOrHiddden(TabBar tab) {
		for (final TabBarListener listener : listeners) {
			listener.tabShownOrHiddden(this);
		}
	}

}
