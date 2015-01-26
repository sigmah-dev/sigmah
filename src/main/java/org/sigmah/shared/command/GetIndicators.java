package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * Retrieves all of a project's indicators
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetIndicators extends AbstractCommand<IndicatorListResult> {

	private int userDatabaseId;

	protected GetIndicators() {
		// Serialization.
	}
	
	public GetIndicators(ProjectDTO project) {
		this(project.getId());
	}
	
	public GetIndicators(int userDatabaseId) {
		this.userDatabaseId = userDatabaseId;
	}
	
	public int getUserDatabaseId() {
		return userDatabaseId;
	}

}
