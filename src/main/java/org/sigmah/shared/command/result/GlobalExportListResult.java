/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.GlobalExportDTO;

/*
 * @author sherzod
 */
public class GlobalExportListResult implements CommandResult{
	
	private static final long serialVersionUID = 6977652060005169846L;
	private List<GlobalExportDTO> list;
	
	public GlobalExportListResult(){}
	
	public GlobalExportListResult(List<GlobalExportDTO> list){
		this.list=list;
	}

	public List<GlobalExportDTO> getList() {
		return list;
	}

	public void setList(List<GlobalExportDTO> list) {
		this.list = list;
	}
	
	

}
