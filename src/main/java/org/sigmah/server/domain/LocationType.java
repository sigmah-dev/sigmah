package org.sigmah.server.domain;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.SchemaElement;

/**
 * <p>
 * Location type domain entit.
 * </p>
 * <p>
 * A LocationType corresponds to a type of site like a distribution site, a school, a health center, a well, etc.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LOCATION_TYPE_TABLE)
public class LocationType extends AbstractEntityId<Integer> implements SchemaElement {

	/**
	 * The name of the "default" location type for a country. The LocationType with this name can be used for locations
	 * that have no additional semantic meaning, e.g. they are not Schools, or clinics, or nutrition centers, but just a
	 * point on the earth. Default LocationType are created as needed, by country.
	 */
	public static final String DEFAULT = "$DEFAULT$";

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 232690778137357387L;

	@Id
	@Column(name = EntityConstants.LOCATION_TYPE_COLUMN_ID, unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = EntityConstants.LOCATION_TYPE_COLUMN_REUSE, nullable = false)
	@NotNull
	private boolean reuse;

	@Column(name = EntityConstants.LOCATION_TYPE_COLUMN_NAME, nullable = false, length = EntityConstants.NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.NAME_MAX_LENGTH)
	private String name;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.COUNTRY_COLUMN_ID, nullable = false)
	@NotNull
	private Country country;

	@OneToMany(mappedBy = "locationType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Location> locations = new HashSet<Location>(0);

	@OneToMany(mappedBy = "locationType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Activity> activities = new HashSet<Activity>(0);

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.LOCATION_TYPE_COLUMN_ADMIN_LEVEL, nullable = true)
	private AdminLevel boundAdminLevel;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public LocationType() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("reuse", reuse);
		builder.append("name", name);
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

	public boolean isReuse() {
		return this.reuse;
	}

	public void setReuse(boolean reuse) {
		this.reuse = reuse;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Country getCountry() {
		return this.country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Set<Location> getLocations() {
		return this.locations;
	}

	public void setLocations(Set<Location> locations) {
		this.locations = locations;
	}

	public Set<Activity> getActivities() {
		return this.activities;
	}

	public void setActivities(Set<Activity> activities) {
		this.activities = activities;
	}

	public AdminLevel getBoundAdminLevel() {
		return boundAdminLevel;
	}

	public void setBoundAdminLevel(AdminLevel boundAdminLevel) {
		this.boundAdminLevel = boundAdminLevel;
	}

}
