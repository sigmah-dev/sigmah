package org.sigmah.shared.dto.pivot.content;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.command.result.Content;

/**
 * @author Alex Bertram (akbertram@gmail.com) (v1.3)
 */
public class PivotContent implements Content {

	public PivotContent() {

	}

	private org.sigmah.shared.util.Filter effectiveFilter;
	private List<FilterDescription> filterDescriptions;
	private PivotTableData data;

	public PivotContent(PivotTableData data, ArrayList<FilterDescription> filterDescriptions) {
		this.data = data;
		this.filterDescriptions = filterDescriptions;
	}

	public List<FilterDescription> getFilterDescriptions() {
		return filterDescriptions;
	}

	public void setFilterDescriptions(List<FilterDescription> filterDescriptions) {
		this.filterDescriptions = filterDescriptions;
	}

	public PivotTableData getData() {
		return data;
	}

	public void setData(PivotTableData data) {
		this.data = data;
	}

	public org.sigmah.shared.util.Filter getEffectiveFilter() {
		return effectiveFilter;
	}

	public void setEffectiveFilter(org.sigmah.shared.util.Filter effectiveFilter) {
		this.effectiveFilter = effectiveFilter;
	}

	@Override
	public String toString() {
		return "PivotContent:\n" + data.toString();
	}
}
