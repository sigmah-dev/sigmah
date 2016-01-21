package org.sigmah.shared.command.result;

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

import org.sigmah.shared.dto.base.EntityDTO;

/**
 * <p>
 * Result of the command creating a new entity.
 * </p>
 * <p>
 * The {@code entity} attribute represents the created entity (with its id).
 * </p>
 * 
 * @see org.sigmah.shared.command.CreateEntity
 * @see org.sigmah.shared.command.CreateReportDef
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CreateResult implements Result {

	/**
	 * The created entity.
	 */
	protected EntityDTO<?> entity;

	protected CreateResult() {
		// Serialization.
	}

	public CreateResult(final EntityDTO<?> entity) {
		this.entity = entity;
	}

	/**
	 * Returns the created {@link EntityDTO}.<br>
	 * <em>A cast is necessary to handle proper result type.</em>
	 * 
	 * @return The created {@link EntityDTO}.
	 */
	public EntityDTO<?> getEntity() {
		return entity;
	}

}
