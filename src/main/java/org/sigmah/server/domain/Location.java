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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Location domain entity.
 * </p>
 * <p>
 * A location corresponds to a site in Sigmah. An indicator needs a site in order to have a value. A site corresponds to
 * a place in the world. These informations are set with the Google Maps panel and has some geographical coordinates.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LOCATION_TABLE)
public class Location extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4928884549525700609L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.LOCATION_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	/**
	 * This is the Guid of the location.<br/>
	 * This column is not used in Sigmah.
	 */
	@Column(name = EntityConstants.LOCATION_COLUMN_LOCATION_GUID, length = EntityConstants.LOCATION_GUID_MAX_LENGTH)
	@Size(max = EntityConstants.LOCATION_GUID_MAX_LENGTH)
	private String locationGuid;

	@Column(name = EntityConstants.LOCATION_COLUMN_X, precision = EntityConstants.LOCATION_COORDINATE_PRECISION, scale = EntityConstants.LOCATION_COORDINATE_SCALE)
	private Double x;

	@Column(name = EntityConstants.LOCATION_COLUMN_Y, precision = EntityConstants.LOCATION_COORDINATE_PRECISION, scale = EntityConstants.LOCATION_COORDINATE_SCALE)
	private Double y;

	@Column(name = EntityConstants.LOCATION_COLUMN_NAME, nullable = false, length = EntityConstants.NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.NAME_MAX_LENGTH)
	private String name;

	/**
	 * Name of the nearest road or waterway.
	 */
	@Column(name = EntityConstants.LOCATION_COLUMN_AXE, length = EntityConstants.NAME_MAX_LENGTH)
	@Size(max = EntityConstants.NAME_MAX_LENGTH)
	private String axe;

	@Column(name = EntityConstants.COLUMN_DATE_CREATED)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;

	@Column(name = EntityConstants.COLUMN_DATE_EDITED)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateEdited;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.LOCATION_TYPE_COLUMN_ID, nullable = false)
	@NotNull
	private LocationType locationType;

	@OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Site> sites = new HashSet<Site>(0);

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = EntityConstants.LOCATION_ADMIN_ENTITY_LINK_TABLE, joinColumns = { @JoinColumn(name = EntityConstants.LOCATION_COLUMN_ID, nullable = false, updatable = false)
	}, inverseJoinColumns = { @JoinColumn(name = EntityConstants.ADMIN_ENTITY_COLUMN_ID, nullable = false, updatable = false)
	})
	private Set<AdminEntity> adminEntities = new HashSet<AdminEntity>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public Location() {
		// Default empty constructor.
	}

	public void setAdminEntity(Integer levelId, AdminEntity newEntity) {

		for (final AdminEntity entity : getAdminEntities()) {
			if (entity.getLevel().getId().equals(levelId)) {

				if (newEntity == null) {
					getAdminEntities().remove(entity);
				} else if (!newEntity.getId().equals(entity.getId())) {
					getAdminEntities().remove(entity);
					getAdminEntities().add(newEntity);
				}

				return;
			}
		}

		if (newEntity != null) {
			getAdminEntities().add(newEntity);
		}
	}

	@PrePersist
	public void onCreate() {
		final Date now = new Date();
		setDateCreated(now);
		setDateEdited(now);
	}

	@PreUpdate
	public void onUpdate() {
		setDateEdited(new Date());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("locationGuid", locationGuid);
		builder.append("x", x);
		builder.append("y", y);
		builder.append("axe", axe);
		builder.append("dateCreated", dateCreated);
		builder.append("dateEdited", dateEdited);
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

	public LocationType getLocationType() {
		return this.locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public String getLocationGuid() {
		return this.locationGuid;
	}

	public void setLocationGuid(String locationGuid) {
		this.locationGuid = locationGuid;
	}

	public Double getX() {
		return this.x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return this.y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAxe() {
		return this.axe;
	}

	public void setAxe(String axe) {
		this.axe = axe;
	}

	public Set<Site> getSites() {
		return this.sites;
	}

	public void setSites(Set<Site> sites) {
		this.sites = sites;
	}

	public Set<AdminEntity> getAdminEntities() {
		return this.adminEntities;
	}

	public void setAdminEntities(Set<AdminEntity> adminEntities) {
		this.adminEntities = adminEntities;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateEdited() {
		return dateEdited;
	}

	public void setDateEdited(Date dateEdited) {
		this.dateEdited = dateEdited;
	}

}
