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
import javax.persistence.Embeddable;

import org.sigmah.server.domain.base.EmbeddableEntity;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Indicator value composite id.<br/>
 * Not an entity.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Embeddable
public class IndicatorValueId implements EmbeddableEntity {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1757987480434102219L;

	@Column(name = EntityConstants.REPORTING_PERIOD_COLUMN_ID, nullable = false)
	private int reportingPeriodId;

	@Column(name = EntityConstants.INDICATOR_COLUMN_ID, nullable = false)
	private int indicatorId;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public IndicatorValueId() {
		// Required empty constructor.
	}

	public IndicatorValueId(int reportingPeriodId, int indicatorId) {
		this.reportingPeriodId = reportingPeriodId;
		this.indicatorId = indicatorId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + indicatorId;
		result = prime * result + reportingPeriodId;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IndicatorValueId)) {
			return false;
		}
		IndicatorValueId other = (IndicatorValueId) obj;
		if (indicatorId != other.indicatorId) {
			return false;
		}
		if (reportingPeriodId != other.reportingPeriodId) {
			return false;
		}
		return true;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public int getReportingPeriodId() {
		return this.reportingPeriodId;
	}

	public void setReportingPeriodId(int reportingPeriodId) {
		this.reportingPeriodId = reportingPeriodId;
	}

	public int getIndicatorId() {
		return this.indicatorId;
	}

	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}

}
