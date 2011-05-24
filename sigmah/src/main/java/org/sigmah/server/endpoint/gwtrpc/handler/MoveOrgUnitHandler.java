package org.sigmah.server.endpoint.gwtrpc.handler;

import javax.persistence.EntityManager;

import org.sigmah.shared.command.MoveOrgUnit;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class MoveOrgUnitHandler implements CommandHandler<MoveOrgUnit> {

    private final EntityManager em;

    @Inject
    public MoveOrgUnitHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public CommandResult execute(MoveOrgUnit cmd, User user) throws CommandException {

        // Retrieves the moved unit.
        final int id = cmd.getId();
        final OrgUnit moved = em.find(OrgUnit.class, id);
        if (moved == null) {
            throw new CommandException("The org unit with id '" + id + "' doesn't exist.");
        }

        // Retrieves the parent unit.
        final int parentId = cmd.getParentId();
        final OrgUnit parent = em.find(OrgUnit.class, parentId);
        if (parent == null) {
            throw new CommandException("The org unit with id '" + parentId + "' doesn't exist.");
        }

        if (id != parentId) {

            // Move.
            moved.setParent(parent);
            em.merge(moved);

        }

        return new VoidResult();
    }
}
