package org.sigmah.client.util;

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

import java.util.HashMap;

import org.sigmah.client.page.Page;

/**
 * Maps the new {@link Page} anchors to the old <code>Sigmah 1.2</code> anchors (essentially to maintain the help
 * manuals table of contents).
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
// TODO [UI] Delegates the URL mapping to the Drupal generation tool ?
// TODO [UI] The manual.html pages reference nonexistent resources (images, CSS).
public final class SigmahOldAnchors {

	private SigmahOldAnchors() {
		// Provide only static methods.
	}

	private static final HashMap<Page, String> map;

	static {

		map = new HashMap<Page, String>();

		map.put(Page.DASHBOARD, "welcome");
		map.put(Page.ORGUNIT_DASHBOARD, "orgunit/0");
		map.put(Page.ORGUNIT_DETAILS, "orgunit/1");
		map.put(Page.ORGUNIT_CALENDAR, "orgunit/2");
		map.put(Page.ORGUNIT_REPORTS, "orgunit/3");
		map.put(Page.PROJECT_DASHBOARD, "project/0");
		map.put(Page.PROJECT_DETAILS, "project/1");
		map.put(Page.PROJECT_LOGFRAME, "project/2");
		map.put(Page.PROJECT_INDICATORS_MANAGEMENT, "project/3");
		map.put(Page.PROJECT_INDICATORS_ENTRIES, "project/4");
		map.put(Page.PROJECT_CALENDAR, "project/5");
		map.put(Page.PROJECT_REPORTS, "project/6");
		map.put(Page.ADMIN_USERS, "Administration/0");
		map.put(Page.ADMIN_ORG_UNITS, "Administration/1");
		map.put(Page.ADMIN_PROJECTS_MODELS, "Administration/2");
		map.put(Page.ADMIN_ORG_UNITS_MODELS, "Administration/3");
		map.put(Page.ADMIN_REPORTS_MODELS, "Administration/4");
		map.put(Page.ADMIN_CATEGORIES, "Administration/5");
		map.put(Page.ADMIN_PARAMETERS, "Administration/6");

	}

	/**
	 * Gets the old anchor for the given Page.
	 * 
	 * @param page
	 *          the page.
	 * @return the old anchor or the new anchor if there is no mapping.
	 */
	public static String map(Page page) {
		final String old = map.get(page);
		return ClientUtils.isNotBlank(old) ? old : page.getToken();
	}

}
