package org.sigmah.client.page;

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

	static final String KEY_SUFFIX = "-";

	/**
	 * Stores the {@link Page} tokens with their corresponding instance.
	 */
	static final Map<String, Page> PAGES = new HashMap<String, Page>();

}
