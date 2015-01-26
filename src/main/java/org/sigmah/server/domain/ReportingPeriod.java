package org.sigmah.server.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * Reporting period domain entity.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.REPORTING_PERIOD_TABLE)
public class ReportingPeriod extends AbstractEntityId<Integer> implements Deleteable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 5691930618952633225L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.REPORTING_PERIOD_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	@Column(name = EntityConstants.REPORTING_PERIOD_COLUMN_MONITORING, nullable = false)
	@NotNull
	private boolean monitoring;

	@Column(name = EntityConstants.REPORTING_PERIOD_COLUMN_DATE1, nullable = false)
	@Temporal(TemporalType.DATE)
	@NotNull
	private Date date1;

	@Column(name = EntityConstants.REPORTING_PERIOD_COLUMN_DATE2, nullable = false)
	@Temporal(TemporalType.DATE)
	@NotNull
	private Date date2;

	@Column(name = EntityConstants.REPORTING_PERIOD_COLUMN_COMMENTS)
	@Lob
	private String comments;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = EntityConstants.COLUMN_DATE_CREATED, nullable = false)
	@NotNull
	private Date dateCreated;

	@Column(name = EntityConstants.COLUMN_DATE_EDITED, nullable = false)
	@Temporal(value = TemporalType.TIMESTAMP)
	@NotNull
	private Date dateEdited;

	@Column(name = EntityConstants.COLUMN_DATE_DELETED)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.SITE_COLUMN_ID, nullable = false)
	@NotNull
	private Site site;

	@OneToMany(mappedBy = "reportingPeriod", fetch = FetchType.LAZY)
	@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.REPORTING_PERIOD_HIDE_DELETED_CONDITION)
	private Set<IndicatorValue> indicatorValues = new HashSet<IndicatorValue>(0);

	// /**
	// * @see https://forum.hibernate.org/viewtopic.php?t=953403
	// */
	// @org.hibernate.annotations.CollectionOfElements
	// @JoinTable(name = "IndicatorValue", joinColumns = { @JoinColumn(name = "reportingPeriodId")
	// })
	// @org.hibernate.annotations.MapKey(columns = { @Column(name = "indicatorId", nullable = false)
	// })
	// @Column(name = "Value", nullable = true, columnDefinition = "REAL")
	// private Map<Integer, Double> indicatorValues = new HashMap<Integer, Double>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public ReportingPeriod() {
		final Date now = new Date();
		setDateCreated(now);
		setDateEdited(now);
	}

	public ReportingPeriod(Site site) {
		this();
		setSite(site);
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
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("monitoring", monitoring);
		builder.append("date1", date1);
		builder.append("date2", date2);
		builder.append("dateCreated", dateCreated);
		builder.append("dateEdited", dateEdited);
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
	public void setId(Integer id) {
		this.id = id;
	}

	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public boolean isMonitoring() {
		return this.monitoring;
	}

	public void setMonitoring(boolean monitoring) {
		this.monitoring = monitoring;
	}

	public Date getDate1() {
		return this.date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return this.date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateEdited() {
		return this.dateEdited;
	}

	public void setDateEdited(Date dateEdited) {
		this.dateEdited = dateEdited;
	}

	public Set<IndicatorValue> getIndicatorValues() {
		return indicatorValues;
	}

	public void setIndicatorValues(Set<IndicatorValue> values) {
		this.indicatorValues = values;
	}

	protected Date getDateDeleted() {
		return this.dateDeleted;
	}

	protected void setDateDeleted(Date date) {
		this.dateDeleted = date;
	}

	// public Map<Integer, Double> getIndicatorValues() {
	// return indicatorValues;
	// }
	//
	// public void setIndicatorValues(Map<Integer,Double> values) {
	// this.indicatorValues = values;
	// }

}
