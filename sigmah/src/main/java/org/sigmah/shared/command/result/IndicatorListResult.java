package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.IndicatorDTO;

public class IndicatorListResult extends ListResult<IndicatorDTO> {

	public IndicatorListResult() {
		super();
	}

	public IndicatorListResult(List<IndicatorDTO> data) {
		super(data);
	}
	
	

}
