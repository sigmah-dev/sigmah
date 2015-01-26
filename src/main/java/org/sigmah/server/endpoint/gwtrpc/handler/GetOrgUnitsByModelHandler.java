package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetOrgUnitsByModel;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.OrgUnitListResult;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.OrgUnitDTO;
import org.sigmah.shared.dto.OrgUnitDTOLight;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * {@link GetOrgUnitByModel} command exectution
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * 
 */
public class GetOrgUnitsByModelHandler implements CommandHandler<GetOrgUnitsByModel> {

	private final static Log log = LogFactory.getLog(GetOrgUnitsByModelHandler.class);

	private final EntityManager em;
	private final Mapper mapper;

	@Inject
	public GetOrgUnitsByModelHandler(EntityManager em, Mapper mapper, Injector injector) {
		this.em = em;
		this.mapper = mapper;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CommandResult execute(GetOrgUnitsByModel cmd, User user) throws CommandException {
		if (cmd == null || cmd.getOrgUnitModelId() == null) {
			return null;
		}

		String qlString = "SELECT o from OrgUnit o WHERE o.orgUnitModel.id= :orgUnitId";
		Query query = em.createQuery(qlString);
		query.setParameter("orgUnitId", cmd.getOrgUnitModelId());

		List<OrgUnit> orgUnitList = (List<OrgUnit>) query.getResultList();
		List<OrgUnitDTOLight> orgUnitDTOList = new ArrayList<OrgUnitDTOLight>();

		if (orgUnitList == null) {
			return null;
		}

		for (OrgUnit orgUnit : orgUnitList) {
			if (isOrgUnitVisible(orgUnit, user)) {
				orgUnitDTOList.add(mapper.map(orgUnit, OrgUnitDTO.class).light());
			}
		}

		OrgUnitListResult result = new OrgUnitListResult();
		result.setOrgUnitDTOLightList(orgUnitDTOList);

		return result;
	}

	/**
	 * Returns if the org unit is visible for the given user.
	 * 
	 * @param orgUnit
	 *            The org unit.
	 * @param user
	 *            The user.
	 * @return If the org unit is visible for the user.
	 */
	public static boolean isOrgUnitVisible(OrgUnit orgUnit, User user) {

		if (orgUnit.getDeleted() != null) {
			return false;
		}

		// Checks that the user can see this org unit.
		final HashSet<OrgUnit> units = new HashSet<OrgUnit>();
		GetProjectHandler.crawlUnits(user.getOrgUnitWithProfiles().getOrgUnit(), units, true);

		for (final OrgUnit unit : units) {
			if (orgUnit.getId() == unit.getId()) {
				return true;
			}
		}

		return false;
	}

}
