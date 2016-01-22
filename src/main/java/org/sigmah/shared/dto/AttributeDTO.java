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
 * One-to-One DTO for the {@link org.sigmah.shared.dto.AttributeDTO} domain object
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class AttributeDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -9170500205294367917L;

	public static final String PROPERTY_PREFIX = "ATTRIB";

	public AttributeDTO() {
		// Serialization.
	}

	public AttributeDTO(AttributeDTO model) {
		super(model.getProperties());

	}

	public AttributeDTO(int id, String name) {
		setId(id);
		setName(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "Attribute";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", getName());
	}

	@Override
	public Integer getId() {
		return (Integer) get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	public void setName(String value) {
		set("name", value);
	}

	public String getName() {
		return get("name");
	}

	public static String getPropertyName(int attributeId) {
		return PROPERTY_PREFIX + attributeId;
	}

	public static String getPropertyName(AttributeDTO attribute) {
		return getPropertyName(attribute.getId());
	}

	public String getPropertyName() {
		return getPropertyName(getId());
	}

	public static int idForPropertyName(String property) {
		return Integer.parseInt(property.substring(PROPERTY_PREFIX.length()));
	}

}
