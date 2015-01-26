package org.sigmah.shared.dto;

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
