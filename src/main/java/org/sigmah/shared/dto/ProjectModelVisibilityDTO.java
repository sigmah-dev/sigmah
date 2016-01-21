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
import org.sigmah.shared.dto.referential.ProjectModelType;

/**
 * DTO mapping class for entity ProjectModelVisibility.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectModelVisibilityDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4517698536716727232L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "ProjectModelVisibility";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("type", getType());
		builder.append("organizationId", getOrganizationId());
	}

	// Visibility id.
	@Override
	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	// Visibility type.
	public ProjectModelType getType() {
		return get("type");
	}

	public void setType(ProjectModelType type) {
		set("type", type);
	}

	// Visibility organization id.
	public Integer getOrganizationId() {
		return get("organizationId");
	}

	public void setOrganizationId(Integer organizationId) {
		set("organizationId", organizationId);
	}
}
