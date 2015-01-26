package org.sigmah.shared.command;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.UserDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DeactivateUsers extends AbstractCommand<VoidResult> {

	private List<UserDTO> users;

	protected DeactivateUsers() {
		// Serialization.
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

	public void addUser(UserDTO user) {
		if (users == null) {
			users = new ArrayList<UserDTO>();
		}
		this.users.add(user);
	}

	public void removeUser(UserDTO user) {
		this.users.remove(user);
	}
}
