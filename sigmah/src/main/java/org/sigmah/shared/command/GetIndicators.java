/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command;

import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;

/**
 * Retrieves all of a project's indicators
 * @author 
 */
public class GetIndicators implements Command<IndicatorListResult> {
    
	private int userDatabaseId;
    
    public GetIndicators() {
    }


    public void setUserDatabaseId(int userDatabaseId) {
        this.userDatabaseId = userDatabaseId;
    }

    public int getUserDatabaseId() {
        return userDatabaseId;
    }


	public static GetIndicators forDatabase(int userDatabaseId) {
		GetIndicators command = new GetIndicators();
		command.setUserDatabaseId(userDatabaseId);
		
		return command;
	}
	
}
