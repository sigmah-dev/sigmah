package org.sigmah.shared.file;

import org.sigmah.offline.js.TransfertJS;

/**
 * Implemented by transfer managers that supports download and upload progress 
 * listeners.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface HasProgressListeners {
    void setProgressListener(TransfertType type, ProgressListener progressListener);
    void removeProgressListener(TransfertType type);
	void resumeUpload(TransfertJS transferJS);
	int getUploadQueueSize();
	int getDownloadQueueSize();
}
