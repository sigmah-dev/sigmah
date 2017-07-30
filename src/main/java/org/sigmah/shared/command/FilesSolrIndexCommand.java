package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.BackupDTO;
import org.sigmah.shared.dto.search.FilesSolrIndexDTO;

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
