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

import com.google.inject.persist.Transactional;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Indicator;
import org.sigmah.server.domain.IndicatorValue;
import org.sigmah.server.domain.ReportingPeriod;
import org.sigmah.server.domain.Site;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.handler.util.ReportingPeriodValidation;
import org.sigmah.shared.command.UpdateMonthlyReports;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.util.Month;

/**
 * Handler for {@link UpdateMonthlyReports} command
 * 
 * @author Alex Bertram
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class UpdateMonthlyReportsHandler extends AbstractCommandHandler<UpdateMonthlyReports, VoidResult> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final UpdateMonthlyReports cmd, final UserExecutionContext context) throws CommandException {

		Site site = em().find(Site.class, cmd.getSiteId());

		final Map<Month, ReportingPeriod> periods = new HashMap<>();

		for (ReportingPeriod period : site.getReportingPeriods()) {
			final ReportingPeriod oldPeriod = periods.put(
                    Handlers.monthFromRange(period.getDate1(), period.getDate2()), period);
            
            if (oldPeriod != null) {
                removeUnusedPeriod(oldPeriod);
            }
		}

		performChanges(cmd, periods, site);

		return new VoidResult();

	}

	@Transactional
	protected void performChanges(final UpdateMonthlyReports cmd, Map<Month, ReportingPeriod> periods, Site site) {
		for (UpdateMonthlyReports.Change change : cmd.getChanges()) {
			
			ReportingPeriod period = periods.get(change.month);
			if (period == null) {
				period = new ReportingPeriod(site);

				final Calendar calendar = Calendar.getInstance();
                calendar.clear();
				calendar.set(Calendar.YEAR, change.month.getYear());
				calendar.set(Calendar.MONTH, change.month.getMonth() - 1);
				calendar.set(Calendar.DATE, 5);
				period.setDate1(calendar.getTime());

				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE) - 5);
				period.setDate2(calendar.getTime());

				em().persist(period);

				periods.put(change.month, period);
			} else {
				final boolean wasValid = ReportingPeriodValidation.validate(period);
				if (!wasValid) {
					em().merge(period);
				}
			}

			updateIndicatorValue(em(), period, change.indicatorId, change.value, false);
		}
	}
	
	@Transactional
    protected void removeUnusedPeriod(final ReportingPeriod period) {
        // Removing every values associated with the given period.
        em().createQuery("delete from IndicatorValue v where v.reportingPeriod.id = :period")
                .setParameter("period", period.getId())
                .executeUpdate();
        
        // Removing the unused period.
        em().remove(period);
    }

	private void updateIndicatorValue(EntityManager em, ReportingPeriod period, int indicatorId, Double value, boolean creating) {
		if (value == null && !creating) {
			int rowsAffected =
					em.createQuery("delete IndicatorValue v where v.indicator.id = ?1 and v.reportingPeriod.id = ?2").setParameter(1, indicatorId)
						.setParameter(2, period.getId()).executeUpdate();

			assert rowsAffected <= 1 : "whoops, deleted too many";

		} else if (value != null) {

			int rowsAffected = 0;

			if (!creating) {
				// Updating the row with the new value.
				rowsAffected =
						em.createQuery("update IndicatorValue v set v.value = ?1 where " + "v.indicator.id = ?2 and " + "v.reportingPeriod.id = ?3")
							.setParameter(1, value).setParameter(2, indicatorId).setParameter(3, period.getId()).executeUpdate();
			}

			if (rowsAffected == 0) {

				IndicatorValue iValue = new IndicatorValue(period, em.getReference(Indicator.class, indicatorId), value);

				em.persist(iValue);

			}
		}

	}

}
