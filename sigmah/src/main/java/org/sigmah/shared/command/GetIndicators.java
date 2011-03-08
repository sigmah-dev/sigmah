/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command;

import org.sigmah.shared.dto.IndicatorDTO;

/**
 * Retrieves all of a project's indicators
 * @author 
 */
public class GetIndicators implements Command<IndicatorDTO> {
    private int userDatabaseId;

    public GetIndicators() {
    }

    public GetIndicators(int userDatabaseId) {
        this.userDatabaseId = userDatabaseId;
    }

    public void setUserDatabaseId(int userDatabaseId) {
        this.userDatabaseId = userDatabaseId;
    }

    public int getUserDatabaseId() {
        return userDatabaseId;
    }
}
