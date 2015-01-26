package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Indicator;
import org.sigmah.server.domain.IndicatorValue;
import org.sigmah.server.domain.ReportingPeriod;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.handler.util.ReportingPeriodValidation;
import org.sigmah.shared.command.GetMonthlyReports;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.IndicatorRowDTO;
import org.sigmah.shared.util.Month;

/**
 * See GetMonthlyReports
 * 
 * @author Alex Bertram (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class GetMonthlyReportsHandler extends AbstractCommandHandler<GetMonthlyReports, ListResult<IndicatorRowDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<IndicatorRowDTO> execute(final GetMonthlyReports cmd, final UserExecutionContext context) throws CommandException {

		final TypedQuery<ReportingPeriod> queryPeriods = em().createQuery("select p from ReportingPeriod p where p.site.id = ?1", ReportingPeriod.class);
		queryPeriods.setParameter(1, cmd.getSiteId());
		final List<ReportingPeriod> periods = queryPeriods.getResultList();

		final TypedQuery<Indicator> queryIndicators =
				em().createQuery("select i from Indicator i where i.activity.id = (select s.activity.id from Site s where s.id = ?1)", Indicator.class);
		queryIndicators.setParameter(1, cmd.getSiteId());
		List<Indicator> indicators = queryIndicators.getResultList();

		final List<IndicatorRowDTO> list = new ArrayList<IndicatorRowDTO>();

		for (final Indicator indicator : indicators) {

			final IndicatorRowDTO dto = new IndicatorRowDTO();
			dto.setIndicatorId(indicator.getId());
			dto.setSiteId(cmd.getSiteId());
			dto.setIndicatorName(indicator.getName());

			for (final ReportingPeriod period : periods) {

				if (!ReportingPeriodValidation.validate(period)) {
					em().merge(period);
				}

				final Month month = Handlers.monthFromRange(period.getDate1(), period.getDate2());
				if (month != null && month.compareTo(cmd.getStartMonth()) >= 0 && month.compareTo(cmd.getEndMonth()) <= 0) {

					for (final IndicatorValue value : period.getIndicatorValues()) {
						if (value.getIndicator().getId().equals(indicator.getId())) {
							dto.setValue(month, value.getValue());
						}
					}
				}
			}

			list.add(dto);
		}

		return new ListResult<IndicatorRowDTO>(list);
	}

}
