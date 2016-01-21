package org.sigmah.server.domain.report;

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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * Project Report domain entity.
 * </p>
 * <p>
 * Report based on a {@link ProjectReportModel}.
 * </p>
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PROJECT_REPORT_TABLE)
@FilterDefs({ @FilterDef(name = EntityFilters.HIDE_DELETED)
})
@Filters({ @Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.PROJECT_REPORT_HIDE_DELETED_CONDITION)
})
public class ProjectReport extends AbstractEntityId<Integer> implements Deleteable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7388489166961720683L;

	/**
	 * Identifier of the report.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PROJECT_REPORT_COLUMN_ID)
	private Integer id;

	/**
	 * Name of the report.
	 */
	@Column(name = EntityConstants.PROJECT_REPORT_COLUMN_NAME)
	@Size(max = EntityConstants.PROJECT_REPORT_NAME_MAX_LENGTH)
	private String name;

	/**
	 * Date of deletion.
	 */
	@Column(name = EntityConstants.PROJECT_REPORT_COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Model defining the structure of this report.
	 */
	@ManyToOne
	@JoinColumn(name = EntityConstants.PROJECT_REPORT_COLUMN_CURRENT_MODEL_ID)
	private ProjectReportModel model;

	/**
	 * Current version of the report (contains the values of each section).
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = EntityConstants.PROJECT_REPORT_COLUMN_CURRENT_VERSION_ID)
	private ProjectReportVersion currentVersion;

	/**
	 * Project hosting this report.
	 */
	@ManyToOne
	@JoinColumn(name = EntityConstants.PROJECT_REPORT_COLUMN_CURRENT_PROJECT_ID)
	private Project project;

	/**
	 * OrgUnit hosting this report.
	 */
	@ManyToOne
	@JoinColumn(name = EntityConstants.PROJECT_REPORT_COLUMN_CURRENT_ORG_UNIT_PARTNER_ID)
	private OrgUnit orgUnit;

	/**
	 * Flexible element hosting this report.
	 */
	@ManyToOne
	@JoinColumn(name = EntityConstants.PROJECT_REPORT_COLUMN_CURRENT_FLEXIBLE_ELEMENT_ID)
	private FlexibleElement flexibleElement;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("dateDeleted", dateDeleted);
	}

	@Override
	public void delete() {
		dateDeleted = new Date();
	}

	@Override
	@Transient
	public boolean isDeleted() {
		return dateDeleted != null;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProjectReportModel getModel() {
		return model;
	}

	public void setModel(ProjectReportModel model) {
		this.model = model;
	}

	public ProjectReportVersion getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(ProjectReportVersion currentVersion) {
		this.currentVersion = currentVersion;
	}

	public OrgUnit getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(OrgUnit orgUnit) {
		this.orgUnit = orgUnit;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public FlexibleElement getFlexibleElement() {
		return flexibleElement;
	}

	public void setFlexibleElement(FlexibleElement flexibleElement) {
		this.flexibleElement = flexibleElement;
	}

	public Date getDateDeleted() {
		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}
}
