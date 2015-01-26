package org.sigmah.server.service.base;

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
