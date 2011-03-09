/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.sync;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the listeners of the synchronizers.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class AbstractSynchronizer implements Synchronizer {
    /**
     * List of registered listeners.
     */
    private final List<Synchronizer.Listener> listeners;

    public AbstractSynchronizer() {
        listeners = new ArrayList<Synchronizer.Listener>();
    }

    @Override
    public void addListener(Synchronizer.Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Synchronizer.Listener listener) {
        listeners.remove(listener);
    }

    /**
     * Invoke the {@link Synchronizer.Listener#onStart()} method from
     * every listener registered with this object.
     */
    protected void fireOnStart() {
        for(int index = listeners.size() -1; index >= 0; index--)
            listeners.get(index).onStart();
    }

    /**
     * Invoke the {@link Synchronizer.Listener#onTaskChange(String)} method from
     * every listener registered with this object.
     * @param task Name of the new task.
     */
    protected void fireOnTaskChange(String task) {
        for(int index = listeners.size() -1; index >= 0; index--)
            listeners.get(index).onTaskChange(task);
    }

    /**
     * Invoke the {@link Synchronizer.Listener#onUpdate(boolean)} method from
     * every listener registered with this object.
     * @param progress Progress of the current task.
     */
    protected void fireOnUpdate(double progress) {
        for(int index = listeners.size() -1; index >= 0; index--)
            listeners.get(index).onUpdate(progress);
    }

    /**
     * Invoke the {@link Synchronizer.Listener#onComplete()} method from
     * every listener registered with this object.
     */
    protected void fireOnComplete() {
        for(int index = listeners.size() -1; index >= 0; index--)
            listeners.get(index).onComplete();
    }

    /**
     * Invoke the {@link Synchronizer.Listener#onFailure(boolean)} method from
     * every listener registered with this object.
     * @param progress <code>true</code> if the failure is critical and must
     * stop the current sync process, <code>false</code> if it can be ignored.
     * @param reason Cause of the error.
     */
    protected void fireOnFailure(boolean critical, String reason) {
        for(int index = listeners.size() -1; index >= 0; index--)
            listeners.get(index).onFailure(critical, reason);
    }
}
