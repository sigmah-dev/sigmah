/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.ProjectSynthesisData;
import org.sigmah.shared.domain.Project;

/*
 * @author sherzod
 */
public class ProjectSynthesisExcelTemplate extends BaseSynthesisExcelTemplate {
 
	public ProjectSynthesisExcelTemplate(
			final ProjectSynthesisData data,
			final HSSFWorkbook wb) throws Throwable {
		super(data,wb,Project.class);	 
	} 
}
