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
import org.sigmah.server.dao.LayoutGroupIterationDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.layout.LayoutGroupIteration;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.UpdateLayoutGroupIterations;
import org.sigmah.shared.command.UpdateLayoutGroupIterations.IterationChange;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;

public class UpdateLayoutGroupIterationsHandler extends AbstractCommandHandler<UpdateLayoutGroupIterations, ListResult<IterationChange>> {

	private final LayoutGroupIterationDAO layoutGroupIterationDAO;

	@Inject
	public UpdateLayoutGroupIterationsHandler(LayoutGroupIterationDAO layoutGroupIterationDAO) {
		this.layoutGroupIterationDAO = layoutGroupIterationDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<IterationChange> execute(final UpdateLayoutGroupIterations cmd, final UserExecutionContext context) throws CommandException {

		int containerId = cmd.getContainerId();

		for(IterationChange iterationChange : cmd.getIterationChanges()) {
			if(iterationChange.isDeleted()) {
				if(iterationChange.isCreated()) {
					continue;
				}

				remove(iterationChange);
			} else if(iterationChange.isCreated()) {
				create(containerId, iterationChange);
			} else {
				rename(iterationChange, context.getUser());
			}
		}

		return new ListResult<>(cmd.getIterationChanges());
	}

	@Transactional
	protected void rename(IterationChange change, User user) {
		LayoutGroupIteration layoutGroupIteration = layoutGroupIterationDAO.findById(change.getIterationId());
		layoutGroupIteration.setName(change.getName());
		layoutGroupIterationDAO.persist(layoutGroupIteration, user);
	}

	@Transactional
	protected void create(int containerId, IterationChange change) {
		LayoutGroupIteration iteration = new LayoutGroupIteration();
		iteration.setContainerId(containerId);
		iteration.setName(change.getName());
		iteration.setLayoutGroup(em().find(LayoutGroup.class, change.getLayoutGroupId()));
		iteration = em().merge(iteration);

		change.setNewIterationId(iteration.getId());
	}

	@Transactional
	protected void remove(IterationChange change) {
		em().remove(em().find(LayoutGroupIteration.class, change.getIterationId()));
	}
}
