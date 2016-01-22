package org.sigmah.client.ui.view.base;

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

/**
 * V1.3
 * A Page/PageState that implements this interface can be displayed in a tab.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
/**
 * V2.0
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
public interface TabPage {

	/**
	 * Returns the title to display in the tab bar.
	 * 
	 * @return the title to display in the tab bar.
	 */
	String getTabTitle();
}
