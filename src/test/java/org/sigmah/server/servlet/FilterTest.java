package org.sigmah.server.servlet;

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

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests filter mechanisms.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class FilterTest {

	@Test
	public void cacheFilterTest() {
		Assert.assertFalse(containsAny(null, CACHE_FILTERS));
		Assert.assertFalse(containsAny("", CACHE_FILTERS));
		Assert.assertTrue(containsAny("__.cache.__", CACHE_FILTERS));
		Assert.assertFalse(containsAny("__.cache .__", CACHE_FILTERS));
		Assert.assertFalse(containsAny("resource.cache._", NO_CACHE_FILTERS));
		Assert.assertTrue(containsAny("resource.nocache.com", NO_CACHE_FILTERS));
		Assert.assertTrue(containsAny("/gxt", CACHE_FILTERS));
		Assert.assertTrue(containsAny("___/gxt", CACHE_FILTERS));
		Assert.assertTrue(containsAny("___/gxt___", CACHE_FILTERS));
		Assert.assertFalse(containsAny("___/gxt___", NO_CACHE_FILTERS));
	}

	/**
	 * Resources containing one of these value(s) will <b>never</b> be cached.
	 * 
	 * @see #containsAny(String, String...)
	 */
	private static final String[] NO_CACHE_FILTERS = { ".nocache."
	};

	/**
	 * Resources containing one of these value(s) will be cached.
	 * 
	 * @see #containsAny(String, String...)
	 */
	private static final String[] CACHE_FILTERS = {
																									".cache.",
																									"/gxt"
	};

	private static boolean containsAny(final String seq, final String... searchSeqs) {

		if (seq == null || searchSeqs == null) {
			return false;
		}

		for (final String searchSeq : searchSeqs) {
			if (StringUtils.containsIgnoreCase(seq, searchSeq)) {
				return true;
			}
		}

		return false;
	}

}
