package org.sigmah.server.servlet.exporter.data;

import java.util.List;
import java.util.Map;

/**
 * Shared global data for excel/calc
 * 
 * @author sherzod (v1.3)
 */
public class GlobalExportData {

	private final Map<String, List<String[]>> exportData;

	public GlobalExportData(final Map<String, List<String[]>> exportData) {
		this.exportData = exportData;
	}

	public Map<String, List<String[]>> getExportData() {
		return exportData;
	}

}
