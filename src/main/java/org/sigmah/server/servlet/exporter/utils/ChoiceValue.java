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

import org.sigmah.server.domain.element.QuestionChoiceElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.util.ValueResultUtils;

/**
 * Store values of question elements.
 * 
 * @author sherzod
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ChoiceValue {
	
	private String valueLabels;
	private String valueIds;

	public ChoiceValue(final QuestionElement questionElement, final ValueResult valueResult) {
		if (valueResult != null && valueResult.isValueDefined()) {
			if (questionElement.getMultiple()) {
				final ExportConstants.MultiItemText item = ExporterUtil.formatMultipleChoices(questionElement.getChoices(), valueResult.getValueObject());
				valueLabels = item.text;
				String selectedChoicesIds = "";
				for (Integer id : ValueResultUtils.splitValuesAsInteger(valueResult.getValueObject())) {
					for (final QuestionChoiceElement choice : questionElement.getChoices()) {
						if (id.equals(choice.getId())) {
							if (choice.getCategoryElement() != null) {
								id = choice.getCategoryElement().getId();
							}
							break;
						}
					}
					selectedChoicesIds += id + ", ";
				}
				if (selectedChoicesIds.length() > 0) {
					valueIds = selectedChoicesIds.substring(0, selectedChoicesIds.length() - 2);
				}
			} else {
				final String idChoice = valueResult.getValueObject();
				for (final QuestionChoiceElement choice : questionElement.getChoices()) {
					if (idChoice.equals(String.valueOf(choice.getId()))) {
						if (choice.getCategoryElement() != null) {
							valueLabels = choice.getCategoryElement().getLabel();
							valueIds = String.valueOf(choice.getCategoryElement().getId());
						} else {
							valueLabels = choice.getLabel();
						}
						break;
					}
				}
			}
		}
	}

	public String getValueLabels() {
		return valueLabels;
	}

	public String getValueIds() {
		return valueIds;
	}
	
}
