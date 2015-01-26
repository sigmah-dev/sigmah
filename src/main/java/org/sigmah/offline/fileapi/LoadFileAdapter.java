package org.sigmah.offline.fileapi;

import org.sigmah.offline.event.ProgressEvent;

/**
 * Adapter to ease the implementation of a LoadFileListener.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class LoadFileAdapter implements LoadFileListener {

	@Override
	public void onLoadStart() {
	}

	@Override
	public void onLoadEnd() {
	}

	@Override
	public void onLoad() {
	}

	@Override
	public void onProgress(ProgressEvent event) {
	}

	@Override
	public void onError() {
	}

	@Override
	public void onAbort() {
	}
	
}
