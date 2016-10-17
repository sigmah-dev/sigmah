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
 * License along with this program.  If not, see
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
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.impl.GlobalExportHibernateDAO;
import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.domain.export.GlobalExport;
import org.sigmah.server.domain.export.GlobalExportContent;
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
public class GlobalExportDataProjectProvider extends GlobalExportDataProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExportDataProvider.class);

	private final Injector injector;
	private final CsvBuilder csvBuilder;
	private final CsvParser csvParser;

	@Inject
	public GlobalExportDataProjectProvider(final Injector injector) {
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
		final Organization organization = entityManager.find(Organization.class, organizationId);
		final List<ProjectModel> pModels = exportDAO.getProjectModelsByOrganization(organization);

		final ProjectDAO projectDao = injector.getInstance(ProjectDAO.class);
		final List<Project> projects = projectDao.getProjects(pModels);

		// project model and its projects
		final Map<String, List<Project>> pModelProjectsMap = new HashMap<String, List<Project>>();
		for (final Project project : projects) {
			if (project.getDateDeleted() == null) {
				final String pModelName = project.getProjectModel().getName();

				List<Project> pModelProjects = pModelProjectsMap.get(pModelName);
				if (pModelProjects == null) {
					pModelProjects = new ArrayList<Project>();
					pModelProjectsMap.put(pModelName, pModelProjects);
				}
				pModelProjects.add(project);
			}
		}

		// project model and its globally exportable fields
		final Map<String, List<GlobalExportDataColumn>> pModelElementsMap = new TreeMap<String, List<GlobalExportDataColumn>>();
		for (final ProjectModel projectModel : pModels) {
			if (projectModel.getStatus() != ProjectModelStatus.DRAFT) {
				final String pModelName = projectModel.getName();

				final List<GlobalExportDataColumn> pModelElements = new ArrayList<GlobalExportDataColumn>();
				pModelElementsMap.put(pModelName, pModelElements);

				// detail elements
				ExporterUtil.fillElementList(pModelElements, projectModel.getProjectDetails().getLayout());

				// phase elements
				for (final PhaseModel phaseModel : projectModel.getPhaseModels()) {
					ExporterUtil.fillElementList(pModelElements, phaseModel.getLayout());
				}
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
			if (pModelProjectsMap.get(pModelName) == null)
				continue;

			final List<GlobalExportDataColumn> elements = pModelElementsMap.get(pModelName);
			final List<ExportDataCell[]> exportData = new ArrayList<ExportDataCell[]>();
			pModelExportDataMap.put(pModelName, exportData);

			// field titles
			final List<ExportDataCell> titles = new ArrayList<ExportDataCell>();

			// layout group tabs
			final Map<String, List<ExportDataCell[]>> layoutGroupsData = new LinkedHashMap<>();

			// special fields for BI
			titles.add(new ExportStringCell(i18nTranslator.t(language, "projectId")));
			titles.add(new ExportStringCell(i18nTranslator.t(language, "projectActivePhase")));

			boolean isFirstLine = true;
			// projects
			for (final Project project : pModelProjectsMap.get(pModelName)) {

				final List<ExportDataCell> values = new ArrayList<>();

				// special fields for BI
				values.add(new ExportStringCell(String.valueOf(project.getId())));
				if (project.getCloseDate() == null) {
					values.add(new ExportStringCell(project.getCurrentPhase().getPhaseModel().getName()));
				} else {
					values.add(new ExportStringCell(i18nTranslator.t(language, "closedProject")));
				}

				// fields
				for (final GlobalExportDataColumn column : elements) {
					
					// Defining the global objects used in the export.
					column.setContainer(project);
					column.setModelName(pModelName);
					column.setCategories(categories);
					column.setValueHandler(handler);
					column.setIterationsHandler(iterationsHandler);
					column.setI18nTranslator(i18nTranslator);
					column.setLanguage(language);
					column.setEntityManager(entityManager);
					
					column.setInitialColumns("projectId", "projectCode", "projectTitle", "iterationName");
					
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

		addProjectFundings(projects, pModelExportDataMap, i18nTranslator, language);

		addCategories(categories, pModelExportDataMap, i18nTranslator, language);

		return pModelExportDataMap;
	}

	private void addProjectFundings(List<Project> projects, Map<String, List<ExportDataCell[]>> exportDataMap, I18nServer i18nTranslator, Language language) {
		List<ExportDataCell[]> dataFundings = new ArrayList<>();

		ExportDataCell[] row = new ExportDataCell[7];
		row[0] = new ExportStringCell(String.valueOf(i18nTranslator.t(language, "fundingId")));
		row[1] = new ExportStringCell(String.valueOf(i18nTranslator.t(language, "fundingCode")));
		row[2] = new ExportStringCell(String.valueOf(i18nTranslator.t(language, "fundingTitle")));
		row[3] = new ExportStringCell(String.valueOf(i18nTranslator.t(language, "fundedId")));
		row[4] = new ExportStringCell(String.valueOf(i18nTranslator.t(language, "fundedCode")));
		row[5] = new ExportStringCell(String.valueOf(i18nTranslator.t(language, "fundedTitle")));
		row[6] = new ExportStringCell(String.valueOf(i18nTranslator.t(language, "fundingAmount")));
		dataFundings.add(row);

		for(Project project : projects) {
			List<ProjectFunding> fundings = project.getFunded();
			if(project.getFunded() != null) {
				for(ProjectFunding funding : fundings) {
					row = new ExportDataCell[7];
					row[0] = new ExportStringCell(String.valueOf(project.getId()));
					row[1] = new ExportStringCell(project.getName());
					row[2] = new ExportStringCell(project.getFullName());
					row[3] = new ExportStringCell(String.valueOf(funding.getFunded().getId()));
					row[4] = new ExportStringCell(funding.getFunded().getName());
					row[5] = new ExportStringCell(funding.getFunded().getFullName());
					row[6] = new ExportStringCell(String.valueOf(funding.getPercentage()));
					dataFundings.add(row);
				}
			}
		}

		exportDataMap.put(i18nTranslator.t(language, "projectsFundings"), dataFundings);
	}

	public void persistGlobalExportDataAsCsv(final GlobalExport globalExport, EntityManager em, Map<String, List<ExportDataCell[]>> exportData) throws Exception {
		for (final String pModelName : exportData.keySet()) {
			final GlobalExportContent content = new GlobalExportContent();
			content.setGlobalExport(globalExport);
			content.setProjectModelName(pModelName);
			content.setCsvContent(csvBuilder.buildCsv(exportData.get(pModelName)));
			em.persist(content);
		}
	}

	public Map<String, List<ExportDataCell[]>> getBackedupGlobalExportData(EntityManager em, Integer gExportId) {
		final Map<String, List<ExportDataCell[]>> exportData = new TreeMap<String, List<ExportDataCell[]>>();
		final GlobalExport export = em.find(GlobalExport.class, gExportId);
		final List<GlobalExportContent> contents = export.getContents();
		for (final GlobalExportContent content : contents) {
			final List<String[]> csvData = csvParser.parseCsv(content.getCsvContent());
			exportData.put(content.getProjectModelName(), CSVDataToGlobalExportData(csvData));
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
