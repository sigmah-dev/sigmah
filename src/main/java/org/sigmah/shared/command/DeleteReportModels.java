package org.sigmah.shared.command;

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

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;

/**
 * Command to delete report models or report model sections
 *
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class DeleteReportModels extends AbstractCommand<ReportModelDTO> {

	private List<ReportModelDTO> reportModelList;
	private List<ProjectReportModelSectionDTO> sectionList;
	private int reportModelId;

	public DeleteReportModels() {
		// Serialization.
	}

	/**
	 * @param reportModelList
	 * @param sectionList
	 */
	public DeleteReportModels(List<ReportModelDTO> reportModelList, List<ProjectReportModelSectionDTO> sectionList) {
		super();
		this.reportModelList = reportModelList;
		this.sectionList = sectionList;
	}

	/**
	 * @return the reportModelList
	 */
	public List<ReportModelDTO> getReportModelList() {
		return reportModelList;
	}

	/**
	 * @param reportModelList
	 *          the reportModelList to set
	 */
	public void setReportModelList(List<ReportModelDTO> reportModelList) {
		this.reportModelList = reportModelList;
	}

	/**
	 * @return the sectionList
	 */
	public List<ProjectReportModelSectionDTO> getSectionList() {
		return sectionList;
	}

	/**
	 * @param sectionList
	 *          the sectionList to set
	 */
	public void setSectionList(List<ProjectReportModelSectionDTO> sectionList) {
		this.sectionList = sectionList;
	}

	/**
	 * @return the reportModelId
	 */
	public int getReportModelId() {
		return reportModelId;
	}

	/**
	 * @param reportModelId
	 *          the reportModelId to set
	 */
	public void setReportModelId(int reportModelId) {
		this.reportModelId = reportModelId;
	}

}
