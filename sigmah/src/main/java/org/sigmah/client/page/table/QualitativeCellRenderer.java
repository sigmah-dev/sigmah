package org.sigmah.client.page.table;

import java.util.Collections;
import java.util.List;

import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.report.content.PivotTableData.Axis;

public class QualitativeCellRenderer extends PivotCellRenderer {

	private List<String> labels;

	public QualitativeCellRenderer(IndicatorDTO indicator) {
		this.labels = indicator.getLabels();
		if(labels == null) {
			labels = Collections.emptyList();
		}
	}

	public QualitativeCellRenderer() {
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	@Override
	protected String formatValue(Axis rowAxis, double value) {
		int code = (int) value;
		if(code >= 1 && code <= labels.size()) {
			return labels.get(code-1);
		}
		return "";
	}
}
