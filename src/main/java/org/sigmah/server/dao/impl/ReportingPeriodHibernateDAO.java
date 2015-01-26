package org.sigmah.server.dao.impl;

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
