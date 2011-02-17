package org.sigmah.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sigmah.shared.dto.EntityDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Caches a collection locally.
 * 
 * @author tmi
 * 
 * @param <E>
 *            The type of the cached elements.
 */
public class LocalCachedCollection<E extends EntityDTO> {

    /**
     * Job to get an entity.
     * 
     * @author tmi
     * 
     * @param <E>
     *            The type of the cached elements.
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
     * 
     * @param <E>
     *            The type of the cached elements.
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
    private final HashMap<Integer, E> map;

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
        map = new HashMap<Integer, E>();
        list = new ArrayList<E>();
        hasBeenSet = false;
        queueEntity = new ArrayList<EntityAsyncCallback<E>>();
        queueEntities = new ArrayList<EntitiesAsyncCallback<E>>();
    }

    /**
     * Gets the entity with the given id. If the collection isn't available
     * immediately, the callback will be called after the collection has been
     * set by the first server call.
     * 
     * @param id
     *            The entity id.
     * @param callback
     *            The callback.
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
     *            The entity id.
     * @return The entity if the cache has been set, <code>null</code>
     *         otherwise.
     */
    public E get(int id) {
        if (hasBeenSet) {
            return map.get(id);
        } else {
            return null;
        }
    }

    /**
     * Gets the whole collection. If the collection isn't available immediately,
     * the callback will be called after the collection has been set by the
     * first server call.
     * 
     * @param callback
     *            The callback.
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
     * @return The collection if the cache has been set, <code>null</code>
     *         otherwise.
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
     *            The entities.
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
