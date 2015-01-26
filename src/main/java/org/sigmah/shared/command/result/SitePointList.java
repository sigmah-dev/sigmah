package org.sigmah.shared.command.result;

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
