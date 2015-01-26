package org.sigmah.shared.file;

/**
 * An abstract adapter class for creating progress listeners.
 * The methods in this class are empty. This class exists as convenience for creating listener objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class ProgressAdapter implements ProgressListener {

    @Override
    public void onProgress(double progress, double speed) {
    }

    @Override
    public void onFailure(Cause cause) {
    }

    @Override
    public void onLoad(String result) {
    }
    
}
