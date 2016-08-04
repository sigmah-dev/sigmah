package org.sigmah.shared.command.result;

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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.Language;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.util.Users;

/**
 * <p>
 * Encapsulates authenticated user identity. If no user is authenticated, this object encapsulates <em>anonymous</em>
 * user identity, see {@link org.sigmah.server.servlet.base.ServletExecutionContext#ANONYMOUS_USER ANONYMOUS_USER}.
 * </p>
 * <p>
 * This object is managed by {@link org.sigmah.client.security.AuthenticationProvider}.
 * </p>
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.sigmah.client.security.AuthenticationProvider
 * @see org.sigmah.server.servlet.base.ServletExecutionContext#ANONYMOUS_USER
 */
public class Authentication implements Result {

	/**
	 * The authentication token required for calls to the command dispatch service.
	 */
	private String authenticationToken;

	/**
	 * The currently authenticated user's unique id.
	 */
	private Integer userId;

	/**
	 * The currently authenticated user's email.
	 */
	private String userEmail;

	/**
	 * The currently authenticated user's last name.
	 */
	private String userName;

	/**
	 * The currently authenticated user's first name.
	 */
	private String userFirstName;

	/**
	 * User language.
	 */
	private Language language;

	/**
	 * The currently authenticated user's Organization id.
	 */
	private Integer organizationId;

	/**
	 * The currently authenticated user's Organization name.
	 */
	private String organizationName;

	/**
	 * The currently authenticated user's Organization logo path.
	 */
	private String organizationLogo;

	/**
	 * The currently authenticated user's OrgUnit id.
	 */
	private Integer mainOrgUnitId;

	private Set<Integer> secondaryOrgUnitIds;

	/**
	 * The currently authenticated user's aggregated profiles.
	 * Aggregation of all user's profile(s) (a user can be linked to multiple profiles).
	 */
	private Map<Integer, ProfileDTO> aggregatedProfiles;

	private Set<Integer> memberOfProjectIds = new HashSet<Integer>();

	/**
	 * Set to <code>true</code> if the current user is the anonymous user.
	 */
	private boolean authorized;

	/**
	 * Necessary constructor for serialization.
	 */
	public Authentication() {
		// Serialization.
	}

	/**
	 * Initializes a new {@code Authentication} instance.
	 *
	 * @param language
	 *          The user's language.
	 */
	public Authentication(final Language language) {
		this.language = language;
	}

	/**
	 * Initializes a new {@code Authentication} instance.
	 *
	 * @param userId
	 *          The user's id (from the server's database).
	 * @param userEmail
	 *          The user's email.
	 * @param userName
	 *          The user's last name.
	 * @param userFirstName
	 *          The user's first name.
	 * @param language
	 *          The user's language.
	 * @param organizationId
	 *          The user's Organization id.
	 * @param organizationName
	 *          The user's Organization name.
	 * @param organizationLogo
	 *          The user's Organization logo path.
	 * @param mainOrgUnitId
	 *          The user's OrgUnit id.
	 * @param aggregatedProfiles
	 *          The user's <em>aggregated profiles</em>.
	 */
	public Authentication(Integer userId, String userEmail, String userName, String userFirstName, Language language,
		Integer organizationId, String organizationName, String organizationLogo, Integer mainOrgUnitId,
		Set<Integer> secondaryOrgUnitIds, Map<Integer, ProfileDTO> aggregatedProfiles, Set<Integer> memberOfProjectIds) {
		this(userId, userEmail, userName, userFirstName, language, organizationId, organizationName, organizationLogo,
			mainOrgUnitId, secondaryOrgUnitIds,  aggregatedProfiles, memberOfProjectIds, false);
	}

