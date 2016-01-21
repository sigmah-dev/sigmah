package org.sigmah.server.dao.util;

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

public class SiteOrder implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -410846304049284582L;

	private String column;
	private boolean descending;

	public SiteOrder() {
	}

	public SiteOrder(String column, boolean descending) {
		this.column = column;
		this.descending = descending;
	}

	public SiteOrder(String column) {
		this.column = column;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public boolean isDescending() {
		return descending;
	}

	public void setDescending(boolean descending) {
		this.descending = descending;
	}

	public static SiteOrder ascendingOn(String column) {
		return new SiteOrder(column, false);
	}

	public static SiteOrder descendingOn(String column) {
		return new SiteOrder(column, true);
	}
}
