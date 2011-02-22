package org.sigmah.shared.command;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.UserDTO;

public class DeactivateUsers implements Command<VoidResult> {
	
	private List<UserDTO> users;
	
	private static final long serialVersionUID = -6750954216001738221L;

	protected DeactivateUsers() {
		//serialization
	}
	
	public DeactivateUsers(List<UserDTO> users) {
		this.setUsers(users);
	}

	public void setUsers(List<UserDTO> users) {
		this.users = users;
	}

	public List<UserDTO> getUsers() {
		return users;
	}

	public void addUser(UserDTO user){
		if(users == null){
			users = new ArrayList<UserDTO>();
		}
		this.users.add(user);
	}
	
	public void removeUser(UserDTO user){
		this.users.remove(user);
	}
}
