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

import java.util.Map;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.report.ReportModelDTO;

/**
 * Update project report model command.
 * 
 * @author HUZHE (zhe.hu32@gmail.com) (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class UpdateProjectReportModel extends AbstractCommand<ReportModelDTO> {

	private ReportModelDTO reportModel;
	private Map<String, Object> changes;
	private int reportModelId;

	public UpdateProjectReportModel() {
		// Serialization.
	}

	public UpdateProjectReportModel(ReportModelDTO reportModel) {
		this.reportModel = reportModel;
	}

	public UpdateProjectReportModel(int reportModelId, Map<String, Object> changes) {
		this.reportModelId = reportModelId;
		this.changes = changes;
	}

	/**
	 * @return the reportModel
	 */
	public ReportModelDTO getReportModel() {
		return reportModel;
	}

	/**
	 * @param reportModel
	 *          the reportModel to set
	 */
	public void setReportModel(ReportModelDTO reportModel) {
		this.reportModel = reportModel;
	}

	/**
	 * @return the changes
	 */
	public Map<String, Object> getChanges() {
		return changes;
	}

	/**
	 * @param changes
	 *          the changes to set
	 */
	public void setChanges(Map<String, Object> changes) {
		this.changes = changes;
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
