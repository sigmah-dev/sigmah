package org.sigmah.shared.dto;

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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataDTO;

/**
 * Projection DTO for the {@link org.sigmah.server.domain.ReportSubscription ReportSubscription} domain class. A row in
 * a list of users who can be invited to view a report. Models a projection of <code>UserPermission</code>,
 * <code>ReportTemplate</code>, and <code>ReportProjection</code>.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class ReportSubscriptionDTO extends AbstractModelDataDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1579057643702812941L;

	public ReportSubscriptionDTO() {
		// Serialization.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("userId", getUserId());
		builder.append("userName", getUserName());
		builder.append("userEmail", getUserEmail());
		builder.append("subscribed", isSubscribed());
	}

	public void setUserId(int userId) {
		set("userId", userId);
	}

	public int getUserId() {
		return (Integer) get("userId");
	}

	public String getUserEmail() {
		return get("userEmail");
	}

	public void setUserEmail(String email) {
		set("userEmail", email);
	}

	public void setUserName(String name) {
		set("userName", name);
	}

	public String getUserName() {
		return get("userName");
	}

	public Boolean isSubscribed() {
		return get("subscribed");
	}

	public void setSubscribed(Boolean subscribed) {
		set("subscribed", subscribed);
	}

}
