package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.shared.dto.map.BaseMap;

/**
 * Data Access Object for the {@link BaseMap} objects available to the user.
 * 
 * @author Alex Bertram (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public interface BaseMapDAO {

	BaseMap getBaseMap(String id);

	List<BaseMap> getBaseMaps();

}
