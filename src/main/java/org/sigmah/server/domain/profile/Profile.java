package org.sigmah.server.domain.profile;

import java.util.List;

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
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Profile domain entity.
 * </p>
 * <p>
 * Defines a permissions profile.
 * </p>
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Entity
@Table(name = EntityConstants.PROFILE_TABLE)
public class Profile extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2985051353402191552L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PROFILE_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.PROFILE_COLUMN_NAME, nullable = false, length = EntityConstants.PROFILE_NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.PROFILE_NAME_MAX_LENGTH)
	private String name;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
	private List<GlobalPermission> globalPermissions;

	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
	private List<PrivacyGroupPermission> privacyGroupPermissions;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID)
	private Organization organization;

	public Profile() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
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

	public List<GlobalPermission> getGlobalPermissions() {
		return globalPermissions;
	}

	public void setGlobalPermissions(List<GlobalPermission> globalPermissions) {
		this.globalPermissions = globalPermissions;
	}

	public List<PrivacyGroupPermission> getPrivacyGroupPermissions() {
		return privacyGroupPermissions;
	}

	public void setPrivacyGroupPermissions(List<PrivacyGroupPermission> privacyGroupPermissions) {
		this.privacyGroupPermissions = privacyGroupPermissions;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}
