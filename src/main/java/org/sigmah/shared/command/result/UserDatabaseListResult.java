package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.UserDatabaseDTO;

public class UserDatabaseListResult implements CommandResult {


	private static final long serialVersionUID = -6604376432667887721L;
	private List<UserDatabaseDTO> list;
	
	public UserDatabaseListResult(){
		
	}
	
	public UserDatabaseListResult(List<UserDatabaseDTO> list) {
        this.list = list;
    }

    public List<UserDatabaseDTO> getList() {
        return list;
    }

    public void setList(List<UserDatabaseDTO> list) {
        this.list = list;
    }
}
