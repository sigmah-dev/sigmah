/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.ProjectSynthesisData;
import org.sigmah.shared.domain.Project;

/*
 * @author sherzod
 */
public class ProjectSynthesisCalcTemplate extends BaseSynthesisCalcTemplate { 
  
	public ProjectSynthesisCalcTemplate(
			final ProjectSynthesisData data,
			final SpreadsheetDocument doc) throws Throwable {
		 	super(data,doc,Project.class);
	}
 
}