package org.sigmah.client.ui.theme;

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

import com.extjs.gxt.ui.client.util.ThemeManager;

/**
 * Defines a UI theme for Sigmah.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public abstract class Theme extends com.extjs.gxt.ui.client.util.Theme {

	private static final long serialVersionUID = -1833042199494220002L;

	/**
	 * Builds the theme.
	 * 
	 * @param id
	 *          The theme id (the name and the default CSS file of the theme are computed from the id).
	 */
	public Theme(String id) {
		super(id, ClientUtils.capitalize(id), "sigmah/themes/css/xtheme-" + id + ".css");
		ThemeManager.register(this);
	}

}
