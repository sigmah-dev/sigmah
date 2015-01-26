package org.sigmah.server.domain.profile;

import java.util.List;

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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Organization Unit Profile domain entity.
 * </p>
 * <p>
 * A profile which associates a user to an organizational unit.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ORG_UNIT_PROFILE_TABLE)
public class OrgUnitProfile extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6006586638356971306L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ORG_UNIT_PROFILE_COLUMN_ID)
	@NotNull
	private Integer id;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORG_UNIT_PROFILE_COLUMN_USER, nullable = false)
	@NotNull
	private User user;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORG_UNIT_PROFILE_COLUMN_ORG_UNIT, nullable = false)
	@NotNull
	private OrgUnit orgUnit;

	@ManyToMany
	@JoinTable(name = EntityConstants.ORG_UNIT_PROFILE_PROFILE_LINK_TABLE, joinColumns = { @JoinColumn(name = EntityConstants.ORG_UNIT_PROFILE_COLUMN_ID)
	}, inverseJoinColumns = { @JoinColumn(name = EntityConstants.PROFILE_COLUMN_ID)
	}, uniqueConstraints = { @UniqueConstraint(columnNames = {
																														EntityConstants.ORG_UNIT_PROFILE_COLUMN_ID,
																														EntityConstants.PROFILE_COLUMN_ID
	})
	})
	private List<Profile> profiles;

	public OrgUnitProfile() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	// Add methods here.

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public OrgUnit getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(OrgUnit orgUnit) {
		this.orgUnit = orgUnit;
	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

}
