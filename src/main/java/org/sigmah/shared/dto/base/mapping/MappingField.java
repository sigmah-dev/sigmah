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
import org.sigmah.shared.conf.PropertyName;

/**
 * Defines properties to map a DTO field.
 * <p>
 * <b>ATTENTION: </b>
 * </p>
 * <ul>
 * <li><code>entityAttributeName</code> must match the {@link org.sigmah.server.domain.base.Entity entity} Java
 * attribute name.</li>
 * <li><code>dtoMapKey</code> must match the {@link org.sigmah.shared.dto.base.DTO DTO} map key.</li>
 * </ul>
 * 
 * <pre>
 * / The DTO. /
 * public class MockDTO implements org.sigmah.shared.dto.base.DTO {
 *  public enum Mode implements org.sigmah.shared.dto.base.IsMappingMode {
 * 	 MODE1(new MappingField("<u>myAttribute</u>", "<u>myKey</u>")); // myAttribute will not be mapped by the beans mapper.
 *  }
 * 
 *  ? get<u>MyAttribute</u>() {
 *    get("<u>myKey</u>");
 *  }
 *  void set<u>MyAttribute</u>(? value) {
 *    set("<u>myKey</u>", value);
 *  }
 * }
 * 
 * / The entity. /
 * public class Mock implements org.sigmah.server.domain.base.Entity {
 *  public ? <u>myAttribute</u>;
 *   
 *  ? get<u>MyAttribute</u>() {
 *    ...
 *  }
 *  void set<u>MyAttribute</u>(? value) {
 *    ...
 *  }
 * }
 * </pre>
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class MappingField {

	/**
	 * The name of the {@link org.sigmah.server.domain.base.Entity entity} Java attribute name.
	 */
	private final String entityAttributeName;

	/**
	 * The key used in the DTO map for the same attribute.
	 */
	private final String dtoMapKey;

	/**
	 * Builds a property with the same entity attribute name and DTO map key.
	 * 
	 * @param entityAttributeName
	 *          The entity attribute name and DTO map key.
	 */
	public MappingField(String entityAttributeName) {
		this(entityAttributeName, entityAttributeName);
	}

	/**
	 * Builds a property.
	 * 
	 * @param entityAttributeName
	 *          The entity attribute name.
	 * @param dtoMapKey
	 *          The DTO map key.
	 */
	public MappingField(String entityAttributeName, String dtoMapKey) {
		this.entityAttributeName = entityAttributeName;
		this.dtoMapKey = dtoMapKey;
	}

	public String getEntityAttributeName() {
		return entityAttributeName;
	}

	public String getDTOMapKey() {
		return dtoMapKey;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("entityAttributeName", entityAttributeName);
		builder.append("dtoMapKey", dtoMapKey);
		return builder.toString();
	}

	/**
	 * Builds a mapping field name which {@code parts} are separated by a dot character.
	 * 
	 * <pre>
	 * n(null) → ""
	 * n("") → ""
	 * n("my", "Key", " rocks ") → "my.Key.rocks"
	 * n("my", "Key", "rocks.like.hell") → "my.Key.rocks.like.hell"
	 * </pre>
	 * 
	 * @param parts
	 *          The mapping field name parts.
	 * @return The mapping field name.
	 */
	public static final String n(final String... parts) {
		return PropertyName.n(parts);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dtoMapKey == null) ? 0 : dtoMapKey.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MappingField other = (MappingField) obj;
		if (dtoMapKey == null) {
			if (other.dtoMapKey != null)
				return false;
		} else if (!dtoMapKey.equals(other.dtoMapKey))
			return false;
		return true;
	}

}
