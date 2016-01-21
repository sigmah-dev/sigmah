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
