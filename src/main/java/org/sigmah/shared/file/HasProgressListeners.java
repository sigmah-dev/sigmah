package org.sigmah.shared.file;

import org.sigmah.offline.js.TransfertJS;

/**
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
