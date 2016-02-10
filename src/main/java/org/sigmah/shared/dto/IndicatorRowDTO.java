package org.sigmah.shared.dto;

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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataDTO;
import org.sigmah.shared.util.Month;

/**
 * <p>
 * Projection DTO of the {@link org.sigmah.server.domain.ReportingPeriod ReportingPeriod},
 * {@link org.sigmah.server.domain.IndicatorValue IndicatorValue} and {@link org.sigmah.server.domain.Indicator
 * Indicator} entities.
 * </p>
 * <p>
 * Each IndicatorRowDTO contains values for a single {@link org.sigmah.server.domain.Site Site}, and a single Indicator,
 * but values (stored as properties) for a series of {@link org.sigmah.server.domain.ReportingPeriod ReportingPeriod}.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class IndicatorRowDTO extends AbstractModelDataDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2986242126039751719L;

	private int siteId;
	private int activityId;
	private int indicatorId;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("siteId", getSiteId());
		builder.append("activityId", getActivityId());
		builder.append("indicatorId", getIndicatorId());
		builder.append("indicatorName", getIndicatorName());
	}

	/**
	 * @return The id of the site to which this row belongs.
	 */
	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	/**
	 * @return The id of the {@link org.sigmah.server.domain.Activity Activity} to which the row's
	 *         {@link org.sigmah.server.domain.Site Site} belongs.
	 */
	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	/**
	 * @return The id of the {@link org.sigmah.server.domain.Indicator Indicator}
	 */
	public int getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}

	/**
	 * @return The name of the {@link org.sigmah.server.domain.Indicator}.
	 */
	public String getIndicatorName() {
		return get("indicatorName");
	}

	public void setIndicatorName(String name) {
		set("indicatorName", name);
	}

	/**
	 * The value of the Indicator for the {@link org.sigmah.server.domain.ReportingPeriod ReportingPeriod} corresponding
	 * to the given <code>year</code> and <code>month</code>
	 *
	 * @param year
	 * @param month
	 * @return The value of the Indicator for the {@link org.sigmah.server.domain.ReportingPeriod ReportingPeriod}
	 *         corresponding to the given <code>year</code> and <code>month</code>.
	 */
	public Double getValue(int year, int month) {
		return get(propertyName(year, month));
	}

	public void setValue(int year, int month, Double value) {
		set(propertyName(year, month), value);
	}

	public static String propertyName(int year, int month) {
		return "M" + year + "-" + month;
	}

	public static String propertyName(Month month) {
		return propertyName(month.getYear(), month.getMonth());
	}

	public void setValue(Month month, Double value) {
		setValue(month.getYear(), month.getMonth(), value);
	}

	public static Month monthForProperty(String property) {
		return Month.parseMonth(property.substring(1));
	}
}
