package org.sigmah.server.handler;

import com.google.inject.persist.Transactional;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.domain.UserPermission;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.RemovePartner;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler for {@link RemovePartner} command
 * 
 * @author Alex Bertram
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class RemovePartnerHandler extends AbstractCommandHandler<RemovePartner, VoidResult> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final RemovePartner cmd, final UserExecutionContext context) throws CommandException {

		// verify the current user has access to this site.
		final UserDatabase db = em().find(UserDatabase.class, cmd.getDatabaseId());
		if (!db.getOwner().getId().equals(context.getUser().getId())) {
			final UserPermission perm = db.getPermissionByUser(context.getUser());
			if (perm == null || perm.isAllowDesign()) {
				throw new CommandException("Illegal access exception.");
			}
		}

		// Check to see if there are already sites associated with this partner.
		final TypedQuery<Number> countQuery =
				em().createQuery(
					"select count(s) from Site s where "
						+ "s.activity.id in (select a.id from Activity a where a.database.id = :dbId) and "
						+ "s.partner.id = :partnerId and "
						+ "s.dateDeleted is null", Number.class);

		countQuery.setParameter("dbId", cmd.getDatabaseId());
		countQuery.setParameter("partnerId", cmd.getPartnerId());

		final int siteCount = countQuery.getSingleResult().intValue();

		if (siteCount > 0) {
			throw new CommandException("Partner has sites exception.");
		}

		removePartner(db, cmd.getPartnerId());

		return new VoidResult();
	}
	
	@Transactional
	protected void removePartner(UserDatabase db, int partnerId) {
		db.getPartners().remove(em().getReference(OrgUnit.class, partnerId));
		// NOTE: Call to merge added. Needs to verify if really needed.
		em().merge(db);
	}
}
