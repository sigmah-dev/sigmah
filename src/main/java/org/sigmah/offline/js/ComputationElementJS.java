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
