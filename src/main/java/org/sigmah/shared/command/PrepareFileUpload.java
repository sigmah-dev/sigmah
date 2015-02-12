package org.sigmah.shared.command;

import java.util.Map;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.dto.value.FileVersionDTO;

/**
 * Ask the server to generate an identifier for an upload.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PrepareFileUpload implements Command<FileVersionDTO> {
	private Map<String, String> properties;
	private String fileName;
	private int size;
	
	protected PrepareFileUpload() {
	}

	public PrepareFileUpload(String fileName, int size, Map<String, String> properties) {
		this.properties = properties;
		this.fileName = fileName;
		this.size = size;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
