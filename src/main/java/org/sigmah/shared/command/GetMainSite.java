package org.sigmah.shared.command;

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.dto.SiteDTO;

/**
 * Fetch the <code>SiteDTO</code> instance of the main site associated to the
 * given project.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetMainSite implements Command<SiteDTO> {
	private int projectId;

	protected GetMainSite() {
	}

	public GetMainSite(int projectId) {
		this.projectId = projectId;
	}

	public int getProjectId() {
		return projectId;
	}
}
