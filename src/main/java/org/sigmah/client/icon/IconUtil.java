package org.sigmah.client.icon;

import com.google.gwt.core.client.GWT;

public class IconUtil {

	public static String iconHtml(String spriteStyle) {
		// we can't use the normal div produced by GWT because the icons need to be inline
		// to display properly in the existing GXT html structure
		return "<img width='16' height='16' src='" + GWT.getModuleBaseURL() + "clear.cache.gif' class='" + spriteStyle + "'>";
	}


}
