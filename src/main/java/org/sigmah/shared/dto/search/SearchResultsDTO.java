package org.sigmah.shared.dto.search;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchResultsDTO implements Serializable{
	
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1227917435084017780L;
	
	private String result;
	
	public SearchResultsDTO(){
		
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
}
