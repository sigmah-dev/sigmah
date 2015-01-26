package org.sigmah.server.handler;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Handler for the {@link PrepareFileUpload} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class PrepareFileUploadHandler extends AbstractCommandHandler<PrepareFileUpload, FileVersionDTO> {

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(PrepareFileUploadHandler.class);

	/**
	 * Injected {@link FileDAO}.
	 */
	@Inject
	private FileDAO fileDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FileVersionDTO execute(final PrepareFileUpload command, final UserDispatch.UserExecutionContext context) throws CommandException {
		final String uid = FileServlet.generateUniqueName();

		final Integer fileId = fileDAO.saveOrUpdate(command.getProperties(), uid, command.getSize());
		final List<FileVersion> versions = fileDAO.findVersions(Collections.singleton(fileId), FileDTO.LoadingScope.LAST_VERSION);

		return mapper().map(versions.get(0), FileVersionDTO.class);
	}

}
