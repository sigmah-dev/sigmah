package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

public class ImportationSchemeListResult implements CommandResult {

	/**
	 * 
	 */
    private static final long serialVersionUID = -2698173188302921059L;
    
	private List<ImportationSchemeDTO> list;
	
	public ImportationSchemeListResult(){
		
	}
	
	public ImportationSchemeListResult(List<ImportationSchemeDTO> schemaDTOList) {
        this.list = schemaDTOList;
    }

    public List<ImportationSchemeDTO> getList() {
        return list;
    }

    public void setList(List<ImportationSchemeDTO> list) {
        this.list = list;
    }

}
