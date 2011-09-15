package org.sigmah.server.policy;

import java.util.Set;

import javax.persistence.EntityManager;

import org.sigmah.shared.domain.LocationType;
import org.sigmah.shared.domain.UserDatabase;

public class LocationUtil {

	public static LocationType locationTypeFromDatabase(EntityManager entityManager, UserDatabase database) {
	
		Set<LocationType> locationTypes = database.getCountry().getLocationTypes();
		for(LocationType type : locationTypes) {
			if(type.getName().equals(LocationType.DEFAULT)) {
				return type;
			}
		}
		// still need to create the default location type for this country
		LocationType defaultType = new LocationType();
		defaultType.setName(LocationType.DEFAULT);
		defaultType.setCountry(database.getCountry());
		
		entityManager.persist(defaultType);
		
		return defaultType;
	}

}
