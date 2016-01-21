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
