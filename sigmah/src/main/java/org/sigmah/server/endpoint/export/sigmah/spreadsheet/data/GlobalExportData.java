/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.data;

import java.util.List;
import java.util.Map;

/*
 * Shared global data for excel/calc
 * @author sherzod
 */
public class GlobalExportData {
	
	private final Map<String,List<String[]>> exportData;
 	
	public GlobalExportData(final Map<String,List<String[]>> exportData ){
		this.exportData=exportData;
 	}

	public Map<String, List<String[]>> getExportData() {
		return exportData;
	}
 
	
	
}
