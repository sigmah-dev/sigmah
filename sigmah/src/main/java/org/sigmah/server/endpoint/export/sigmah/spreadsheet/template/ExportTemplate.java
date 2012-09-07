/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import java.io.OutputStream;

/*
 * @author sherzod
 */
public interface ExportTemplate {
	
	void write(OutputStream output) throws Throwable;

}
