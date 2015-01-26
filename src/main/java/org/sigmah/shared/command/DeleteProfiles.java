package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.profile.ProfileDTO;


public class DeleteProfiles implements Command<VoidResult> {

	/**
	 * Command to delete profiles
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ProfileDTO> profilesList;
	
	protected DeleteProfiles() {
		
	}

	/**
	 * 
	 * @param profilesList
	 */
	public DeleteProfiles(List<ProfileDTO> profilesList) {
		this.setProfiles(profilesList);
	}

	/**
	 * 
	 * @return the profiles
	 */
	public List<ProfileDTO> getProfilesList() {
		return profilesList;
	}

	/**
	 * 
	 * @param profiles the profiles to set
	 */
	public void setProfiles(List<ProfileDTO> profilesList) {
		this.profilesList = profilesList;
	}
}
