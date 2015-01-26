/**
 * 
 */
package org.sigmah.shared.command;

import java.util.Map;

import org.sigmah.shared.command.result.ProjectReportModelResult;
import org.sigmah.shared.dto.report.ReportModelDTO;

/**
 * 
 * Update project report model command.
 * 
 * @author HUZHE (zhe.hu32@gmail.com)
 *
 */
public class UpdateProjectReportModel implements Command<ProjectReportModelResult>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private ReportModelDTO reportModel;
	private Map<String,Object>changes;
	private int reportModelId;

	public UpdateProjectReportModel()
	{
		
	}
	public UpdateProjectReportModel(ReportModelDTO reportModel)
	{
		this.reportModel = reportModel;
	}
	
	public UpdateProjectReportModel(int reportModelId,Map<String,Object> changes)
	{
		this.reportModelId=reportModelId;
		this.changes=changes;
	}
	
	/**
	 * @return the reportModel
	 */
	public ReportModelDTO getReportModel() {
		return reportModel;
	}
	/**
	 * @param reportModel the reportModel to set
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
	 * @param changes the changes to set
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
	 * @param reportModelId the reportModelId to set
	 */
	public void setReportModelId(int reportModelId) {
		this.reportModelId = reportModelId;
	}
	

}
