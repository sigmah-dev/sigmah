package org.sigmah.shared.dto.base;

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

import java.util.Map;

import org.sigmah.client.util.ToStringBuilder;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * <p>
 * Abstract layer for <em>model data</em> DTO (without id).
 * </p>
 * <p>
 * Inherits {@link com.extjs.gxt.ui.client.data.BaseModelData}.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see com.extjs.gxt.ui.client.data.ModelData
 */
public abstract class AbstractModelDataDTO extends BaseModelData implements ModelData, DTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7586213402175592079L;

	/**
	 * Creates a new model data instance.
	 */
	public AbstractModelDataDTO() {
		super();
	}

	/**
	 * Creates a new model with the given properties.
	 * 
	 * @param properties
	 *          the initial properties.
	 */
	public AbstractModelDataDTO(final Map<String, Object> properties) {
		super(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		if (this instanceof EntityDTO) {
			final EntityDTO<?> entityDTO = (EntityDTO<?>) this;
			builder.append("entity", entityDTO.getEntityName());
			builder.append(EntityDTO.ID, entityDTO.getId());
		}

		appendToString(builder); // Appends child entity specific properties.

		return builder.toString();
	}

	/**
	 * <p>
	 * Appends specific properties to the given {@code toString} {@code builder}.
	 * </p>
	 * 
	 * @param builder
	 *          The {@code toString} client builder.
	 */
	protected abstract void appendToString(final ToStringBuilder builder);

}
