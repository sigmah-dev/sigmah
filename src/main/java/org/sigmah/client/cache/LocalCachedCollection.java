package org.sigmah.client.cache;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sigmah.shared.dto.base.EntityDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Caches a collection locally.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <E>
 *          The type of the cached elements.
 */
public class LocalCachedCollection<E extends EntityDTO<?>> {

	/**
	 * Job to get an entity.
	 * 
	 * @author tmi
	 * @param <E>
	 *          The type of the cached elements.
	 */
	private static final class EntityAsyncCallback<E> {

		private final int id;
		private final AsyncCallback<E> callback;

		private EntityAsyncCallback(int id, AsyncCallback<E> callback) {
			this.id = id;
			this.callback = callback;
		}
	}

	/**
	 * Job to get the whole collection.
	 * 
	 * @author tmi
	 * @param <E>
	 *          The type of the cached elements.
	 */
	private static final class EntitiesAsyncCallback<E> {

		private final AsyncCallback<List<E>> callback;

		private EntitiesAsyncCallback(AsyncCallback<List<E>> callback) {
			this.callback = callback;
		}
	}

	/**
	 * Cached collection/
	 */
	private final ArrayList<E> list;

	/**
	 * Cached elements mapped to their ids.
	 */
	private final HashMap<Serializable, E> map;

	/**
	 * If the cache has been set.
	 */
	private boolean hasBeenSet;

	/**
	 * Waiting jobs to get a single entity instance.
	 */
	private final ArrayList<EntityAsyncCallback<E>> queueEntity;

	/**
	 * Waiting jobs to get a whole collection.
	 */
	private final ArrayList<EntitiesAsyncCallback<E>> queueEntities;

	public LocalCachedCollection() {
		map = new HashMap<Serializable, E>();
		list = new ArrayList<E>();
		hasBeenSet = false;
		queueEntity = new ArrayList<EntityAsyncCallback<E>>();
		queueEntities = new ArrayList<EntitiesAsyncCallback<E>>();
	}

	/**
	 * Gets the entity with the given id. If the collection isn't available immediately, the callback will be called after
	 * the collection has been set by the first server call.
	 * 
	 * @param id
	 *          The entity id.
	 * @param callback
	 *          The callback.
	 */
	public void get(int id, AsyncCallback<E> callback) {

		// If the collection is available, returns the entity immediately.
		if (hasBeenSet) {
			callback.onSuccess(map.get(id));
		}
		// Else put the callback in queue to be called later.
		else {
			queueEntity.add(new EntityAsyncCallback<E>(id, callback));
		}
	}

	/**
	 * Tries to get an entity without waiting.
	 * 
	 * @param id
	 *          The entity id.
	 * @return The entity if the cache has been set, <code>null</code> otherwise.
	 */
	public E get(int id) {
		if (hasBeenSet) {
			return map.get(id);
		} else {
			return null;
		}
	}

	/**
	 * Gets the whole collection. If the collection isn't available immediately, the callback will be called after the
	 * collection has been set by the first server call.
	 * 
	 * @param callback
	 *          The callback.
	 */
	public void get(AsyncCallback<List<E>> callback) {

		// If the countries list is available, returns the list immediately.
		if (hasBeenSet) {
			callback.onSuccess(list);
		}
		// Else put the callback in queue to be called later.
		else {
			queueEntities.add(new EntitiesAsyncCallback<E>(callback));
		}
	}

	/**
	 * Tries to get the collection without waiting.
	 * 
	 * @return The collection if the cache has been set, <code>null</code> otherwise.
	 */
	public List<E> get() {
		if (hasBeenSet) {
			return list;
		} else {
			return null;
		}
	}

	/**
	 * Sets the collection and call all waiting jobs.
	 * 
	 * @param entities
	 *          The entities.
	 */
	protected void set(List<E> entities) {

		// This method is called once.
		if (hasBeenSet) {
			return;
		}

		// Stores entities.
		if (entities != null) {
			list.addAll(entities);
			for (final E entity : list) {
				map.put(entity.getId(), entity);
			}
		}

		// Calls the waiting jobs.
		for (final EntityAsyncCallback<E> job : queueEntity) {
			job.callback.onSuccess(map.get(job.id));
		}

		for (final EntitiesAsyncCallback<E> job : queueEntities) {
			job.callback.onSuccess(list);
		}

		// Clears the queues.
		queueEntity.clear();
		queueEntities.clear();

		hasBeenSet = true;
	}
}
