package org.sigmah.shared.dto.history;

import java.io.Serializable;

import org.sigmah.shared.dto.referential.ValueEventChangeType;

public class HistoryTokenDTO implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2644629638564832900L;

	private String value;
	private ValueEventChangeType type;

	public HistoryTokenDTO() {
	}

	public HistoryTokenDTO(String value, ValueEventChangeType type) {
		this.value = value;
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ValueEventChangeType getType() {
		return type;
	}

	public void setType(ValueEventChangeType type) {
		this.type = type;
	}

}
