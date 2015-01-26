package org.sigmah.shared.command.result;

import java.util.List;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

/**
 * Result for command GetImportationModelList
 */
public class ImportationSchemeModelListResult implements CommandResult {


    private static final long serialVersionUID = 421199757833286879L;
    
    private List<ImportationSchemeModelDTO> list;
    
    public ImportationSchemeModelListResult() {}
    
    public ImportationSchemeModelListResult(List<ImportationSchemeModelDTO> list) {
    	this.list = list;
    }

	public List<ImportationSchemeModelDTO> getList() {
	    return list;
    }

	public void setList(List<ImportationSchemeModelDTO> list) {
	    this.list = list;
    }
    

}
