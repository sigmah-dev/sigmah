package org.sigmah.offline.js;

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
	
	@Override
	public TripletValueDTO toDTO() {
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
