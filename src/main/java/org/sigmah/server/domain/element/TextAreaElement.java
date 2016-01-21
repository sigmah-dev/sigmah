package org.sigmah.server.domain.element;

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
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Text area element domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.TEXTAREA_ELEMENT_TABLE)
public class TextAreaElement extends FlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1147116003259320146L;

	/**
	 * The type of the value expected in this text field. This character defines this type.<br/>
	 * See the other attributes which define the expected format for each type of value.
	 * <ul>
	 * <li><strong>T</strong> ; &quot;Text&quot;: a short text.</li>
	 * <li><strong>P</strong> ; &quot;Paragraph&quot;: a long text.</li>
	 * <li><strong>D</strong> ; &quot;Date&quot;: a date.</li>
	 * <li><strong>N</strong> ; &quot;Number&quot;: a number.</li>
	 * </ul>
	 */
	@Column(name = EntityConstants.TEXTAREA_ELEMENT_COLUMN_TYPE, nullable = true)
	private Character type = 'P';

	/**
	 * If the type of the value is Number or Date, this attribute defines the min value allowed (stored as a timestamp for
	 * a date).<br/>
	 * Could be <code>null</code> to avoid this constraint.
	 */
	@Column(name = EntityConstants.TEXTAREA_ELEMENT_COLUMN_MIN_VALUE, nullable = true)
	private Long minValue;

	/**
	 * If the type of the value is Number or Date, this attribute defines the max value allowed (stored as a timestamp for
	 * a date).<br/>
	 * Could be <code>null</code> to avoid this constraint.
	 */
	@Column(name = EntityConstants.TEXTAREA_ELEMENT_COLUMN_MAX_VALUE, nullable = true)
	private Long maxValue;

	/**
	 * If the type of the value is Number, this attribute defines if the number can be a decimal value.
	 */
	@Column(name = EntityConstants.TEXTAREA_ELEMENT_COLUMN_IS_DECIMAL, nullable = true)
	private Boolean isDecimal;

	/**
	 * If the type of the value is Text or Paragraph, this attribute defines the max length allowed for the text.<br/>
	 * Could be <code>null</code> to avoid this constraint.
	 */
	@Column(name = EntityConstants.TEXTAREA_ELEMENT_COLUMN_LENGTH, nullable = true)
	private Integer length;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	@Transient
	public boolean isHistorable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("type", type);
		builder.append("minValue", minValue);
		builder.append("maxValue", maxValue);
		builder.append("isDecimal", isDecimal);
		builder.append("length", length);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public Character getType() {
		return type;
	}

	public void setType(Character type) {
		this.type = type;
	}

	public Long getMinValue() {
		return minValue;
	}

	public void setMinValue(Long minValue) {
		this.minValue = minValue;
	}

	public Long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}

	public Boolean getIsDecimal() {
		return isDecimal;
	}

	public void setIsDecimal(Boolean isDecimal) {
		this.isDecimal = isDecimal;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}
}
