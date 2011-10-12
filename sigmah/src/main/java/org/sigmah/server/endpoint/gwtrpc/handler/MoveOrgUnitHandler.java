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
        final boolean theKidIsMySon = theKidIsMySon(moved, parentId);
        if (theKidIsMySon) {
            throw new MoveException("Cycle detected : cannot move an org unit as a child of one of its children.",
                    MoveException.CYCLE_ERR_CODE);
        }

        // Performs the move.
        moved.setParent(parent);
        moved = em.merge(moved);

        return new VoidResult();
    }

    /**
     * Let's sing...
     * 
     * @param me
     *            Mickael J.
     * @param theKidName
     *            Billie Jean son's name.
     * @see Thriller
     * @since 1982
     */
    private boolean theKidIsMySon(OrgUnit me, int theKidName) {

        boolean sheSaysIAmTheOne = false;

        if (me.getChildren() != null) {

            // For each of my sons.
            for (final OrgUnit son : me.getChildren()) {

                // My son ?
                if (son.getId() == theKidName) {
                    sheSaysIAmTheOne = true;
                }
                // Son of my son ?
                else {
                    sheSaysIAmTheOne = theKidIsMySon(son, theKidName);
                }

                // Damn it, Billie Jean was right...
                if (sheSaysIAmTheOne) {
                    break;
                }

            }

        }

        return sheSaysIAmTheOne;

    }
}
