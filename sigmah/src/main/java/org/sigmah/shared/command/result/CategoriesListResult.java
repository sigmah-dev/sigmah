package org.sigmah.shared.command.result;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author nrebiai
 * 
 */

import org.sigmah.shared.dto.category.CategoryTypeDTO;


public class CategoriesListResult implements CommandResult {
	private static final long serialVersionUID = -8216142487637992507L;
	private List<CategoryTypeDTO> list;
	
	public CategoriesListResult(){
		
	}
	
	public CategoriesListResult(List<CategoryTypeDTO> list) {
        this.list = list;
    }

    public List<CategoryTypeDTO> getList() {
    	if(list == null){
    		list = new ArrayList<CategoryTypeDTO>();
    	}
        return list;
    }

    public void setList(List<CategoryTypeDTO> list) {
        this.list = list;
    }
}
