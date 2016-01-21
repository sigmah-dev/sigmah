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
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.ReportFrequency;

/**
 * One-to-one DTO for the {@link org.sigmah.server.domain.ReportDefinition} domain class
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class ReportDefinitionDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8176612048620814942L;

	/**
	 * Dummy reference to assure that GWT includes ReportFrequency is included in the list of classes to serialize.
	 */
	@SuppressWarnings("unused")
	private ReportFrequency freq_;

	public ReportDefinitionDTO() {
		setFrequency(ReportFrequency.NotDateBound);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "ReportDefinition";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("title", getTitle());
		builder.append("frequency", getFrequency());
		builder.append("ownerName", getOwnerName());
	}

	/**
	 * @return this ReportDefinition's id
	 */
	@Override
	public Integer getId() {
		return (Integer) get("id");
	}

	/**
	 * Sets the id of this ReportDefinition
	 */
	public void setId(Integer id) {
		set("id", id);
	}

	/**
	 * Sets the title of this ReportDefinition
	 */
	public void setTitle(String title) {
		set("title", title);
	}

	/**
	 * @return the title of this ReportDefinition
	 */
	public String getTitle() {
		return get("title");
	}

	/**
	 * Sets the description of this ReportDefinition
	 */
	public void setDescription(String description) {
		set("description", description);
	}

	/**
	 * @return the description of this ReportDefinition
	 */
	public String getDescription() {
		return get("description");
	}

	/**
	 * Sets the name of the UserDatabase which this ReportDefinition references. If this ReportDefinition references
	 * multiple UserDatabases, name should be null.
	 */
	public void setDatabaseName(String name) {
		set("databaseName", name);
	}

	/**
	 * @return true if the current user permission to edit this report definition
	 */
	public boolean isEditAllowed() {
		return (Boolean) get("editAllowed");
	}

	/**
	 * Sets the permission of the current user to edit this report definition
	 */
	public void setEditAllowed(boolean allowed) {
		set("editAllowed", allowed);
	}

	/**
	 * @return the name of the User who owns this ReportDefinition
	 */
	public String getOwnerName() {
		return get("ownerName");
	}

	/**
	 * Sets the name of the User who own this ReportDefinition
	 */
	public void setOwnerName(String name) {
		set("ownerName", name);
	}

	/**
	 * Sets whether the current user is the owner of this ReportDefintion
	 */
	public void setAmOwner(boolean amOwner) {
		set("amOwner", amOwner);
	}

	/**
	 * @return true if the current user is the owner of this ReportDefinition
	 */
	public boolean getAmOwner() {
		return (Boolean) get("amOwner");
	}

	/**
	 * @return the ReportFrequency of this ReportDefinition
	 */
	public ReportFrequency getFrequency() {
		return get("frequency");
	}

	/**
	 * Sets the ReportFrequency of this ReportDefinition
	 */
	public void setFrequency(ReportFrequency frequency) {
		set("frequency", frequency);
	}

	/**
	 * @return the day of the month [1, 31] on which this ReportDefinition is to be published
	 */
	public Integer getDay() {
		return get("day");
	}

	/**
	 * Sets the day of the month on which this ReportDefinition is to be published.
	 */
	public void setDay(Integer day) {
		set("day", day);
	}

	/**
	 * See {@link org.sigmah.server.domain.ReportSubscription#isSubscribed()}
	 *
	 * @return true if the current user is subscribed to this ReportDefinition
	 */
	public boolean isSubscribed() {
		return (Boolean) get("subscribed");
	}

	/**
	 * Sets whether the current user is subscribed to this ReportDefinition. See
	 * {@link org.sigmah.server.domain.ReportSubscription#setSubscribed(boolean)}
	 */
	public void setSubscribed(boolean subscribed) {
		set("subscribed", subscribed);
	}
}
