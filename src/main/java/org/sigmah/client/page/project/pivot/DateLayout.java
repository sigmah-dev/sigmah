package org.sigmah.client.page.project.pivot;

import java.util.Date;

import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.report.model.DateRange;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Layout fixed by time period. (indicators x site or site x indicators)
 * @author alexander
 *
 */
public class DateLayout extends PivotLayout {

	private final DateRangeModel dateRangeModel;
	private final boolean axesSwapped;
	
	public DateLayout(DateRangeModel dateRangeModel, boolean axesSwapped) {
		super();
		this.dateRangeModel = dateRangeModel;
		this.axesSwapped = axesSwapped;
	}
	
	@Override
	public String serialize() {
		DateRange dateRange = dateRangeModel.getDateRange();
		return "D" +  dateRangeModel.getLabel() + ":" + serialize(dateRange.getMinDate()) + ":" + serialize(dateRange.getMaxDate()) + 
		  ":" + (axesSwapped ? "T" : "F");
	}
	
	private String serialize(Date date) {
		return date == null ? "0" : Long.toString(date.getTime());
	}

	public DateRangeModel getModel() {
		return dateRangeModel;
	}

	public DateRange getDateRange() {
		return dateRangeModel.getDateRange();
	}
	

	public boolean getAxesSwapped() {
		return axesSwapped;
	}

	public DateLayout swapAxes() {
		return new DateLayout(dateRangeModel, !axesSwapped);
	}
	
	public static void deserializeDate(String text, AsyncCallback<PivotLayout> callback) {
		try {
			String parts[] = text.split(":");
			DateRangeModel model = new DateRangeModel(parts[0], deserializeDate(parts[1]), deserializeDate(parts[2]));
			DateLayout dateLayout = new DateLayout(model, parts[3].equals("T"));
			callback.onSuccess(dateLayout);
		} catch(Throwable caught) {
			callback.onFailure(caught);
		}
	}
	
	private static Date deserializeDate(String s) {
		if(s.equals("0")) {
			return null;
		} else {
			return new Date(Long.parseLong(s));
		}
	}
}
