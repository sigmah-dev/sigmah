package org.sigmah.server.service.base;

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

import java.io.Serializable;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.base.EntityDTO;

/**
 * <p>
 * Entity services are responsible for <b>creating</b> and <b>updating</b> entities on behalf of users.
 * </p>
 * <p>
 * All entity services should annotated with {@code @Singleton} annotation.
 * </p>
 *
 * @param <E>
 *          Entity type.
 * @param <K>
 *          Entity id type.
 * @param <D>
 *          Entity DTO type.
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface EntityService<E extends EntityId<K>, K extends Serializable, D extends EntityDTO<K>> {

	/**
	 * Builds the given {@code entity} corresponding {@code CreateResult}.
	 * 
	 * @return The given {@code entity} corresponding {@code CreateResult}.
	 * @throws CommandException
	 *           If an error occurs during creation result building.
	 */
	// Generic <GE> type is necessary, see 'CreateEntityHandler' implementation.
	<GE extends EntityId<?>> CreateResult buildCreateResult(GE entity) throws CommandException;

	/**
	 * Creates the entity of type T on behalf of the given user, initialized with the given properties.
	 *
	 * @param properties
	 *          A map between property names and property values.
	 * @param context
	 *          The user context on whose behalf this entity is to be created. The user most have appropriate
	 *          authorization to create the particular entity.
	 * @return The newly created entity.
	 * @throws CommandException
	 *           If an error occurs during entity creation process.
	 */
	E create(PropertyMap properties, UserExecutionContext context) throws CommandException;

	/**
	 * Updates the given {@code entityId} corresponding entity with the given {@code changes}.
	 * 
	 * @param entityId
	 *          The entity id.
	 * @param changes
	 *          The changes to apply during update.
	 * @param context
	 *          The user context.
	 * @return The updated entity.
	 * @throws CommandException
	 *           If an error occurs during entity update process.
	 */
	E update(K entityId, PropertyMap changes, UserExecutionContext context) throws CommandException;

}
