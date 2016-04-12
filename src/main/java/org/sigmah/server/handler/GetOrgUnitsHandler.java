package org.sigmah.server.handler;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.command.GetOrgUnit;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link GetOrgUnit} command implementation.
 */
public class GetOrgUnitsHandler extends AbstractCommandHandler<GetOrgUnits, ListResult<OrgUnitDTO>> {
	private static final Logger LOG = LoggerFactory.getLogger(GetOrgUnitsHandler.class);

	private final Mapper mapper;
	private final OrgUnitDAO orgUnitDAO;

	@Inject
	public GetOrgUnitsHandler(Mapper mapper, OrgUnitDAO orgUnitDAO) {
		this.mapper = mapper;
		this.orgUnitDAO = orgUnitDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<OrgUnitDTO> execute(GetOrgUnits cmd, final UserExecutionContext context) throws CommandException {
		List<OrgUnit> orgUnits;
		if (cmd.getOrgUnitIds() == null) {
			orgUnits = orgUnitDAO.findByOrganizationId(context.getUser().getOrganization().getId());
		} else {
			orgUnits = orgUnitDAO.findByIds(cmd.getOrgUnitIds());
		}

		List<OrgUnitDTO> orgUnitDTOs = new ArrayList<>();
		for (OrgUnit orgUnit : orgUnits) {
			orgUnitDTOs.add(mapper.map(orgUnit, OrgUnitDTO.class));
		}

		return new ListResult<>(orgUnitDTOs);
	}

}
