package org.sigmah.shared.command;

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
