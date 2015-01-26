package org.sigmah.shared.command.result;

/**
 * Contains the server's information.
 * 
 * @author HUZHE
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class HostServerInfo implements Result {

	private String hostUrl;

	public HostServerInfo() {
		// Serialization.
	}

	/**
	 * @return the hostUrl
	 */
	public String getHostUrl() {
		return hostUrl;
	}

	/**
	 * @param hostUrl
	 *          the hostUrl to set
	 */
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

}
