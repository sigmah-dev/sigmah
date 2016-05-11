package org.sigmah.server.service;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.sigmah.server.auth.SecureTokenGenerator;
import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.ContactModelDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ProfileDAO;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dao.UserUnitDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.OrgUnitProfile;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.mail.MailService;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.security.Authenticator;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.result.UserUnitsResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dispatch.FunctionalException.ErrorCode;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.UserUnitDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.EmailKey;
import org.sigmah.shared.dto.referential.EmailKeyEnum;
import org.sigmah.shared.dto.referential.EmailType;
import org.sigmah.shared.util.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * {@link User} service implementation.
 * 
 * @author nrebiai
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class UserService extends AbstractEntityService<User, Integer, UserDTO> {

	/**
	 * Logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	private final Injector injector;
	private final Mapper mapper;
	private final ContactDAO contactDAO;
	private final ContactModelDAO contactModelDAO;
	private final OrgUnitDAO orgUnitDAO;
	private final ProfileDAO profileDAO;
	private final UserDAO userDAO;
	private final UserUnitDAO userUnitDAO;
	private final MailService mailService;
	private final Authenticator authenticator;

	@Inject
	public UserService(Injector injector, ContactDAO contactDAO, ContactModelDAO contactModelDAO, OrgUnitDAO orgUnitDAO, ProfileDAO profileDAO, UserDAO userDAO, UserUnitDAO userUnitDAO, MailService mailService, Mapper mapper, Authenticator authenticator) {
		this.injector = injector;
		this.contactDAO = contactDAO;
		this.contactModelDAO = contactModelDAO;
		this.orgUnitDAO = orgUnitDAO;
		this.profileDAO = profileDAO;
		this.userDAO = userDAO;
		this.userUnitDAO = userUnitDAO;
		this.mailService = mailService;
		this.mapper = mapper;
		this.authenticator = authenticator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User create(final PropertyMap properties, final UserExecutionContext context) throws CommandException {

		final User executingUser = context.getUser();

		User userToPersist;
		User userFound = null;

		// get User that need to be saved from properties
		final Integer id = properties.get(UserDTO.ID);
		final String email = properties.get(UserDTO.EMAIL);
		final String name = properties.get(UserDTO.NAME);
		final String firstName = properties.get(UserDTO.FIRST_NAME);
		final Language language = properties.get(UserDTO.LOCALE);
		String password = properties.get(UserDTO.PASSWORD);
		UserUnitsResult userUnits = properties.get(UserDTO.USER_UNITS);

		if (email == null && name == null) {
			throw new IllegalArgumentException("Invalid argument.");
		}

		// Saves user.
		if(id != null) {
			userFound = userDAO.findById(id);
		}

		userToPersist = createNewUser(email, name, language.getLocale());
		userToPersist.setFirstName(firstName);
		userToPersist.setOrganization(executingUser.getOrganization());

		if (StringUtils.isNotBlank(password)) {
			userToPersist.setHashedPassword(authenticator.hashPassword(password));
			userToPersist.setChangePasswordKey(null);
			userToPersist.setDateChangePasswordKeyIssued(new Date());

		} else if (userFound != null) {
			userToPersist.setHashedPassword(userFound.getHashedPassword());
			userToPersist.setOrgUnitsWithProfiles(userUnitDAO.findAllOrgUnitProfilesByUserId(userFound.getId()));
		}

		if (userFound != null && userFound.getId() != null) {
			// Updates user.
			userToPersist.setId(userFound.getId());
			// BUGFIX #736 : Keeping the active state of modified users.
			userToPersist.setActive(userFound.getActive());
			
			userToPersist = em().merge(userToPersist);

		} else {
			// Creates new user.
			if (userDAO.doesUserExist(email)) {
				throw new FunctionalException(ErrorCode.ADMIN_USER_DUPLICATE_EMAIL, email);
			}

			password = authenticator.generatePassword();
			userToPersist.setHashedPassword(authenticator.hashPassword(password));
			userToPersist = userDAO.persist(userToPersist, executingUser);

			try {

				// Invitation email parameters.
				final Map<EmailKey, String> parameters = new HashMap<>();
				parameters.put(EmailKeyEnum.USER_USERNAME, Users.getUserCompleteName(userToPersist.getFirstName(), userToPersist.getName()));
				parameters.put(EmailKeyEnum.INVITING_USERNAME, Users.getUserCompleteName(executingUser.getFirstName(), executingUser.getName()));
				parameters.put(EmailKeyEnum.INVITING_EMAIL, executingUser.getEmail());
				parameters.put(EmailKeyEnum.APPLICATION_LINK, context.getApplicationUrl());
				parameters.put(EmailKeyEnum.USER_LOGIN, email);
				parameters.put(EmailKeyEnum.USER_PASSWORD, password);

				mailService.send(EmailType.INVITATION, parameters, language, email);

			} catch (final Exception e) {
				// Ignore, don't abort because mail didn't work
				if (LOG.isDebugEnabled()) {
					LOG.debug("Error occured during invitation email sending. Continuing user creation process anyway.", e);
				}
			}

			// Let's create a contact for this user
			ContactModel contactModel = contactModelDAO.findById((Integer) properties.get(UserDTO.CONTACT_MODEL));
			Contact parent = contactDAO.findById((Integer) properties.get(UserDTO.CONTACT_ORGANIZATION));
			Contact contact = new Contact();
			contact.setUser(userToPersist);
			contact.setContactModel(contactModel);
			contact.setParent(parent);
			contact.setDateCreated(new Date());
			contactDAO.persist(contact, context.getUser());
		}

		// update link to profile
		if (userToPersist.getId() != null) {
			// Needs to remove all subs profiles to avoid orphan entities
			while (!userToPersist.getOrgUnitsWithProfiles().isEmpty()) {
				userToPersist.getOrgUnitsWithProfiles().get(0).getProfiles().removeAll(userToPersist.getOrgUnitsWithProfiles().get(0).getProfiles());
				userToPersist.getOrgUnitsWithProfiles().remove(0);
			}
			userToPersist.getOrgUnitsWithProfiles().removeAll(userToPersist.getOrgUnitsWithProfiles());

			OrgUnitProfile mainOrgUnitProfile = createOrUpdateUserUnit(userUnits.getMainUserUnit(), userToPersist);
			if (mainOrgUnitProfile == null) {
				throw new IllegalStateException("A main orgUnit profile should be provided.");
			}
			userToPersist.getOrgUnitsWithProfiles().add(mainOrgUnitProfile);

			// Avoid duplicates by checking the OrgUnit id of each UserUnit
			Set<Integer> orgUnitIds = new HashSet<>();
			orgUnitIds.add(userUnits.getMainUserUnit().getOrgUnit().getId());
			for (UserUnitDTO userUnitDTO : userUnits.getSecondaryUserUnits()) {
				if (!orgUnitIds.add(userUnitDTO.getOrgUnit().getId())) {
					continue;
				}

				OrgUnitProfile secondaryOrgUnitProfile = createOrUpdateUserUnit(userUnitDTO, userToPersist);
				userToPersist.getOrgUnitsWithProfiles().add(secondaryOrgUnitProfile);
				}
			userToPersist = userDAO.persist(userToPersist, context.getUser());
		}

		return userToPersist;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EntityDTO<?> handleMapping(final User createdUser) throws CommandException {

		UserDTO userPersisted = null;

		if (createdUser != null) {
			userPersisted = mapper.map(createdUser, new UserDTO(), UserDTO.Mode.WITH_BASE_ORG_UNIT_AND_BASE_PROFILES);
			userPersisted.setIdd(createdUser.getId());
		}

		if (userPersisted != null) {
			// [UserPermission trigger] Updates UserPermission table after user creation/modification.
			injector.getInstance(UserPermissionPolicy.class).updateUserPermissionByUser(userPersisted.getIdd());
		}

		return userPersisted;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User update(Integer entityId, PropertyMap changes, final UserExecutionContext context) {
		throw new UnsupportedOperationException("No policy update operation implemented for '" + entityClass.getSimpleName() + "' entity.");
	}

	private OrgUnitProfile createOrUpdateUserUnit(UserUnitDTO userUnitDTO, User user) {
		OrgUnitProfile orgUnitProfile = new OrgUnitProfile();
		if (userUnitDTO.getMainUserUnit()) {
			orgUnitProfile.setType(OrgUnitProfile.OrgUnitProfileType.MAIN);
		} else {
			orgUnitProfile.setType(OrgUnitProfile.OrgUnitProfileType.SECONDARY);
		}

		orgUnitProfile.setUser(user);

		// Apply the new orgUnit
		Integer orgUnitId = userUnitDTO.getOrgUnit().getId();
		orgUnitProfile.setOrgUnit(orgUnitDAO.findById(orgUnitId));

		// Apply the new profiles
		Set<Integer> profileIds = new HashSet<>();
		for (ProfileDTO profileDTO : userUnitDTO.getProfiles()) {
			if (profileDTO == null) {
				continue;
			}

			profileIds.add(profileDTO.getId());
		}

		orgUnitProfile.setProfiles(profileDAO.findByIds(profileIds));

		return orgUnitProfile;
	}

	/**
	 * <p>
	 * Initializes a new {@link User} with a secure changePasswordKey.
	 * </p>
	 * <p>
	 * Sets the following user properties:
	 * <ul>
	 * <li>{@code name}</li>
	 * <li>{@code email} (populated with given one)</li>
	 * <li>{@code newUser} (set to {@code true})</li>
	 * <li>{@code locale} (populated with given one)</li>
	 * <li>{@code changePasswordKey} (see {@link SecureTokenGenerator#generate()})</li>
	 * </ul>
	 * </p>
	 * 
	 * @param email
	 *          The user email value.
	 * @param name
	 *          The user name value.
	 * @param locale
	 *          The user locale value.
	 * @return the initialized new user.
	 */
	private static User createNewUser(final String email, final String name, final String locale) {
		final User user = new User();
		user.setEmail(email);
		user.setName(name);
		user.setNewUser(true);
		user.setLocale(locale);
		user.setChangePasswordKey(SecureTokenGenerator.generate());
		// BUGFIX: #771 new users should be active when created.
		user.setActive(Boolean.TRUE);
		return user;
	}

}
