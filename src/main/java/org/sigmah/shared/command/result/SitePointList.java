package org.sigmah.shared.command.result;

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

import java.util.List;

import org.sigmah.shared.dto.BoundingBoxDTO;
import org.sigmah.shared.dto.SitePointDTO;

/**
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SitePointList implements Result {

	private BoundingBoxDTO bounds;
	private List<SitePointDTO> points;
	private int withoutCoordinates;

	protected SitePointList() {
		// Serialization.
	}

	public SitePointList(BoundingBoxDTO bounds, List<SitePointDTO> points, int withoutCoordinates) {
		this.bounds = bounds;
		this.points = points;
		this.withoutCoordinates = withoutCoordinates;
	}

	public BoundingBoxDTO getBounds() {
		return bounds;
	}

	public void setBounds(BoundingBoxDTO bounds) {
		this.bounds = bounds;
	}

	public List<SitePointDTO> getPoints() {
		return points;
	}

	public void setPoints(List<SitePointDTO> points) {
		this.points = points;
	}

	public int getWithoutCoordinates() {
		return withoutCoordinates;
	}

	public void setWithoutCoordinates(int withoutCoordinates) {
		this.withoutCoordinates = withoutCoordinates;
	}

}
