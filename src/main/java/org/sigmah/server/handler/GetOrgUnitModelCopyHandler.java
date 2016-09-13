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

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.service.LayoutGroupService;
import org.sigmah.server.servlet.exporter.models.Realizer;
import org.sigmah.shared.command.GetOrgUnitModelCopy;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link GetOrgUnitModelCopy} command
 *
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class GetOrgUnitModelCopyHandler extends AbstractCommandHandler<GetOrgUnitModelCopy, OrgUnitModelDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetOrgUnitModelCopyHandler.class);

	private final LayoutGroupService layoutGroupService;

	@Inject
	public GetOrgUnitModelCopyHandler(LayoutGroupService layoutGroupService) {
		this.layoutGroupService = layoutGroupService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public OrgUnitModelDTO execute(final GetOrgUnitModelCopy cmd, final UserExecutionContext context) throws CommandException {

		LOG.debug("Duplicating Organizational unit model for command: {}", cmd);

		final Integer modelId = cmd.getModelId();

		final OrgUnitModel orgUnitModel = em().find(OrgUnitModel.class, modelId);

		if (orgUnitModel == null) {
			LOG.debug("Organizational unit model id#{} doesn't exist.", modelId);
			throw new CommandException("OrgUnit with id #" + modelId + " does not exist.");
		}

		LOG.debug("Found organizational unit model {}.", modelId);

		final OrgUnitModel copyOrgUnitModel = Realizer.realize(orgUnitModel);
		copyOrgUnitModel.resetImport(true);
		copyOrgUnitModel.setStatus(ProjectModelStatus.DRAFT);
		saveLayouts(copyOrgUnitModel);
		copyOrgUnitModel.setName(cmd.getNewModelName());
		copyOrgUnitModel.setOrganization(orgUnitModel.getOrganization());
		em().persist(copyOrgUnitModel);

		return mapper().map(copyOrgUnitModel, new OrgUnitModelDTO(), cmd.getMappingMode());
	}

	/**
	 * Saves the flexible elements of the imported organizational unit model.
	 * 
	 * @param orgUnitModel
	 *          The imported organizational unit model.
	 */
	private void saveLayouts(OrgUnitModel orgUnitModel) {
		if (orgUnitModel.getBanner() != null && orgUnitModel.getBanner().getLayout() != null) {
			layoutGroupService.saveLayoutGroups(orgUnitModel.getBanner().getLayout().getGroups());
		}

		if (orgUnitModel.getDetails() != null && orgUnitModel.getDetails().getLayout() != null) {
			layoutGroupService.saveLayoutGroups(orgUnitModel.getDetails().getLayout().getGroups());
		}
	}
}
