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
    private String newUserPassword;
    
    public Invitation(User newUser, User invitingUser) {
        this.newUser = newUser;
        this.invitingUser = invitingUser;
    }
     
    public Invitation(User newUser, User invitingUser,String hostUrl) {
        this.newUser = newUser;
        this.invitingUser = invitingUser;
        this.hostUrl = hostUrl;
    }    
     
    public Invitation(User newUser, User invitingUser, String hostUrl, String newUserPassword) {
        this.newUser = newUser;
        this.invitingUser = invitingUser;
        this.hostUrl = hostUrl;
        this.newUserPassword = newUserPassword;
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

	/**
	 * @return the password of the invited user
	 */
	public String getNewUserPassword() {
		return this.newUserPassword;
	}

	
}
