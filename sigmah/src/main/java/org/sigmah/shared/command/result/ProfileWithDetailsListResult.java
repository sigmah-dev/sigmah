package org.sigmah.shared.command.result;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.dto.profile.ProfileDTO;


public class ProfileWithDetailsListResult implements CommandResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1573218693064391344L;
	private List<ProfileDTO> list;
	
	public ProfileWithDetailsListResult(){
		
	}
	
	public ProfileWithDetailsListResult(List<ProfileDTO> list) {
        this.list = list;
    }

    public List<ProfileDTO> getList() {
    	if(list == null){
    		list = new ArrayList<ProfileDTO>();
    	}
        return list;
    }

    public void setList(List<ProfileDTO> list) {
        this.list = list;
    }
}
