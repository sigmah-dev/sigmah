package org.sigmah.client.page;

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
import java.util.Map;

/**
 * Utility class for {@link Page} class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class Pages {

	private Pages() {
		// Only provides static constants.
	}

	static final String PROJECT_PARENT_KEY = "project";
	static final String ORGUNIT_PARENT_KEY = "orgunit";
	static final String ADMIN_PARENT_KEY = "admin";
	static final String CONTACT_PARENT_KEY = "contact";

	static final String KEY_SUFFIX = "-";

	/**
	 * Stores the {@link Page} tokens with their corresponding instance.
	 */
	static final Map<String, Page> PAGES = new HashMap<String, Page>();

}
