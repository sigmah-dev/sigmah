package org.sigmah.server.servlet.exporter.utils;

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

import java.util.Map;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.util.ValueResultUtils;

/**
 * Store values of budget elements.
 * 
 * @author sherzod
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class BudgetValues {
	
	private final Double spent;
	private final Double planned;

	public BudgetValues(final BudgetElement budgetElement, final ValueResult valueResult) {
		boolean hasValue = valueResult != null && valueResult.isValueDefined();
		Double plannedBudget = 0d;
		Double spentBudget = 0d;
		if (hasValue) {
			final Map<Integer, String> val = ValueResultUtils.splitMapElements(valueResult.getValueObject());
			if (budgetElement.getRatioDividend() != null) {
				if (val.get(budgetElement.getRatioDividend().getId()) != null) {
					spentBudget = Double.valueOf(val.get(budgetElement.getRatioDividend().getId()));
				}
			}
			if (budgetElement.getRatioDivisor() != null) {
				if (val.get(budgetElement.getRatioDivisor().getId()) != null) {
					plannedBudget = Double.valueOf(val.get(budgetElement.getRatioDivisor().getId()));
				}
			}
		}
		spent = spentBudget;
		planned = plannedBudget;
	}

	public Double getSpent() {
		return spent;
	}

	public Double getPlanned() {
		return planned;
	}

	public Double getRatio() {
		return spent / planned;
	}
	
}
