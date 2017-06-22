package org.sigmah.shared.dto.search;

import java.io.Serializable;
import java.util.ArrayList;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

public class SearchResultsDTO extends AbstractModelDataEntityDTO<Integer> implements Serializable
{
	
	//public static final String ENTITY_NAME = "search.SearchResults";
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1227917435084017780L;
	
	private String result;
	private String DTOid;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDTOid() {
		return DTOid;
	}

	public void setDTOid(String dTOid) {
		DTOid = dTOid;
	}
	
}
