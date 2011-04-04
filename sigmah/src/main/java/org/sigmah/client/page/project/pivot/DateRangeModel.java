package org.sigmah.client.page.project.pivot;

import java.util.Date;

import org.sigmah.shared.report.content.MonthCategory;
import org.sigmah.shared.report.model.DateRange;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.google.gwt.i18n.client.DateTimeFormat;

public class DateRangeModel extends BaseModelData {
	private DateRange dateRange;
	private static final DateTimeFormat MONTH_FORMAT = DateTimeFormat.getFormat("MMM yy");

	
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
	
	public static DateRangeModel monthModel(MonthCategory month) {
		DateWrapper date = new DateWrapper(month.getYear(), month.getMonth()-1, 1);
		return monthModel(date);
	}
	
	public static DateRangeModel monthModel(DateWrapper month) {
		DateWrapper lastDayOfMonth = month.addMonths(1).addDays(-1);
		return new DateRangeModel(MONTH_FORMAT.format(month.asDate()), month.asDate(), lastDayOfMonth.asDate());
	}
}