/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.OrgUnitSynthesisData;
import org.sigmah.shared.domain.OrgUnit;

/*
 * @author sherzod
 */
public class OrgUnitSynthesisExcelTemplate extends BaseSynthesisExcelTemplate {

 
	public OrgUnitSynthesisExcelTemplate(
			final OrgUnitSynthesisData data,
			final HSSFWorkbook wb) throws Throwable {
		super(data,wb,OrgUnit.class);	 
	}

}