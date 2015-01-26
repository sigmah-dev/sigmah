package org.sigmah.offline.js;

import org.sigmah.shared.dto.element.TextAreaElementDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class TextAreaElementJS extends FlexibleElementJS {
	
	protected TextAreaElementJS() {
	}
	
	public static TextAreaElementJS toJavaScript(TextAreaElementDTO textAreaElementDTO) {
		final TextAreaElementJS textAreaElementJS = Values.createJavaScriptObject(TextAreaElementJS.class);
		
		textAreaElementJS.setType(textAreaElementDTO.getType());
		textAreaElementJS.setMinValue(textAreaElementDTO.getMinValue());
		textAreaElementJS.setMaxValue(textAreaElementDTO.getMaxValue());
		textAreaElementJS.setDecimal(textAreaElementDTO.getIsDecimal());
		textAreaElementJS.setLength(textAreaElementDTO.getLength());
		
		return textAreaElementJS;
	}
	
	@Override
	protected TextAreaElementDTO createDTO() {
		final TextAreaElementDTO textAreaElementDTO = new TextAreaElementDTO();
		
		textAreaElementDTO.setType(getTypeCharacter());
		textAreaElementDTO.setMinValue(getMinValueLong());
		textAreaElementDTO.setMaxValue(getMaxValueLong());
		textAreaElementDTO.setIsDecimal(isDecimalBoolean());
		textAreaElementDTO.setLength(getLengthInteger());
		
		return textAreaElementDTO;
	}

	public native boolean hasType() /*-{
		return typeof this.type != 'undefined';
	}-*/;
	
	public native char getType() /*-{
		return this.type;
	}-*/;

	public Character getTypeCharacter() {
		if(hasType()) {
			return getType();
		}
		return null;
	}

	public native void setType(char type) /*-{
		this.type = type;
	}-*/;
	
	public void setType(Character type) {
		if(type != null) {
			setType(type.charValue());
		}
	}

	public native boolean hasMinValue() /*-{
		return typeof this.minValue != 'undefined';
	}-*/;
	
	public native double getMinValue() /*-{
		return this.minValue;
	}-*/;
	
	public Long getMinValueLong() {
		if(hasMinValue()) {
			return (long) getMinValue();
		}
		return null;
	}

	public native void setMinValue(double minValue) /*-{
		this.minValue = minValue;
	}-*/;

	public void setMinValue(Long minValue) {
		if(minValue != null) {
			setMinValue(minValue.doubleValue());
		}
	}
	
	public native boolean hasMaxValue() /*-{
		return typeof this.maxValue != 'undefined';
	}-*/;
	
	public native double getMaxValue() /*-{
		return this.maxValue;
	}-*/;
	
	public Long getMaxValueLong() {
		if(hasMaxValue()) {
			return (long) getMaxValue();
		}
		return null;
	}

	public native void setMaxValue(double maxValue) /*-{
		this.maxValue = maxValue;
	}-*/;

	public void setMaxValue(Long maxValue) {
		if(maxValue != null) {
			setMaxValue(maxValue.doubleValue());
		}
	}

	public native boolean hasDecimal() /*-{
		return typeof this.decimal != 'undefined';
	}-*/;
	
	public native boolean isDecimal() /*-{
		return this.decimal;
	}-*/;
	
	public Boolean isDecimalBoolean() {
		if(hasDecimal()) {
			return isDecimal();
		}
		return null;
	}

	public native void setDecimal(boolean decimal) /*-{
		this.decimal = decimal;
	}-*/;

	public void setDecimal(Boolean decimal) {
		if(decimal != null) {
			setDecimal(decimal.booleanValue());
		}
	}

	public native boolean hasLength() /*-{
		return typeof this.length != 'undefined';
	}-*/;
	
	public native int getLength() /*-{
		return this.length;
	}-*/;
	
	public Integer getLengthInteger() {
		if(hasLength()) {
			return getLength();
		}
		return null;
	}

	public native void setLength(int length) /*-{
		this.length = length;
	}-*/;

	public void setLength(Integer length) {
		if(length != null) {
			setLength(length.intValue());
		}
	}
}
