package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.IndicatorDataSourceDTO;

/**
 * Retrieves the indicator DataSources for the given indicator.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetIndicatorDataSources extends AbstractCommand<ListResult<IndicatorDataSourceDTO>> {

	private int indicatorId;

	public GetIndicatorDataSources() {
		// Serialization.
	}

	public GetIndicatorDataSources(int indicatorId) {
		this.indicatorId = indicatorId;
	}

	public int getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}
}