	/**
	 * Initializes a new {@code Authentication} instance.
	 *
	 * @param userId
	 *          The user's id (from the server's database).
	 * @param userEmail
	 *          The user's email.
	 * @param userName
	 *          The user's last name.
	 * @param userFirstName
	 *          The user's first name.
	 * @param language
	 *          The user's language.
	 * @param organizationId
	 *          The user's Organization id.
	 * @param organizationName
	 *          The user's Organization name.
	 * @param organizationLogo
	 *          The user's Organization logo path.
	 * @param mainOrgUnitId
	 *          The user's OrgUnit id.
	 * @param aggregatedProfile
	 *          The user's <em>aggregated profile</em>.
	 * @param authorized
	 *			<code>true</code> to allow the user to use Sigmah without cookie.
	 */
	public Authentication(Integer userId, String userEmail, String userName, String userFirstName, Language language,
		Integer organizationId, String organizationName, String organizationLogo, Integer mainOrgUnitId,
		Set<Integer> secondaryOrgUnitIds, Map<Integer, ProfileDTO> aggregatedProfiles, Set<Integer> memberOfProjectIds, boolean authorized) {
		this.userId = userId;
		this.userEmail = userEmail;
		this.userName = userName;
		this.userFirstName = userFirstName;
		this.language = language;
		this.organizationId = organizationId;
		this.organizationName = organizationName;
		this.organizationLogo = organizationLogo;
		this.mainOrgUnitId = mainOrgUnitId;
		this.secondaryOrgUnitIds = secondaryOrgUnitIds;
		this.aggregatedProfiles = aggregatedProfiles;
		this.memberOfProjectIds = memberOfProjectIds;
		this.authorized = authorized;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {

		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("userId", userId);
		builder.append("userEmail", userEmail);
		builder.append("userName", userName);
		builder.append("userFirstName", userFirstName);
		builder.append("language", language);
		builder.append("organizationId", organizationId);
		builder.append("organizationName", organizationName);
		builder.append("organizationLogo", organizationLogo);
		builder.append("mainOrgUnitId", mainOrgUnitId);

		StringBuilder serializedSecondaryOrgUnitIds = new StringBuilder("[");
		for (Iterator<Integer> iterator = secondaryOrgUnitIds.iterator(); iterator.hasNext();) {
			Integer secondaryOrgUnitId = iterator.next();
			serializedSecondaryOrgUnitIds.append(secondaryOrgUnitId);
			if (iterator.hasNext()) {
				serializedSecondaryOrgUnitIds.append(", ");
			}
		}
		serializedSecondaryOrgUnitIds.append("]");

		builder.append("secondaryOrgUnitIds", serializedSecondaryOrgUnitIds.toString());

		StringBuilder serializedAggregatedProfiles = new StringBuilder("[");
		for (Iterator<Map.Entry<Integer, ProfileDTO>> iterator = aggregatedProfiles.entrySet().iterator(); iterator.hasNext(); ) {
			Map.Entry<Integer, ProfileDTO> entry = iterator.next();
			serializedAggregatedProfiles.append("{" + entry.getKey() + ": " + entry.getValue() + "}");
			if (iterator.hasNext()) {
				serializedAggregatedProfiles.append(", ");
			}
		}
		serializedAggregatedProfiles.append("]");
		builder.append("aggregatedProfiles", serializedAggregatedProfiles);

		StringBuilder serializedMemberOfProjectIds = new StringBuilder("[");
		for (Iterator<Integer> iterator = memberOfProjectIds.iterator(); iterator.hasNext();) {
			Integer memberOfProjectId = iterator.next();
			serializedMemberOfProjectIds.append(memberOfProjectId);
			if (iterator.hasNext()) {
				serializedMemberOfProjectIds.append(", ");
			}
		}
		serializedMemberOfProjectIds.append("]");

		builder.append("memberOfProjectIds", serializedMemberOfProjectIds.toString());

		return builder.toString();
	}

	/**
	 * See {@link Users#getUserCompleteName(String, String)} for javadoc.
	 *
	 * @return The current authentication related user's <em>complete</em> name.
	 */
	public String getUserCompleteName() {
		return Users.getUserCompleteName(userFirstName, userName);
	}

	/**
	 * See {@link Users#getUserShortName(String, String)} for javadoc.
	 *
	 * @return The current authentication related user's <em>short</em> name.
	 */
	public String getUserShortName() {
		return Users.getUserShortName(userFirstName, userName);
	}

	// ------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// ------------------------------------------------------------

	/**
	 * Returns the authentication token, from {@link org.sigmah.server.domain.Authentication}.
	 *
	 * @return The authentication token, from {@link org.sigmah.server.domain.Authentication}.
	 */
	public String getAuthenticationToken() {
		return authenticationToken;
	}

	/**
	 * Sets the authentication token.
	 * </p>
	 * <p>
	 * <em>Should <b>only</b> be called by {@link org.sigmah.server.handler.LoginCommandHandler} or {@link AuthenticationProvider}.</em>
	 * </p>
	 *
	 * @param authenticationToken
	 *          The authentication token.
	 */
	// Only required setter.
	public void setAuthenticationToken(final String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	/**
	 * Returns the authenticated user id or {@code null} if anonymous.
	 *
	 * @return The authenticated user id or {@code null} if anonymous.
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * Returns the authenticated user email or {@code null} if anonymous.
	 *
	 * @return The authenticated user email or {@code null} if anonymous.
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * Sets the authenticated user email.
	 *
	 * @param userEmail The authenticated user email or {@code null} if anonymous.
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * Returns the authenticated user's Organization id or {@code null} if anonymous.
	 *
	 * @return The authenticated user's Organization id or {@code null} if anonymous.
	 */
	public Integer getOrganizationId() {
		return organizationId;
	}

	/**
	 * Returns the authenticated user's Organization name or {@code null} if anonymous.
	 *
	 * @return The authenticated user's Organization name or {@code null} if anonymous.
	 */
	public String getOrganizationName() {
		return organizationName;
	}

	/**
	 * Returns the authenticated user's Organization logo or {@code null} if anonymous.
	 *
	 * @return The authenticated user's Organization logo or {@code null} if anonymous.
	 */
	public String getOrganizationLogo() {
		return organizationLogo;
	}

	/**
	 * Returns the authenticated user's main OrgUnit id or {@code null} if anonymous.
	 *
	 * @return The authenticated user's main OrgUnit id or {@code null} if anonymous.
	 */
	public Integer getMainOrgUnitId() {
		return mainOrgUnitId;
	}

	/**
	 * Returns the authenticated user's secondary OrgUnit ids or {@code Collections.emptySet()} if anonymous.
	 */
	public Set<Integer> getSecondaryOrgUnitIds() {
		return secondaryOrgUnitIds;
	}

	public Set<Integer> getOrgUnitIds() {
		Set<Integer> orgUnitIds = new HashSet<Integer>();
		orgUnitIds.add(getMainOrgUnitId());
		orgUnitIds.addAll(getSecondaryOrgUnitIds());
		return orgUnitIds;
	}

	/**
	 * Returns the authenticated user last name or {@code null} if anonymous.
	 *
	 * @return The authenticated user last name or {@code null} if anonymous.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Returns the authenticated user first name or {@code null} if anonymous.
	 *
	 * @return The authenticated user first name or {@code null} if anonymous.
	 */
	public String getUserFirstName() {
		return userFirstName;
	}

	/**
	 * Returns the authenticated user aggregated profiles by OrgUnit id or {@code null} if anonymous.
	 *
	 * @return The authenticated user aggregated profiles by OrgUnit id or {@code null} if anonymous.
	 */
	public Map<Integer, ProfileDTO> getAggregatedProfiles() {
		return aggregatedProfiles;
	}

	/**
	 * Returns the list of project ids for which the authenticated user is a member.
	 */
	public Set<Integer> getMemberOfProjectIds() {
		return memberOfProjectIds;
	}

	/**
	 * Returns the user {@link Language}.
	 *
	 * @return The user {@link Language}, never {@code null}.
	 */
	public Language getLanguage() {
		return language;
	}

	/**
	 * Tells if the current user is allowed to connect to Sigmah without the
	 * login cookie.
	 * Used only by the online mode.
	 *
	 * @return <code>true</code> if the current user is authorized to connect, <code>false</code> otherwise.
	 */
	public boolean isAuthorized() {
		return authorized;
	}

}
