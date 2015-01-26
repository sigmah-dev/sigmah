package org.sigmah.server.service;

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
