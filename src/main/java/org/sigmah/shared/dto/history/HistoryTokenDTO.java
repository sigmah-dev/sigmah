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
	private String comment;
	private String coreVersionName;

	public HistoryTokenDTO() {
	}

	public HistoryTokenDTO(String value, ValueEventChangeType type, String comment, String coreVersionName) {
		this.value = value;
		this.type = type;
		this.comment = comment;
		this.coreVersionName = coreVersionName;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCoreVersionName() {
		return coreVersionName;
	}

	public void setCoreVersionName(String coreVersionName) {
		this.coreVersionName = coreVersionName;
	}

}
