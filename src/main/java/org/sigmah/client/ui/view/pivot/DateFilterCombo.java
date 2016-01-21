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

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.sigmah.shared.dto.pivot.content.MonthCategory;
import org.sigmah.shared.util.DateRange;

public class DateFilterCombo extends ComboBox<DateRangeModel> {

	static final DateTimeFormat MONTH_FORMAT = DateTimeFormat.getFormat("MMM yy");

	public DateFilterCombo() {
		setStore(new ListStore<DateRangeModel>());
		setDisplayField("label");
		setTriggerAction(TriggerAction.ALL);
	}
	
	public DateRange getSelectedDateRange() {
		assert getValue() != null;
		return value.getDateRange();
	}
	
	
	public void fillMonths(Date startDate) {
		
		store.removeAll();
		
        DateWrapper start = new DateWrapper(startDate);
        DateWrapper today = new DateWrapper().addMonths(6);
        
        DateWrapper month = new DateWrapper(start.getFullYear(), start.getMonth(), 1);
        do {
        	DateWrapper lastDayOfMonth = month.addMonths(1).addDays(-1);
        	store.add(DateFilterCombo.monthModel(month)); 
            month = month.addMonths(1);
        } while (month.before(today));
	}

	public static DateRangeModel monthModel(MonthCategory month) {
		DateWrapper date = new DateWrapper(month.getYear(), month.getMonth()-1, 1);
		return monthModel(date);
	}

	public static DateRangeModel monthModel(DateWrapper month) {
		DateWrapper lastDayOfMonth = month.addMonths(1).addDays(-1);
		return new DateRangeModel(DateFilterCombo.MONTH_FORMAT.format(month.asDate()), month.asDate(), lastDayOfMonth.asDate());
	}
}
