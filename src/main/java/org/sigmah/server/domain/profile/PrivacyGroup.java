package org.sigmah.server.domain.profile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Privacy group domain entity.
 * </p>
 * <p>
 * Defines a privacy group to be contained in a profile.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PRIVACY_GROUP_TABLE)
public class PrivacyGroup extends AbstractEntityId<Integer> {

	/**
	 * Server version UID.
	 */
	private static final long serialVersionUID = -4389579698326568953L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PRIVACY_GROUP_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.PRIVACY_GROUP_COLUMN_CODE, nullable = false)
	@NotNull
	private Integer code;

	@Column(name = EntityConstants.PRIVACY_GROUP_COLUMN_TITLE, nullable = false, length = EntityConstants.PRIVACY_GROUP_TITLE_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.PRIVACY_GROUP_TITLE_MAX_LENGTH)
	private String title;

	/**
	 * Set to {@code true} if the privacy group has been updated.<br>
	 * Set to {@code false} if it has been created.
	 */
	@Transient
	private boolean updated;

	public PrivacyGroup() {
	}

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID)
	private Organization organization;

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
		builder.append("code", code);
		builder.append("title", title);
		builder.append("updated (transient)", updated);
	}

	/**
	 * Reset the identifiers of the object.
	 */
	public void resetImport() {
		this.id = null;
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

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
}
