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

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.ReportingPeriod;

/**
 * Data Access Object for {@link org.sigmah.server.domain.ReportingPeriod} domain classes.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ReportingPeriodDAO extends DAO<ReportingPeriod, Integer> {

	/**
	 * Efficiently adds an {@link org.sigmah.server.domain.IndicatorValue IndicatorValue} to the given
	 * {@link org.sigmah.server.domain.ReportingPeriod}
	 * 
	 * @param reportingPeriodId
	 * @param indicatorId
	 * @param value
	 */
	void addIndicatorValue(int reportingPeriodId, int indicatorId, double value);

	/**
	 * Efficiently adds updates an {@link org.sigmah.server.domain.IndicatorValue IndicatorValue} to the given value. If
	 * the {@link org.sigmah.server.domain.IndicatorValue IndicatorValue} object does not exist it is inserted.
	 * 
	 * @param reportingPeriodId
	 * @param indicatorId
	 * @param value
	 */
	void updateIndicatorValue(int reportingPeriodId, int indicatorId, Double value);

}
