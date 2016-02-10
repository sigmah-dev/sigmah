package org.sigmah.server.domain;

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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * Indicator value domain entity.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.INDICATOR_VALUE_TABLE)
@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.INDICATOR_VALUE_HIDE_DELETED_CONDITION)
public class IndicatorValue extends AbstractEntityId<IndicatorValueId> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6044381165902879418L;

	@EmbeddedId
	private IndicatorValueId id;

	@Column(name = EntityConstants.INDICATOR_VALUE_COLUMN_VALUE, precision = EntityConstants.INDICATOR_VALUE_VALUE_PRECISION, scale = EntityConstants.INDICATOR_VALUE_VALUE_SCALE, nullable = false)
	@NotNull
	private Double value;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.INDICATOR_COLUMN_ID, nullable = false, insertable = false, updatable = false)
	private Indicator indicator;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.REPORTING_PERIOD_COLUMN_ID, nullable = false, insertable = false, updatable = false)
	private ReportingPeriod reportingPeriod;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public IndicatorValue() {
	}

	public IndicatorValue(IndicatorValueId id, Indicator indicator, ReportingPeriod reportingPeriod) {
		this.id = id;
		this.indicator = indicator;
		this.reportingPeriod = reportingPeriod;
	}

	public IndicatorValue(IndicatorValueId id, Indicator indicator, ReportingPeriod reportingPeriod, Double value) {
		this.id = id;
		this.indicator = indicator;
		this.reportingPeriod = reportingPeriod;
		this.value = value;
	}

	public IndicatorValue(ReportingPeriod period, Indicator indicator, double value) {
		this.id = new IndicatorValueId(period.getId(), indicator.getId());
		this.indicator = indicator;
		this.reportingPeriod = period;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("value", value);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public IndicatorValueId getId() {
		return this.id;
	}

	@Override
	public void setId(IndicatorValueId id) {
		this.id = id;
	}

	public Indicator getIndicator() {
		return this.indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public ReportingPeriod getReportingPeriod() {
		return this.reportingPeriod;
	}

	public void setReportingPeriod(ReportingPeriod reportingPeriod) {
		this.reportingPeriod = reportingPeriod;
	}

	public Double getValue() {
		return this.value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

}
