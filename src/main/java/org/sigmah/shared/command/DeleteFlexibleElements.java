package org.sigmah.shared.command;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DeleteFlexibleElements extends AbstractCommand<VoidResult> {

	private List<FlexibleElementDTO> flexElts;

	protected DeleteFlexibleElements() {
		// Serialization.
	}

	public DeleteFlexibleElements(List<FlexibleElementDTO> flexElts) {
		this.setFlexibleElements(flexElts);
	}

	public void setFlexibleElements(List<FlexibleElementDTO> flexElts) {
		this.flexElts = flexElts;
	}

	public List<FlexibleElementDTO> getFlexibleElements() {
		return flexElts;
	}

	public void addFlexibleElement(FlexibleElementDTO flexElt) {
		if (flexElts == null) {
			flexElts = new ArrayList<FlexibleElementDTO>();
		}
		this.flexElts.add(flexElt);
	}

	public void removeFlexibleElement(FlexibleElementDTO flexElt) {
		this.flexElts.remove(flexElt);
	}
}
