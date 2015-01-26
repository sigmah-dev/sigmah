package org.sigmah.server.handler;

import java.util.Map;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.service.base.EntityService;
import org.sigmah.server.service.base.EntityServices;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dispatch.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.persist.Transactional;

/**
 * Handler for {@link CreateEntity} command.
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @see org.sigmah.shared.command.CreateEntity
 */
public class CreateEntityHandler extends AbstractCommandHandler<CreateEntity, CreateResult> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(CreateEntityHandler.class);

	/**
	 * Application injector.
	 */
	private final Injector injector;

	@Inject
	public CreateEntityHandler(final Injector injector) {
		this.injector = injector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CreateResult execute(final CreateEntity cmd, final UserExecutionContext context) throws CommandException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating entity for following command: {}.", cmd);
		}

		final Map<String, Object> properties = cmd.getProperties().getTransientMap();
		final PropertyMap propertyMap = new PropertyMap(properties);

		final String entityName = cmd.getEntityName();

		final Class<? extends EntityService<?, ?, ?>> entityServiceClass = EntityServices.getServiceClass(entityName);
		if (entityServiceClass == null) {
			throw new CommandException("No entity service has been found for entity id '" + entityName + "'.");
		}

		final EntityService<?, ?, ?> entityService = injector.getInstance(entityServiceClass);
		EntityId<?> entity = performCreation(entityService, propertyMap, context);

		return entityService.buildCreateResult(entity);
	}

	/**
	 * Creates the entity in a transaction.
	 * 
	 * @param entityService Entity service to use.
	 * @param propertyMap Properties of the new entity.
	 * @param context Execution context.
	 * @return The new entity.
	 * @throws CommandException If the creation failed. 
	 */
	@Transactional
	protected EntityId<?> performCreation(final EntityService<?, ?, ?> entityService, final PropertyMap propertyMap, final UserExecutionContext context) throws CommandException {
		return entityService.create(propertyMap, context);
	}

}
