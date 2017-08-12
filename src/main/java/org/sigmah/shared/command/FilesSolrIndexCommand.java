package org.sigmah.shared.command;

import org.sigmah.server.search.FilesSolrManager;

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

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.search.FilesSolrIndexDTO;

/**
 * Command used by client side to execute indexing of files
 * 
 * @author 
 */
public class FilesSolrIndexCommand extends AbstractCommand<FilesSolrIndexDTO>{

	private FilesSolrIndexDTO filesSolrIndexDTO;
	
	public FilesSolrIndexCommand() {
		// Serialization.
	}
	
	public FilesSolrIndexCommand(FilesSolrIndexDTO filesSolrIndexDTO) {
		this.filesSolrIndexDTO = filesSolrIndexDTO;
	}

	public FilesSolrIndexDTO getFilesSolrIndexDTO() {
		return filesSolrIndexDTO;
	}

	public void setFilesSolrIndexDTO(FilesSolrIndexDTO filesSolrIndexDTO) {
		this.filesSolrIndexDTO = filesSolrIndexDTO;
	}

}
