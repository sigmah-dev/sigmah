package org.sigmah.offline.js;

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

import org.sigmah.shared.dto.element.CheckboxElementDTO;

/**
 * JavaScript version of <code>CheckboxElementDTO</code>.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class CheckboxElementJS extends FlexibleElementJS {

	/**
	 * Protected constructor. Required by GWT JNSI compiler.
	 */
	protected CheckboxElementJS() {
		// Nothing.
	}
	
	/**
	 * Maps the given DTO to a new JavaScript object.
	 * 
	 * @param checkboxElementDTO DTO to map.
	 * @return A new JavaScript object.
	 */
	public static CheckboxElementJS toJavaScript(CheckboxElementDTO checkboxElementDTO) {
		return Values.createJavaScriptObject(CheckboxElementJS.class);
	}
	
	/**
	 * Maps this JavaScript object to a new DTO.
	 * 
	 * @return A new CheckboxElementDTO.
	 */
	protected CheckboxElementDTO toCheckboxElementDTO() {
		return new CheckboxElementDTO();
	}
	
}
