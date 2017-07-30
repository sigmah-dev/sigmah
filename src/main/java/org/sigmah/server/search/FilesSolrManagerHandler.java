package org.sigmah.server.search;

import java.io.IOException;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.file.BackupArchiveManager;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.BackupArchiveManagementCommand;
import org.sigmah.shared.command.FilesSolrIndexCommand;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.BackupDTO;
import org.sigmah.shared.dto.search.FilesSolrIndexDTO;

import com.google.inject.Inject;

public class FilesSolrManagerHandler extends AbstractCommandHandler<FilesSolrIndexCommand, FilesSolrIndexDTO>{

	/**
	 * Injected {@link BackupArchiveManager}.
	 */
	private FilesSolrManager filesSolrManager;
	
	
	@Inject
	public FilesSolrManagerHandler(FilesSolrManager filesSolrManager) {
		this.filesSolrManager = filesSolrManager;
	}
	
	@Override
	protected FilesSolrIndexDTO execute(FilesSolrIndexCommand command, UserExecutionContext context)
			throws CommandException {
		FilesSolrIndexDTO res = new FilesSolrIndexDTO();
		try {
			filesSolrManager.FilesImport(SolrSearcher.getInstance());
			res.setResult(true);
			
		} catch (IOException e) {
			e.printStackTrace();
			res.setResult(false);
		}
		return res;
	}

}
