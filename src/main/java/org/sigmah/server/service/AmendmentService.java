package org.sigmah.server.service;

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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.sigmah.server.dao.AmendmentDAO;
import org.sigmah.server.dao.LayoutGroupIterationDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Amendment;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.IterationHistoryToken;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.layout.LayoutGroupIteration;
import org.sigmah.server.domain.logframe.LogFrameCopyContext;
import org.sigmah.server.handler.GetLayoutGroupIterationsHandler;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;
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

	@Inject
	private LayoutGroupIterationDAO layoutGroupIterationDAO;

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
	 * @param name
	 *			Name of the new amendment.
	 * @return The created {@link Amendment} instance.
	 */
	@Transactional
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
			// simple group
			if(!group.getHasIterations()) {
				for (final LayoutConstraint constraint : group.getConstraints()) {
					final FlexibleElement element = constraint.getElement();

					// Since the transformation of amendments into project core
					// versions, every value has to be saved.

					storeValue(project.getId(), element.getId(), null, amendment, historyTokens);
				}
			}

			// iterative group

			List<LayoutGroupIteration> iterations = layoutGroupIterationDAO.findByLayoutGroupAndContainer(group.getId(), project.getId(), -1);
			for(LayoutGroupIteration iteration : iterations) {

				storeIteration(iteration, amendment);

				for (final LayoutConstraint constraint : group.getConstraints()) {
					final FlexibleElement element = constraint.getElement();

					// Since the transformation of amendments into project core
					// versions, every value has to be saved.

					storeValue(project.getId(), element.getId(), iteration.getId(), amendment, historyTokens);
				}
			}
		}

		amendment.setValues(historyTokens);
		em().merge(amendment);

		return amendment;
	}

	private void storeIteration(LayoutGroupIteration iteration, Amendment amendment) {
		IterationHistoryToken iterationToken = new IterationHistoryToken();
		iterationToken.setDate(new Date());
		iterationToken.setCoreVersion(amendment);
		iterationToken.setLayoutGroup(iteration.getLayoutGroup());
		iterationToken.setLayoutGroupIterationId(iteration.getId());
		iterationToken.setName(iteration.getName());
		iterationToken.setProjectId(iteration.getContainerId());

		em().merge(iterationToken);
	}

	private void storeValue(Integer projectId, Integer elementId, Integer iterationId, Amendment amendment, List<HistoryToken> historyTokens) {
		// The value of the current flexible element must be saved.
		final Query maxDateQuery;
		if(iterationId == null) {
			maxDateQuery = em().createQuery("SELECT MAX(h.date) FROM HistoryToken h WHERE h.elementId = :elementId AND h.projectId = :projectId");
			maxDateQuery.setParameter("projectId", projectId);
			maxDateQuery.setParameter("elementId", elementId);
		} else {
			maxDateQuery = em().createQuery("SELECT MAX(h.date) FROM HistoryToken h WHERE h.elementId = :elementId AND h.projectId = :projectId AND h.layoutGroupIterationId = :iterationId");
			maxDateQuery.setParameter("projectId", projectId);
			maxDateQuery.setParameter("elementId", elementId);
			maxDateQuery.setParameter("iterationId", iterationId);
		}

		try {
			final Date maxDate = (Date) maxDateQuery.getSingleResult();

			final Query query;

			if(iterationId == null) {
				query = em().createQuery("SELECT h FROM HistoryToken h WHERE h.elementId = :elementId AND h.projectId = :projectId AND h.date = :maxDate");
				query.setParameter("projectId", projectId);
				query.setParameter("elementId", elementId);
				query.setParameter("maxDate", maxDate);
			} else {
				query = em().createQuery("SELECT h FROM HistoryToken h WHERE h.elementId = :elementId AND h.projectId = :projectId AND h.date = :maxDate AND h.layoutGroupIterationId = :iterationId");
				query.setParameter("projectId", projectId);
				query.setParameter("elementId", elementId);
				query.setParameter("maxDate", maxDate);
				query.setParameter("iterationId", iterationId);
			}

			final HistoryToken token = (HistoryToken) query.getSingleResult();
			token.setCoreVersion(amendment);
			em().merge(token);

			historyTokens.add(token);

		} catch (NoResultException e) {
			// There is no history token for the given element. No action.
		}
	}

}
