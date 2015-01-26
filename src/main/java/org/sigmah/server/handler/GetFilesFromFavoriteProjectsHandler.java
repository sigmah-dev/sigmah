package org.sigmah.server.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.sigmah.server.dao.impl.FileHibernateDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectDetails;
import org.sigmah.server.domain.element.FilesListElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetFilesFromFavoriteProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.value.FileDTO;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.util.ValueResultUtils;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetFilesFromFavoriteProjectsHandler extends AbstractCommandHandler<GetFilesFromFavoriteProjects, ListResult<FileVersionDTO>> {

	@Inject
	private FileHibernateDAO fileHibernateDAO;
	
	@Override
	protected ListResult<FileVersionDTO> execute(GetFilesFromFavoriteProjects command, UserDispatch.UserExecutionContext context) throws CommandException {
		
		// Creating queries
		final TypedQuery<Project> projectQuery = em().createQuery("SELECT p FROM Project p WHERE :user MEMBER OF p.favoriteUsers", Project.class);
		final TypedQuery<String> valueQuery = em().createQuery("SELECT v.value FROM Value v WHERE v.containerId = :projectId AND v.element.id = :elementId", String.class);
		
		final ArrayList<Integer> fileIds = new ArrayList<Integer>();
		
		// Searching for favorites projects of the current user
		projectQuery.setParameter("user", context.getUser());
		final List<Project> projects = projectQuery.getResultList();
		
		// Searching every FilesListElement of the found projects
		for(final Project project : projects) {
			for(final PhaseModel phaseModel : project.getProjectModel().getPhaseModels()) {
				getFilesFromGroups(phaseModel.getLayout().getGroups(), valueQuery, project, fileIds);
			}
			
			final ProjectDetails projectDetails = project.getProjectModel().getProjectDetails();
			if(projectDetails != null) {
				getFilesFromGroups(projectDetails.getLayout().getGroups(), valueQuery, project, fileIds);
			}
		}
		
		final List<FileVersion> versions;
		
		if(!fileIds.isEmpty()) {
			versions = fileHibernateDAO.findVersions(fileIds, FileDTO.LoadingScope.LAST_VERSION);
		} else {
			versions = Collections.emptyList();
		}
		
		return new ListResult<FileVersionDTO>(mapper().mapCollection(versions, FileVersionDTO.class));
	}

	private void getFilesFromGroups(final List<LayoutGroup> groups, final TypedQuery<String> valueQuery, final Project project, final ArrayList<Integer> fileIds) {
		for(final LayoutGroup group : groups) {
			for(final LayoutConstraint constraint : group.getConstraints()) {
				final FlexibleElement flexibleElement = constraint.getElement();
				
				if(flexibleElement instanceof FilesListElement) {
					// Retrieving the file ids
					valueQuery.setParameter("projectId", project.getId());
					valueQuery.setParameter("elementId", flexibleElement.getId());
					
					try {
						final String value = valueQuery.getSingleResult();
						if(value != null) {
							fileIds.addAll(ValueResultUtils.splitValuesAsInteger(value));
						}
						
					} catch (NoResultException | ClassCastException e) {
						// No value
					}
				}
			}
		}
	}
	
}
