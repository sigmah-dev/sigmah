package org.sigmah.shared.dto.element;

import com.extjs.gxt.ui.client.widget.Component;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.ui.widget.form.StringField;
import org.sigmah.client.util.NumberUtils;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;

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

/**
 * BudgetRatioElementDTO.
 * 
 * @author Cihan Yagan (cihan.yagan@netapsys.fr)
 */
public class BudgetRatioElementDTO extends DefaultFlexibleElementDTO {

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "element.BudgetRatioElement";

	// DTO attributes keys.
	public static final String SPENT_BUDGET = "spentBudget";
	public static final String PLANNED_BUDGET = "plannedBudget";

	public BudgetRatioElementDTO() {
		// Default constructor.
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {
		
		final StringField field = new StringField();
		field.setFieldLabel(getLabel());
    
		preferredWidth = FlexibleElementDTO.NUMBER_FIELD_WIDTH;
		
		final FlexibleElementDTO plannedBudgetElement = getPlannedBudget();
		final FlexibleElementDTO spentBudgetElement = getSpentBudget();
		
		final BatchCommand batchCommand = new BatchCommand();
		
		batchCommand.add(new GetValue(currentContainerDTO.getId(), plannedBudgetElement.getId(), plannedBudgetElement.getEntityName()));
		batchCommand.add(new GetValue(currentContainerDTO.getId(), spentBudgetElement.getId(), spentBudgetElement.getEntityName()));
		
		dispatch.execute(batchCommand, new CommandResultHandler<ListResult<Result>>() {
			
			@Override
			protected void onCommandSuccess(ListResult<Result> results) {
				final ValueResult plannedBudgetResult = (ValueResult) results.getList().get(0);
				final ValueResult spentBudgetResult = (ValueResult) results.getList().get(1);
				final ComputedValue plannedValue = ComputedValues.from(plannedBudgetResult);
				final ComputedValue spentValue = ComputedValues.from(spentBudgetResult);
				
				if (plannedValue.get() == null || spentValue.get() == null) {
					field.setValue("0 %");
				} else {
					field.setValue(NumberUtils.ratioAsString(spentValue.get(), plannedValue.get()));
				}
			}
			
		}, field);
		
		// Sets the value of the field.
		if (valueResult != null && valueResult.isValueDefined()) {
			field.setValue(valueResult.getValueObject());
		}
        
		return field;
	}
	
	public void setPlannedBudget(FlexibleElementDTO plannedBudget) {
		set(PLANNED_BUDGET, plannedBudget);
	}

	public FlexibleElementDTO getPlannedBudget() {
		return get(PLANNED_BUDGET);
	}

	public void setSpentBudget(FlexibleElementDTO spentBudget) {
		set(SPENT_BUDGET, spentBudget);
	}

	public FlexibleElementDTO getSpentBudget() {
		return get(SPENT_BUDGET);
	}


}
