/**
 * 
 */
package org.sigmah.shared.command.result;
import java.util.List;

import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;



/**
 * @author HUZHE
 *
 */
public class ReportElementsResult implements CommandResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ReportElementDTO> reportElements;
	private List<ReportListElementDTO>reportListElements;
	
	

	/**
	 * 
	 */
	public ReportElementsResult() {
		
	}
	
	

	/**
	 * @param reportElements
	 * @param reportListElements
	 */
	public ReportElementsResult(List<ReportElementDTO> reportElements,
			List<ReportListElementDTO> reportListElements) {
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
	 * @param reportElements the reportElements to set
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
	 * @param reportListElements the reportListElements to set
	 */
	public void setReportListElements(List<ReportListElementDTO> reportListElements) {
		this.reportListElements = reportListElements;
	}


	
	
	
	
	
	

}
