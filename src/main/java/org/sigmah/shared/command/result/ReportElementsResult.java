package org.sigmah.shared.command.result;

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

import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;

/**
 * @author HUZHE
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReportElementsResult implements Result {

	private List<ReportElementDTO> reportElements;
	private List<ReportListElementDTO> reportListElements;

	/**
	 * 
	 */
	public ReportElementsResult() {
		// Serialization.
	}

	/**
	 * @param reportElements
	 * @param reportListElements
	 */
	public ReportElementsResult(List<ReportElementDTO> reportElements, List<ReportListElementDTO> reportListElements) {
		super();
		this.reportElements = reportElements;
		this.reportListElements = reportListElements;
	}

	/**
	 * @return the reportElements
	 */
	public List<ReportElementDTO> getReportElements() {
		return reportElements;
	}

	/**
	 * @param reportElements
	 *          the reportElements to set
	 */
	public void setReportElements(List<ReportElementDTO> reportElements) {
		this.reportElements = reportElements;
	}

	/**
	 * @return the reportListElements
	 */
	public List<ReportListElementDTO> getReportListElements() {
		return reportListElements;
	}

	/**
	 * @param reportListElements
	 *          the reportListElements to set
	 */
	public void setReportListElements(List<ReportListElementDTO> reportListElements) {
		this.reportListElements = reportListElements;
	}

}
