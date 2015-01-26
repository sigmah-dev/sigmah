package org.sigmah.client.page.project.pivot;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.server.endpoint.gwtrpc.CommandTestCase;
import org.sigmah.server.util.DateUtilCalendarImpl;
import org.sigmah.shared.command.Month;
import org.sigmah.shared.date.DateUtil;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.report.model.DateRange;
import org.sigmah.test.InjectionSupport;

import com.google.gwt.user.client.rpc.AsyncCallback;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/project-indicator.db.xml")
public class PivotLayoutTest extends CommandTestCase {

	private DateUtil dateUtil = new DateUtilCalendarImpl();
	
	@Test
	public void dateSerialization() {
		
		final DateRange range = dateUtil.monthRange(new Month(2011,1));

		DateRangeModel model = new DateRangeModel("Jan 2011", range.getMinDate(), range.getMaxDate());
		DateLayout layout = new DateLayout(model, false);
		
		String serialized = layout.serialize();
		
		PivotLayout.deserialize(dispatcher, 1, serialized, new AsyncCallback<PivotLayout>() {
			
			@Override
			public void onSuccess(PivotLayout result) {
				DateLayout relayout = (DateLayout)result;
				assertThat(relayout.getModel().getLabel(), equalTo("Jan 2011"));
				assertThat(relayout.getAxesSwapped(), equalTo(false));
				assertThat(relayout.getDateRange(), equalTo(range));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				throw new AssertionError(caught);
			}
		});
	}
	
	@Test
	public void indicatorSerialization() {
		
		IndicatorDTO indicator = new IndicatorDTO();
		indicator.setId(1);
		indicator.setName("Nombre de menages ayant recu une kit nfi");
		
		IndicatorLayout layout = new IndicatorLayout(indicator);
		
		String serialized = layout.serialize();
		
		PivotLayout.deserialize(dispatcher, 1, serialized, new AsyncCallback<PivotLayout>() {

			@Override
			public void onFailure(Throwable caught) {
				throw new AssertionError(caught);
			}

			@Override
			public void onSuccess(PivotLayout result) {
				IndicatorLayout relayout = (IndicatorLayout)result;
				assertThat(relayout.getIndicator().getId(), equalTo(1));
				assertThat(relayout.getIndicator().getName(), equalTo("Nombre de menages ayant recu une kit nfi"));
			}
		});
		
	}
	
	@Test
	public void siteSerialization() {
		
		SiteDTO site = new SiteDTO();
		site.setId(2);
		site.setLocationName("Ngshwe");
		
		SiteLayout layout = new SiteLayout(site);
		
		PivotLayout.deserialize(dispatcher, 1, layout.serialize(), new AsyncCallback<PivotLayout>() {

			@Override
			public void onFailure(Throwable caught) {
				throw new AssertionError(caught);
			}

			@Override
			public void onSuccess(PivotLayout result) {
				SiteLayout resite = (SiteLayout)result;
				assertThat(resite.getSite().getId(), equalTo(2));
				assertThat(resite.getSite().getLocationName(), equalTo("Ngshwe"));
			}
		});
		
	}
	
}
