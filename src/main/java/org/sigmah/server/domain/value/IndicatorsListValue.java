package org.sigmah.server.domain.value;

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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sigmah.server.domain.Indicator;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.INDICATORS_LIST_VALUE_TABLE)
public class IndicatorsListValue extends AbstractEntityId<IndicatorsListValueId> {

	private static final long serialVersionUID = -8267821835924810690L;

	@EmbeddedId
	@AttributeOverrides({
												@AttributeOverride(name = "idList", column = @Column(name = EntityConstants.INDICATORS_LIST_VALUE_COLUMN_ID_INDICATORS_LIST, nullable = false)),
												@AttributeOverride(name = "indicatorId", column = @Column(name = EntityConstants.INDICATORS_LIST_VALUE_COLUMN_ID_INDICATOR, nullable = false))
	})
	private IndicatorsListValueId id;

	@Column(name = EntityConstants.INDICATORS_LIST_VALUE_COLUMN_ID_INDICATORS_LIST, nullable = false, insertable = false, updatable = false)
	private Integer idList;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.INDICATORS_LIST_VALUE_COLUMN_ID_INDICATOR, nullable = false, insertable = false, updatable = false)
	private Indicator indicator;

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public IndicatorsListValueId getId() {
		return this.id;
	}

	@Override
	public void setId(IndicatorsListValueId id) {
		this.id = id;
	}

	public void setIdList(Integer id) {
		this.idList = id;
	}

	public Integer getIdList() {
		return idList;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public Indicator getIndicator() {
		return indicator;
	}
}
