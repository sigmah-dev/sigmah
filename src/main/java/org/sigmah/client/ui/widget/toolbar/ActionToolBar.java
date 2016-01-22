package org.sigmah.client.ui.widget.toolbar;

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

import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;

import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Convenience subclass for the GXT {@link ToolBar} that offers a method to easily add button(s).
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ActionToolBar extends ToolBar {

	/**
	 * Adds a new {@link org.sigmah.client.ui.widget.button.Button} to the ToolBar.
	 * 
	 * @param label
	 *          The button label (may contain HTML).
	 * @param icon
	 *          The button icon. See {@link org.sigmah.client.ui.res.icon.IconImageBundle}.
	 * @return The added button.
	 */
	public Button addButton(final String label, final AbstractImagePrototype icon) {
		final Button button = Forms.button(label, icon);
		add(button);
		return button;
	}

}
