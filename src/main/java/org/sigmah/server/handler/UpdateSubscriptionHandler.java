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

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.ReportDefinition;
import org.sigmah.server.domain.ReportSubscription;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.UpdateSubscription;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;

import com.google.inject.Inject;

/**
 * Handler for {@link UpdateSubscription} command
 * 
 * @author Alex Bertram
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
@Deprecated
public class UpdateSubscriptionHandler extends AbstractCommandHandler<UpdateSubscription, VoidResult> {


	@Inject
	public UpdateSubscriptionHandler() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final UpdateSubscription cmd, final UserExecutionContext context) throws CommandException {

		User currentUser = context.getUser();

		int userId = cmd.getUserId() == null ? currentUser.getId() : cmd.getUserId();

		final TypedQuery<ReportSubscription> query =
				em().createQuery("select sub from ReportSubscription sub where sub.template.id = :templateId and sub.user.id = :userId", ReportSubscription.class);

		final List<ReportSubscription> results = query.setParameter("templateId", cmd.getReportTemplateId()).getResultList();

		if (results.size() == 0) {

			// new subscriptions can be created either by the user themselves
			// or by a second user (invitation)

			if (cmd.isSubscribed()) {

				ReportSubscription sub =
						new ReportSubscription(em().getReference(ReportDefinition.class, cmd.getReportTemplateId()), em().getReference(User.class, userId));

				sub.setSubscribed(true);

				if (userId != currentUser.getId()) {
					sub.setInvitingUser(currentUser);
				}

				em().persist(sub);
			}
		} else {

			// only the user themselves can change a subscription once it's been
			// created.

			if (userId != currentUser.getId()) {
				throw new CommandException("Illegal access exception.");
			}

			ReportSubscription sub = results.get(0);
			sub.setSubscribed(cmd.isSubscribed());
		}

		return null;
	}
}
