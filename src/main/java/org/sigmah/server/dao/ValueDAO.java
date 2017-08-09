package org.sigmah.server.dao;

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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.value.Value;

/**
 * Data Access Object for the {@link Value} domain class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ValueDAO extends DAO<Value, Integer> {

	/**
	 * Retrieves the {@link Value} list related to the given {@code orgUnits} entities.
	 * 
	 * @param orgUnits
	 *          The {@link OrgUnit} entities.
	 * @return The {@link Value} list related to the given {@code orgUnits} entities.
	 */
	List<Value> findValuesForOrgUnits(Collection<OrgUnit> orgUnits);

	Value getValueByElementAndContainer(Integer elementId, Integer containerId);

	List<Value> findValuesByContainerId(Integer containerId);

	List<Integer> findContainerIdByElementAndValue(Integer elementId, String value);

	/**
	 * Find values with a value equals to or containing the given id.
	 * <p>
	 *   The value can be a multivalued value.
	 * </p>
   */
	List<Value> findValuesByIdInSerializedValue(Integer containerId);
}
