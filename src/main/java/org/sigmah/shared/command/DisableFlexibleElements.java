package org.sigmah.shared.command;

import java.util.List;
import org.sigmah.shared.command.base.Command;

import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Enable or disable flexible elements.
 * 
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DisableFlexibleElements implements Command<VoidResult> {
	private static final long serialVersionUID = 3902096705358448566L;

	private List<FlexibleElementDTO> flexibleElements;
	private boolean disable;
	
	protected DisableFlexibleElements() {
		// Serialization.
	}
	
	public DisableFlexibleElements(List<FlexibleElementDTO> flexibleElements, boolean disable) {
		this.flexibleElements = flexibleElements;
		this.disable = disable;
	}

	public List<FlexibleElementDTO> getFlexibleElements() {
		return flexibleElements;
	}

	public boolean isDisable() {
		return disable;
	}

}
