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

import java.sql.Connection;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Indicator;
import org.sigmah.shared.command.result.IndicatorListResult;

/**
 * Data Access Object for {@link org.sigmah.server.domain.Indicator} domain objects.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public interface IndicatorDAO extends DAO<Indicator, Integer> {

	public void queryIndicatorGroups(Connection connection, final int databaseId);

	public void queryIndicatorsByDatabaseWithCurrentValues(Connection connection, final int databaseId);

	public IndicatorListResult getResult();

}
