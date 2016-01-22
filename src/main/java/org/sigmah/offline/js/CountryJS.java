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

import org.sigmah.shared.dto.country.CountryDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class CountryJS extends JavaScriptObject {
	
	protected CountryJS() {
	}
	
	public static CountryJS toJavaScript(CountryDTO countryDTO) {
		final CountryJS countryJS = Values.createJavaScriptObject(CountryJS.class);
		
		countryJS.setId(countryDTO.getId());
		countryJS.setName(countryDTO.getName());
		countryJS.setCodeISO(countryDTO.getCodeISO());
		countryJS.setBounds(BoundingBoxJS.toJavaScript(countryDTO.getBounds()));
		
		return countryJS;
	}
	
	public CountryDTO toDTO() {
		final CountryDTO dto = new CountryDTO();
		
		dto.setId(getId());
		dto.setName(getName());
		dto.setCodeISO(getCodeISO());
		if(getBounds() != null) {
			dto.setBounds(getBounds().toDTO());
		}
		
		return dto;
	}
	
	public native void setId(int id) /*-{
		this.id = id;
	}-*/;
	
	public native int getId() /*-{
		return this.id;
	}-*/;
			
	public native void setName(String name) /*-{
		this.name = name;
	}-*/;
	
	public native String getName() /*-{
		return this.name;
	}-*/;
	
	public native void setCodeISO(String codeISO) /*-{
		this.codeISO = codeISO;
	}-*/;
	
	public native String getCodeISO() /*-{
		return this.codeISO;
	}-*/;
	
	public native void setBounds(BoundingBoxJS boundingBoxJS) /*-{
		this.bounds = boundingBoxJS;
	}-*/;
	
	public native BoundingBoxJS getBounds() /*-{
		return this.bounds;
	}-*/;
	
}
