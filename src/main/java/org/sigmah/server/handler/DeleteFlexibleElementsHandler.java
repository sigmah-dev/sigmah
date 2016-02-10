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


import java.util.List;

import javax.persistence.Query;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DeleteFlexibleElements;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import javax.persistence.TypedQuery;
import org.sigmah.server.domain.value.Value;

/**
 * Handler for {@link DeleteFlexibleElements} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DeleteFlexibleElementsHandler extends AbstractCommandHandler<DeleteFlexibleElements, VoidResult> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(DeleteFlexibleElementsHandler.class);

	@Inject
	public DeleteFlexibleElementsHandler() {

	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public VoidResult execute(final DeleteFlexibleElements cmd, final UserExecutionContext context) throws CommandException {

		if (cmd.getFlexibleElements() != null) {
			performDelete(cmd.getFlexibleElements());
		}

		return null;
	}
	
	@Transactional
	protected void performDelete(List<FlexibleElementDTO> flexibleElements) {
		for (FlexibleElementDTO dto : flexibleElements) {

			FlexibleElement flexibleElement = em().find(FlexibleElement.class, dto.getId());

            Query valueQuery = em().createQuery("DELETE FROM Value v WHERE v.element = :element");
            valueQuery.setParameter("element", flexibleElement);
            valueQuery.executeUpdate();

			TypedQuery<LayoutConstraint> query = em().createQuery("Select l from LayoutConstraint l Where l.element = :flexibleElement", LayoutConstraint.class);
			query.setParameter("flexibleElement", flexibleElement);
			for (LayoutConstraint layout : query.getResultList()) {
				em().remove(layout);
			}
			LOG.debug("DeactivateUsersHandler flexElt " + dto.getId() + " name" + dto.getLabel());

			em().remove(flexibleElement);
		}
	}

}
