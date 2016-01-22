package org.sigmah.server.domain.importation;

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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * Importation scheme variable flexible element domain entity.
 * </p>
 * 
 * @author Jérémie BRIAND (jbriand@ideia.fr)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = EntityConstants.VARIABLE_FLEXIBLE_ELEMENT_TABLE)
@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.VARIABLE_FLEXIBLE_ELEMENT_HIDE_DELETED_CONDITION)
public class VariableFlexibleElement extends AbstractEntityId<Integer> implements Deleteable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8601037728276093624L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.VARIABLE_FLEXIBLE_ELEMENT_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	@Column(name = EntityConstants.VARIABLE_FLEXIBLE_ELEMENT_COLUMN_IS_KEY)
	private Boolean isKey;

	@Column(name = EntityConstants.VARIABLE_FLEXIBLE_ELEMENT_COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne
	@JoinColumn(name = EntityConstants.IMPORTATION_VARIABLE_COLUMN_ID, updatable = false)
	private Variable variable;

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_ID, nullable = false, updatable = false)
	@NotNull
	private FlexibleElement flexibleElement;

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.IMPORTATION_SCHEME_MODEL_COLUMN_ID, nullable = false, updatable = false)
	@NotNull
	private ImportationSchemeModel importationSchemeModel;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("dateDeleted", dateDeleted);
	}

	@Override
	public void delete() {
		Date now = new Date();
		setDateDeleted(now);
	}

	@Override
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() != null;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer Id) {
		this.id = Id;
	}

	public Boolean getIsKey() {
		return isKey;
	}

	public void setIsKey(Boolean isKey) {
		this.isKey = isKey;
	}

	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	public FlexibleElement getFlexibleElement() {
		return flexibleElement;
	}

	public void setFlexibleElement(FlexibleElement flexibleElement) {
		this.flexibleElement = flexibleElement;
	}

	public ImportationSchemeModel getImportationSchemeModel() {
		return importationSchemeModel;
	}

	public void setImportationSchemeModel(ImportationSchemeModel importationSchemeModel) {
		this.importationSchemeModel = importationSchemeModel;
	}

	public Date getDateDeleted() {
		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}
}
