package org.sigmah.shared.dto.base.mapping;

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

/**
 * Defines a <b>custom</b> mapping field.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see MappingField
 */
public class CustomMappingField extends MappingField {

	/**
	 * The mapping mode used to map custom field.
	 */
	private final IsMappingMode mappingMode;

	/**
	 * Builds a custom field property with the same entity attribute name and DTO map key.
	 * 
	 * @param entityAttributeName
	 *          The entity attribute name and DTO map key.
	 */
	public CustomMappingField(String entityAttributeName) {
		this(entityAttributeName, entityAttributeName, null);
	}

	/**
	 * Builds a custom field property.
	 * 
	 * @param entityAttributeName
	 *          The entity attribute name.
	 * @param dtoMapKey
	 *          The DTO map key.
	 */
	public CustomMappingField(String entityAttributeName, String dtoMapKey) {
		this(entityAttributeName, dtoMapKey, null);
	}

	/**
	 * Builds a custom field property.
	 * 
	 * @param entityAttributeName
	 *          The entity attribute name and DTO map key.
	 * @param mappingMode
	 *          (optional) Mapping mode used to map the custom field. Only used for <em>custom</em> fields (ignored for
	 *          <em>excluded</em> fields).
	 */
	public CustomMappingField(String entityAttributeName, IsMappingMode mappingMode) {
		this(entityAttributeName, entityAttributeName, mappingMode);
	}

	/**
	 * Builds a custom field property.
	 * 
	 * @param entityAttributeName
	 *          The entity attribute name.
	 * @param dtoMapKey
	 *          The DTO map key.
	 * @param mappingMode
	 *          (optional) Mapping mode used to map the custom field. Only used for <em>custom</em> fields (ignored for
	 *          <em>excluded</em> fields).
	 */
	public CustomMappingField(String entityAttributeName, String dtoMapKey, IsMappingMode mappingMode) {
		super(entityAttributeName, dtoMapKey);
		this.mappingMode = mappingMode;
	}

	public IsMappingMode getMappingMode() {
		return mappingMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("entityAttributeName", getEntityAttributeName());
		builder.append("dtoMapKey", getDTOMapKey());
		builder.append("mappingMode", mappingMode);
		return builder.toString();
	}

}
