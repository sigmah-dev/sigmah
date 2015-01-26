package org.sigmah.shared.command;

import org.sigmah.shared.command.result.UserDatabaseListResult;

/**
 * Retrieves the list of project models available to the user.
 * 
 * @author nrebiai
 * 
 */
public class GetUserDatabase implements Command<UserDatabaseListResult> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3006666313649601894L;

	/**
     * The type of model of the models for the current user organization (set to
     * <code>null</code> to ignore this filter).
     */
    public GetUserDatabase() {
        // serialization.
    }
}
