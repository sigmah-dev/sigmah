/**
 * 
 */
package org.sigmah.shared.command.result;

/**
 * 
 * Contains the server's information
 * 
 * @author HUZHE
 *
 */
public class HostServerInfo implements CommandResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9071193754727789431L;
    
	private  String hostUrl;

	

	/**
	 * 
	 */
	public HostServerInfo() {
		
	}


	/**
	 * @return the hostUrl
	 */
	public  String getHostUrl() {
		return hostUrl;
	}


	/**
	 * @param hostUrl the hostUrl to set
	 */
	public  void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
	

	
}
