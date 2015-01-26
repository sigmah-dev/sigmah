package org.sigmah.offline.sync;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface SynchroProgressListener {
    void onProgress(double progress);
    void onComplete();
    void onFailure(Throwable caught);
}
