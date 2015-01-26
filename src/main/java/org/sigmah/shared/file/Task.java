package org.sigmah.shared.file;

import org.sigmah.offline.js.TransfertJS;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Task {
	private TransfertJS transfert;
	private ProgressListener progressListener;
	private int tries;

	public Task() {
	}

	public Task(TransfertJS transfert, ProgressListener progressListener) {
		this.transfert = transfert;
		this.progressListener = progressListener;
	}

	public TransfertJS getTransfert() {
		return transfert;
	}

	public void setTransfert(TransfertJS transfert) {
		this.transfert = transfert;
	}

	public ProgressListener getProgressListener() {
		return progressListener;
	}

	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	public int getTries() {
		return tries;
	}

	public void setTries(int tries) {
		this.tries = tries;
	}
	
	public boolean hasListener() {
		return progressListener != null;
	}
}
