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

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DeleteGroups;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * Handler for {@link DeleteGroups} command
 *
 * @author Nikita Jibhkate (niksj1996@gmail.com))
 */
public class DeleteGroupsHandler extends AbstractCommandHandler<DeleteGroups, VoidResult> {

    @Inject
    public DeleteGroupsHandler() {

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public VoidResult execute(final DeleteGroups cmd, final UserExecutionContext context) throws CommandException {

        if (cmd.getLayoutGroups() != null) {

            performDelete(cmd.getLayoutGroups());
        }

        return null;
    }

    @Transactional
    protected void performDelete(List<LayoutGroupDTO> groups) {
        if (groups != null) {
            for (LayoutGroupDTO layoutgroup : groups) {
                LayoutGroup lg = em().find(LayoutGroup.class, layoutgroup.getId());
                if (lg != null) {
                    em().remove(lg);
                }
            }
        }

        em().flush();
    }

}
