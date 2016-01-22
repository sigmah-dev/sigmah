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
import java.util.Set;

import org.sigmah.server.dao.LocationTypeDAO;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.server.domain.LocationType;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.server.inject.util.Injectors;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.base.EntityDTO;

import com.google.inject.Inject;

/**
 * Abstract layer for entity services. Each {@link EntityService} implementation should inherit this class.
 *
 * @param <E>
 *          Entity type.
 * @param <K>
 *          Entity id type.
 * @param <D>
 *          Entity DTO type.
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public abstract class AbstractEntityService<E extends EntityId<K>, K extends Serializable, D extends EntityDTO<K>> extends EntityManagerProvider
																																																																								implements
																																																																								EntityService<E, K, D> {

	/**
	 * Entity class.
	 */
	protected final Class<E> entityClass;

	/**
	 * Entity class.
	 */
	protected final Class<D> entityDTOClass;

	/**
	 * Injected {@link LocationTypeDAO}.
	 */
	@Inject
	private LocationTypeDAO locationTypeDAO;

	/**
	 * Injected application mapper.
	 */
	@Inject
	private Mapper mapper;

	/**
	 * Service initialization.
	 */
	@SuppressWarnings("unchecked")
	protected AbstractEntityService() {
		this.entityClass = (Class<E>) Injectors.findGenericSuperClass(getClass()).getActualTypeArguments()[0];
		this.entityDTOClass = (Class<D>) Injectors.findGenericSuperClass(getClass()).getActualTypeArguments()[2];
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final <T extends EntityId<?>> CreateResult buildCreateResult(final T entity) throws CommandException {

		if (entity != null && !(entity.getClass().equals(entityClass))) {
			throw new IllegalArgumentException("Provided entity type '" + entity + "' does not match '" + entityClass.getName() + "' type.");
		}

		final EntityDTO<?> entityDTO;

		if (entity == null || entityDTOClass == null || EntityDTO.class.equals(entityDTOClass)) {

			entityDTO = new EntityDTO<K>() {

				@Override
				public K getId() {
					return (K) entity.getId();
				}

				@Override
				public String getEntityName() {
					return entity.getClass().getSimpleName();
				}
			};

		} else {
			entityDTO = handleMapping((E) entity);
		}

		return new CreateResult(entityDTO);
	}

	/**
	 * Handles the given {@code entity} mapping to a proper DTO.<br>
	 * <em>Can be overridden by child implementations.</em>
	 * 
	 * @param entity
	 *          The created entity.
	 * @return The proper DTO instance.
	 */
	protected EntityDTO<?> handleMapping(final E entity) throws CommandException {
		return mapper.map(entity, entityDTOClass);
	}

	/**
	 * Retrieves the <em>default</em> {@link LocationType} from the given {@code database} instance.<br>
	 * If no locationType cannot be found, a new <em>default</em> one is persisted.
	 * 
	 * @param database
	 *          The user database.
	 * @param user
	 *          The user executing the action.
	 * @return The <em>default</em> {@link LocationType} instance (found or persisted).
	 */
	protected final LocationType locationTypeFromDatabase(final UserDatabase database, final User user) {

		// Looking for 'default' locationType among database country's locations.
		final Set<LocationType> locationTypes = database.getCountry().getLocationTypes();
		for (final LocationType type : locationTypes) {
			if (type.getName().equals(LocationType.DEFAULT)) {
				return type;
			}
		}

		// Still need to create the default location type for this country.
		final LocationType defaultType = new LocationType();
		defaultType.setName(LocationType.DEFAULT);
		defaultType.setCountry(database.getCountry());

		return locationTypeDAO.persist(defaultType, user);
	}

}
