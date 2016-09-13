package org.sigmah.server.domain;

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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.profile.OrgUnitProfile;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.Language;
import org.sigmah.shared.util.Users;

/**
 * <p>
 * User domain entity.
 * </p>
 * <p>
 * <em>We want to avoid calling this table {@code 'User'} as it is a reserved word in some dialects of SQL.</em>
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.USER_TABLE)
public class User extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1511545346659844141L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.USER_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	@Column(name = EntityConstants.USER_COLUMN_EMAIL, nullable = false, length = EntityConstants.EMAIL_MAX_LENGTH, unique = true)
	@NotNull
	@Size(max = EntityConstants.EMAIL_MAX_LENGTH)
	private String email;

	@Column(name = EntityConstants.USER_COLUMN_FIRSTNAME, nullable = true, length = EntityConstants.NAME_MAX_LENGTH)
	@Size(max = EntityConstants.NAME_MAX_LENGTH)
	private String firstName;

	@Column(name = EntityConstants.USER_COLUMN_NAME, nullable = false, length = EntityConstants.NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.NAME_MAX_LENGTH)
	private String name;

	@Column(name = EntityConstants.USER_COLUMN_NEW_USER, nullable = false)
	@NotNull
	private boolean newUser;

	@Column(name = EntityConstants.USER_COLUMN_LOCALE, nullable = false, length = EntityConstants.LOCALE_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.LOCALE_MAX_LENGTH)
	private String locale;

	/**
	 * The secure key required to change the user's password.
	 * This is a random 128-bit key that can be safely sent to the user by email.
	 */
	@Column(name = EntityConstants.USER_COLUMN_CHANGE_PASSWORD_KEY, nullable = true, length = EntityConstants.CHANGE_PASSWORD_KEY_MAX_LENGTH)
	@Size(max = EntityConstants.CHANGE_PASSWORD_KEY_MAX_LENGTH)
	private String changePasswordKey;

	/**
	 * The date on which the password key was issued; the application should not let users change passwords with really
	 * old keys.
	 */
	@Column(name = EntityConstants.USER_COLUMN_DATE_CHANGE_PASSWORD_KEY_ISSUED)
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date dateChangePasswordKeyIssued;

	/**
	 * The user's password, hashed with the BCrypt algorithm.
	 */
	@Column(name = EntityConstants.USER_COLUMN_PASSWORD, length = EntityConstants.PASSWORD_MAX_LENGTH)
	@Size(max = EntityConstants.PASSWORD_MAX_LENGTH)
	private String hashedPassword;

	@Column(name = EntityConstants.USER_COLUMN_ACTIVE, nullable = true)
	private Boolean active;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID, nullable = true)
	private Organization organization;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrgUnitProfile> orgUnitsWithProfiles = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
	private Contact contact;

	@Transient
	private OrgUnitProfile mainOrgUnitWithProfiles;

	@Transient
	private List<OrgUnitProfile> secondaryOrgUnitsWithProfiles = new ArrayList<>();

	public User() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * <p>
	 * Overrides default behaviour to only display {@link #getUserCompleteName(User)} value.
	 * </p>
	 * 
	 * @see #getUserCompleteName(User)
	 */
	@Override
	public String toString() {
		return getUserCompleteName(this);
	}

	/**
	 * Clears the user's change password key (as well as the change password date).
	 */
	public void clearChangePasswordKey() {
		this.setChangePasswordKey(null);
		this.setDateChangePasswordKeyIssued(null);
	}

	/**
	 * <p>
	 * Override default behaviour to rely on {@link #getEmail()} value.
	 * </p>
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof User)) {
			return false;
		}
		final User that = (User) other;
		return this.getEmail().equals(that.getEmail());
	}

	/**
	 * <p>
	 * Override default behaviour to rely on {@link #getEmail()} value.
	 * </p>
	 */
	@Override
	public int hashCode() {
		return getEmail().hashCode();
	}

	// --------------------------------------------------------------------------------
	//
	// STATIC METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Gets the formatted complete name of a user.
	 * <ul>
	 * <li>If the user has a first name and a last name, returns '<i>John Doe</i>'.</li>
	 * <li>If the user hasn't a first name and has a last name, returns '<i>Doe</i>'.</li>
	 * <li>If the user has neither a first name or a last name, returns an empty string.</li>
	 * </ul>
	 * 
	 * @param user
	 *          The user
	 * @return The complete name.
	 */
	public static String getUserCompleteName(final User user) {

		if (user == null) {
			return Users.getUserCompleteName(null, null);
		}

		return Users.getUserCompleteName(user.firstName, user.name);
	}

	/**
	 * Gets the formatted short name of a user.
	 * <ul>
	 * <li>If the user has a first name and a last name, returns '<i>J. Doe</i>'.</li>
	 * <li>If the user hasn't a first name and has a last name, returns '<i>Doe</i>'.</li>
	 * <li>If the user has neither a first name or a last name, returns an empty string.</li>
	 * </ul>
	 * 
	 * @param user
	 *          The user
	 * @return The short name.
	 */
	public static String getUserShortName(final User user) {

		if (user == null) {
			return Users.getUserShortName(null, null);
		}

		return Users.getUserShortName(user.firstName, user.name);
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

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public String getFullName() {
		if (firstName == null) {
			return name;
		}
		return firstName + " " + name.toUpperCase();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public boolean isNewUser() {
		return this.newUser;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}

	public String getLocale() {
		return this.locale;
	}
	
	public Locale getLocaleInstance() {
		final Language language = Language.fromString(getLocale());
		return new Locale(language.getLocale());
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getHashedPassword() {
		return this.hashedPassword;
	}

	public void setHashedPassword(String hashed) {
		this.hashedPassword = hashed;
	}

	public String getChangePasswordKey() {
		return changePasswordKey;
	}

	public void setChangePasswordKey(String changePasswordKey) {
		this.changePasswordKey = changePasswordKey;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getActive() {
		return active;
	}

	public Date getDateChangePasswordKeyIssued() {
		return dateChangePasswordKeyIssued;
	}

	public void setDateChangePasswordKeyIssued(Date dateChangePasswordKeyIssued) {
		this.dateChangePasswordKeyIssued = dateChangePasswordKeyIssued;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public List<OrgUnitProfile> getOrgUnitsWithProfiles() {
		return orgUnitsWithProfiles;
	}

	public void setOrgUnitsWithProfiles(List<OrgUnitProfile> orgUnitsWithProfiles) {
		this.orgUnitsWithProfiles = orgUnitsWithProfiles;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public OrgUnitProfile getMainOrgUnitWithProfiles() {
		if (mainOrgUnitWithProfiles == null) {
			// FIXME: For some reason, the are some cases where postLoad is not called when getting this entity
			// For the moment, let's execute postLoad manually in case it was not called
			postLoad();
		}
		return mainOrgUnitWithProfiles;
	}

	public List<OrgUnitProfile> getSecondaryOrgUnitsWithProfiles() {
		if (secondaryOrgUnitsWithProfiles == null) {
			// FIXME: For some reason, the are some cases where postLoad is not called when getting this entity
			// For the moment, let's execute postLoad manually in case it was not called
			postLoad();
		}
		return secondaryOrgUnitsWithProfiles;
	}

	/*
	 * Helper for Dozer.
	 * Dozer can't map a User.secondaryOrgUnitsWithProfiles.*.orgUnit to a orgUnit list
	 */
	public List<OrgUnit> getSecondaryOrgUnits() {
		if (secondaryOrgUnitsWithProfiles == null) {
			return null;
		}

		List<OrgUnit> orgUnits = new ArrayList<OrgUnit>(secondaryOrgUnitsWithProfiles.size());
		for (OrgUnitProfile orgUnitProfile : secondaryOrgUnitsWithProfiles) {
			orgUnits.add(orgUnitProfile.getOrgUnit());
		}
		return orgUnits;
	}

	@PostLoad
	void postLoad() {
		List<OrgUnitProfile> secondaryOrgUnitProfiles = new ArrayList<>();

		for (OrgUnitProfile orgUnitsWithProfile : orgUnitsWithProfiles) {
			if (orgUnitsWithProfile.getType() == null) {
				// In case the related user unit is not updated since last Sigmah update,
				// this value can be null
				// Let's prevent a NPE by making it the main OrgUnitProfile
				orgUnitsWithProfile.setType(OrgUnitProfile.OrgUnitProfileType.MAIN);
			}

			switch (orgUnitsWithProfile.getType()) {
				case MAIN:
					mainOrgUnitWithProfiles = orgUnitsWithProfile;
					break;
				case SECONDARY:
					secondaryOrgUnitProfiles.add(orgUnitsWithProfile);
					break;
				default:
					throw new IllegalStateException("Unknown OrgUnitProfileType : " + orgUnitsWithProfile.getType());
			}
		}
		this.secondaryOrgUnitsWithProfiles = Collections.unmodifiableList(secondaryOrgUnitProfiles);
	}
}
