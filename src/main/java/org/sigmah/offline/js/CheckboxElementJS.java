package org.sigmah.offline.js;

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
