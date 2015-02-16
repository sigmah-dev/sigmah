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
import java.util.Map;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.FilesListElement;
import org.sigmah.server.handler.util.Conflicts;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.shared.Language;
import org.sigmah.shared.dispatch.UpdateConflictException;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.util.ValueResultUtils;

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
	
	@Inject
	private Conflicts conflicts;
	
	@Inject
	private I18nServer i18nServer;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FileVersionDTO execute(final PrepareFileUpload command, final UserDispatch.UserExecutionContext context) throws CommandException {
		searchForConflicts(command.getProperties(), context.getLanguage());
		
		final String uid = FileServlet.generateUniqueName();

		final Integer fileId = fileDAO.saveOrUpdate(command.getProperties(), uid, command.getSize());
		final List<FileVersion> versions = fileDAO.findVersions(Collections.singleton(fileId), FileDTO.LoadingScope.LAST_VERSION);

		return mapper().map(versions.get(0), FileVersionDTO.class);
	}

	private void searchForConflicts(Map<String, String> properties, Language language) throws UpdateConflictException {
		// Element.
		final int elementId = ClientUtils.asInt(properties.get(FileUploadUtils.DOCUMENT_FLEXIBLE_ELEMENT), 0);
		final FilesListElement filesListElement = em().find(FilesListElement.class, elementId);
		
		// Project.
		final int projectId = ClientUtils.asInt(properties.get(FileUploadUtils.DOCUMENT_PROJECT), 0);
		final Project project = em().find(Project.class, projectId);
		
		if(conflicts.isParentPhaseClosed(elementId, projectId)) {
			final String fileName = ValueResultUtils.normalizeFileName(properties.get(FileUploadUtils.DOCUMENT_NAME));
			throw new UpdateConflictException(project, true, i18nServer.t(language, "conflictAddingFileToAClosedPhase", fileName, filesListElement.getLabel()));
		}
		
//		// Retrieving the current value
//		final TypedQuery<Value> query = em().createQuery("SELECT v FROM Value v WHERE v.containerId = :projectId and v.element.id = :elementId", Value.class);
//		query.setParameter("projectId", projectId);
//		query.setParameter("elementId", elementId);
//
//		Value currentValue = null;
//
//		try {
//			currentValue = query.getSingleResult();
//		} catch (NoResultException nre) {
//			// No current value
//		}
//		
//		if(currentValue != null) {
//			
//		}
	}
}
