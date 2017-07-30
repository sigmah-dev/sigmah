package org.sigmah.shared.dto.search;

import org.sigmah.shared.dto.base.DTO;

public class FilesSolrIndexDTO implements DTO{

	public boolean result;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
}
