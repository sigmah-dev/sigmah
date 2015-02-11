package org.sigmah.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.sigmah.server.dao.AmendmentDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Amendment;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.logframe.LogFrameCopyContext;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.referential.IndicatorCopyStrategy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * Creates and updates project amendments.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class AmendmentService extends AbstractEntityService<Amendment, Integer, AmendmentDTO> {

	/**
	 * Injected {@link ProjectDAO}.
	 */
	@Inject
	private ProjectDAO projectDAO;

	@Inject
	private AmendmentDAO amendmentDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Amendment create(final PropertyMap properties, final UserExecutionContext context) {

		final String name = properties.get("name");
		final Integer projectId = properties.get("projectId");
		final Project project = projectDAO.findById(projectId);

		return createAmendment(project, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Amendment update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) {

		Amendment amendment = amendmentDAO.findById(entityId);

		amendment.setName((String) changes.get("name"));

		em().persist(amendment);

		em().flush();

		return amendment;

	}

	/**
	 * Creates a new {@link Amendment} for the given {@code project}.
	 * 
	 * @param project
	 *          The Project instance.
	 * @return The created {@link Amendment} instance.
	 */
	public Amendment createAmendment(final Project project, String name) {

		final Amendment amendment = new Amendment();
		amendment.setParentProject(project);
		amendment.setLogFrame(project.getLogFrame().copy(LogFrameCopyContext.toProject(project).withStrategy(IndicatorCopyStrategy.REFERENCE)));
		amendment.setState(project.getAmendmentState());
		amendment.setName(name);
		amendment.setVersion(project.getAmendmentVersion());
		amendment.setRevision(project.getAmendmentRevision());
		amendment.setDate(new Date());
		
		em().persist(amendment);

		// Running through every flexible element attached to the project [...] and saving the last history token in the
		// values property.
		// @see GetHistoryHandler
		final List<HistoryToken> historyTokens = new ArrayList<HistoryToken>();

		// Looking for all groups
		final List<LayoutGroup> groups = new ArrayList<LayoutGroup>();
		for (final PhaseModel phaseModel : project.getProjectModel().getPhaseModels()) {
			groups.addAll(phaseModel.getLayout().getGroups());
		}
		groups.addAll(project.getProjectModel().getProjectDetails().getLayout().getGroups());

		// Iterating on groups
		for (final LayoutGroup group : groups) {
			for (final LayoutConstraint constraint : group.getConstraints()) {
				final FlexibleElement element = constraint.getElement();
				
				// Since the transformation of amendments into project core
				// versions, every value has to be saved.
				
				// The value of the current flexible element must be saved.
				final Query maxDateQuery = em().createQuery("SELECT MAX(h.date) FROM HistoryToken h WHERE h.elementId = :elementId AND h.projectId = :projectId");
				maxDateQuery.setParameter("projectId", project.getId());
				maxDateQuery.setParameter("elementId", element.getId());

				try {
					final Date maxDate = (Date) maxDateQuery.getSingleResult();

					final Query query =
							em().createQuery("SELECT h FROM HistoryToken h WHERE h.elementId = :elementId AND h.projectId = :projectId AND h.date = :maxDate");
					query.setParameter("projectId", project.getId());
					query.setParameter("elementId", element.getId());
					query.setParameter("maxDate", maxDate);

					final HistoryToken token = (HistoryToken) query.getSingleResult();
					token.setCoreVersion(amendment);
					em().merge(token);
					
					historyTokens.add(token);

				} catch (NoResultException e) {
					// There is no history token for the given element. No action.
				}
			}
		}

		amendment.setValues(historyTokens);
		em().merge(amendment);

		return amendment;
	}

}
