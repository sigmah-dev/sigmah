package org.sigmah.shared.file;

import org.sigmah.shared.command.result.Result;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class FileSlice implements Result {
	private byte[] data;
	private boolean last;

	public FileSlice() {
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
