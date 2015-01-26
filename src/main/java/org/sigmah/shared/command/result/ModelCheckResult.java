/**
 * 
 */
package org.sigmah.shared.command.result;

/**
 * @author HUZHE (zhe.hu32@gmail.com)
 *
 */
public class ModelCheckResult implements CommandResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Is the model used ?
     */
    private Boolean isUsed;
	
	public ModelCheckResult(){
		
	}

	public Boolean isUsed() {
		return isUsed;
	}

	public void setUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}
	
	
    
    
}
