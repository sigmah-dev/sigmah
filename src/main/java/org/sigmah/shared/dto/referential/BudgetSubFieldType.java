package org.sigmah.shared.dto.referential;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.core.client.GWT;

/**
 * The possible types of budget sub fields
 * 
 * @author Jérémie BRIAND (jbriand@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum BudgetSubFieldType implements Result {

	PLANNED,
	SPENT,
	RECEIVED;

	/**
	 * <p>
	 * Returns the given {@code BudgetSubFieldType} corresponding name.
	 * </p>
	 * <p>
	 * If this method is executed from server-side, it returns the given {@code budgetSubFieldType} constant name.
	 * </p>
	 * 
	 * @param budgetSubFieldType
	 *          The budget sub-field type.
	 * @return the given {@code budgetSubFieldType} corresponding name, or {@code null}.
	 */
	public static String getName(final BudgetSubFieldType budgetSubFieldType) {

		if (budgetSubFieldType == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return budgetSubFieldType.name();
		}

		switch (budgetSubFieldType) {
			case PLANNED:
				return I18N.CONSTANTS.projectPlannedBudget();
			case RECEIVED:
				return I18N.CONSTANTS.projectReceivedBudget();
			case SPENT:
				return I18N.CONSTANTS.projectSpendBudget();
			default:
				return budgetSubFieldType.name();
		}
	}

}
