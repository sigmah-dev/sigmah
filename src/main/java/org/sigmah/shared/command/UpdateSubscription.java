package org.sigmah.shared.command;

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


import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * <p>
 * Updates the frequency with which a given report is mailed to a given user.
 * </p>
 * <p>
 * Normally, only the users themselves are permitted to change subscription preferences. However, the owner of a report
 * can "invite" other users to subscribe to their report, if the user has not already set a subscription preference.
 * </p>
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Deprecated
public class UpdateSubscription extends AbstractCommand<VoidResult> {

	private int reportTemplateId;
	private boolean subscribed;

	private Integer userId;

	public UpdateSubscription() {
		// Serialization.
	}

	public UpdateSubscription(int reportTemplateId, boolean subscribed) {
		this.reportTemplateId = reportTemplateId;
		this.subscribed = subscribed;
	}

	public int getReportTemplateId() {
		return reportTemplateId;
	}

	public void setReportTemplateId(int reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}
}
