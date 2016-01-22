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
