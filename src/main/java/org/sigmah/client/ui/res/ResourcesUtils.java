package org.sigmah.client.ui.res;

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
