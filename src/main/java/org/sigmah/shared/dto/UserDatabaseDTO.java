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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.country.CountryDTO;

/**
 * One-to-one DTO of the {@link org.sigmah.server.domain.UserDatabase} domain object.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class UserDatabaseDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2946337272871273815L;

	private CountryDTO country;
	private List<PartnerDTO> partners = new ArrayList<PartnerDTO>(0);
	private List<ActivityDTO> activities = new ArrayList<ActivityDTO>(0);

	public UserDatabaseDTO() {
	}

	public UserDatabaseDTO(int id, String name) {
		setId(id);
		setName(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "UserDatabase";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", getName());
		builder.append("fullName", getFullName());
		builder.append("ownerName", getOwnerName());
		builder.append("ownerEmail", getOwnerEmail());
	}

	/**
	 * Searches this UserDatabase's list of Partners for the PartnerDTO with the given id.
	 * 
	 * @return the matching UserDatabaseDTO or null if no matches
	 */
	public PartnerDTO getPartnerById(int id) {
		for (PartnerDTO partner : getPartners()) {
			if (partner.getId().equals(id)) {
				return partner;
			}
		}
		return null;
	}

	/**
	 * @return this UserDatabase's id
	 */
	@Override
	public Integer getId() {
		return (Integer) get("id");
	}

	/**
	 * Sets this UserDatabase's id
	 */
	@Override
	public void setId(Integer id) {
		set("id", id);
	}

	/**
	 * @return the name of this UserDatabase
	 */
	public String getName() {
		return get("name");
	}

	/**
	 * Sets the name of this UserDatabase
	 */
	public void setName(String name) {
		set("name", name);
	}

	/**
	 * Sets the name of this UserDatabase's owner
	 * 
	 * @param ownerName
	 */
	public void setOwnerName(String ownerName) {
		set("ownerName", ownerName);
	}

	/**
	 * @return the name of this UserDatabase's owner
	 */
	public String getOwnerName() {
		return get("ownerName");
	}

	/**
	 * Sets the email of this UserDatabase's owner
	 */
	public void setOwnerEmail(String ownerEmail) {
		set("ownerEmail", ownerEmail);
	}

	/**
	 * @return the email of this UserDatabase's owner
	 */
	public String getOwnerEmail() {
		return get("ownerEmail");
	}

	/**
	 * Sets the full, descriptive name of this UserDatabase
	 */
	public void setFullName(String fullName) {
		set("fullName", fullName);
	}

	/**
	 * Gets the full, descriptive name of this UserDatabase
	 */
	public String getFullName() {
		return get("fullName");
	}

	/**
	 * @return this list of ActivityDTOs that belong to this UserDatabase
	 */
	public List<ActivityDTO> getActivities() {
		return activities;
	}

	/**
	 * @param activities
	 *          sets the list of Activities in this UserDatabase
	 */
	public void setActivities(List<ActivityDTO> activities) {
		this.activities = activities;
	}

	/**
	 * @return the Country in which this UserDatabase is set
	 */
	public CountryDTO getCountry() {
		return country;
	}

	/**
	 * Sets the Country to which this UserDatabase belongs
	 */
	public void setCountry(CountryDTO country) {
		this.country = country;
	}

	/**
	 * @return the list of Partners who belong to this UserDatabase
	 */
	public List<PartnerDTO> getPartners() {
		return partners;
	}

	/**
	 * Sets the list of Partners who belong to this UserDatabase
	 */
	public void setPartners(List<PartnerDTO> partners) {
		this.partners = partners;
	}

	/**
	 * @return the Partner of the UserDatabase to which the client belongs
	 */
	public PartnerDTO getMyPartner() {
		return getPartnerById(getMyPartnerId());
	}

	/**
	 * @return the id of the Partner to which the client belongs
	 */
	public int getMyPartnerId() {
		return (Integer) get("myPartnerId");
	}

	/**
	 * Sets the id of the Partner to which the current user belongs
	 */
	public void setMyPartnerId(int partnerId) {
		set("myPartnerId", partnerId);
	}

	/**
	 * @return true if the client owns this UserDatabase
	 */
	public boolean getAmOwner() {
		return (Boolean) get("amOwner");
	}

	/**
	 * Sets the flag to determine whether the current user is the owner of this database.
	 */
	public void setAmOwner(boolean value) {
		set("amOwner", value);
	}

}
