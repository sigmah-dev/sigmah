package org.sigmah.server.handler;

import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.profile.PrivacyGroup;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetPrivacyGroups;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

/**
 * Handler for {@link GetPrivacyGroups} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetPrivacyGroupsHandler extends AbstractCommandHandler<GetPrivacyGroups, ListResult<PrivacyGroupDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<PrivacyGroupDTO> execute(GetPrivacyGroups cmd, final UserExecutionContext context) throws CommandException {

		final TypedQuery<PrivacyGroup> query = em().createQuery("SELECT p FROM PrivacyGroup p WHERE p.organization.id = :orgid ORDER BY p.id", PrivacyGroup.class);
		query.setParameter("orgid", context.getUser().getOrganization().getId());

		final List<PrivacyGroup> resultPrivacyGroups = query.getResultList();

		return new ListResult<PrivacyGroupDTO>(mapper().mapCollection(resultPrivacyGroups, PrivacyGroupDTO.class));
	}

}
