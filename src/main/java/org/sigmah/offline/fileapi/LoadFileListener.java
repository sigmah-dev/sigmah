package org.sigmah.offline.fileapi;

import org.sigmah.offline.event.ProgressEvent;

/**
 * Listener of FileReader events.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface LoadFileListener {
	void onLoadStart();
	void onLoadEnd();
	void onLoad();
	void onProgress(ProgressEvent event);
	void onError();
	void onAbort();
}
