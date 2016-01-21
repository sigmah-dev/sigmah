package org.sigmah.server.dao.impl;

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

import org.sigmah.server.dao.ReportingPeriodDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Indicator;
import org.sigmah.server.domain.IndicatorValue;
import org.sigmah.server.domain.ReportingPeriod;

/**
 * ReportingPeriodDAO implementation.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReportingPeriodHibernateDAO extends AbstractDAO<ReportingPeriod, Integer> implements ReportingPeriodDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addIndicatorValue(int reportingPeriodId, int indicatorId, double value) {
		addValueRow(reportingPeriodId, indicatorId, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateIndicatorValue(int reportingPeriodId, int indicatorId, Double value) {
		if (value == null) {
			removeValueRow(reportingPeriodId, indicatorId);
		} else {
			int rowsAffected = updateValueRow(reportingPeriodId, indicatorId, value);
			if (rowsAffected == 0) {
				addValueRow(reportingPeriodId, indicatorId, value);
			}
		}
	}

	// --------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// --------------------------------------------------------------------------------

	private void addValueRow(int reportingPeriodId, int indicatorId, Double value) {
		IndicatorValue iValue =
				new IndicatorValue(em().getReference(ReportingPeriod.class, reportingPeriodId), em().getReference(Indicator.class, indicatorId), value);

		em().persist(iValue);
	}

	private int updateValueRow(int reportingPeriodId, int indicatorId, double value) {
		int rowsAffected;
		rowsAffected =
				em().createQuery("update IndicatorValue v set v.value = ?1 where " + "v.indicator.id = ?2 and " + "v.reportingPeriod.id = ?3").setParameter(1, value)
					.setParameter(2, indicatorId).setParameter(3, reportingPeriodId).executeUpdate();
		return rowsAffected;
	}

	private void removeValueRow(int reportingPeriodId, int indicatorId) {
		int rowsAffected =
				em().createQuery("delete IndicatorValue v where v.indicator.id = ?1 and v.reportingPeriod.id = ?2").setParameter(1, indicatorId)
					.setParameter(2, reportingPeriodId).executeUpdate();

		assert rowsAffected <= 1 : "whoops, deleted too many";
	}

}
