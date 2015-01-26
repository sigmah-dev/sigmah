package org.sigmah.server.servlet.exporter.template;

import java.io.OutputStream;

/**
 * @author sherzod (v1.3)
 */
public interface ExportTemplate {

	void write(OutputStream output) throws Throwable;

}
