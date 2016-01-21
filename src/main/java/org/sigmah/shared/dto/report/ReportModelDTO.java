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
 * DTO mapping class for entity report.ProjectReportModel
 * 
 * @author nrebiai
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReportModelDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3300196624126690838L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "report.ProjectReportModel";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String SECTIONS = "sections";
	public static final String ORGANIZATION_ID = "organizationId";

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
		builder.append("name", getName());
		builder.append("organizationId", getOrganizationId());
	}

	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		this.set(NAME, name);
	}

	public List<ProjectReportModelSectionDTO> getSections() {
		return get(SECTIONS);
	}

	public void setSections(List<ProjectReportModelSectionDTO> sections) {
		this.set(SECTIONS, sections);
	}

	public int getOrganizationId() {
		return (Integer) get(ORGANIZATION_ID);
	}

	public void setOrganizationId(int organizationId) {
		this.set(ORGANIZATION_ID, organizationId);
	}

}
