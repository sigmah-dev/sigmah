package org.sigmah.shared.command.result;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.dto.report.ReportModelDTO;


public class ReportModelsListResult implements CommandResult {
	private static final long serialVersionUID = -5265384304479915026L;
	private List<ReportModelDTO> list;
	
	public ReportModelsListResult(){
		
	}
	
	public ReportModelsListResult(List<ReportModelDTO> list) {
        this.list = list;
    }

    public List<ReportModelDTO> getList() {
    	if(list == null){
    		list = new ArrayList<ReportModelDTO>();
    	}
        return list;
    }

    public void setList(List<ReportModelDTO> list) {
        this.list = list;
    }
}
