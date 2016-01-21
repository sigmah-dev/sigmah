package org.sigmah.server.handler;

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
