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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.shared.dto.referential.ReportFrequency;

/**
 * <p>
 * Report definition domain entity.
 * </p>
 * <p>
 * Defines a Report and its subscriptions.
 * </p>
 */
@Entity
@Table(name = EntityConstants.REPORT_DEFINITION_TABLE)
@Filters({
					@Filter(name = EntityFilters.USER_VISIBLE, condition = EntityFilters.REPORT_DEFINITION_USER_VISIBLE_CONDITION),

					@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.REPORT_DEFINITION_HIDE_DELETED_CONDITION)
})
public class ReportDefinition extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -685272362058010792L;

	@Id
	@Column(name = EntityConstants.REPORT_DEFINITION_COLUMN_ID)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = EntityConstants.REPORT_DEFINITION_COLUMN_TITLE)
	private String title;

	@Column(name = EntityConstants.REPORT_DEFINITION_COLUMN_DAY, nullable = true)
	private Integer day;

	@Column(name = EntityConstants.REPORT_DEFINITION_COLUMN_VISIBILITY)
	private int visibility;

	@Column(name = EntityConstants.REPORT_DEFINITION_COLUMN_FREQUENCY)
	@Enumerated(EnumType.STRING)
	private ReportFrequency frequency;

	@Column(name = EntityConstants.COLUMN_DATE_DELETED, nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDeleted;

	@Column(name = EntityConstants.REPORT_DEFINITION_COLUMN_XML, nullable = false)
	@Lob
	@NotNull
	private String xml;

	@Column(name = EntityConstants.REPORT_DEFINITION_COLUMN_DESCRIPTION)
	@Lob
	private String description;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.REPORT_DEFINITION_COLUMN_OWNER_USER_ID, nullable = false)
	@NotNull
	private User owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.USER_DATABASE_COLUMN_ID, nullable = true, updatable = false)
	@NotNull
	private UserDatabase database;

	@OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
	@org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	private Set<ReportSubscription> subscriptions = new HashSet<ReportSubscription>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public ReportDefinition() {
		// Default empty constructor.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("title", title);
		builder.append("frequency", frequency);
		builder.append("day", day);
		builder.append("visibility", visibility);
		builder.append("dateDeleted", dateDeleted);
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

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public UserDatabase getDatabase() {
		return database;
	}

	public void setDatabase(UserDatabase database) {
		this.database = database;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ReportFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(ReportFrequency frequency) {
		this.frequency = frequency;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Set<ReportSubscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Set<ReportSubscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public Date getDateDeleted() {
		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}
}
