package org.sigmah.client.ui.view.admin;

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
import org.sigmah.client.ui.presenter.admin.AdminPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.SubMenuWidget;
import org.sigmah.client.ui.widget.SubMenuWidget.Orientation;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.inject.Singleton;

/**
 * {@link AdminPresenter}'s view implementation.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class AdminView extends AbstractView implements AdminPresenter.View {

	private SubMenuWidget subMenu;
	private LayoutContainer subViewPlaceHolder;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// Sub-menu widget.
		final Map<Page, String> linksMap = new LinkedHashMap<Page, String>();
		linksMap.put(Page.ADMIN_USERS, I18N.CONSTANTS.adminUsers());
		linksMap.put(Page.ADMIN_ORG_UNITS, I18N.CONSTANTS.adminOrgUnit());
		linksMap.put(Page.ADMIN_PROJECTS_MODELS, I18N.CONSTANTS.adminProjectModels());
		linksMap.put(Page.ADMIN_ORG_UNITS_MODELS, I18N.CONSTANTS.adminOrgUnitsModels());
		linksMap.put(Page.ADMIN_CONTACT_MODELS, I18N.CONSTANTS.adminContactModels());
		linksMap.put(Page.ADMIN_REPORTS_MODELS, I18N.CONSTANTS.adminProjectModelReports());
		linksMap.put(Page.ADMIN_CATEGORIES, I18N.CONSTANTS.adminCategories());
		linksMap.put(Page.ADMIN_IMPORTATION_SCHEME, I18N.CONSTANTS.adminImportationSchemes());
		linksMap.put(Page.ADMIN_PARAMETERS, I18N.CONSTANTS.adminManagementTitle());
		subMenu = new SubMenuWidget(Orientation.VERTICAL, linksMap);

		// Sub-views place holder.
		subViewPlaceHolder = Layouts.fit();

		add(subMenu, Layouts.borderLayoutData(LayoutRegion.WEST, Layouts.VERTICAL_MENU_WIDTH, Margin.RIGHT));
		add(subViewPlaceHolder);
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

}
