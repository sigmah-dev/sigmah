/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.template;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.OrgUnitSynthesisData;
import org.sigmah.shared.domain.OrgUnit;


/*
 * @author sherzod
 */
public class OrgUnitSynthesisCalcTemplate extends BaseSynthesisCalcTemplate { 
  
	public OrgUnitSynthesisCalcTemplate(
			final OrgUnitSynthesisData data,
			final SpreadsheetDocument doc) throws Throwable {
		 	super(data,doc,OrgUnit.class);
	}
 
}