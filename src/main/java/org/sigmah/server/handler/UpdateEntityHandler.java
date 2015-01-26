package org.sigmah.server.handler;

import java.io.Serializable;
import java.util.Map;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.service.base.EntityService;
import org.sigmah.server.service.base.EntityServices;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.persist.Transactional;

/**
 * Handler for {@link UpdateEntity} command.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.sigmah.shared.command.UpdateEntity
 */
public class UpdateEntityHandler extends AbstractCommandHandler<UpdateEntity, VoidResult> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UpdateEntityHandler.class);

	/**
	 * Injected application injector.
	 */
	private final Injector injector;

	@Inject
	public UpdateEntityHandler(final Injector injector) {
		this.injector = injector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final UpdateEntity cmd, final UserExecutionContext context) throws CommandException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Update handler for command '{}'.", cmd);
		}

		final Map<String, Object> changes = cmd.getChanges().getTransientMap();
		final PropertyMap changeMap = new PropertyMap(changes);

		final String entityName = cmd.getEntityName();
		final Serializable entityId = cmd.getId();

		final Class<? extends EntityService<?, Serializable, ?>> entityServiceClass = EntityServices.getServiceClass(entityName);
		if (entityServiceClass == null) {
			throw new CommandException("No entity service has been found for entity id '" + entityName + "'.");
		}

		final EntityService<?, Serializable, ?> entityService = injector.getInstance(entityServiceClass);
		performUpdate(entityService, entityId, changeMap, context);

		return new VoidResult();
	}

	/**
	 * Updates the entity identified by <code>entityId</code> with the given
	 * properties in a transaction.
	 * 
	 * @param entityService Entity service to use.
	 * @param entityId Identifier of the entity to update.
	 * @param changeMap Changes to apply to the entity.
	 * @param context Execution context.
	 * @throws CommandException If the update failed.
	 */
	@Transactional
	protected void performUpdate(final EntityService<?, Serializable, ?> entityService, final Serializable entityId, final PropertyMap changeMap, final UserExecutionContext context) throws CommandException {
		entityService.update(entityId, changeMap, context);
	}

}
