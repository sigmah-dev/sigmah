package org.sigmah.shared.command;

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.file.FileSlice;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DownloadSlice implements Command<FileSlice> {
	
	private int fileVersionId;
	private long offset;
	private int size;

	public DownloadSlice() {
	}

	public DownloadSlice(FileVersionDTO fileVersion, long offset, int size) {
		this.fileVersionId = fileVersion.getId();
		this.offset = offset;
		this.size = size;
	}

	public int getFileVersionId() {
		return fileVersionId;
	}

	public void setFileVersionId(int fileVersionId) {
		this.fileVersionId = fileVersionId;
	}
	
	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
