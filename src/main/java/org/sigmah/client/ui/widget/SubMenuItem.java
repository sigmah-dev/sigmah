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


import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;

/**
 * Sub-menu item widget.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SubMenuItem extends Composite implements HasClickHandlers {

	private static final String CSS_SUBMENU_ITEM = "sub-menu-item";

	private final PageRequest request;
	private final HTML titlePanel;
	private GlobalPermissionEnum[] requiredPermissions;
	
	/**
	 * Initializes a new item navigating to the given {@code page}.
	 * 
	 * @param page
	 *          The {@link Page} to navigate to.
	 * @param requiredPermissions
	 *			Required permissions to access this menu item.
	 */
	public SubMenuItem(final Page page, GlobalPermissionEnum... requiredPermissions) {
		this(page != null ? page.request() : null, requiredPermissions);
	}

	/**
	 * Initializes a new item navigating to the given {@code request}.
	 * 
	 * @param request
	 *          The {@link PageRequest} to navigate to.
	 * @param requiredPermissions
	 *			Required permissions to access this menu item.
	 */
	public SubMenuItem(final PageRequest request, GlobalPermissionEnum... requiredPermissions) {

		this.request = request;
		this.requiredPermissions = requiredPermissions;

		titlePanel = new HTML();
		titlePanel.setStyleName(CSS_SUBMENU_ITEM);

		initWidget(titlePanel);
	}

	/**
	 * Returns the {@link PageRequest} associated to the current sub-menu item.
	 * 
	 * @return The {@link PageRequest} associated to the current sub-menu item, or {@code null}.
	 */
	public PageRequest getRequest() {
		return request;
	}

	/**
	 * Sets the sub-menu item title value (as well as tool-tip value).
	 * 
	 * @param title
	 *          The new title value.
	 */
	public void setMenuItemTitle(final String title) {
		setTitle(title);
		titlePanel.setHTML(title);
	}

	/**
	 * Sets the required permissions to view this menu item.
	 * 
	 * @param requiredPermissions 
	 *			Array of all the permissions required to view this menu item.
	 */
	public void setRequiredPermissions(GlobalPermissionEnum[] requiredPermissions) {
		this.requiredPermissions = requiredPermissions;
	}
	
	/**
	 * Returns <code>true</code> if the given <code>authentication</code> has
	 * all the required permissions to view this menu item.
	 * 
	 * @param authentication Authenticated user information.
	 * @return <code>true</code> if the user has all the required permissions, 
	 * <code>false</code> otherwise.
	 */
	public boolean hasRequiredPermissions(Authentication authentication) {
		if(authentication == null) {
			return requiredPermissions.length == 0;
		} else {
			return ProfileUtils.isGranted(authentication, requiredPermissions);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return titlePanel.addClickHandler(handler);
	}

}
