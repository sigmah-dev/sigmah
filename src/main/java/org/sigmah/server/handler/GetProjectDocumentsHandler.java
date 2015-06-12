package org.sigmah.server.handler;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.PrivacyGroup;
import org.sigmah.server.domain.profile.PrivacyGroupPermission;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.domain.value.File;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProjectDocuments;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.util.ValueResultUtils;

/**
 * Handler for {@link GetProjectDocuments} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProjectDocumentsHandler extends AbstractCommandHandler<GetProjectDocuments, ListResult<ReportReference>> {

	/**
	 * Allow access to the files.
	 */
	@Inject
	private FileStorageProvider fileStorageProvider;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ReportReference> execute(GetProjectDocuments cmd, final UserExecutionContext context) throws CommandException {

		final List<ReportReference> references = new ArrayList<ReportReference>();

		if (cmd.getProjectId() == null || cmd.getElements() == null) {
			throw new IllegalArgumentException("GetProjectDocuments should specify a project id and a element ids list.");
		}

		// Query to retrieves the value of a files list element in this project.
		final TypedQuery<Value> valuesQuery = em().createQuery("SELECT v FROM Value v WHERE v.containerId = :projectId AND v.element.id = :elementId", Value.class);

		// Query to retrieves all the files of a list.
		final TypedQuery<File> filesQuery = em().createQuery("SELECT f FROM File f WHERE f.id IN (:idsList)", File.class);

		// For each files list.
		for (final GetProjectDocuments.FilesListElement fle : cmd.getElements()) {

			valuesQuery.setParameter("projectId", cmd.getProjectId());
			valuesQuery.setParameter("elementId", fle.getId());

			final List<Value> values = valuesQuery.getResultList();

			if (values != null) {

				// For each value, retrieves the files list.
				for (final Value v : values) {
					if (isViewableByUser(v, context.getUser())) {
						filesQuery.setParameter("idsList", ValueResultUtils.splitValuesAsInteger(v.getValue()));

						final List<File> documents = filesQuery.getResultList();

						if (documents != null) {
							for (final File document : documents) {

								final FileVersion lastVersion = document.getLastVersion();
								final FileVersionDTO lastVersionDTO = mapper().map(lastVersion, FileVersionDTO.class);
								
								lastVersionDTO.setAvailable(fileStorageProvider.exists(lastVersionDTO.getPath()));

								final ReportReference r = new ReportReference(lastVersionDTO);
								r.setId(lastVersion.getId());
								r.setName(lastVersion.getName() + '.' + lastVersion.getExtension());
								r.setLastEditDate(lastVersion.getAddedDate());
								r.setEditorName(User.getUserShortName(lastVersion.getAuthor()));
								r.setPhaseName(fle.getPhaseName());
								r.setFlexibleElementLabel(fle.getElementLabel());

								references.add(r);
							}
						}
					}
				}
			}
		}

		return new ListResult<ReportReference>(references);
	}

	/***
	 * Checks if the given {@code user} has the right to see the given {@code value}.
	 * 
	 * @param value
	 *          The value.
	 * @param user
	 *          The user.
	 * @return {@code true} if the user has the right to see the value, {@code false} otherwise.
	 */
	private static boolean isViewableByUser(final Value value, final User user) {

		final PrivacyGroup documentPG = value.getElement() != null ? value.getElement().getPrivacyGroup() : null;

		if (documentPG == null) {
			return true;
		}

		for (final Profile profile : user.getOrgUnitWithProfiles().getProfiles()) {
			for (final PrivacyGroupPermission pgp : profile.getPrivacyGroupPermissions()) {
				if (documentPG.equals(pgp.getPrivacyGroup())) {
					return true;
				}
			}
		}

		return false;
	}

}
