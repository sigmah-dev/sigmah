package org.sigmah.server.service;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sigmah.server.dao.ProjectReportDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.ReportElement;
import org.sigmah.server.domain.report.KeyQuestion;
import org.sigmah.server.domain.report.ProjectReport;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.domain.report.ProjectReportModelSection;
import org.sigmah.server.domain.report.ProjectReportVersion;
import org.sigmah.server.domain.report.RichTextElement;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.ReportListElement;

/**
 * Handle the creation and the update procedure of the project reports.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectReportService extends AbstractEntityService<ProjectReport, Integer, ProjectReportDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

	private final ProjectReportDAO dao;
	private final ValueService valueService;

	@Inject
	public ProjectReportService(final ProjectReportDAO dao, final ValueService valueService) {
		this.dao = dao;
		this.valueService = valueService;
	}

	/**
	 * <p>
	 * Creates a new report.
	 * </p>
	 * <p>
	 * Requires the following properties :
	 * <ul>
	 * <li><code>name</code> - Name of the report.</li>
	 * <li><code>phaseName</code> - Name of the current phase.</li>
	 * <li><code>reportModelId</code> - ID of the project report model to use.</li>
	 * <li><code>projectId</code> - ID of the project owning the report.</li>
	 * <li><code>containerId</code> - ID of the project owning the report.</li>
	 * <li><code>flexibleElementId</code> - ID of the flexible element owning the report.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param properties
	 *          Properties of the new report.
	 * @param context
	 *          User context creating the report.
	 * @return The ID of the new report.
	 */
	@Override
	public ProjectReport create(PropertyMap properties, final UserExecutionContext context) {
		return createReport(context, properties);
	}

	/**
	 * Creates a new report version in draft mode.<br>
	 * <br>
	 * Requires the following properties :<br>
	 * <code>reportId</code> - ID of the report<br>
	 * <code>phaseName</code> - Name of the current phase.<br>
	 * 
	 * @param user
	 *          User creating the draft.
	 * @param properties
	 *          Properties of the draft.
	 * @return The ID of the new draft.
	 */
	public Integer createDraft(User user, PropertyMap properties) {
		return createReportDraft(user, properties).getId();
	}

	private void iterateOnSection(ProjectReportModelSection section, List<RichTextElement> elements, ProjectReportVersion version) {
		int areaCount = section.getNumberOfTextarea();

		// Key questions
		List<KeyQuestion> keyQuestions = section.getKeyQuestions();
		if (keyQuestions == null) {
			keyQuestions = Collections.emptyList();
		}

		for (int index = 0; index < keyQuestions.size(); index++) {
			final RichTextElement element = new RichTextElement();
			element.setIndex(index);
			element.setSectionId(section.getId());
			element.setVersion(version);
			elements.add(element);
		}

		int index = 0;

		// Sub sections and rich text elements
		List<ProjectReportModelSection> subSections = section.getSubSections();
		if (subSections == null) {
			subSections = Collections.emptyList();
		}

		for (final ProjectReportModelSection subSection : subSections) {

			LOG.debug("Sub-section: {}", subSection);

			while (index < subSection.getIndex() && areaCount > 0) {
				// New rich text element
				final RichTextElement element = new RichTextElement();
				element.setIndex(index + keyQuestions.size());
				element.setSectionId(section.getId());
				element.setVersion(version);
				elements.add(element);

				index++;
				areaCount--;
			}

			iterateOnSection(subSection, elements, version);
		}

		while (areaCount > 0) {
			// New rich text element
			final RichTextElement element = new RichTextElement();
			element.setIndex(index + keyQuestions.size());
			element.setSectionId(section.getId());
			element.setVersion(version);
			elements.add(element);

			index++;
			areaCount--;
		}
	}

	protected ProjectReport createReport(final UserExecutionContext context, PropertyMap properties) {

		final User user = context.getUser();
		final ProjectReport report = new ProjectReport();
		final ProjectReportVersion initialVersion = new ProjectReportVersion();

		report.setCurrentVersion(initialVersion);

		// Defining the common properties
		report.setName((String) properties.get(ProjectReportDTO.NAME));

		initialVersion.setReport(report);
		initialVersion.setVersion(1);
		initialVersion.setEditor(user);
		initialVersion.setEditDate(new Date());
		initialVersion.setPhaseName((String) properties.get(ProjectReportDTO.PHASE_NAME));

		final ProjectReportModel model;

		final Integer reportModelId = properties.get(ProjectReportDTO.REPORT_MODEL_ID);
		if(reportModelId != null) {
			model = dao.findModelById((Integer) properties.get(ProjectReportDTO.REPORT_MODEL_ID));
			
		} else if(properties.get(ProjectReportDTO.FLEXIBLE_ELEMENT_ID) != null) {
			final int flexibleElementId = properties.get(ProjectReportDTO.FLEXIBLE_ELEMENT_ID);
			final FlexibleElement element = em().find(FlexibleElement.class, flexibleElementId);
			
			if(element instanceof ReportListElement) {
				model = ((ReportListElement) element).getModel();
				
			} else if(element instanceof ReportElement) {
				model = ((ReportElement) element).getModel();
				
			} else {
				model = null;
			}
		} else {
			model = null;
		}
		
		if(model == null) {
			throw new IllegalStateException("Impossible to find the requested project model.");
		}
		
		report.setModel(model);

		final Integer projectId = (Integer) properties.get(ProjectReportDTO.PROJECT_ID);
		if (projectId != null) {
			final Project project = new Project();
			project.setId(projectId);
			report.setProject(project);
		}

		final Integer orgUnitId = (Integer) properties.get(ProjectReportDTO.ORGUNIT_ID);
		if (orgUnitId != null) {
			final OrgUnit orgUnit = new OrgUnit();
			orgUnit.setId(orgUnitId);
			report.setOrgUnit(orgUnit);
		}

		// Parent
		final Integer flexibleElementId = (Integer) properties.get(ProjectReportDTO.FLEXIBLE_ELEMENT_ID);
		final Integer containerId = (Integer) properties.get(ProjectReportDTO.CONTAINER_ID);

		final boolean multiple;
		if (properties.get(ProjectReportDTO.MULTIPLE) == null) {
			multiple = false;
		} else {
			multiple = (Boolean) properties.get(ProjectReportDTO.MULTIPLE);
		}

		final Value flexibleElementValue;
		if (flexibleElementId != null && containerId != null) {
			final ReportElement element = new ReportElement();
			element.setId(flexibleElementId);
			report.setFlexibleElement(element);

			flexibleElementValue = valueService.retrieveOrCreateValue(containerId, flexibleElementId, user);
			if (!multiple && !(flexibleElementValue == null || flexibleElementValue.getValue() == null || "".equals(flexibleElementValue.getValue()))) {
				throw new IllegalStateException("A report has already been created for the flexible element " + flexibleElementId);
			}
		} else {
			flexibleElementValue = null;
		}

		// RichTextElements
		final ArrayList<RichTextElement> elements = new ArrayList<RichTextElement>();

		for (final ProjectReportModelSection section : model.getSections()) {
			iterateOnSection(section, elements, initialVersion);
		}

		initialVersion.setTexts(elements);

		// Saving
		dao.persist(report, user);

		// Updating the flexible element
		if (flexibleElementValue != null) {
			final String value;

			if (multiple && flexibleElementValue.getValue() != null) {
				// Multiple values mode
				value = flexibleElementValue.getValue() + ValueResultUtils.DEFAULT_VALUE_SEPARATOR + report.getId().toString();

			} else {
				// Single value mode
				value = report.getId().toString();
			}

			flexibleElementValue.setValue(value);
			dao.merge(flexibleElementValue);
		}

		return report;
	}

	protected ProjectReportVersion createReportDraft(User user, PropertyMap properties) {
		final ProjectReportVersion version = new ProjectReportVersion();

		version.setEditor(user);
		version.setEditDate(new Date());
		version.setPhaseName((String) properties.get(ProjectReportDTO.PHASE_NAME));

		// Linking the draft to the report
		final ProjectReport report = dao.findReportById((Integer) properties.get(ProjectReportDTO.REPORT_ID));
		version.setReport(report);

		// Copying the current values
		final ArrayList<RichTextElement> texts = new ArrayList<RichTextElement>();

		final List<RichTextElement> currentTexts = report.getCurrentVersion().getTexts();
		for (final RichTextElement text : currentTexts) {
			final RichTextElement element = text.duplicate();
			element.setVersion(version);

			texts.add(element);
		}

		version.setTexts(texts);

		// Saving
		dao.persist(version);

		return version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectReport update(Integer entityId, PropertyMap changes, final UserExecutionContext context) {

		for (final Map.Entry<String, Object> entry : changes.entrySet()) {
			if (ProjectReportDTO.CURRENT_PHASE.equals(entry.getKey())) {
				final ProjectReportVersion version = dao.findReportVersionById(entityId);
				version.setPhaseName((String) entry.getValue());
				version.setEditor(context.getUser());
				version.setEditDate(new Date());
				dao.merge(version);

			} else {
				final RichTextElement element = dao.findRichTextElementById(new Integer(entry.getKey()));
				element.setText((String) entry.getValue());
				dao.merge(element);
			}
		}

		return dao.findById(entityId);
	}

}
