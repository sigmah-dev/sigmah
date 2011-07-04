/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.mail;

import org.sigmah.shared.domain.User;

public class Invitation {
    private User newUser;
    private User invitingUser;
    private String hostUrl;
    
    public Invitation(User newUser, User invitingUser) {
        this.newUser = newUser;
        this.invitingUser = invitingUser;
    }
     
    public Invitation(User newUser, User invitingUser,String hostUrl) {
        this.newUser = newUser;
        this.invitingUser = invitingUser;
        this.hostUrl = hostUrl;
    }

    public User getNewUser() {
        return newUser;
    }

    public User getInvitingUser() {
        return invitingUser;
    }

	/**
	 * @return the hostUrl
	 */
	public String getHostUrl() {
		return hostUrl;
	}

	/**
	 * @param hostUrl the hostUrl to set
	 */
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

	
}
