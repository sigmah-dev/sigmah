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
import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.service.LayoutGroupService;
import org.sigmah.server.servlet.exporter.models.Realizer;
import org.sigmah.shared.command.GetContactModelCopy;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetContactModelCopyHandler extends AbstractCommandHandler<GetContactModelCopy, ContactModelDTO> {
	private static final Logger LOG = LoggerFactory.getLogger(GetContactModelCopyHandler.class);

	private final LayoutGroupService layoutGroupService;

	@Inject
	public GetContactModelCopyHandler(LayoutGroupService layoutGroupService) {
		this.layoutGroupService = layoutGroupService;
	}

	@Override
	@Transactional
	public ContactModelDTO execute(final GetContactModelCopy cmd, final UserExecutionContext context) throws CommandException {

		LOG.debug("Duplicating Contact model for command: {}", cmd);

		final Integer modelId = cmd.getModelId();

		final ContactModel contactModel = em().find(ContactModel.class, modelId);

		if (contactModel == null) {
			LOG.debug("Contact model id#{} doesn't exist.", modelId);
			throw new CommandException("Contact with id #" + modelId + " does not exist.");
		}

		LOG.debug("Found contact model {}.", modelId);

		final ContactModel copyContactModel = Realizer.realize(contactModel);
		copyContactModel.resetImport(true);
		copyContactModel.setStatus(ProjectModelStatus.DRAFT);
		saveLayouts(copyContactModel);
		copyContactModel.setName(cmd.getNewModelName());
		copyContactModel.setOrganization(contactModel.getOrganization());
		em().persist(copyContactModel);

		return mapper().map(copyContactModel, new ContactModelDTO());
	}

	private void saveLayouts(ContactModel contactModel) {
		if (contactModel.getCard() != null && contactModel.getCard().getLayout() != null) {
			layoutGroupService.saveLayoutGroups(contactModel.getCard().getLayout().getGroups());
		}

		if (contactModel.getDetails() != null && contactModel.getDetails().getLayout() != null) {
			layoutGroupService.saveLayoutGroups(contactModel.getDetails().getLayout().getGroups());
		}
	}
}
