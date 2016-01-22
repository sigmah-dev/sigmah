package org.sigmah.server.handler;

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

import java.util.Collections;
import java.util.List;

import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.servlet.FileServlet;
import org.sigmah.shared.command.PrepareFileUpload;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.value.FileDTO;
import org.sigmah.shared.dto.value.FileVersionDTO;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.server.handler.util.Conflicts;

/**
 * Handler for the {@link PrepareFileUpload} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class PrepareFileUploadHandler extends AbstractCommandHandler<PrepareFileUpload, FileVersionDTO> {

	/**
	 * Injected {@link FileDAO}.
	 */
	@Inject
	private FileDAO fileDAO;
	
	@Inject
	private Conflicts conflicts;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FileVersionDTO execute(final PrepareFileUpload command, final UserDispatch.UserExecutionContext context) throws CommandException {
		conflicts.searchForFileAddConflicts(command.getProperties(), context.getLanguage(), context.getUser());
		
		final String uid = FileServlet.generateUniqueName();

		final Integer fileId = fileDAO.saveOrUpdate(command.getProperties(), uid, command.getSize());
		final List<FileVersion> versions = fileDAO.findVersions(Collections.singleton(fileId), FileDTO.LoadingScope.LAST_VERSION);

		return mapper().map(versions.get(0), new FileVersionDTO());
	}
	
}
