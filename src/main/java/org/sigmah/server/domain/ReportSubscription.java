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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Report Subscription domain entity.
 * </p>
 * <p>
 * Defines a subscription to a given report.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.REPORT_SUBSCRIPTION_TABLE)
public class ReportSubscription extends AbstractEntityId<ReportSubscriptionId> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1051330230806968889L;

	@EmbeddedId
	private ReportSubscriptionId id;

	/**
	 * The subscription status to <code>report</code>.<br/>
	 * {@code true} if the user is subscribed to the <code>report</code>.
	 */
	@Column(name = EntityConstants.REPORT_SUBSCRIPTION_COLUMN_SUBSCRIBED, nullable = false)
	@NotNull
	private boolean subscribed;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * The ReportTemplate to which the user is subscribed.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.REPORT_DEFINITION_COLUMN_ID, nullable = false, insertable = false, updatable = false)
	@NotNull
	private ReportDefinition template;

	/**
	 * The user who will receive the report by mail.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.USER_COLUMN_ID, nullable = false, insertable = false, updatable = false)
	@NotNull
	private User user;

	/**
	 * The second user who has invited {@code user} to subscribe to this report.<br/>
	 * {@code null} if the user has set its own preferences.
	 */
	// FIXME Online documentation describes this column as NOT nullable.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.REPORT_SUBSCRIPTION_COLUMN_INVITING_USER_ID, nullable = true)
	private User invitingUser;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public ReportSubscription() {
	}

	public ReportSubscription(final ReportDefinition template, final User user) {
		this.id = new ReportSubscriptionId(template.getId(), user.getId());
		this.template = template;
		this.user = user;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("subscribed", subscribed);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public ReportSubscriptionId getId() {
		return this.id;
	}

	@Override
	public void setId(ReportSubscriptionId id) {
		this.id = id;
	}

	public ReportDefinition getTemplate() {
		return this.template;
	}

	public void setTemplate(ReportDefinition template) {
		this.template = template;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getInvitingUser() {
		return invitingUser;
	}

	public void setInvitingUser(User invitingUser) {
		this.invitingUser = invitingUser;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}
}
