package org.sigmah.shared.dto;

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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.DTO;

/**
 * Partial DTO of the {@link org.sigmah.server.domain.Site Site} domain object and its
 * {@link org.sigmah.server.domain.Location Location} location that includes only the id and geographic position.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class SitePointDTO implements DTO {

	private int siteId;
	private String name;
	private double y;
	private double x;

	@SuppressWarnings("unused")
	private SitePointDTO() {
	}

	public SitePointDTO(int siteId, String name, double x, double y) {
		this.name = name;
		this.y = y;
		this.x = x;
		this.siteId = siteId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("siteId", siteId);
		builder.append("name", name);
		builder.append("x", x);
		builder.append("y", y);
		return builder.toString();
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * location.x
	 * 
	 * @return the x (longitudinal) coordinate of this Site
	 */
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	/**
	 * location.y
	 * 
	 * @return the y (latitudinal) coordinate of this Site
	 */
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
}
