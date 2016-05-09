package org.sigmah.shared.dto.element;

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

import com.extjs.gxt.ui.client.data.ModelData;

import java.util.Date;
import java.util.Set;

import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;

public interface DefaultContactFlexibleElementContainer extends FlexibleElementContainer, ModelData, EntityDTO<Integer> {

	String getFamilyName();

	String getFirstName();

	String getOrganizationName();

	Integer getMainOrgUnitId();

	Set<Integer> getSecondaryOrgUnitIds();

	Date getCreationDate();

	String getLogin();

	String getEmailAddress();

	String getPhoneNumber();

	String getPostalAddress();

	String getPhotoUrl();
}
