package org.sigmah.server.domain.profile;

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;

/**
 * <p>
 * Privacy group permission domain entity.
 * </p>
 * <p>
 * Wrap a permission linked to a privacy group.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PRIVACY_GROUP_PERMISSION_TABLE)
public class PrivacyGroupPermission extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2124906244118541577L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PRIVACY_GROUP_PERMISSION_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.PRIVACY_GROUP_PERMISSION_COLUMN_PERMISSION, nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private PrivacyGroupPermissionEnum permission;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.PROFILE_COLUMN_ID, nullable = false)
	@NotNull
	private Profile profile;

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.PRIVACY_GROUP_COLUMN_ID, nullable = false)
	@NotNull
	private PrivacyGroup privacyGroup;

	public PrivacyGroupPermission() {
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
	protected void appendToString(ToStringBuilder builder) {
		builder.append("permission", permission);
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

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public PrivacyGroup getPrivacyGroup() {
		return privacyGroup;
	}

	public void setPrivacyGroup(PrivacyGroup privacyGroup) {
		this.privacyGroup = privacyGroup;
	}

	public PrivacyGroupPermissionEnum getPermission() {
		return permission;
	}

	public void setPermission(PrivacyGroupPermissionEnum permission) {
		this.permission = permission;
	}

}
