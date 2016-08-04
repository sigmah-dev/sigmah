package org.sigmah.server.dao.base;

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
import java.util.List;
import java.util.Set;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.sigmah.server.domain.User;
import org.sigmah.server.domain.base.Entity;
import org.sigmah.server.domain.util.Deleteable;

/**
 * <p>
 * DAO interface.
 * </p>
 * <p>
 * Should be implemented by all DAO implementations.
 * </p>
 *
 * @param <E>
 *          Entity type.
 * @param <K>
 *          Entity id type (primary key).
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface DAO<E extends Entity, K extends Serializable> {

	/**
	 * Returns the {@link CriteriaBuilder} instance.
	 *
	 * @return The {@link CriteriaBuilder} instance.
	 */
	CriteriaBuilder getCriteriaBuilder();

	// --------------------------------------------------------------------------------
	//
	// COUNT METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Counts the total number of entities.
	 *
	 * @return the total number of entities.
	 */
	int countAll();

	/**
	 * Counts the given {@code criteriaQuery} corresponding number of entities.
	 *
	 * @return The given {@code criteriaQuery} corresponding number of entities.
	 */
	int countByCriteria(final CriteriaQuery<Number> criteriaQuery);

	// --------------------------------------------------------------------------------
	//
	// FIND METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Get an instance, whose state may be lazily fetched. If the requested instance does not exist in the database, the
	 * {@code EntityNotFoundException} is thrown when the instance state is first accessed. (The persistence provider
	 * runtime is permitted to throw the {@code EntityNotFoundException} when {@code getReference} is called.) The
	 * application should not expect that the instance state will be available upon detachment, unless it was accessed by
	 * the application while the entity manager was open.
	 *
	 * @param primaryKey
	 *          The primary key.
	 * @return The found entity instance.
	 * @throws IllegalArgumentException
	 *           If the first argument does not denote an entity type or the second argument is not a valid type for that
	 *           entitys primary key or is {@code null}.
	 * @throws EntityNotFoundException
	 *           If the entity state cannot be accessed.
	 */
	E getReference(final K primaryKey);

	/**
	 * Finds all the entities.
	 *
	 * @return All the entities.
	 */
	List<E> findAll();

	/**
	 * Finds the given {@code primaryKey} corresponding entity.
	 *
	 * @param primaryKey
	 *          The entity primary key.
	 * @return the given {@code primaryKey} corresponding entity.
	 */
	E findById(final K primaryKey);

	/**
	 * Finds the given {@code primaryKey} corresponding entities.
	 *
	 * @param primaryKeys
	 *          The entity primary keys.
	 * @return the given {@code primaryKey} corresponding entities.
	 */
	List<E> findByIds(final Set<K> primaryKeys);

	/**
	 * Finds the given {@code criteriaQuery} corresponding entities.
	 *
	 * @param criteriaQuery
	 *          The criteria query.
	 * @return The given {@code criteriaQuery} corresponding entities.
	 */
	List<E> findByCriteria(final CriteriaQuery<E> criteriaQuery);

	// --------------------------------------------------------------------------------
	//
	// PERSIST METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Persists the given {@code entity}.
	 *
	 * @param entity
	 *          The entity to persist. Does nothing if {@code null}.
	 * @return the persisted (updated) entity.
	 * @deprecated Persist action should be executed with user. See {@link #persist(Entity, User)}.
	 */
	@Deprecated
	E persist(final E entity);

	/**
	 * Persists the given {@code entity} with the given {@code user}.
	 *
	 * @param entity
	 *          The entity to persist. Does nothing if {@code null}.
	 * @param user
	 *          The user executing the persist action, may be {@code null}.
	 * @return the persisted (updated) entity.
	 */
	E persist(final E entity, final User user);

	// --------------------------------------------------------------------------------
	//
	// REMOVE METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * <p>
	 * Removes the given {@code primaryKey} corresponding entity.
	 * </p>
	 * <p>
	 * If the {@code primaryKey} corresponding entity is a {@link Deleteable} instance, its {@link Deleteable#delete()}
	 * method is executed and a <b>logical</b> deletion is processed.
	 * Otherwise, a <b>physical</b> deletion action is processed.
	 * </p>
	 *
	 * @param primaryKey
	 *          The primary key referencing the entity to remove. Does nothing if {@code null}.
	 * @deprecated Remove action should be executed with user. See {@link #remove(Serializable, User)}.
	 */
	@Deprecated
	void remove(final K primaryKey);

	/**
	 * <p>
	 * Removes the given {@code entity}.
	 * </p>
	 * <p>
	 * If the {@code entity} is a {@link Deleteable} instance, its {@link Deleteable#delete()} method is executed and a
	 * <b>logical</b> deletion is processed.
	 * Otherwise, a <b>physical</b> deletion action is processed.
	 * </p>
	 *
	 * @param entity
	 *          The entity to remove. Does nothing if {@code null}.
	 * @deprecated Remove action should be executed with user. See {@link #remove(Entity, User)}.
	 */
	@Deprecated
	void remove(final E entity);

	/**
	 * <p>
	 * Removes the given {@code primaryKey} corresponding entity with the given {@code user}.
	 * </p>
	 * <p>
	 * If the {@code primaryKey} corresponding entity is a {@link Deleteable} instance, its {@link Deleteable#delete()}
	 * method is executed and a <b>logical</b> deletion is processed.
	 * Otherwise, a <b>physical</b> deletion action is processed.
	 * </p>
	 *
	 * @param primaryKey
	 *          The primary key referencing the entity to remove. Does nothing if {@code null}.
	 * @param user
	 *          The user executing the remove action, may be {@code null}.
	 */
	void remove(final K primaryKey, final User user);

	/**
	 * <p>
	 * Removes the given {@code entity} with the given {@code user}.
	 * </p>
	 * <p>
	 * If the {@code entity} is a {@link Deleteable} instance, its {@link Deleteable#delete()} method is executed and a
	 * <b>logical</b> deletion is processed.
	 * Otherwise, a <b>physical</b> deletion action is processed.
	 * </p>
	 *
	 * @param entity
	 *          The entity to remove. Does nothing if {@code null}.
	 * @param user
	 *          The user executing the remove action, may be {@code null}.
	 */
	void remove(final E entity, final User user);

}
