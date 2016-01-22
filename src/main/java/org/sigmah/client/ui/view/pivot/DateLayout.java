package org.sigmah.client.ui.view.pivot;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.shared.util.DateRange;

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
		} catch(Exception caught) {
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
