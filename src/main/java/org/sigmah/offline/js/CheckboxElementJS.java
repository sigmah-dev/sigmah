package org.sigmah.offline.js;

import org.sigmah.shared.dto.element.CheckboxElementDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class CheckboxElementJS extends FlexibleElementJS {

	protected CheckboxElementJS() {
	}
	
	public static CheckboxElementJS toJavaScript(CheckboxElementDTO checkboxElementDTO) {
		return Values.createJavaScriptObject(CheckboxElementJS.class);
	}
	
	@Override
	protected CheckboxElementDTO createDTO() {
		return new CheckboxElementDTO();
	}
}
