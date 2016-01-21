package org.sigmah.shared.dto.referential;

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
