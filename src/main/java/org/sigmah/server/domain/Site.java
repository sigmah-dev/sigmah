package org.sigmah.server.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * Site domain entity.
 * </p>
 * <p>
 * Concrete realization of... ?
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.SITE_TABLE)
@Filters({
					@Filter(name = EntityFilters.USER_VISIBLE, condition = EntityFilters.SITE_USER_VISIBLE_CONDITION),
					@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.SITE_HIDE_DELETED_CONDITION)
})
public class Site extends AbstractEntityId<Integer> implements Deleteable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6200911357882396424L;

	/**
	 * Id of this site.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.SITE_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	/**
	 * The Globally-Unique Identifier (GUID) for this Site, used to link this Site to external systems.
	 */
	@Column(name = EntityConstants.SITE_COLUMN_GUID, length = EntityConstants.SITE_GUID_MAX_LENGTH)
	@Size(max = EntityConstants.SITE_GUID_MAX_LENGTH)
	private String siteGuid;

	/**
	 * @deprecated No longer used.
	 */
	@Deprecated
	@Column(name = "Status", nullable = false)
	@NotNull
	private int status;

	/**
	 * The date on which work at this Site began.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = EntityConstants.SITE_COLUMN_DATE1)
	private Date date1;

	/**
	 * The date on which work at this Site ended.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = EntityConstants.SITE_COLUMN_DATE2)
	private Date date2;

	/**
	 * The time at which this Site created. Used for synchronization.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = EntityConstants.COLUMN_DATE_CREATED, nullable = false)
	@NotNull
	private Date dateCreated;

	/**
	 * The time at which this Site was last edited. Initially equal to dateCreated.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = EntityConstants.COLUMN_DATE_EDITED, nullable = false)
	@NotNull
	private Date dateEdited;

	/**
	 * The time at which this Site was deleted. Used for synchronization with clients.
	 */
	@Column(name = EntityConstants.COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	/**
	 * The type of site: 0 for work complete, 1 for program targets.<br/>
	 * <b>Note: Not yet implemented.</b>
	 */
	@Column(name = EntityConstants.SITE_COLUMN_TARGET, nullable = false)
	@NotNull
	private int target;

	/**
	 * The time at which this Site was last synchronized with an external system.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = EntityConstants.SITE_COLUMN_DATE_SYNCHRONIZED)
	private Date dateSynchronized;

	/**
	 * The plain-text comments associated with this Site.
	 */
	@Column(name = EntityConstants.SITE_COLUMN_COMMENTS)
	@Lob
	private String comments;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * The Site of the needs assessment on which this Site is based.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.SITE_COLUMN_ASSESSMENT_SITE_ID)
	private Site assessment;

	/**
	 * The Activity to which this Site belongs.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ACTIVITY_COLUMN_ID, nullable = true)
	private Activity activity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.USER_DATABASE_COLUMN_ID, nullable = false)
	@NotNull
	private UserDatabase database;

	/**
	 * The geographic Location of this Site.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = EntityConstants.LOCATION_COLUMN_ID, nullable = false)
	@NotNull
	private Location location;

	/**
	 * The OrgUnit who owns this Site. In some cases, the OrgUnit will have been the actually implementer who produced the
	 * results (e.g. actually delivered the kits) but not necessarily: the meaning of partner is potentially more general.
	 * The only semantic meaning we enforce is that this is the OrgUnit that owns the data and thus has control over its
	 * modification and visibility.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORG_UNIT_COLUMN_ID, nullable = false)
	@NotNull
	private OrgUnit partner;

	/**
	 * The list of AttributeValues for this Site.
	 */
	@OneToMany(mappedBy = "site", fetch = FetchType.LAZY)
	private Set<AttributeValue> attributeValues = new HashSet<AttributeValue>(0);

	/**
	 * The ReportingPeriods for this Site. The number of ReportingPeriods depend on the ReportingFrequency of this Site's
	 * Activity. The Sites of Activities with a ONCE ReportingFrequency will have exactly one ReportingPeriod, while those
	 * with MONTHLY reporting will have zero or more ReportingPeriods, one for each calendar month in which data is
	 * available.
	 */
	@OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
	private Set<ReportingPeriod> reportingPeriods = new HashSet<ReportingPeriod>(0);

	/**
	 * For assessment Sites, a list of intervention Sites that are based on this assessment Site.
	 */
	@OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Site> interventions = new HashSet<Site>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public Site() {
		final Date now = new Date();
		setDateCreated(now);
		setDateEdited(now);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Note that deleted Sites are not physically removed from the database, they are retained to allow for the
	 * possibility of undoing of catastrophic error as well as to retain a record for synchronization with clients.
	 * </p>
	 */
	@Override
	public void delete() {
		final Date now = new Date();
		setDateDeleted(now);
		setDateEdited(now);
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
		builder.append("siteGuid", siteGuid);
		builder.append("status", status);
		builder.append("target", target);
		builder.append("date1", date1);
		builder.append("date2", date2);
		builder.append("dateCreated", dateCreated);
		builder.append("dateEdited", dateEdited);
		builder.append("dateDeleted", dateDeleted);
		builder.append("dateSynchronized", dateSynchronized);
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
	public void setId(Integer siteId) {
		this.id = siteId;
	}

	public Site getAssessment() {
		return this.assessment;
	}

	public void setAssessment(Site assessment) {
		this.assessment = assessment;
	}

	public Activity getActivity() {
		return this.activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public UserDatabase getDatabase() {
		return database;
	}

	public void setDatabase(UserDatabase database) {
		this.database = database;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getSiteGuid() {
		return this.siteGuid;
	}

	public void setSiteGuid(String siteGuid) {
		this.siteGuid = siteGuid;
	}

	public OrgUnit getPartner() {
		return this.partner;
	}

	public void setPartner(OrgUnit partner) {
		this.partner = partner;
	}

	/**
	 * @deprecated No longer used.
	 */
	@Deprecated
	public int getStatus() {
		return this.status;
	}

	/**
	 * @deprecated No longer used.
	 */
	@Deprecated
	public void setStatus(int status) {
		this.status = status;
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

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getDateSynchronized() {
		return this.dateSynchronized;
	}

	public void setDateSynchronized(Date dateSynchronized) {
		this.dateSynchronized = dateSynchronized;
	}

	public Set<AttributeValue> getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(Set<AttributeValue> values) {
		attributeValues = values;
	}

	public Set<ReportingPeriod> getReportingPeriods() {
		return this.reportingPeriods;
	}

	public void setReportingPeriods(Set<ReportingPeriod> reportingPeriods) {
		this.reportingPeriods = reportingPeriods;
	}

	public Set<Site> getInterventions() {
		return this.interventions;
	}

	public void setInterventions(Set<Site> interventions) {
		this.interventions = interventions;
	}

	public Date getDateDeleted() {
		return this.dateDeleted;
	}

	public void setDateDeleted(Date date) {
		this.dateDeleted = date;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}
}
