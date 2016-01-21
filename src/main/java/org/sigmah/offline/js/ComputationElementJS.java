package org.sigmah.offline.js;

import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 * JavaScript version of <code>ComputationElementDTO</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class ComputationElementJS extends FlexibleElementJS {
	
	/**
	 * Protected constructor. Required by GWT JNSI compiler.
	 */
	protected ComputationElementJS() {
		// Nothing.
	}
	
	/**
	 * Maps the given DTO to a new JavaScript object.
	 * 
	 * @param computationElementDTO DTO to map.
	 * @return A new JavaScript object.
	 */
	public static FlexibleElementJS toJavaScript(ComputationElementDTO computationElementDTO) {
		final ComputationElementJS js = Values.createJavaScriptObject(ComputationElementJS.class);
		js.setRule(computationElementDTO.getRule());
		js.setMinimumValue(computationElementDTO.getMinimumValue());
		js.setMaximumValue(computationElementDTO.getMaximumValue());
		return js;
	}
	
	/**
	 * Maps this JavaScript object to a new DTO.
	 * 
	 * @return A new ComputationElementDTO.
	 */
	protected ComputationElementDTO toComputationElementDTO() {
		final ComputationElementDTO dto = new ComputationElementDTO();
		dto.setRule(getRule());
		dto.setMinimumValue(getMinimumValue());
		dto.setMaximumValue(getMaximumValue());
		return dto;
	}
	
	public native String getRule() /*-{
		return this.rule;
	}-*/;

	public native void setRule(String rule) /*-{
		this.rule = rule;
	}-*/;
	
	public native String getMinimumValue() /*-{
		return this.minimumValue;
	}-*/;

	public native void setMinimumValue(String minimumValue) /*-{
		this.minimumValue = minimumValue;
	}-*/;
	
	public native String getMaximumValue() /*-{
		return this.maximumValue;
	}-*/;

	public native void setMaximumValue(String maximumValue) /*-{
		this.maximumValue = maximumValue;
	}-*/;
	
}
