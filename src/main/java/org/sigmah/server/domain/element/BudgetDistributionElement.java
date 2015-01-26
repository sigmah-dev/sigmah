package org.sigmah.server.domain.element;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Budget distribution element domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.BUDGET_DISTRIBUTION_ELEMENT_TABLE)
public class BudgetDistributionElement extends FlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 7802749241559520698L;

}
