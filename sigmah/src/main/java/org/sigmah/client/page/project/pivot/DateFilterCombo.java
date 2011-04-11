package org.sigmah.client.page.project.pivot;

import java.util.Date;

import org.sigmah.shared.report.model.DateRange;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.i18n.client.DateTimeFormat;

public class DateFilterCombo extends ComboBox<DateRangeModel> {

	public DateFilterCombo() {
		setStore(new ListStore());
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
        DateWrapper today = new DateWrapper();
        
        DateWrapper month = new DateWrapper(start.getFullYear(), start.getMonth(), 1);
        do {
        	DateWrapper lastDayOfMonth = month.addMonths(1).addDays(-1);
        	store.add(DateRangeModel.monthModel(month)); 
            month = month.addMonths(1);
        } while (month.before(today));
	}
}
