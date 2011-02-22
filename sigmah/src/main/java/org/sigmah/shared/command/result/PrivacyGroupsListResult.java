package org.sigmah.shared.command.result;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.dto.profile.PrivacyGroupDTO;


public class PrivacyGroupsListResult implements CommandResult {
	private static final long serialVersionUID = -8216142487637992507L;
	private List<PrivacyGroupDTO> list;
	
	public PrivacyGroupsListResult(){
		
	}
	
	public PrivacyGroupsListResult(List<PrivacyGroupDTO> list) {
        this.list = list;
    }

    public List<PrivacyGroupDTO> getList() {
    	if(list == null){
    		list = new ArrayList<PrivacyGroupDTO>();
    	}
        return list;
    }

    public void setList(List<PrivacyGroupDTO> list) {
        this.list = list;
    }
}
