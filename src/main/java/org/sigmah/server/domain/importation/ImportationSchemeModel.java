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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * Importation Scheme Model domain entity.
 * </p>
 * 
 * @author Jérémie BRIAND (jbriand@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.IMPORTATION_SCHEME_MODEL_TABLE)
@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.IMPORTATION_SCHEME_MODEL_HIDE_DELETED_CONDITION)
public class ImportationSchemeModel extends AbstractEntityId<Integer> implements Deleteable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4157572319565532207L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.IMPORTATION_SCHEME_MODEL_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	@Column(name = EntityConstants.IMPORTATION_SCHEME_MODEL_COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.IMPORTATION_SCHEME_COLUMN_ID, nullable = false, updatable = false)
	@NotNull
	private ImportationScheme importationScheme;

	@ManyToOne
	@JoinColumn(name = EntityConstants.PROJECT_MODEL_COLUMN_ID, updatable = false)
	private ProjectModel projectModel;

	@ManyToOne
	@JoinColumn(name = EntityConstants.ORG_UNIT_MODEL_COLUMN_ID, updatable = false)
	private OrgUnitModel orgUnitModel;

	@OneToMany(mappedBy = "importationSchemeModel", cascade = CascadeType.ALL)
	@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.IMPORTATION_SCHEME_MODEL_HIDE_DELETED_CONDITION)
	private List<VariableFlexibleElement> variableFlexibleElements = new ArrayList<VariableFlexibleElement>();

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
	public void setId(Integer id) {
		this.id = id;
	}

	public ImportationScheme getImportationScheme() {
		return importationScheme;
	}

	public void setImportationScheme(ImportationScheme importationScheme) {
		this.importationScheme = importationScheme;
	}

	public ProjectModel getProjectModel() {
		return projectModel;
	}

	public void setProjectModel(ProjectModel projectModel) {
		this.projectModel = projectModel;
	}

	/**
	 * @return the orgUnitModel
	 */

	public OrgUnitModel getOrgUnitModel() {
		return orgUnitModel;
	}

	/**
	 * @param orgUnitModel
	 *          the orgUnitModel to set
	 */
	public void setOrgUnitModel(OrgUnitModel orgUnitModel) {
		this.orgUnitModel = orgUnitModel;
	}

	public List<VariableFlexibleElement> getVariableFlexibleElements() {
		return variableFlexibleElements;
	}

	public void setVariableFlexibleElements(List<VariableFlexibleElement> variableFlexibleElements) {
		this.variableFlexibleElements = variableFlexibleElements;
	}

	public Date getDateDeleted() {
		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}
}
