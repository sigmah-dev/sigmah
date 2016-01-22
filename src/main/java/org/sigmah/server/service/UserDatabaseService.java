package org.sigmah.server.service;

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

import java.util.Date;

import org.sigmah.server.dao.CountryDAO;
import org.sigmah.server.dao.UserDatabaseDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * {@link UserDatabase} service implementation.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class UserDatabaseService extends AbstractEntityService<UserDatabase, Integer, UserDatabaseDTO> {

	private final UserDatabaseDAO databaseDAO;
	private final CountryDAO countryDAO;

	@Inject
	public UserDatabaseService(UserDatabaseDAO databaseDAO, CountryDAO countryDAO) {
		this.databaseDAO = databaseDAO;
		this.countryDAO = countryDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserDatabase create(PropertyMap properties, final UserExecutionContext context) {

		final User user = context.getUser();
		final UserDatabase database = new UserDatabase();
		database.setCountry(findCountry(properties));
		database.setOwner(user);

		applyProperties(database, properties);

		return databaseDAO.persist(database, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserDatabase update(Integer entityId, PropertyMap changes, final UserExecutionContext context) {
		throw new UnsupportedOperationException("No policy update operation implemented for '" + entityClass.getSimpleName() + "' entity.");
	}

	private Country findCountry(final PropertyMap properties) {

		final Integer countryId;

		if (properties.containsKey("countryId")) {
			countryId = (Integer) properties.get("countryId");
		} else {
			// This is the default country.
			countryId = Country.DEFAULT_COUNTRY_ID;
		}

		return countryDAO.findById(countryId);
	}

	private static void applyProperties(UserDatabase database, PropertyMap properties) {

		database.setLastSchemaUpdate(new Date());

		if (properties.containsKey("name")) {
			database.setName((String) properties.get("name"));
		}

		if (properties.containsKey("fullName")) {
			database.setFullName((String) properties.get("fullName"));
		}
	}

}
