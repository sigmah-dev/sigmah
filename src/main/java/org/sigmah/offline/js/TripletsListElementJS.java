package org.sigmah.offline.js;

import org.sigmah.shared.dto.element.TripletsListElementDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class TripletsListElementJS extends FlexibleElementJS {
	
	protected TripletsListElementJS() {
	}
	
	public static TripletsListElementJS toJavaScript(TripletsListElementDTO tripletsListElementDTO) {
		return Values.createJavaScriptObject(TripletsListElementJS.class);
	}
	
	@Override
	protected TripletsListElementDTO createDTO() {
		return new TripletsListElementDTO();
	}
}
