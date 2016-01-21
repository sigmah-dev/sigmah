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

import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;

/**
 * Defines a DTO class that contains default flexible elements.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface DefaultFlexibleElementContainer extends FlexibleElementContainer, ModelData {

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#CODE}.
	 */
	String getName();

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#TITLE}.
	 */
	String getFullName();

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#START_DATE}.
	 */
	Date getStartDate();

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#END_DATE}.
	 */
	Date getEndDate();

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#BUDGET}.
	 */
	Double getPlannedBudget();

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#BUDGET}.
	 */
	Double getSpendBudget();

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#BUDGET}.
	 */
	Double getReceivedBudget();

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#COUNTRY}.
	 */
	CountryDTO getCountry();

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#OWNER}.
	 */
	String getOwnerFirstName();

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#OWNER}.
	 */
	String getOwnerName();

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#MANAGER}.
	 */
	UserDTO getManager();

	/**
	 * @return the property for the default type {@link DefaultFlexibleElementType#ORG_UNIT}.
	 */
	Integer getOrgUnitId();

}
