package org.sigmah.client.ui.view.zone;

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

import org.sigmah.client.ui.presenter.zone.MenuBannerPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.tab.TabBar;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Menu banner view.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class MenuBannerView extends AbstractView implements MenuBannerPresenter.View {

	// CSS.
	private static final String CSS_PANEL = "bar";
	private static final String CSS_DECORATION_LEFT = "bar-decoration-left";
	private static final String CSS_DECORATION_LEFT_ROUND = "bar-decoration-left-round";
	private static final String CSS_TABS = "tabs";
	private static final String CSS_BUTTONS = "menu-buttons";
	private static final String CSS_BUTTON_LEFT = "menu-button-left";
	private static final String CSS_BUTTON_RIGHT = "menu-button-right";

	private Panel menuPanel;
	private TabBar tabBar;
	private Button leftButton;
	private Button rightButton;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// Tabs.

		final SimplePanel tabsPanel = new SimplePanel();
		tabsPanel.addStyleName(CSS_TABS);

		tabBar = new TabBar();
		tabsPanel.add(tabBar);
		
		// Decoration.

		final SimplePanel decorationLeftPanel = new SimplePanel();
		decorationLeftPanel.addStyleName(CSS_DECORATION_LEFT);

		final SimplePanel decorationLeftRoundPanel = new SimplePanel();
		decorationLeftRoundPanel.addStyleName(CSS_DECORATION_LEFT_ROUND);

		// Buttons.

		final FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.addStyleName(CSS_BUTTONS);

		leftButton = new Button();
		leftButton.addStyleName(CSS_BUTTON_LEFT);

		rightButton = new Button();
		rightButton.addStyleName(CSS_BUTTON_RIGHT);

		buttonsPanel.add(leftButton);
		buttonsPanel.add(rightButton);

		// Main panel.

		menuPanel = new FlowPanel();
		menuPanel.addStyleName(CSS_PANEL);

		menuPanel.add(tabsPanel);
		menuPanel.add(decorationLeftPanel);
		menuPanel.add(decorationLeftRoundPanel);
		menuPanel.add(buttonsPanel);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewRevealed() {
		// Nothing to do here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getMenuPanel() {
		return menuPanel;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TabBar getTabBar() {
		return tabBar;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasClickHandlers getLeftHandler() {
		return leftButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasClickHandlers getRightHandler() {
		return rightButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLeftHandlerEnabled(boolean enabled) {
		leftButton.setEnabled(enabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRightHandlerEnabled(boolean enabled) {
		rightButton.setEnabled(enabled);
	}

}
