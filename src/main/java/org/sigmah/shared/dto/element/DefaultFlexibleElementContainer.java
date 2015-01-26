package org.sigmah.shared.dto.element;

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
