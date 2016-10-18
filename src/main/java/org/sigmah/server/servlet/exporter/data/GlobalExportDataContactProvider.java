package org.sigmah.server.servlet.exporter.data;

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
 * License along with t his program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.impl.GlobalExportHibernateDAO;
import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.domain.export.GlobalContactExport;
import org.sigmah.server.domain.export.GlobalContactExportContent;
import org.sigmah.server.handler.GetLayoutGroupIterationsHandler;
import org.sigmah.server.handler.GetValueHandler;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.data.cells.ExportDataCell;
import org.sigmah.server.servlet.exporter.data.cells.ExportStringCell;
import org.sigmah.server.servlet.exporter.data.columns.GlobalExportDataColumn;
import org.sigmah.server.servlet.exporter.utils.CsvBuilder;
import org.sigmah.server.servlet.exporter.utils.CsvParser;
import org.sigmah.server.servlet.exporter.utils.ExporterUtil;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class Provides global export data
 * 
 * @author sherzod (v1.3)
 */
@Singleton
public class GlobalExportDataContactProvider extends GlobalExportDataProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExportDataProvider.class);

	private final Injector injector;
	private final CsvBuilder csvBuilder;
	private final CsvParser csvParser;

	@Inject
	public GlobalExportDataContactProvider(final Injector injector) {
		super();
		this.injector = injector;
		this.csvBuilder = new CsvBuilder();
		this.csvParser = new CsvParser();
	}

	public Map<String, List<ExportDataCell[]>> generateGlobalExportData(final Integer organizationId, EntityManager entityManager, final I18nServer i18nTranslator,
																																			final Language language, final ServletExecutionContext context) throws Exception {
		if (entityManager == null) {
			entityManager = injector.getInstance(EntityManager.class);
		}

		final GlobalExportDAO exportDAO = injector.getInstance(GlobalExportHibernateDAO.class);
		final List<ContactModel> cModels = exportDAO.getContactModels();

		final ContactDAO contactDao = injector.getInstance(ContactDAO.class);
		final List<Contact> contacts = contactDao.getContacts(cModels);

		// contact model and its contacts
		final Map<String, List<Contact>> pModelContactsMap = new HashMap<String, List<Contact>>();
		for (final Contact contact : contacts) {
			if (contact.getDateDeleted() == null) {
				final String pModelName = contact.getContactModel().getName();

				List<Contact> pModelContacts = pModelContactsMap.get(pModelName);
				if (pModelContacts == null) {
					pModelContacts = new ArrayList<Contact>();
					pModelContactsMap.put(pModelName, pModelContacts);
				}
				pModelContacts.add(contact);
			}
		}

		// contact model and its globally exportable fields
		final Map<String, List<GlobalExportDataColumn>> pModelElementsMap = new TreeMap<String, List<GlobalExportDataColumn>>();
		for (final ContactModel contactModel : cModels) {
			if (contactModel.getStatus() != ProjectModelStatus.DRAFT) {
				final String pModelName = contactModel.getName();

				final List<GlobalExportDataColumn> pModelElements = new ArrayList<GlobalExportDataColumn>();
				pModelElementsMap.put(pModelName, pModelElements);

				// detail elements
				ExporterUtil.fillElementList(pModelElements, contactModel.getDetails().getLayout());
			}
		}

		final CommandHandler<GetValue, ValueResult> handler = injector.getInstance(GetValueHandler.class);
		final CommandHandler<GetLayoutGroupIterations, ListResult<LayoutGroupIterationDTO>> iterationsHandler = injector.getInstance(GetLayoutGroupIterationsHandler.class);

		final Map<String, List<ExportDataCell[]>> pModelExportDataMap = new LinkedHashMap<String, List<ExportDataCell[]>>();

		// categories
		final Set<CategoryType> categories = new HashSet<>();

		// collect export data
		for (final String pModelName : pModelElementsMap.keySet()) {

			// if no project for a given project model, skip even headers for
			// flexible elements
			if (pModelContactsMap.get(pModelName) == null)
				continue;

			final List<GlobalExportDataColumn> elements = pModelElementsMap.get(pModelName);
			final List<ExportDataCell[]> exportData = new ArrayList<ExportDataCell[]>();
			pModelExportDataMap.put(pModelName, exportData);

			// field titles
			final List<ExportDataCell> titles = new ArrayList<ExportDataCell>();

			// layout group tabs
			final Map<String, List<ExportDataCell[]>> layoutGroupsData = new LinkedHashMap<>();

			// special fields for BI
			titles.add(new ExportStringCell(i18nTranslator.t(language, "contactPermanentId")));

			boolean isFirstLine = true;
			// projects
			for (final Contact contact : pModelContactsMap.get(pModelName)) {

				final List<ExportDataCell> values = new ArrayList<ExportDataCell>();

				// special fields for BI
				values.add(new ExportStringCell(String.valueOf(contact.getId())));

				// fields
				for (final GlobalExportDataColumn column : elements) {
					
					// Defining the global objects used in the export.
					column.setContainer(contact);
					column.setModelName(pModelName);
					column.setCategories(categories);
					column.setValueHandler(handler);
					column.setIterationsHandler(iterationsHandler);
					column.setI18nTranslator(i18nTranslator);
					column.setLanguage(language);
					column.setEntityManager(entityManager);
					
					column.setInitialColumns("contactPermanentId", "contactFullName", "iterationName");
					
					column.export(isFirstLine, titles, values, layoutGroupsData);
				}

				// add titles
				if (isFirstLine) {
					exportData.add(titles.toArray(new ExportDataCell[titles.size()]));
					isFirstLine = false;
				}

				// add values
				exportData.add(values.toArray(new ExportDataCell[values.size()]));

				// add iterative layout groups tabs
				for(String groupName : layoutGroupsData.keySet()) {
					pModelExportDataMap.put(groupName, layoutGroupsData.get(groupName));
				}

			}// projects

		}

		addCategories(categories, pModelExportDataMap, i18nTranslator, language);

		return pModelExportDataMap;
	}

	public void persistGlobalExportDataAsCsv(final GlobalContactExport globalContactExport, EntityManager em, Map<String, List<ExportDataCell[]>> exportData) throws Exception {
		for (final String pModelName : exportData.keySet()) {
			final GlobalContactExportContent content = new GlobalContactExportContent();
			content.setGlobalContactExport(globalContactExport);
			content.setContactModelName(pModelName);
			content.setCsvContent(csvBuilder.buildCsv(exportData.get(pModelName)));
			em.persist(content);
		}
	}

	public Map<String, List<ExportDataCell[]>> getBackedupGlobalExportData(EntityManager em, Integer gExportId) {
		final Map<String, List<ExportDataCell[]>> exportData = new TreeMap<String, List<ExportDataCell[]>>();
		final GlobalContactExport export = em.find(GlobalContactExport.class, gExportId);
		final List<GlobalContactExportContent> contents = export.getContents();
		for (final GlobalContactExportContent content : contents) {
			final List<String[]> csvData = csvParser.parseCsv(content.getCsvContent());
			exportData.put(content.getContactModelName(), CSVDataToGlobalExportData(csvData));
		}
		return exportData;
	}

	private List<ExportDataCell[]> CSVDataToGlobalExportData(List<String[]> csvData) {
		List<ExportDataCell[]> globalExportData = new ArrayList<>();

		for(String[] line : csvData) {
			ExportDataCell[] convertedLine = new ExportDataCell[line.length];

			for(int i = 0; i < line.length; i++) {
				convertedLine[i] = new ExportStringCell(line[i]);
			}

			globalExportData.add(convertedLine);
		}

		return globalExportData;
	}

}
