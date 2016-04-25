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

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.ElementTypeEnum;

@Entity
@Table(name = EntityConstants.FRAMEWORK_ELEMENT_TABLE)
public class FrameworkElement extends AbstractEntityId<Integer> {
	private static final long serialVersionUID = 1356617277418779006L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.FRAMEWORK_ELEMENT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.FRAMEWORK_ELEMENT_COLUMN_LABEL, nullable = false)
	@NotNull
	private String label;

	@Column(name = EntityConstants.FRAMEWORK_ELEMENT_COLUMN_VALUE_RULE, nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private ValueRule valueRule;

	@Column(name = EntityConstants.FRAMEWORK_ELEMENT_COLUMN_DATA_TYPE, nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private ElementTypeEnum dataType;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.FRAMEWORK_HIERARCHY_COLUMN_ID, nullable = false)
	private FrameworkHierarchy frameworkHierarchy;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ValueRule getValueRule() {
		return valueRule;
	}

	public void setValueRule(ValueRule valueRule) {
		this.valueRule = valueRule;
	}

	public ElementTypeEnum getDataType() {
		return dataType;
	}

	public void setDataType(ElementTypeEnum dataType) {
		this.dataType = dataType;
	}

	public FrameworkHierarchy getFrameworkHierarchy() {
		return frameworkHierarchy;
	}

	public void setFrameworkHierarchy(FrameworkHierarchy frameworkHierarchy) {
		this.frameworkHierarchy = frameworkHierarchy;
	}

	public enum ValueRule {
		LAST_VALUE, FIRST_NOT_NULL_VALUE, MIN_VALUE, MAX_VALUE
	}
}
