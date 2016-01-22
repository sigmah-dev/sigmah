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

import org.sigmah.shared.dto.referential.ValueEventChangeType;
import org.sigmah.shared.dto.value.TripletValueDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class TripletValueJS extends ListableValueJS {
	
	protected TripletValueJS() {
	}
	
	public static TripletValueJS toJavaScript(TripletValueDTO tripletValueDTO) {
		final TripletValueJS tripletValueJS = Values.createJavaScriptObject(TripletValueJS.class);
		tripletValueJS.setListableValueType(Type.TRIPLET);

		tripletValueJS.setId(tripletValueDTO.getId());
		tripletValueJS.setCode(tripletValueDTO.getCode());
		tripletValueJS.setName(tripletValueDTO.getName());
		tripletValueJS.setPeriod(tripletValueDTO.getPeriod());
		tripletValueJS.setIndex(tripletValueDTO.getIndex());
		tripletValueJS.setChangeType(tripletValueDTO.getType());
		
		return tripletValueJS;
	}
	
	public TripletValueDTO toTripletValueDTO() {
		final TripletValueDTO tripletValueDTO = new TripletValueDTO();
		
		tripletValueDTO.setId(getId());
		tripletValueDTO.setCode(getCode());
		tripletValueDTO.setName(getName());
		tripletValueDTO.setPeriod(getPeriod());
		tripletValueDTO.setType(getChangeTypeEnum());
		
		return tripletValueDTO;
	}

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native String getCode() /*-{
		return this.code;
	}-*/;

	public native void setCode(String code) /*-{
		this.code = code;
	}-*/;

	public native String getName() /*-{
		return this.name;
	}-*/;

	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public native String getPeriod() /*-{
		return this.period;
	}-*/;

	public native void setPeriod(String period) /*-{
		this.period = period;
	}-*/;

	public native int getIndex() /*-{
		return this.index;
	}-*/;

	public native void setIndex(int index) /*-{
		this.index = index;
	}-*/;

	public native String getChangeType() /*-{
		return this.changeType;
	}-*/;

	public ValueEventChangeType getChangeTypeEnum() {
		if(getChangeType() != null) {
			return ValueEventChangeType.valueOf(getChangeType());
		}
		return null;
	}

	public void setChangeType(ValueEventChangeType changeType) {
		if(changeType != null) {
			setChangeType(changeType.name());
		}
	}
	
	public native void setChangeType(String changeType) /*-{
		this.changeType = changeType;
	}-*/;
}
