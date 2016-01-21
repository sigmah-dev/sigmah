package org.sigmah.shared.command;

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

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.value.FileVersionDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class UploadSlice implements Command<VoidResult> {
	private FileVersionDTO fileVersionDTO;
	private int offset;
	private byte[] data;
	private boolean last;

	public UploadSlice() {
	}

	public FileVersionDTO getFileVersionDTO() {
		return fileVersionDTO;
	}

	public void setFileVersionDTO(FileVersionDTO fileVersionDTO) {
		this.fileVersionDTO = fileVersionDTO;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}
}
