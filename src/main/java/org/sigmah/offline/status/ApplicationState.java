package org.sigmah.offline.status;

/**
 * Possibles connection states.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum ApplicationState {
    /**
     * Sigmah is checking the current state of the application.
     */
    UNKNOWN,
    /**
     * No connection has been found. User is most likely offline.
     */
    OFFLINE,
    /**
     * User is online but has uncommited changes. A synchronization is required
     * to return back online.
     */
    READY_TO_SYNCHRONIZE,
    /**
     * User if online.
     */
    ONLINE;
}
