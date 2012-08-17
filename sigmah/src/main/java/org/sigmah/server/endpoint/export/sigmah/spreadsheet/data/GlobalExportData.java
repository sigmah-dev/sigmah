package org.sigmah.server.endpoint.export.sigmah.spreadsheet.data;

import java.util.List;
import java.util.Map;


public class GlobalExportData {
	
	private final Map<String,List<String[]>> exportData;
	
	public GlobalExportData(final Map<String,List<String[]>> exportData){
		this.exportData=exportData;
	}

	public Map<String, List<String[]>> getExportData() {
		return exportData;
	}
	
	
}
