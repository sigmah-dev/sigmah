/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.shared.command;

import org.sigmah.shared.dto.GlobalExportSettingsDTO;

/*
 * @author sherzod
 */
public class GetGlobalExportSettings implements Command<GlobalExportSettingsDTO>  {
		
	private int organizationId;
	
	public GetGlobalExportSettings(){}
	
	public GetGlobalExportSettings(int organizationId){
		this.organizationId=organizationId;
	}

	public int getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}
	
	

}
