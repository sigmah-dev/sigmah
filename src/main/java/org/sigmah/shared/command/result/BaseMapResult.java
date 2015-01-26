package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.map.BaseMap;

/**
 * List of <code>BaseMap</code>s returned by the <code>GetBaseMaps</code> command.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.sigmah.shared.command.GetBaseMaps
 */
public class BaseMapResult implements Result {

	private List<BaseMap> baseMaps;

	protected BaseMapResult() {
		// Serialization.
	}

	public BaseMapResult(List<BaseMap> baseMaps) {
		this.baseMaps = baseMaps;
	}

	public List<BaseMap> getBaseMaps() {
		return baseMaps;
	}

	public void setBaseMaps(List<BaseMap> baseMaps) {
		this.baseMaps = baseMaps;
	}
}
