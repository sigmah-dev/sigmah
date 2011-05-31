package org.sigmah.shared.command.result;

import org.sigmah.shared.dto.report.ReportModelDTO;




/**
 * A report model command result
 * 
 * @author HUZHE
 *
 */
public class ProjectReportModelResult implements CommandResult {

    private static final long serialVersionUID = -9082324685553248367L;

    private ReportModelDTO reportModelDTO;

    public ProjectReportModelResult() {
    }

    public ProjectReportModelResult(ReportModelDTO reportModelDTO) {
        this.reportModelDTO = reportModelDTO;
    }

	/**
	 * @return the reportModelDTO
	 */
	public ReportModelDTO getReportModelDTO() {
		return reportModelDTO;
	}

	/**
	 * @param reportModelDTO the reportModelDTO to set
	 */
	public void setReportModelDTO(ReportModelDTO reportModelDTO) {
		this.reportModelDTO = reportModelDTO;
	}

   
}
