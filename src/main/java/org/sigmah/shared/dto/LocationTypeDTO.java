package org.sigmah.shared.dto;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * One-to-one DTO of the {@link org.sigmah.server.domain.LocationType LocationType} domain object.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LocationTypeDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1187763034988828905L;

	public LocationTypeDTO() {
	}

	public LocationTypeDTO(int id, String name) {
		setId(id);
		setName(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "LocationType";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", getName());
		builder.append("boundAdminLevelId", getBoundAdminLevelId());
	}

	public void setId(Integer id) {
		set("id", id);
	}

	@Override
	public Integer getId() {
		return (Integer) get("id");
	}

	public void setName(String value) {
		set("name", value);
	}

	public String getName() {
		return get("name");
	}

	public Integer getBoundAdminLevelId() {
		return get("boundAdminLevelId");
	}

	public void setBoundAdminLevelId(Integer id) {
		set("boundAdminLevelId", id);
	}

	public boolean isAdminLevel() {
		return getBoundAdminLevelId() != null;
	}

}
