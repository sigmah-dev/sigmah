package org.sigmah.server.search;

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

import java.io.IOException;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.file.BackupArchiveManager;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.FilesSolrIndexCommand;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.search.FilesSolrIndexDTO;

import com.google.inject.Inject;

/**
 * Handler for command {@link FilesSolrIndexCommand} to execute a manual 
 * indexing of files command from the client side
 * 
 * @author Aditya Adhikary (aditya15007@iiitd.ac.in)
 */
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
			filesSolrManager.filesImport(SolrSearcher.getInstance());
			res.setResult(true);
			
		} catch (IOException e) {
			e.printStackTrace();
			res.setResult(false);
		}
		return res;
	}

}
