package org.sigmah.server.endpoint.export.sigmah.spreadsheet.data;

import org.sigmah.server.endpoint.export.sigmah.Exporter;

public abstract class ExportData {

	protected final Exporter exporter;
	private final int numbOfCols;
	
	public ExportData(final Exporter exporter,final int numbOfCols){
		this.exporter=exporter;
		this.numbOfCols=numbOfCols;
	}
	
	public String getLocalizedVersion(String key) {
		return exporter.localize(key);
	}
	
	public int getNumbOfCols() {
		return numbOfCols;
	}
	
}
