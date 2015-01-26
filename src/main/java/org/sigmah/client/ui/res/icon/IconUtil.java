package org.sigmah.client.ui.res.icon;

import com.google.gwt.core.client.GWT;

/**
 * Icons utility class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class IconUtil {

	private IconUtil() {
		// ONly provides static constants.
	}

	public static String iconHtml(final String spriteStyle) {
		// We can't use the normal div produced by GWT because the icons need to be inline to display properly in the
		// existing GXT html structure.
		return "<img width='16' height='16' src='" + GWT.getModuleBaseURL() + "clear.cache.gif' class='" + spriteStyle + "'>";
	}

}
