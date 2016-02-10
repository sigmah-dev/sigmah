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


import com.google.inject.Inject;
import org.sigmah.shared.command.GetSchema;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sigmah.server.dao.UserDatabaseDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Activity;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.domain.UserPermission;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.PartnerDTO;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler for the {@link GetSchema} command.
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class GetSchemaHandler extends AbstractCommandHandler<GetSchema, SchemaDTO> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GetSchemaHandler.class);

	@Inject
    private UserDatabaseDAO userDatabaseDAO;

	@Override
	protected SchemaDTO execute(GetSchema command, UserDispatch.UserExecutionContext context) throws CommandException {
        final SchemaDTO schema = new SchemaDTO();
        Date lastUpdate = new Date(0);

        // Note that hibernate is already filtering by user so it is not necessary to pass the user
        final List<UserDatabase> databases = userDatabaseDAO.queryAllUserDatabasesAlphabetically();

        final Map<Integer, CountryDTO> countries = new HashMap<Integer, CountryDTO>();

        for (final UserDatabase database : databases) {
			
            if (database.getLastSchemaUpdate().after(lastUpdate)) {
                lastUpdate = database.getLastSchemaUpdate();
            }

            UserDatabaseDTO databaseDTO = new UserDatabaseDTO();

            databaseDTO.setId(database.getId());
            databaseDTO.setName(database.getName());
            databaseDTO.setFullName(database.getFullName());
            databaseDTO.setOwnerName(database.getOwner().getName());
            databaseDTO.setOwnerEmail(database.getOwner().getEmail());

            CountryDTO country = countries.get(database.getCountry().getId());
            LOGGER.debug("country: " +country);
            if (country == null) {
            	LOGGER.debug("country.locationTypes " +database.getCountry().getLocationTypes().size());
                country = mapper().map(database.getCountry(), new CountryDTO());
                countries.put(country.getId(), country);
                schema.getCountries().add(country);
            }
            
            LOGGER.debug("" + country.getLocationTypeById(2));
			
			final User user = context.getUser();
            
            databaseDTO.setCountry(country);
            databaseDTO.setAmOwner(database.getOwner().getId() != null && database.getOwner().getId().equals(user.getId()));

            UserPermission permission = null;
            if (!databaseDTO.getAmOwner()) {
            	// don't support user permission when running in browser
            	if (database.getPermissionByUser(user) != null) {
            		if (database.getPermissionByUser(user).getPartner() != null) {
            			  databaseDTO.setMyPartnerId(database.getPermissionByUser(user).getPartner().getId());
            		}
            	}

                permission = database.getPermissionByUser(user);

                if (permission != null && permission.getLastSchemaUpdate().after(lastUpdate)) {
                    lastUpdate = permission.getLastSchemaUpdate();
                }
            }
            if (permission == null) {
            	// don't support user permission when running in browser
            	permission = new UserPermission();
            }
            databaseDTO.setViewAllAllowed(databaseDTO.getAmOwner() || permission.isAllowViewAll());
            databaseDTO.setEditAllowed(databaseDTO.getAmOwner() || permission.isAllowEdit());
            databaseDTO.setEditAllAllowed(databaseDTO.getAmOwner() || permission.isAllowEditAll());
            databaseDTO.setDesignAllowed(databaseDTO.getAmOwner() || permission.isAllowDesign());
            databaseDTO.setManageUsersAllowed(databaseDTO.getAmOwner() || permission.isAllowManageUsers());
            databaseDTO.setManageAllUsersAllowed(databaseDTO.getAmOwner() || permission.isAllowManageAllUsers());

			databaseDTO.setPartners(mapper().mapCollection(database.getPartners(), PartnerDTO.class));
            
            for (Activity activity : database.getActivities()) {
                ActivityDTO activityDTO = mapper().map(activity, new ActivityDTO());
                databaseDTO.getActivities().add(activityDTO);
                activityDTO.setDatabase(databaseDTO);
            }

            schema.getDatabases().add(databaseDTO);
        }

        schema.setVersion(lastUpdate.getTime());
        return schema;
    }
}
