package org.sigmah.server.endpoint.gwtrpc.handler;

import javax.persistence.EntityManager;

import org.sigmah.shared.command.MoveOrgUnit;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.shared.exception.MoveException;

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
        OrgUnit moved = em.find(OrgUnit.class, id);
        if (moved == null) {
            throw new CommandException("The org unit with id '" + id + "' doesn't exist.");
        }

        // Retrieves the parent unit.
        final int parentId = cmd.getParentId();
        OrgUnit parent = em.find(OrgUnit.class, parentId);
        if (parent == null) {
            throw new CommandException("The org unit with id '" + parentId + "' doesn't exist.");
        }

        // Do not move an org unit as the parent of itself.
        if (id == parentId) {
            throw new MoveException("Cannot move an org unit as it own child.", MoveException.ITSELF_ERR_CODE);
        }

        // Checks that my new parent is not already one of my child !
        if (!theKidIsNotMySon(moved, parentId)) {
            throw new MoveException("Cycle detected : cannot move an org unit as a child of one of its children.",
                    MoveException.CYCLE_ERR_CODE);
        }

        moved.setParent(parent);
        moved = em.merge(moved);

        // // If the moved org unit is the root.
        // if (moved.getParent() == null) {
        //
        // final Organization organization = moved.getOrganization();
        //
        // parent.setParent(null);
        // parent.setOrganization(moved.getOrganization());
        // parent = em.merge(parent);
        //
        // moved.setParent(parent);
        // moved.setOrganization(null);
        // moved = em.merge(moved);
        //
        // organization.setRoot(parent);
        // em.merge(organization);
        //
        // }
        // // Classic move.
        // else {
        //
        // moved.setParent(parent);
        // moved = em.merge(moved);
        //
        // }

        return new VoidResult();
    }

    /**
     * Let's sing.
     * 
     * @see Mickael J.
     */
    private boolean theKidIsNotMySon(OrgUnit me, int id) {

        boolean sheSaysIAmTheOne = true;

        if (me.getChildren() != null) {
            for (OrgUnit child : me.getChildren()) {
                if (child.getId() == id) {
                    // Billie Jean was right...
                    sheSaysIAmTheOne = false;
                } else {
                    sheSaysIAmTheOne = sheSaysIAmTheOne || theKidIsNotMySon(child, id);
                }
            }
        }

        return sheSaysIAmTheOne;

    }
}
