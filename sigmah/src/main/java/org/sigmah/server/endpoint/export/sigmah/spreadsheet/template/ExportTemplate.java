package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;

public interface ExportTemplate {
	
	void write(OutputStream output) throws Throwable;

}
