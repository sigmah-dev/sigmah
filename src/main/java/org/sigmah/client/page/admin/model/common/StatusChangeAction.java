/**
 * 
 */
package org.sigmah.client.page.admin.model.common;

/**
 * 
 * 
 * 
 * @author HUZHE (zhe.hu32@gmail.com)
 *
 */
public class StatusChangeAction {
	
	
	/**
	 * Boolean value to decide if this changing action is valid
	 */
	private boolean isValid; 
	
	/**
	 * Give user feedback message (Warning,Error...) depends on the situations
	 */
	private String feedBackMessage;
	
	
	public StatusChangeAction()
	{
		//Default values
		this.isValid=false;
		this.feedBackMessage=null;
	}


	public boolean isValid() {
		return isValid;
	}


	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}


	public String getFeedBackMessage() {
		return feedBackMessage;
	}


	public void setFeedBackMessage(String feedBackMessage) {
		this.feedBackMessage = feedBackMessage;
	}

	
	
}
