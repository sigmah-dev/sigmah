/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.mail;

import org.sigmah.shared.domain.User;

public class Invitation {
    private User newUser;
    private User invitingUser;
    private String userConfirmServerUrl;
    
    public Invitation(User newUser, User invitingUser) {
        this.newUser = newUser;
        this.invitingUser = invitingUser;
    }
     
    public Invitation(User newUser, User invitingUser,String userConfirmServerUrl) {
        this.newUser = newUser;
        this.invitingUser = invitingUser;
        this.userConfirmServerUrl = userConfirmServerUrl;
    }

    public User getNewUser() {
        return newUser;
    }

    public User getInvitingUser() {
        return invitingUser;
    }

	/**
	 * @return the userConfirmServerUrl
	 */
	public String getUserConfirmServerUrl() {
		return userConfirmServerUrl;
	}

	/**
	 * @param userConfirmServerUrl the userConfirmServerUrl to set
	 */
	public void setUserConfirmServerUrl(String userConfirmServerUrl) {
		this.userConfirmServerUrl = userConfirmServerUrl;
	}
}
