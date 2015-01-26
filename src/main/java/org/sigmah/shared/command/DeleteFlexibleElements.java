package org.sigmah.shared.command;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.element.FlexibleElementDTO;


public class DeleteFlexibleElements implements Command<VoidResult> {
	
	private List<FlexibleElementDTO> flexElts;
	
	private static final long serialVersionUID = -6750954216001738221L;

	protected DeleteFlexibleElements() {
		//serialization
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

	public void addFlexibleElement(FlexibleElementDTO flexElt){
		if(flexElts == null){
			flexElts = new ArrayList<FlexibleElementDTO>();
		}
		this.flexElts.add(flexElt);
	}
	
	public void removeFlexibleElement(FlexibleElementDTO flexElt){
		this.flexElts.remove(flexElt);
	}
}
