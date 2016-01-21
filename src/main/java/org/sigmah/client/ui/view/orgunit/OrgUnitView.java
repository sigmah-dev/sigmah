package org.sigmah.client.ui.view.orgunit;

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

import java.util.LinkedHashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitPresenter;
import org.sigmah.client.ui.res.icon.orgunit.OrgUnitImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.view.project.ProjectView;
import org.sigmah.client.ui.widget.SubMenuWidget;
import org.sigmah.client.ui.widget.SubMenuWidget.Orientation;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.util.Pair;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;

/**
 * {@link OrgUnitPresenter} view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class OrgUnitView extends AbstractView implements OrgUnitPresenter.View {

	// Grids coordinates.
	private static final Pair<Integer, Integer> HEADER_BANNER_LOGO_CELL = new Pair<Integer, Integer>(0, 0);
	private static final Pair<Integer, Integer> HEADER_BANNER_WIDGET_CELL = new Pair<Integer, Integer>(0, 1);

	// UI widgets.
	private ContentPanel orgUnitBannerPanel;
	private Grid orgUnitBannerGrid;

	private SubMenuWidget subMenu;
	private LayoutContainer subViewPlaceHolder;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		subViewPlaceHolder = Layouts.fit();

		final Map<Page, String> linksMap = new LinkedHashMap<Page, String>();
		linksMap.put(Page.ORGUNIT_DASHBOARD, I18N.CONSTANTS.orgUnitTabOverview());
		linksMap.put(Page.ORGUNIT_DETAILS, I18N.CONSTANTS.orgUnitTabInformations());
		linksMap.put(Page.ORGUNIT_CALENDAR, I18N.CONSTANTS.projectTabCalendar());
		linksMap.put(Page.ORGUNIT_REPORTS, I18N.CONSTANTS.projectTabReports());

		subMenu = new SubMenuWidget(Orientation.HORIZONTAL, linksMap);

		final LayoutContainer layoutContainerHeader = Layouts.vBox();
		layoutContainerHeader.add(createOrgUnitBannerPanel(), Layouts.vBoxData(Margin.BOTTOM));
		layoutContainerHeader.add(subMenu.asWidget());

		add(layoutContainerHeader, Layouts.borderLayoutData(LayoutRegion.NORTH, Layouts.BANNER_PANEL_HEIGHT, Margin.BOTTOM));
		add(subViewPlaceHolder);
	}

	/**
	 * Creates the OrgUnit banner panel.
	 * 
	 * @return The OrgUnit banner panel.
	 */
	private Component createOrgUnitBannerPanel() {

		// Main panel.
		orgUnitBannerPanel = Panels.content(I18N.CONSTANTS.orgunit()); // Temporary title.

		// Main grid.
		orgUnitBannerGrid = new Grid(1, 2);
		orgUnitBannerGrid.addStyleName(ProjectView.STYLE_HEADER_BANNER);
		orgUnitBannerGrid.setCellPadding(0);
		orgUnitBannerGrid.setCellSpacing(0);
		orgUnitBannerGrid.setWidth("100%");
		orgUnitBannerGrid.setHeight("100%");

		// Logo cell.
		orgUnitBannerGrid.getCellFormatter().setStyleName(HEADER_BANNER_LOGO_CELL.left, HEADER_BANNER_LOGO_CELL.right, ProjectView.STYLE_HEADER_BANNER_LOGO);
		orgUnitBannerGrid.setWidget(HEADER_BANNER_LOGO_CELL.left, HEADER_BANNER_LOGO_CELL.right, OrgUnitImageBundle.ICONS.orgUnitLarge().createImage());

		orgUnitBannerPanel.add(orgUnitBannerGrid);

		return orgUnitBannerPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOrgUnitTitle(final String orgUnitName) {

		// Panel header title.
		orgUnitBannerPanel.setHeadingText(orgUnitName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOrgUnitBanner(final Widget bannerWidget) {
		orgUnitBannerGrid.setWidget(HEADER_BANNER_WIDGET_CELL.left, HEADER_BANNER_WIDGET_CELL.right, bannerWidget);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HTMLTable buildBannerTable(final int rows, final int cols) {

		final Grid gridLayout = new Grid(rows, cols);
		gridLayout.addStyleName(ProjectView.STYLE_HEADER_BANNER_FLEX);
		gridLayout.setCellPadding(0);
		gridLayout.setCellSpacing(0);
		gridLayout.setWidth("100%");
		gridLayout.setHeight("100%");

		for (int i = 0; i < gridLayout.getColumnCount() - 1; i++) {
			gridLayout.getColumnFormatter().setWidth(i, "325px");
		}

		return gridLayout;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutContainer getPlaceHolder() {
		return subViewPlaceHolder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SubMenuWidget getSubMenuWidget() {
		return subMenu;
	}

	@Override
	public ContentPanel getOrgUnitBannerPanel() {
		return orgUnitBannerPanel;
	}

}
