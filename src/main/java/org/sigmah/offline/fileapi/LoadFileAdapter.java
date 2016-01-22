package org.sigmah.offline.fileapi;

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
