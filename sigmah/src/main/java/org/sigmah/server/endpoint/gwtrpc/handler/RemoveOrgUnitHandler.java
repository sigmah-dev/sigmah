package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.sigmah.shared.command.RemoveOrgUnit;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class RemoveOrgUnitHandler implements CommandHandler<RemoveOrgUnit> {

    private final EntityManager em;

    @Inject
    public RemoveOrgUnitHandler(EntityManager em) {
        this.em = em;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CommandResult execute(RemoveOrgUnit cmd, User user) throws CommandException {

        // Retrieves the removed unit.
        final int id = cmd.getId();
        final OrgUnit removed = em.find(OrgUnit.class, id);
        if (removed == null) {
            throw new CommandException("The org unit with id '" + id + "' doesn't exist.");
        }

        // Has children.
        if (removed.getChildren() != null && removed.getChildren().size() > 0) {
            throw new CommandException("The org unit with id '" + id + "' has children and cannot be removed.");
        }

        // Has projects.
        final Query query = em.createQuery("SELECT p FROM Project p WHERE :unit MEMBER OF p.partners");
        query.setParameter("unit", removed);
        final List<Project> listResults = (List<Project>) query.getResultList();
        if (listResults != null && listResults.size() > 0) {
            throw new CommandException("The org unit with id '" + id + "' has projects and cannot be removed.");
        }

        // Remove.
        removed.setDeleted(new Date());
        em.merge(removed);

        return new VoidResult();
    }
}
