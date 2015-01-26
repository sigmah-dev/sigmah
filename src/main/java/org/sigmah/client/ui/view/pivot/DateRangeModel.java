package org.sigmah.client.ui.view.pivot;

import java.util.Date;


import com.extjs.gxt.ui.client.data.BaseModelData;
import org.sigmah.shared.util.DateRange;

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