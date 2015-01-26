package org.sigmah.client.ui.theme;

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
