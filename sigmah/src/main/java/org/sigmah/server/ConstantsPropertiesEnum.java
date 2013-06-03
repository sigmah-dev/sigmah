package org.sigmah.server;

public enum ConstantsPropertiesEnum {
	
	/**
	 *  Property for the maximum size of uploaded files
    *  @author Guerline Jean-Baptiste(gjbaptiste@ideia.fr)
	 */
	UPLOAD_MAX_SIZE("upload.maxSize");
	
	private String value;

	ConstantsPropertiesEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
