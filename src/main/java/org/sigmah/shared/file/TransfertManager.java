package org.sigmah.shared.file;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.shared.dto.value.FileVersionDTO;

/**
 * Handle upload and download of files.
 * 
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
	 * @param fileVersion File to download and cache.
	 */
	void cache(FileVersionDTO fileVersion);
	
	/**
	 * Returns <code>true</code> if the given file version has been cached.
	 * 
	 * @param fileVersion File to search in the cache.
	 * @param callback Called with <code>true</code> if the file is cached, <code>false</code> otherwise.
	 */
	void isCached(FileVersionDTO fileVersion, AsyncCallback<Boolean> callback);
	
	/**
	 * Returns <code>true</code> if the manager is able to download the given 
	 * file.
	 * 
	 * @param fileVersion File to download.
	 * @param callback Called with <code>true</code> if the file is downloadable, <code>false</code> otherwise.
	 */
	void canDownload(FileVersionDTO fileVersion, AsyncCallback<Boolean> callback);
	
	/**
	 * Upload a file enclosed in a FormPanel.
	 * 
	 * @param formPanel Form element containing an input[type='file'] element and properties.
	 * @param progressListener Tracks upload progress. Called on progress, success and failure.
	 */
	void upload(FormPanel formPanel, ProgressListener progressListener);
	
	/**
	 * Returns <code>true</code> if the manager is able to upload a file.
	 * 
	 * @return <code>true</code> if the manager can upload a file, <code>false</code> otherwise.
	 */
	boolean canUpload();
}
