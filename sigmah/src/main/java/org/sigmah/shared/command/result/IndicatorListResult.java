package org.sigmah.shared.command.result;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;

public class IndicatorListResult extends ListResult<IndicatorDTO> {

	private List<IndicatorGroup> groups = new ArrayList<IndicatorGroup>();
	private List<IndicatorDTO> ungroupedIndicators = new ArrayList<IndicatorDTO>();

	public IndicatorListResult() {
		super();
	}

	public IndicatorListResult(List<IndicatorDTO> data) {
		super(data);
	}

	public List<IndicatorGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<IndicatorGroup> groups) {
		this.groups = groups;
	}

	public List<IndicatorDTO> getUngroupedIndicators() {
		return ungroupedIndicators;
	}

	public void setUngroupedIndicators(List<IndicatorDTO> ungroupedIndicators) {
		this.ungroupedIndicators = ungroupedIndicators;
	}
	
	
}
