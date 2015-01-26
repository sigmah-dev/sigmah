package org.sigmah.offline.sync;

import org.sigmah.offline.status.ApplicationState;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface StateListener {
    void onStateKnown(ApplicationState state);
}
