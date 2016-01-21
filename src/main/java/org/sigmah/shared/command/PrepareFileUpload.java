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
