package org.sigmah.shared.dto.report;

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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * ProjectReportModelSectionDTO.
 * 
 * @author nrebiai (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ProjectReportModelSectionDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3100003531351081230L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "report.ProjectReportModelSection";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String PARENT_SECTION_MODEL_ID = "parentSectionModelId";
	public static final String PARENT_SECTION_MODEL_NAME = "parentSectionModelName";
	public static final String PROJECT_MODEL_ID = "projectModelId";
	public static final String REPORT_MODEL_NAME = "reportModelName";
	public static final String INDEX = "index";
	public static final String ROW = "row";
	public static final String NUMBER_OF_TEXTAREA = "numberOfTextarea";
	public static final String SUB_SECTIONS = "subSections";
	public static final String COMPOSITE_NAME = "compositeName";

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
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(NAME, getName());
		builder.append(PARENT_SECTION_MODEL_ID, getParentSectionModelId());
		builder.append(PARENT_SECTION_MODEL_NAME, getParentSectionModelName());
		builder.append(PROJECT_MODEL_ID, getProjectModelId());
		builder.append(REPORT_MODEL_NAME, getReportModelName());
		builder.append(INDEX, getIndex());
		builder.append(ROW, getRow());
		builder.append(NUMBER_OF_TEXTAREA, getNumberOfTextarea());
		builder.append(COMPOSITE_NAME, getCompositeName());
	}

	public Integer getParentSectionModelId() {
		return (Integer) get(PARENT_SECTION_MODEL_ID);
	}

	public void setParentSectionModelId(Integer parentSectionModelId) {
		this.set(PARENT_SECTION_MODEL_ID, parentSectionModelId);
	}

	public String getParentSectionModelName() {
		return (String) get(PARENT_SECTION_MODEL_NAME);
	}

	public void setParentSectionModelName(String parentSectionModelName) {
		this.set(PARENT_SECTION_MODEL_NAME, parentSectionModelName);
	}

	public Integer getProjectModelId() {
		return (Integer) get(PROJECT_MODEL_ID);
	}

	public void setProjectModelId(Integer projectModelId) {
		this.set(PROJECT_MODEL_ID, projectModelId);
	}

	public String getReportModelName() {
		return (String) get(REPORT_MODEL_NAME);
	}

	public void setReportModelName(String name) {
		this.set(REPORT_MODEL_NAME, name);
	}

	public String getName() {
		return (String) get(NAME);
	}

	public void setName(String name) {
		this.set(NAME, name);
	}

	public Integer getIndex() {
		return (Integer) get(INDEX);
	}

	public void setIndex(Integer index) {
		this.set(INDEX, index);
	}

	public Integer getRow() {
		return (Integer) get(ROW);
	}

	public void setRow(Integer row) {
		this.set(ROW, row);
	}

	public Integer getNumberOfTextarea() {
		return (Integer) get(NUMBER_OF_TEXTAREA);
	}

	public void setNumberOfTextarea(Integer numberOfTextarea) {
		this.set(NUMBER_OF_TEXTAREA, numberOfTextarea);
	}

	public List<ProjectReportModelSectionDTO> getSubSections() {
		return get(SUB_SECTIONS);
	}

	public void setSubSections(List<ProjectReportModelSectionDTO> subSections) {
		this.set(SUB_SECTIONS, subSections);
	}

	// name (id)
	public String getCompositeName() {
		// return getName()+"<i>("+getId()+")</i>";
		return (String) get(COMPOSITE_NAME);
	}

	public void setCompositeName(String compositeName) {
		set(COMPOSITE_NAME, compositeName);
	}
}
