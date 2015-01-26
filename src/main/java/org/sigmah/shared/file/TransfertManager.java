package org.sigmah.shared.file;

import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.shared.dto.value.FileVersionDTO;

/**
 * Handle upload and download of files.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface TransfertManager {
	/**
	 * Initiate the download of the file represented by the given FileVersionDTO.
	 * 
	 * @param fileVersionDTO File to download.
	 * @param progressListener Tracks download progress. Called on progress, success and failure.
	 */
	void download(FileVersionDTO fileVersionDTO, ProgressListener progressListener);
	
	/**
	 * Ask the transfert manager to download a file for future access.
	 * Will do nothing if not supported.
	 * 
	 * @param fileVersionDTO File to download and cache.
	 */
	void cache(FileVersionDTO fileVersionDTO);
	
	void canDownload(FileVersionDTO fileVersionDTO, AsyncCallback<Boolean> callback);
	
	/**
	 * Upload a file enclosed in a FormPanel.
	 * 
	 * @param formPanel Form element containing an input[type='file'] element and properties.
	 * @param progressListener Tracks upload progress. Called on progress, success and failure.
	 */
	void upload(FormPanel formPanel, ProgressListener progressListener);
	
	boolean canUpload();
}
