package org.sigmah.server.handler;

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

		Map<Month, ReportingPeriod> periods = new HashMap<Month, ReportingPeriod>();

		for (ReportingPeriod period : site.getReportingPeriods()) {
			periods.put(Handlers.monthFromRange(period.getDate1(), period.getDate2()), period);
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

				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, change.month.getYear());
				calendar.set(Calendar.MONTH, change.month.getMonth() - 1);
				calendar.set(Calendar.DATE, 5);
				period.setDate1(calendar.getTime());

				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE) - 5);
				period.setDate2(calendar.getTime());

				em().persist(period);

				periods.put(change.month, period);
			} else {
				boolean wasValid = ReportingPeriodValidation.validate(period);
				if (!wasValid) {
					em().merge(period);
				}
			}

			updateIndicatorValue(em(), period, change.indicatorId, change.value, false);
		}
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
