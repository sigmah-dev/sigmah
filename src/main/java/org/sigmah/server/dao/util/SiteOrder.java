package org.sigmah.server.dao.util;

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
