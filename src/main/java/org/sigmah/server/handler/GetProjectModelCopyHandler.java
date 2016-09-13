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


import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.ProjectModelVisibility;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.service.LayoutGroupService;
import org.sigmah.server.servlet.exporter.models.Realizer;
import org.sigmah.shared.command.GetProjectModelCopy;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for the {@link GetProjectModelCopy} command.
 * 
 * @author Kristela Macaj (kmacaj@ideia.fr) (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetProjectModelCopyHandler extends AbstractCommandHandler<GetProjectModelCopy, ProjectModelDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetProjectModelCopyHandler.class);

	private final LayoutGroupService layoutGroupService;

	@Inject
	public GetProjectModelCopyHandler(LayoutGroupService layoutGroupService) {
		this.layoutGroupService = layoutGroupService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ProjectModelDTO execute(final GetProjectModelCopy cmd, final UserExecutionContext context) throws CommandException {

		LOG.debug("Retrieving project model for command: {}", cmd);

		final ProjectModel existentProjectModel = em().find(ProjectModel.class, cmd.getModelId());

		if (existentProjectModel == null) {
			LOG.debug("Project model id with #{} doesn't exist.", cmd.getModelId());
			throw new CommandException("Project model with id " + cmd.getModelId() + " cannot be found.");
		}

		LOG.debug("Found project model with id #{}.", cmd.getModelId());

		final ProjectModel copyProjectModel = Realizer.realize(existentProjectModel);
		copyProjectModel.resetImport(true);

		// Set status 'DRAFT'
		copyProjectModel.setStatus(ProjectModelStatus.DRAFT);

		// Set the visibility
		final ProjectModelVisibility visibility = new ProjectModelVisibility();
		visibility.setModel(copyProjectModel);
		if (copyProjectModel.getVisibilities() != null && !copyProjectModel.getVisibilities().isEmpty()) {
			visibility.setType(copyProjectModel.getVisibilities().get(0).getType());
		}
		visibility.setOrganization(context.getUser().getOrganization());

		final ArrayList<ProjectModelVisibility> visibilities = new ArrayList<ProjectModelVisibility>();
		visibilities.add(visibility);
		copyProjectModel.setVisibilities(visibilities);

		saveCopy(copyProjectModel, cmd);

		return mapper().map(copyProjectModel, new ProjectModelDTO(), cmd.getMappingMode());
	}

	/**
	 * Save the given copy in a transaction.
	 * 
	 * @param copyProjectModel
	 * @param cmd 
	 */
	@Transactional
	protected void saveCopy(final ProjectModel copyProjectModel, final GetProjectModelCopy cmd) {
		// Save project elements
		saveLayouts(copyProjectModel);

		copyProjectModel.setName(cmd.getNewModelName());

		em().persist(copyProjectModel);
	}

	/**
	 * Saves the flexible elements of the imported project model.
	 * 
	 * @param projectModel
	 *          The imported project model.
	 */
	private void saveLayouts(ProjectModel projectModel) {
		if (projectModel.getProjectBanner() != null && projectModel.getProjectBanner().getLayout() != null) {
			layoutGroupService.saveLayoutGroups(projectModel.getProjectBanner().getLayout().getGroups());
		}

		if (projectModel.getProjectDetails() != null && projectModel.getProjectDetails().getLayout() != null) {
			layoutGroupService.saveLayoutGroups(projectModel.getProjectDetails().getLayout().getGroups());
		}

		EntityManager entityManager = em();
		List<PhaseModel> phases = projectModel.getPhaseModels();
		if (phases == null) {
			return;
		}
		projectModel.setPhaseModels(null);
		entityManager.persist(projectModel);
		for (PhaseModel phase : phases) {
			phase.setParentProjectModel(projectModel);
			if (phase.getLayout() != null) {
				layoutGroupService.saveLayoutGroups(phase.getLayout().getGroups());
			}
			if (phase.getDefinition() != null) {
				entityManager.persist(phase.getDefinition());
			}
			entityManager.persist(phase);
		}
		projectModel.setPhaseModels(phases);
	}

}
