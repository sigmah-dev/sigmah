package org.sigmah.shared.file;

/**
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface ProgressListener {
	void onProgress(double progress, double speed);
	void onFailure(Cause cause);
	void onLoad(String result);
}
