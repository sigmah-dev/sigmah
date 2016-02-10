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
import java.sql.Connection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.internal.SessionImpl;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.base.Entity;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.inject.util.Injectors;

import com.google.inject.persist.Transactional;

/**
 * <p>
 * Abstract DAO implementation.
 * </p>
 * <p>
 * Parent class of all DAO implementations.
 * </p>
 * 
 * @param <E>
 *          Entity type.
 * @param <K>
 *          Entity id type (primary key).
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public abstract class AbstractDAO<E extends Entity, K extends Serializable> extends EntityManagerProvider implements DAO<E, K> {

	/**
	 * The entity class managed by DAO implementation.
	 */
	protected final Class<E> entityClass;

	/**
	 * Initializes a new AbstractDAO.<br/>
	 * Populates the {@link #entityClass} attribute.
	 */
	@SuppressWarnings("unchecked")
	protected AbstractDAO() {
		this.entityClass = (Class<E>) Injectors.findGenericSuperClass(getClass()).getActualTypeArguments()[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final CriteriaBuilder getCriteriaBuilder() {
		return em().getCriteriaBuilder();
	}

	// --------------------------------------------------------------------------------
	//
	// COUNT METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countAll() {
		final TypedQuery<Number> query = em().createQuery("SELECT COUNT(e) FROM " + entityClass.getName() + " e", Number.class);
		return query.getSingleResult().intValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countByCriteria(final CriteriaQuery<Number> criteriaQuery) {
		final TypedQuery<Number> query = em().createQuery(criteriaQuery);
		return query.getSingleResult().intValue();
	}

	// --------------------------------------------------------------------------------
	//
	// FIND METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E getReference(final K primaryKey) {
		return em().getReference(entityClass, primaryKey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<E> findAll() {
		return em().createQuery("SELECT e FROM " + entityClass.getName() + " e", entityClass).getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E findById(final K primaryKey) {
		return em().find(entityClass, primaryKey);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<E> findByCriteria(final CriteriaQuery<E> criteriaQuery) {
		return em().createQuery(criteriaQuery).getResultList();
	}

	// --------------------------------------------------------------------------------
	//
	// PERSIST METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	@Transactional
	public E persist(final E entity) {
		return persist(entity, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public E persist(final E entity, final User user) {

		if (entity == null) {
			return entity;
		}

		// TODO [DAO] Automatically set edition user and date into persisted entity.
		// entity.setDateUpdated(new Date());
		// entity.setUserUpdated(user.toString());
		em().persist(entity);

		return entity;
	}

	// --------------------------------------------------------------------------------
	//
	// REMOVE METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	@Transactional
	public void remove(final K primaryKey) {
		remove(primaryKey, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	@Transactional
	public void remove(final E entity) {
		remove(entity, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void remove(final K primaryKey, final User user) {

		if (primaryKey == null) {
			return;
		}

		remove(findById(primaryKey), user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void remove(final E entity, final User user) {

		if (entity instanceof Deleteable) {
			final Deleteable deleteable = (Deleteable) entity;
			deleteable.delete();
			em().persist(deleteable); // Logical removal.

		} else if (entity != null) {
			em().remove(entity); // Physical removal.
		}
	}

	// ------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ------------------------------------------------------------------------------------------

	/**
	 * Executes the given JPQL or native SQL {@code updateQuery}.<br/>
	 * Checks the transaction before execution (see {@link #checkTransaction(EntityManager)}).
	 * 
	 * @param updateQuery
	 *          The update JPQL or native SQL query.
	 * @return the number of elements updated/deleted.
	 */
	@Transactional
	protected final int update(final Query updateQuery) {

		if (updateQuery == null) {
			throw new IllegalArgumentException("Update query is required.");
		}

		checkTransaction(em());

		return updateQuery.executeUpdate();
	}

	/**
	 * Returns the given {@code em} inner session implementation.
	 * 
	 * @param em
	 *          The entity manager.
	 * @return the given {@code em} inner session implementation.
	 * @throws IllegalArgumentException
	 *           If the given {@code em} is {@code null}.
	 */
	public static final org.hibernate.Session getSession(final EntityManager em) {
		if (em == null) {
			throw new IllegalArgumentException("Entity manager instance is required.");
		}
		return em.unwrap(org.hibernate.Session.class);
	}

	/**
	 * Returns the {@code java.sql.Connection} from the given {@code em}.<br/>
	 * Entity manager is flushed if active transaction is running.
	 * 
	 * @param em
	 *          The entity manager.
	 * @return the {@code java.sql.Connection} unwrapped from the given {@code em}.
	 */
	public static Connection getConnection(final EntityManager em) {
		if (em == null) {
			return null;
		}
		checkTransaction(em);
		return em.unwrap(SessionImpl.class).connection();
	}

	/**
	 * Checks if active transaction is running. If so, given {@code em} is flushed in order to synchronize persistence
	 * context.
	 * 
	 * @param em
	 *          The entity manager.
	 */
	private static void checkTransaction(final EntityManager em) {
		if (em == null) {
			return;
		}
		if (em.getTransaction().isActive()) {
			// Active transaction running, flushing em to synchronize context.
			em.flush();
		}
	}

}
