/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command.result;

import org.sigmah.shared.dto.BoundingBoxDTO;
import org.sigmah.shared.dto.SitePointDTO;

import java.util.List;

/**
 * @author Alex Bertram
 */
public class SitePointList implements CommandResult {

    private BoundingBoxDTO bounds;
    private List<SitePointDTO> points;
    private int withoutCoordinates;

    private SitePointList() {

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
