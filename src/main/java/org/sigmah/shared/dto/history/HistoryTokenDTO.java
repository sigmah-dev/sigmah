package org.sigmah.shared.dto.history;

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
