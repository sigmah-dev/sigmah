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

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;

/**
 * <p>
 * Global permissions domain entity.
 * </p>
 * <p>
 * Defines a global permission to be contained in a profile.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.GLOBAL_PERMISSION_TABLE)
public class GlobalPermission extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2678220725834763884L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.GLOBAL_PERMISSION_COLUMN_ID)
	private Integer id;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.PROFILE_COLUMN_ID, nullable = false)
	@NotNull
	private Profile profile;

	@Column(name = EntityConstants.GLOBAL_PERMISSION_COLUMN_PERMISSION, nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private GlobalPermissionEnum permission;

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

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public GlobalPermissionEnum getPermission() {
		return permission;
	}

	public void setPermission(GlobalPermissionEnum permission) {
		this.permission = permission;
	}

}
