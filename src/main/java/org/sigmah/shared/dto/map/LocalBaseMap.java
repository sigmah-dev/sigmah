package org.sigmah.shared.dto.map;

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

/**
 * Encapsulates a set of 256x256 tiles stored locally on the server. Part of a structure that ultimately needs to be
 * replaced by WMS/TMS.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class LocalBaseMap extends BaseMap {

	private int requestIndex;
	private int version;
	private String tileRoot;

	public int getRequestIndex() {
		return requestIndex;
	}

	public void setRequestIndex(int requestIndex) {
		this.requestIndex = requestIndex;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getTileRoot() {
		return tileRoot;
	}

	public void setTileRoot(String tileRoot) {
		this.tileRoot = tileRoot;
	}

	@Override
	public String getTileUrl(int zoom, int x, int y) {

		// certain browsers restrict the number of simultaneous connections
		// to a single host. This has a negative impact on the performance
		// of google maps.
		//
		// to circumvente this problem, we have added a number of aliases
		// for the activityinfo.org server, at mt0.activityinfo.org, mt1.activityinfo.org
		// mt2.activityinfo.org, and mt3.activityinfo.
		//
		// choose an arbitrary server here based on the grid coordinates

		int server = (x % 2) + 2 * (y % 2);

		StringBuilder sb = new StringBuilder();
		sb.append("http://mt").append(server).append(".activityinfo.org/tiles/").append(getId()).append("/v").append(version).append("/z").append(zoom).append("/")
			.append(x).append("x").append(y).append(".png");

		return sb.toString();
	}

	@Override
	public String getLocalTilePath(int zoom, int x, int y) {
		StringBuilder sb = new StringBuilder();
		sb.append(tileRoot).append("/").append(getId()).append("/v").append(version).append("/z").append(zoom).append("/").append(x).append("x").append(y)
			.append(".png");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LocalBaseMap)) {
			return false;
		}
		LocalBaseMap other = (LocalBaseMap) obj;

		return getId().equals(other.getId()) && version == other.getVersion();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
