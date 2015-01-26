package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.IndicatorDataSourceDTO;

public class IndicatorDataSourceList extends ListResult<IndicatorDataSourceDTO>{

	public IndicatorDataSourceList() {
		super();
	}

	public IndicatorDataSourceList(List<IndicatorDataSourceDTO> data) {
		super(data);
	}

	public IndicatorDataSourceList(ListResult<IndicatorDataSourceDTO> result) {
		super(result);
	}

}
