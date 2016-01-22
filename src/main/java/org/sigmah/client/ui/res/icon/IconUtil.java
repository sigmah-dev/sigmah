package org.sigmah.client.ui.res.icon;

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
