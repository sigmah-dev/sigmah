package org.sigmah.client.page.project.pivot;

import java.util.Date;

import org.sigmah.shared.report.model.DateRange;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class DateRangeModel extends BaseModelData {
	private DateRange dateRange;
	
	public DateRangeModel(String label, Date start, Date end) {
		this.dateRange = new DateRange(start, end);
		set("label", label);
	}
	
	public DateRange getDateRange() {
		return dateRange;
	}
	
	public String getLabel() {
		return get("label");
	}
}