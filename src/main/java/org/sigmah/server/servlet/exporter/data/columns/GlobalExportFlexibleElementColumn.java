package org.sigmah.server.servlet.exporter.data.columns;

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

import java.util.List;
import java.util.Map;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportStringCell;
import org.sigmah.server.servlet.exporter.utils.ExporterUtil;
import org.sigmah.server.servlet.exporter.utils.ValueLabel;
import org.sigmah.shared.command.result.ValueResult;

/**
 * Column containing the value of a flexible element.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class GlobalExportFlexibleElementColumn extends GlobalExportDataColumn {

	private final FlexibleElement flexibleElement;
	
	public GlobalExportFlexibleElementColumn(final FlexibleElement flexibleElement) {
		this.flexibleElement = flexibleElement;
	}

	public FlexibleElement getFlexibleElement() {
		return flexibleElement;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void export(final boolean firstLine, final List<ExportDataCell> titles, final List<ExportDataCell> values, final Map<String, List<ExportDataCell[]>> layoutGroupsData) {
		
		final ValueResult valueResult = ExporterUtil.getValueResult(flexibleElement, container, valueHandler);

		/* BUDGET */
		if (flexibleElement instanceof BudgetElement) {
			// Budget is a special case where the element corresponds to 3 columns.
			if (firstLine) {
				ExporterUtil.addBudgetTitles(titles, flexibleElement, i18nTranslator, language);
			}
			ExporterUtil.addBudgetValues(values, valueResult, flexibleElement, i18nTranslator, language);
		}
		/* CHOICE */
		else if (flexibleElement instanceof QuestionElement) {
			// Choice is a special case where the element corresponds to 2 columns and 1 additional tab.
			if (firstLine) {
				ExporterUtil.addChoiceTitles(titles, categories, flexibleElement, i18nTranslator, language);
			}
			ExporterUtil.addChoiceValues(values, valueResult, flexibleElement);
		}
		/* OTHERS */
		else {
			final ValueLabel pair = ExporterUtil.getPair(valueResult, flexibleElement, container, entityManager, i18nTranslator, language, null);

			// Titles
			if (firstLine) {
				titles.add(new ExportStringCell(pair != null ? pair.getFormattedLabel() : null));
			}

			// Values
			values.add(new ExportStringCell(pair != null ? pair.toValueString(): null));
		}
	}

}
