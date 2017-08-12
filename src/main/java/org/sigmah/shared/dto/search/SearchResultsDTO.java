package org.sigmah.shared.dto.search;

import java.io.Serializable;
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
	private String DTOtype;
	
	//only for files
	private String file_name;
	private String file_ext;

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

	public String getDTOtype() {
		return DTOtype;
	}

	public void setDTOtype(String dTOtype) {
		DTOtype = dTOtype;
	}

	public String getFileName() {
		return file_name;
	}

	public void setFileName(String file_name) {
		this.file_name = file_name;
	}

	public String getFileExt() {
		return file_ext;
	}

	public void setFileExt(String file_ext) {
		this.file_ext = file_ext;
	}
	
	
}
