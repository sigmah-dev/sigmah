package org.sigmah.server.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Administrative entity domain entity.
 * </p>
 * <p>
 * These entities are created in order to split a country in administrative areas. It could be use to localizing a site
 * within an administrative area and on what an indicator is about.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ADMIN_ENTITY_TABLE)
public class AdminEntity extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 7813010816730061755L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ADMIN_ENTITY_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	/**
	 * Human readable name for the administrative entity.
	 */
	@Column(name = EntityConstants.ADMIN_ENTITY_COLUMN_NAME, nullable = false, length = EntityConstants.NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.NAME_MAX_LENGTH)
	private String name;

	/**
	 * Name of the administrative entity.
	 */
	@Column(name = EntityConstants.ADMIN_ENTITY_COLUMN_SOUNDEX, length = EntityConstants.NAME_MAX_LENGTH)
	@Size(max = EntityConstants.NAME_MAX_LENGTH)
	private String soundex;

	/**
	 * Short name for the administrative entity.
	 */
	@Column(name = EntityConstants.ADMIN_ENTITY_COLUMN_CODE, length = EntityConstants.ADMIN_ENTITY_CODE_MAX_LENGTH)
	@Size(max = EntityConstants.ADMIN_ENTITY_CODE_MAX_LENGTH)
	private String code;

	/**
	 * Area coordinates.
	 */
	@Embedded
	private Bounds bounds;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ADMIN_LEVEL_COLUMN_ID, nullable = false)
	@NotNull
	private AdminLevel level;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = EntityConstants.ADMIN_ENTITY_COLUMN_PARENT, nullable = true)
	private AdminEntity parent;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = EntityConstants.LOCATION_ADMIN_ENTITY_LINK_TABLE, joinColumns = { @JoinColumn(name = EntityConstants.ADMIN_ENTITY_COLUMN_ID, nullable = false, updatable = false)
	}, inverseJoinColumns = { @JoinColumn(name = EntityConstants.LOCATION_COLUMN_ID, nullable = false, updatable = false)
	})
	private Set<Location> locations = new HashSet<Location>(0);

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<AdminEntity> children = new HashSet<AdminEntity>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public AdminEntity() {
		// Default empty constructor.
	}

	public AdminEntity(Integer id, AdminLevel adminLevel, String name) {
		this.id = id;
		this.level = adminLevel;
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("soundex", soundex);
		builder.append("code", code);
		builder.append("bounds", bounds);
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
	public void setId(Integer adminEntityId) {
		this.id = adminEntityId;
	}

	public AdminLevel getLevel() {
		return this.level;
	}

	public void setLevel(AdminLevel level) {
		this.level = level;
	}

	public Set<Location> getLocations() {
		return this.locations;
	}

	public void setLocations(Set<Location> locations) {
		this.locations = locations;
	}

	public AdminEntity getParent() {
		return this.parent;
	}

	public void setParent(AdminEntity parent) {
		this.parent = parent;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSoundex() {
		return this.soundex;
	}

	public void setSoundex(String soundex) {
		this.soundex = soundex;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public Set<AdminEntity> getChildren() {
		return this.children;
	}

	public void setChildren(Set<AdminEntity> children) {
		this.children = children;
	}

}
