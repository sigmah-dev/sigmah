package org.sigmah.shared.dto.search;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.Serializable;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO used to transfer the results of a search query as a simple JSON string.
 * 
 * @author Aditya Adhikary (aditya15007@iiitd.ac.in)
 */
public class SearchResultsDTO extends AbstractModelDataEntityDTO<Integer> implements Serializable
{
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
