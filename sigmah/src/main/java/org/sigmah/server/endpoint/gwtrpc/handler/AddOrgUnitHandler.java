package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.HashSet;

import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.sigmah.server.dao.Transactional;
import org.sigmah.shared.command.AddOrgUnit;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.domain.Country;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.calendar.PersonalCalendar;
import org.sigmah.shared.dto.OrgUnitDTO;
import org.sigmah.shared.dto.OrgUnitDTOLight;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class AddOrgUnitHandler implements CommandHandler<AddOrgUnit> {

    private final EntityManager em;
    private final Mapper mapper;

    @Inject
    public AddOrgUnitHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public CommandResult execute(AddOrgUnit cmd, User user) throws CommandException {

        // Retrieves the parent unit.
        final int parentId = cmd.getParentId();
        final OrgUnit parent = em.find(OrgUnit.class, parentId);
        if (parent == null) {
            throw new CommandException("The parent org unit with id '" + parentId + "' doesn't exist.");
        }

        // Retrieves the model.
        final int modelId = cmd.getModelId();
        final OrgUnitModel model = em.find(OrgUnitModel.class, modelId);
        if (model == null) {
            throw new CommandException("The org unit model with id '" + modelId + "' doesn't exist.");
        }

        // Retrieves the new org unit.
        final OrgUnitDTOLight orgUnitDTO = cmd.getUnit();

        // Retrieves the country.
        final Country country;

        if (orgUnitDTO.getOfficeLocationCountry() != null) {
            country = em.find(Country.class, orgUnitDTO.getOfficeLocationCountry().getId());
        } else {
            country = null;
        }

        // Creates the calendar.
        PersonalCalendar calendar = new PersonalCalendar();
        calendar.setName(cmd.getCalendarName());
        calendar = em.merge(calendar);

        // Creates and saves the new unit.
        OrgUnit newOrgUnit = new OrgUnit();
        newOrgUnit.setParent(parent);
        newOrgUnit.setLocation(null);
        newOrgUnit.setName(orgUnitDTO.getName());
        newOrgUnit.setFullName(orgUnitDTO.getFullName());
        newOrgUnit.setChildren(new HashSet<OrgUnit>(0));
        newOrgUnit.setDatabases(null);
        newOrgUnit.setCalendarId(calendar.getId());
        newOrgUnit.setOfficeLocationCountry(country);
        newOrgUnit.setOrganization(null);
        newOrgUnit.setOrgUnitModel(model);

        newOrgUnit = em.merge(newOrgUnit);

        // Updates the model status.
        model.setStatus(ProjectModelStatus.USED);
        em.merge(model);

        final CreateResult result = new CreateResult(newOrgUnit.getId());
        result.setEntity(mapper.map(newOrgUnit, OrgUnitDTO.class).light());
        return result;
    }
}
