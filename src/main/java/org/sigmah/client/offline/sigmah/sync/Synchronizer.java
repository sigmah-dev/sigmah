/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.sync;

/**
 * Describes a synchronize mechanism used to switch between the online and offline modes.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface Synchronizer {
    /**
     * Describes a listener of the progress of this <code>Synchronizer</code>.<br>
     * <br>
     * A <code>Listener</code> can be registered with a <code>Synchronizer</code>
     * by using the {@link Synchronizer#addListener(Synchronizer.Listener)}
     * method.
     */
    public static interface Listener {
        /**
         * Invoked when a task is starting.
         */
        void onStart();

        /**
         * Invoked when a new task is starting.
         * @param taskName Name of the new task.
         */
        void onTaskChange(String taskName);

        /**
         * Invoked when the progress of the current task has changed.
         * @param progress Current progress of the <code>Synchronizer</code>.
         */
        void onUpdate(double progress);

        /**
         * Invoked when the <code>Synchronizer</code> has completed his current
         * task.
         */
        void onComplete();

        /**
         * Invoked when the current task has failed.<br>
         * <br>
         * Exceptions needs to be logged before calling this method.
         * @param critical <code>true</code> if the global sync process must be
         * stopped, <code>false</code> otherwise.
         * @param reason Cause of the error.
         */
        void onFailure(boolean critical, String reason);
    }

    /**
     * Updates the local copy of Sigmah to allow the use of a functionnality when offline.
     */
    void synchronizeLocalDatabase();

    /**
     * Updates the remote server with the changes made offline.
     */
    void updateDistantDatabase();

    /**
     * Registers a listener with this synchronizer.
     * @param listener A listener.
     * @see Synchronizer#removeListener(Synchronizer.Listener)
     */
    void addListener(Listener listener);
    
    /**
     * Unregister the given listener (if it was registered, otherwise nothing will happen).
     * @param listener A listener.
     */
    void removeListener(Listener listener);
}
