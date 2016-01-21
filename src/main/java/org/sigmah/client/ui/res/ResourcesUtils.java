package org.sigmah.client.ui.res;

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

import org.sigmah.client.util.ClientUtils;

/**
 * Provides useful methods to manipulate ui resources on client side.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public final class ResourcesUtils {

	private ResourcesUtils() {
		// utility class.
	}

	// URLs.
	private static final String RESOURCES_URL = "sigmah/";
	private static final String IMAGES_URL = RESOURCES_URL + "images/";

	/**
	 * Builds and returns an absolute URL to request an image.
	 * 
	 * @param name
	 *          The image's name.
	 * @return The URL.
	 */
	public static String buildImageURL(String name) {
		return ClientUtils.appendToApplicationUrl(IMAGES_URL + name);
	}

	/**
	 * Builds and returns an image CSS property.
	 * 
	 * @param url
	 *          The image URL (relative or absolute).
	 * @return The CSS property.
	 */
	public static String buildCSSImageProperty(String url) {
		return "url(" + url + ")";
	}

}
