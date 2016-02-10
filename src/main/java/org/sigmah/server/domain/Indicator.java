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
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.IndexColumn;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.quality.QualityCriterion;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.server.domain.util.Orderable;
import org.sigmah.server.domain.util.SchemaElement;

/**
 * <p>
 * Indicator domain entity.
 * </p>
 * <p>
 * Defines an Indicator, a numeric value that can change over time.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.INDICATOR_TABLE)
@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.INDICATOR_HIDE_DELETED_CONDITION)
public class Indicator extends AbstractEntityId<Integer> implements Orderable, Deleteable, SchemaElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 5978350531347182242L;

	/**
	 * The id of this Indicator.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.INDICATOR_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	/**
	 * The time at which this Indicator was deleted.
	 */
	@Column(name = EntityConstants.COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	/**
	 * The name of this Indicator.
	 */
	@Column(name = EntityConstants.INDICATOR_COLUMN_NAME, nullable = false, length = EntityConstants.INDICATOR_NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.INDICATOR_NAME_MAX_LENGTH)
	private String name;

	/**
	 * <p>
	 * A description of the units in which this Indicator is expressed. Note that this is for descriptive purpose only for
	 * the user, it does not carry any semantics for our system.
	 * </p>
	 * <p>
	 * Examples: "households", "%" "cm"
	 * </p>
	 */
	@Column(name = EntityConstants.INDICATOR_COLUMN_UNITS, nullable = true, length = EntityConstants.INDICATOR_UNITS_MAX_LENGTH)
	@Size(max = EntityConstants.INDICATOR_UNITS_MAX_LENGTH)
	private String units;

	/**
	 * The numerical objective for this Indicator.
	 */
	@Column(name = EntityConstants.INDICATOR_COLUMN_OBJECTIVE, precision = EntityConstants.INDICATOR_OBJECTIVE_PRECISION, scale = EntityConstants.INDICATOR_OBJECTIVE_SCALE, nullable = true)
	private Double objective;

	/**
	 * A full description of this indicator, containing perhaps detailed instructions on how it is to be collected or
	 * calculated.
	 */
	@Column(name = EntityConstants.INDICATOR_COLUMN_DESCRIPTION)
	private String description;

	/**
	 * This Indicator's category. Categories are just strings that are used for organizing the display of Indicators in
	 * the user interface.
	 */
	@Column(name = EntityConstants.INDICATOR_COLUMN_CATEGORY, length = EntityConstants.INDICATOR_CATEGORY_MAX_LENGTH)
	@Size(max = EntityConstants.INDICATOR_CATEGORY_MAX_LENGTH)
	private String category;

	/**
	 * {@code true} if this Indicator is collected during the actual intervention. (Some indicators are only collected
	 * afterwords, during the monitoring phase).
	 */
	@Column(name = EntityConstants.INDICATOR_COLUMN_COLLECT_INTERVENTION, nullable = false)
	@NotNull
	private boolean collectIntervention;

	/**
	 * {@code true} if this Indicator is collected during the monitoring phase.
	 */
	@Column(name = EntityConstants.INDICATOR_COLUMN_COLLECT_MONITORING, nullable = false)
	@NotNull
	private boolean collectMonitoring;

	/**
	 * The method by which this Indicator is aggregated.
	 */
	@Column(name = EntityConstants.INDICATOR_COLUMN_AGGREGATION, nullable = false)
	@NotNull
	private int aggregation;

	/**
	 * The sort order of this Indicator within its Activity.
	 */
	@Column(name = EntityConstants.COLUMN_SORT_ORDER, nullable = false)
	@NotNull
	private int sortOrder;

	/**
	 * A short list header that is used when this Indicator's values are displayed in a grid.
	 */
	@Column(name = EntityConstants.INDICATOR_COLUMN_CODE, length = EntityConstants.INDICATOR_CODE_MAX_LENGTH)
	@Size(max = EntityConstants.INDICATOR_CODE_MAX_LENGTH)
	private String code;

	/**
	 * The text description of how this indicator will be verified.
	 */
	@Column(name = EntityConstants.INDICATOR_COLUMN_SOURCE_OF_VERIFICATION)
	private String sourceOfVerification;

	/**
	 * {@code true} if the user can associate indicator values with this project, or false if this indicator takes its
	 * value exclusively from its data sources.
	 */
	@Column(name = EntityConstants.INDICATOR_COLUMN_DIRECT_DATA_ENTRY_ENABLED, columnDefinition = EntityConstants.INDICATOR_COLUMN_DEFINITION_DIRECT_DATA_ENTRY_ENABLED)
	private boolean directDataEntryEnabled;

	// TODO [ENTITY] NEED TO BE TESTED !
	@ElementCollection
	@CollectionTable(name = EntityConstants.INDICATOR_TABLE + "_labels")
	@IndexColumn(name = "code", base = 1)
	@Column(name = "element")
	private List<String> labels;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * The Activity which is implemented at this Site.
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = {
																								CascadeType.PERSIST,
																								CascadeType.MERGE
	})
	@JoinColumn(name = EntityConstants.ACTIVITY_COLUMN_ID, nullable = true)
	private Activity activity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.USER_DATABASE_COLUMN_ID, nullable = true)
	private UserDatabase database;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.QUALITY_CRITERION_COLUMN_ID, nullable = true)
	private QualityCriterion qualityCriterion;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = EntityConstants.INDICATOR_SELF_LINK_TABLE, joinColumns = { @JoinColumn(name = EntityConstants.INDICATOR_COLUMN_ID, nullable = false, updatable = false)
	}, inverseJoinColumns = { @JoinColumn(name = EntityConstants.INDICATOR_SELF_LINK_COLUMN, nullable = false, updatable = false)
	})
	private Set<Indicator> dataSources = new HashSet<Indicator>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public Indicator() {
		// Default empty constructor.
	}

	/**
	 * Copies a indicator to another database.
	 * 
	 * @param databaseCopy
	 *          The instance to set into the cloned current {@code Indicator}.
	 * @return the cloned current {@code Indicator} instance.
	 */
	public Indicator copy(final UserDatabase databaseCopy) {

		final Indicator copy = new Indicator();

		copy.name = this.name;
		copy.code = this.code;
		copy.units = this.units;
		copy.objective = this.objective;
		copy.description = this.description;
		copy.category = this.category;
		copy.collectIntervention = this.collectIntervention;
		copy.collectMonitoring = this.collectMonitoring;
		copy.aggregation = this.aggregation;
		copy.sortOrder = this.sortOrder;
		copy.database = databaseCopy;
		copy.labels = this.labels;
		copy.sourceOfVerification = this.sourceOfVerification;

		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {
		setDateDeleted(new Date());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("code", code);
		builder.append("category", category);
		builder.append("units", units);
		builder.append("objective", objective);
		builder.append("aggregation", aggregation);
		builder.append("collectIntervention", collectIntervention);
		builder.append("collectMonitoring", collectMonitoring);
		builder.append("directDataEntryEnabled", directDataEntryEnabled);
		builder.append("sortOrder", sortOrder);
		builder.append("dateDeleted", dateDeleted);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer indicatorId) {
		this.id = indicatorId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnits() {
		return this.units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public Double getObjective() {
		return objective;
	}

	public void setObjective(Double objective) {
		this.objective = objective;
	}

	@Lob
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Activity getActivity() {
		return this.activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public boolean getCollectIntervention() {
		return this.collectIntervention;
	}

	public void setCollectIntervention(boolean collectIntervention) {
		this.collectIntervention = collectIntervention;
	}

	public int getAggregation() {
		return this.aggregation;
	}

	public void setAggregation(int aggregation) {
		this.aggregation = aggregation;
	}

	public boolean isDirectDataEntryEnabled() {
		return directDataEntryEnabled;
	}

	public void setDirectDataEntryEnabled(boolean directDataEntryEnabled) {
		this.directDataEntryEnabled = directDataEntryEnabled;
	}

	public boolean isCollectMonitoring() {
		return this.collectMonitoring;
	}

	public void setCollectMonitoring(boolean collectMonitoring) {
		this.collectMonitoring = collectMonitoring;
	}

	@Override
	public int getSortOrder() {
		return this.sortOrder;
	}

	@Override
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Date getDateDeleted() {
		return this.dateDeleted;
	}

	public void setDateDeleted(Date deleteTime) {
		this.dateDeleted = deleteTime;
	}

	public QualityCriterion getQualityCriterion() {
		return qualityCriterion;
	}

	public void setQualityCriterion(QualityCriterion qualityCriterion) {
		this.qualityCriterion = qualityCriterion;
	}

	public UserDatabase getDatabase() {
		return this.database;
	}

	public void setDatabase(UserDatabase database) {
		this.database = database;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public Set<Indicator> getDataSources() {
		return dataSources;
	}

	public void setDataSources(Set<Indicator> dataSources) {
		this.dataSources = dataSources;
	}

	@Lob
	public String getSourceOfVerification() {
		return sourceOfVerification;
	}

	public void setSourceOfVerification(String sourceOfVerification) {
		this.sourceOfVerification = sourceOfVerification;
	}

}
