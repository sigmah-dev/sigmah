package org.sigmah.server.servlet.exporter.template;

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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Injector;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.server.domain.element.ContactListElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.handler.GetLayoutGroupIterationsHandler;
import org.sigmah.server.handler.GetValueHandler;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.data.BaseSynthesisData;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportLinkCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportStringCell;
import org.sigmah.server.servlet.exporter.utils.CalcUtils;
import org.sigmah.server.servlet.exporter.utils.ExportConstants;
import org.sigmah.server.servlet.exporter.utils.ExporterUtil;
import org.sigmah.server.servlet.exporter.utils.ValueLabel;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;

/**
 * Base calc template for project/orgunit/contact calc templates
 * 
 * @author sherzod (v1.3)
 */
public class BaseSynthesisCalcTemplate implements ExportTemplate {

	private final Table table;
	private final SpreadsheetDocument doc;
	private final BaseSynthesisData data;
	private final Class<?> clazz;

	private Row row;

	private final ServletExecutionContext context;
	private Injector injector;

	public BaseSynthesisCalcTemplate(final BaseSynthesisData data, final SpreadsheetDocument doc, final Class<?> clazz, final ServletExecutionContext context, final I18nServer i18nTranslator, final Language language, final Injector injector) throws Throwable {
		this.context = context;
		this.data = data;
		this.doc = doc;
		table = doc.getSheetByIndex(0);
		this.clazz = clazz;
		this.injector = injector;

		String title = data.getLocalizedVersion("projectSynthesis");
		if (clazz.equals(OrgUnit.class))
			title = data.getLocalizedVersion("orgUnitSynthesis");
		if (clazz.equals(Contact.class))
			title = data.getLocalizedVersion("contactSynthesis");

		table.setTableName(title.replace(" ", "_"));
		int rowIndex = -1;

		// skip row
		++rowIndex;

		// title
		CalcUtils.putMainTitle(table, ++rowIndex, data.getNumbOfCols(), title.toUpperCase());

		// emptry row
		CalcUtils.putEmptyRow(table, ++rowIndex);

		// column headers
		row = table.getRowByIndex(++rowIndex);
		CalcUtils.putHeader(row, 2, data.getLocalizedVersion("adminFlexibleName"));
		CalcUtils.putHeader(row, 3, data.getLocalizedVersion("value"));
		row.setHeight(5, false);

		// empty row
		row = table.getRowByIndex(++rowIndex);
		row.setHeight(3.8, false);
		row.getCellByIndex(1).setCellStyleName(null);
		row.getCellByIndex(2).setCellStyleName(null);
		row.getCellByIndex(3).setCellStyleName(null);

		// details
		row = table.getRowByIndex(++rowIndex);
		CalcUtils.putHeader(row, 1, data.getLocalizedVersion("projectDetails"));

		// Project synthesis
		if (clazz.equals(Project.class)) {
			rowIndex = putLayout(table, data.getProject().getProjectModel().getProjectDetails().getLayout(), rowIndex, i18nTranslator, language);

			// empty row
			row = table.getRowByIndex(++rowIndex);
			row.setHeight(3.8, false);
			row.getCellByIndex(1).setCellStyleName(null);
			row.getCellByIndex(2).setCellStyleName(null);
			row.getCellByIndex(3).setCellStyleName(null);

			// run through project phases to get synthesis data
			for (final PhaseModel phaseModel : data.getProject().getProjectModel().getPhaseModels()) {

				// phase name
				row = table.getRowByIndex(++rowIndex);
				CalcUtils.putHeader(row, 1, phaseModel.getName());
				rowIndex = putLayout(table, phaseModel.getLayout(), rowIndex, i18nTranslator, language);

			}
		} else if (clazz.equals(OrgUnit.class)) {
			// Org Unit synthesis
			rowIndex = putLayout(table, data.getOrgUnit().getOrgUnitModel().getDetails().getLayout(), rowIndex, i18nTranslator, language);
		} else {
			// Contact synthesis
			rowIndex = putLayout(table, data.getContact().getContactModel().getDetails().getLayout(), rowIndex, i18nTranslator, language);
		}

		table.getColumnByIndex(0).setWidth(3.8);
		table.getColumnByIndex(1).setWidth(49);
		table.getColumnByIndex(2).setWidth(115);
		table.getColumnByIndex(3).setWidth(115);
	}

