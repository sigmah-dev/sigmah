package org.sigmah.server.handler;

import java.util.HashSet;

import org.sigmah.server.dao.CountryDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.calendar.PersonalCalendar;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.AddOrgUnit;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * Adds {@link OrgUnit} from {@link AddOrgUnit}.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class AddOrgUnitHandler extends AbstractCommandHandler<AddOrgUnit, CreateResult> {

	/**
	 * Injected {@link OrgUnitDAO}.
	 */
	@Inject
	private OrgUnitDAO orgUnitDAO;

	/**
	 * Injected {@link CountryDAO}.
	 */
	@Inject
	private CountryDAO countryDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CreateResult execute(final AddOrgUnit cmd, final UserExecutionContext context) throws CommandException {

		// --
		// Controls command arguments.
		// --

		final Integer parentId = cmd.getParentId();
		final Integer modelId = cmd.getModelId();
		final OrgUnitDTO orgUnitDTO = cmd.getUnit();

		if (parentId == null || modelId == null || orgUnitDTO == null) {
			throw new CommandException("Invalid command arguments.");
		}

		// --
		// Retrieves the parent unit.
		// --

		final OrgUnit parent = orgUnitDAO.findById(parentId);

		if (parent == null) {
			throw new CommandException("The parent org unit with id '" + parentId + "' doesn't exist.");
		}

		// --
		// Retrieves the model.
		// --

		final OrgUnitModel model = em().find(OrgUnitModel.class, modelId);
		if (model == null) {
			throw new CommandException("The org unit model with id '" + modelId + "' doesn't exist.");
		}

		// --
		// Retrieves the country.
		// --

		final Country country;
		if (orgUnitDTO.getOfficeLocationCountry() != null) {
			country = countryDAO.findById(orgUnitDTO.getOfficeLocationCountry().getId());
		} else {
			country = null;
		}


		final OrgUnit newOrgUnit = performCreation(parent, orgUnitDTO, country, model, cmd.getCalendarName());

		return new CreateResult(mapper().map(newOrgUnit, new OrgUnitDTO(), cmd.getMappingMode()));
	}

	/**
	 * Creates a new org unit and its calendar.
	 * 
	 * @param parent Parent org unit.
	 * @param orgUnitDTO DTO containing the name and the full name of the new org unit.
	 * @param country Country of the org unit.
	 * @param model Model to use.
	 * @param calendarName Name of the calendar of the org unit.
	 * @return The new org unit.
	 */
	@Transactional
	protected OrgUnit performCreation(final OrgUnit parent, final OrgUnitDTO orgUnitDTO, final Country country, final OrgUnitModel model, String calendarName) {
		// --
		// Creates the calendar.
		// --

		PersonalCalendar calendar = new PersonalCalendar();
		calendar.setName(calendarName);
		calendar = em().merge(calendar);
		// --
		// Creates and saves the new unit.
		// --

		OrgUnit newOrgUnit = new OrgUnit();
		newOrgUnit.setParentOrgUnit(parent);
		newOrgUnit.setLocation(null);
		newOrgUnit.setName(orgUnitDTO.getName());
		newOrgUnit.setFullName(orgUnitDTO.getFullName());
		newOrgUnit.setChildrenOrgUnits(new HashSet<OrgUnit>(0));
		newOrgUnit.setDatabases(null);
		newOrgUnit.setCalendarId(calendar.getId());
		newOrgUnit.setOfficeLocationCountry(country);
		newOrgUnit.setOrganization(null);
		newOrgUnit.setOrgUnitModel(model);
		newOrgUnit = em().merge(newOrgUnit);
		// --
		// Updates the model status.
		// --

		model.setStatus(ProjectModelStatus.USED);
		em().merge(model);
		return newOrgUnit;
	}
}
