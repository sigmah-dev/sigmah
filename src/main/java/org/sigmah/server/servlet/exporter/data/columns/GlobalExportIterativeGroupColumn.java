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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportLinkCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportStringCell;
import org.sigmah.server.servlet.exporter.utils.ChoiceValue;
import org.sigmah.server.servlet.exporter.utils.ExporterUtil;
import org.sigmah.server.servlet.exporter.utils.ValueLabel;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Column containing the values of an iteration.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class GlobalExportIterativeGroupColumn extends GlobalExportDataColumn {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExportIterativeGroupColumn.class);

	private final LayoutGroup layoutGroup;

	public GlobalExportIterativeGroupColumn(final LayoutGroup layoutGroup) {
		this.layoutGroup = layoutGroup;
	}

	public LayoutGroup getLayoutGroup() {
		return layoutGroup;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void export(final boolean firstLine, final List<ExportDataCell> titles, final List<ExportDataCell> values, final Map<String, List<ExportDataCell[]>> layoutGroupsData) {
		
		final List<LayoutConstraint> allConstraints = layoutGroup.getConstraints();
		final List<LayoutConstraint> constraints = new ArrayList<>();

		// Keeping only exportable constraints.
		for (final LayoutConstraint constraint : allConstraints) {
			if (constraint.getElement().isGloballyExportable()) {
				constraints.add(constraint);
			}
		}

		if (constraints.isEmpty()) {
			return;
		}
		
		// Command to get element value
		final String groupName = modelName + "_" + layoutGroup.getTitle();
		final GetLayoutGroupIterations command = new GetLayoutGroupIterations(layoutGroup.getId(), container.getId(), -1);

		try {

			if (firstLine) {
				exportFirstLine(titles, constraints, layoutGroupsData, groupName, layoutGroup.getIterationType());
			}

			final ListResult<LayoutGroupIterationDTO> iterationsResult = iterationsHandler.execute(command, null);

			values.add(new ExportLinkCell(String.valueOf(iterationsResult.getSize()), groupName));

			// Iterative layout group values.
			final List<ExportDataCell[]> groupValues = layoutGroupsData.get(groupName);
			for (final LayoutGroupIterationDTO iteration : iterationsResult.getList()) {
				final List<ExportDataCell> columns = new ArrayList<>();

				// Default columns.
				addDefaultColumns(columns, iteration);

				for (final LayoutConstraint constraint : constraints) {
					final FlexibleElement element = constraint.getElement();
					try {
						final ValueResult iterationValueResult = ExporterUtil.getValueResult(element, iteration.getId(), container, valueHandler);

						/* CHOICE */
						if (element instanceof QuestionElement) {
							// Choice is a special case where the element corresponds to 2 columns and 1 additional tab
							final ChoiceValue choiceValue = new ChoiceValue((QuestionElement) element, iterationValueResult);

							columns.add(new ExportStringCell(choiceValue.getValueLabels()));
							if (((QuestionElement)element).getCategoryType() != null) {
								columns.add(new ExportStringCell(choiceValue.getValueIds()));
							}
						}
						/* OTHERS */
						else {
							final ValueLabel pair = ExporterUtil.getPair(iterationValueResult, element, constraint, entityManager, i18nTranslator, language, null);
							columns.add(new ExportStringCell(pair != null ? pair.toValueString(): null));
						}
					} catch (Exception e) {
						LOGGER.warn("No value found for the element #" + element.getId() + " (" + element.getLabel() + ")", e);
						
						// No value found in database : empty cells.
						columns.add(new ExportStringCell(""));
						if (element instanceof QuestionElement) {
							columns.add(new ExportStringCell(""));
						}
					}
				}

				groupValues.add(columns.toArray(new ExportDataCell[columns.size()]));
			}

		} catch (Exception e) {
			LOGGER.error("Failed to get iterations of group '" + layoutGroup.getId() + "' of project '" + container.getId() + "'.", e);
		}
	}

	/**
	 * Adds the default columns to the column list based on the container type.
	 * 
	 * @param columns
	 *			List of columns.
	 * @param iteration 
	 *			Current iteration.
	 */
	private void addDefaultColumns(final List<ExportDataCell> columns, final LayoutGroupIterationDTO iteration) {
		
		columns.add(new ExportStringCell(String.valueOf(container.getId())));
		
		/* PROJECT */
		if (container instanceof Project) {
			final Project project = (Project) container;
			columns.add(new ExportStringCell(project.getName()));
			columns.add(new ExportStringCell(project.getFullName()));
		}
		/* CONTACT */
		else if (container instanceof Contact) {
			final Contact contact = (Contact) container;
			columns.add(new ExportStringCell(contact.getFullName()));
		}
		else {
			throw new UnsupportedOperationException("Container type not supported: " + container.getClass().getName());
		}
		
		columns.add(new ExportStringCell(iteration.getName()));
	}

	/**
	 * Add the titles and creates the columns.
	 * 
	 * @param titles
	 *			List of titles.
	 * @param constraints
	 *			Layout constraints to export.
	 * @param layoutGroupsData
	 *			Map associating each group with its values
	 * @param groupName 
	 *			Name of the current group.
	 */
	private void exportFirstLine(final List<ExportDataCell> titles, final List<LayoutConstraint> constraints, final Map<String, List<ExportDataCell[]>> layoutGroupsData, final String groupName, final String groupIterationType) {
		
		titles.add(new ExportStringCell(layoutGroup.getTitle()));
		
		// Iterative layout group columns titles.
		final List<ExportDataCell[]> groupTitles = new ArrayList<>();
		final List<ExportDataCell> columns = new ArrayList<>();
		
		for (final String initialColumn : initialColumns) {
			if ("iterationName".equals(initialColumn) && groupIterationType != null) {
				columns.add(new ExportStringCell(i18nTranslator.t(language, "iterationNameMessage", groupIterationType)));
			} else {
				columns.add(new ExportStringCell(i18nTranslator.t(language, initialColumn)));
			}
		}
		
		for (final LayoutConstraint constraint : constraints) {
			final FlexibleElement element = constraint.getElement();
			if (element instanceof QuestionElement) {
				// choice is a special case where the element corresponds to 2 columns and 1 additional tab
				final QuestionElement questionElement = (QuestionElement) element;
				String choiceLabel = element.getLabel();
				
				columns.add(new ExportStringCell(choiceLabel));
				if (questionElement.getCategoryType() != null) {
					columns.add(new ExportStringCell(choiceLabel + " (" + questionElement.getCategoryType().getLabel() + ") " + i18nTranslator.t(language, "categoryId")));
					categories.add(((QuestionElement) element).getCategoryType());
				}
			} else {
				columns.add(new ExportStringCell(element.getLabel()));
			}
		}
		groupTitles.add(columns.toArray(new ExportDataCell[columns.size()]));
		layoutGroupsData.put(groupName, groupTitles);
	}
  
}