	private int putLayout(final Table table, final Layout layout, int rowIndex, final I18nServer i18nTranslator, final Language language) throws Throwable {

		EntityManager entityManager = injector.getInstance(EntityManager.class);

		int typeStartRow = rowIndex;
		boolean firstGroup = true;
		
		final EntityId<Integer> container = data.getContainerWithClass(clazz);
		
		// layout groups for each phase
		for (final LayoutGroup layoutGroup : layout.getGroups()) {

			// layout group cell
			if(layoutGroup.getHasIterations()) {

				if(createIterativeGroupSheet(layoutGroup, container, i18nTranslator, language, entityManager)) {
					if (!firstGroup) {
						row = table.getRowByIndex(++rowIndex);
					}
					firstGroup = false;

					CalcUtils.putGroupCell(table, 2, rowIndex, layoutGroup.getTitle());
					CalcUtils.applyLink(table.getCellByPosition(3, rowIndex), data.getLocalizedVersion("seeIterationDetails"), ExportConstants.GROUP_ITERATIONS_SHEET_PREFIX + layoutGroup.getTitle());
				}

				continue;
			}

			if (!firstGroup) {
				row = table.getRowByIndex(++rowIndex);
			}
			firstGroup = false;

			CalcUtils.putGroupCell(table, 2, rowIndex, layoutGroup.getTitle());
			CalcUtils.mergeCell(table, 2, rowIndex, data.getNumbOfCols(), rowIndex);

			// elements for each layout group
			for (final LayoutConstraint constraint : layoutGroup.getConstraints()) {
				final FlexibleElement element = constraint.getElement();

				// skip if element is not exportable
				if (!element.isExportable()) {
					continue;
				}
				
				/* CONTACT LIST */
				if (data.isWithContacts() && element instanceof ContactListElement) {
					final ValueResult valueResult = ExporterUtil.getValueResult(element, container, data.getHandler());
					CalcUtils.createBasicCell(table, 2, rowIndex, element.getLabel());
					CalcUtils.applyLink(table.getCellByPosition(3, rowIndex), String.valueOf(ExporterUtil.getContactListCount(valueResult)), ExportConstants.CONTACT_SHEET_PREFIX + element.getLabel());
				}
				/* OTHERS */
				else {
					final ValueLabel pair = ExporterUtil.getPair(element, container, data.getEntityManager(), data.getHandler(), i18nTranslator, language, data);
					
					if (pair != null) {
						putElement(table, ++rowIndex, pair, pair.isMessage());
					}
				}

			}// elements
		}
		CalcUtils.mergeCell(table, 1, typeStartRow, 1, rowIndex);

		return rowIndex;

	}

	/**
	 * returns true if sheet was really created (if iterative group contains exportable fields).
	 */
	private boolean createIterativeGroupSheet(final LayoutGroup group, final EntityId<Integer> container, final I18nServer i18nTranslator, final Language language, final EntityManager entityManager) throws Throwable {

		List<LayoutConstraint> allConstraints = group.getConstraints();
		List<LayoutConstraint> constraints = new ArrayList<>();

		// keeping only exportable constraints
		for (LayoutConstraint constraint : allConstraints) {
			if (constraint.getElement().isExportable()) {
				constraints.add(constraint);
			}
		}

		if (constraints.isEmpty()) {
			return false;
		}
		final Table sheet = doc.appendSheet(CalcUtils.normalizeAsLink(ExportConstants.GROUP_ITERATIONS_SHEET_PREFIX + group.getTitle()));

		final GetLayoutGroupIterations command = new GetLayoutGroupIterations(group.getId(), container.getId(), -1);

		// headers
		List<String> headers = new ArrayList<>();

		headers.add(i18nTranslator.t(language, "iterationName"));

		for (LayoutConstraint constraint : constraints) {
			FlexibleElement element = constraint.getElement();
			headers.add(element.getLabel());
		}

		putHeaders(sheet, headers.toArray(new String[headers.size()]));

		final CommandHandler<GetLayoutGroupIterations, ListResult<LayoutGroupIterationDTO>> iterationsHandler = injector.getInstance(GetLayoutGroupIterationsHandler.class);
		final CommandHandler<GetValue, ValueResult> handler = injector.getInstance(GetValueHandler.class);

		try {
			final ListResult<LayoutGroupIterationDTO> iterationsResult = iterationsHandler.execute(command, new UserExecutionContext(context));

			int rowIndex = 1;

			for (final LayoutGroupIterationDTO iteration : iterationsResult.getList()) {
				final List<ExportDataCell> values = new ArrayList<>();
				values.add(new ExportStringCell(iteration.getName()));
				values.addAll(ExporterUtil.getCellsForIteration(iteration, constraints, container, entityManager, i18nTranslator, language, data));
				
				putLine(sheet, rowIndex++, values.toArray(new ExportDataCell[values.size()]));
			}

		} catch(Exception e) {
			// TODO : message d'erreur récupération de l'iteration
		}

		return true;
	}

	private void putHeaders(Table sheet, String[] values) {

		row = sheet.getRowByIndex(0);

		for (int i = 0; i < values.length; i++) {
			CalcUtils.putHeader(row, i, values[i]);
		}
	}

	private void putLine(Table sheet, int rowIndex, ExportDataCell[] values) throws Throwable {

		row = sheet.getRowByIndex(rowIndex);

		for (int i = 0; i < values.length; i++) {
			ExportDataCell cell = values[i];
			if (cell instanceof ExportStringCell) {
				CalcUtils.createBasicCell(sheet, i, rowIndex, cell.toCSVString());
			} else {
				ExportLinkCell link = (ExportLinkCell)cell;
				CalcUtils.applyLink(sheet.getCellByPosition(i, rowIndex), link.getText(), link.getTarget());
			}
		}
	}

	private void putElement(Table table, int rowIndex, ValueLabel pair, boolean isMessage) {
		row = table.getRowByIndex(rowIndex);
		row.getCellByIndex(2).setCellStyleName(null);
		row.getCellByIndex(3).setCellStyleName(null);
		CalcUtils.createBasicCell(table, 2, rowIndex, pair.getFormattedLabel());
		CalcUtils.createBasicCell(table, 3, rowIndex, pair.getValue());
		if (isMessage)
			row.getCellByIndex(3).setFont(CalcUtils.getFont(11, false, true));
	}

	@Override
	public void write(OutputStream output) throws Throwable {
		doc.save(output);
		doc.close();
	}

}
