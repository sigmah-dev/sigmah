package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

/**
 * Command to delete privacy groups
 * @author gjb
 */
public class DeletePrivacyGroups implements Command<VoidResult> {


	private static final long serialVersionUID = 1L;
	
	private List<PrivacyGroupDTO> privacyGroupsList;
	
	protected DeletePrivacyGroups() {
		
	}

	/**
	 * 
	 * @param privacyGroupsList
	 */
	public DeletePrivacyGroups(List<PrivacyGroupDTO> privacyGroupsList) {
		this.setPrivacyGroupsList(privacyGroupsList);
	}

	/**
	 * 
	 * @return the privacy groups
	 */
	public List<PrivacyGroupDTO> getPrivacyGroupsList() {
		return privacyGroupsList;
	}

	/**
	 * 
	 * @param privacyGroupsList the privacy groups to set
	 */
	public void setPrivacyGroupsList(List<PrivacyGroupDTO> privacyGroupsList) {
		this.privacyGroupsList = privacyGroupsList;
	}
}
