package org.sigmah.client.page.project.pivot;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.server.endpoint.gwtrpc.CommandTestCase;
import org.sigmah.server.util.DateUtilCalendarImpl;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.date.DateUtil;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.shared.report.content.PivotContent;
import org.sigmah.shared.report.content.PivotTableData.Axis;
import org.sigmah.shared.report.model.PivotTableElement;
import org.sigmah.test.InjectionSupport;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/project-indicator.db.xml")
public class LayoutTest extends CommandTestCase {

	
	private LayoutComposer composer;
	private DateUtil dateUtil = new DateUtilCalendarImpl();
	
	@Before
	public void setupComposer() {
		composer = new LayoutComposer(new DateUtilCalendarImpl(), 1, ymd(2010,1,15), null);
	}
	
	
	private Date ymd(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}


	@Test
	public void dateFixed() throws CommandException {

		setUser(1);
		PivotTableElement element = executeLayout(composer.fixDateRange(dateUtil.monthRange(2010, 1)));			
	
		System.out.println(element.getContent());

		List<Axis> indicatorColumns = element.getContent().getData().getRootColumn().getDescendantsAtDepth(2);
	
		assertThat(indicatorColumns.size(), equalTo(4));
		
	}
	
	@Test
	public void siteFixed() throws CommandException {

		setUser(1);
					
		PivotTableElement element = executeLayout(composer.fixSite(1));
		
		System.out.println(element.getContent());
		
	}
	
	@Test
	public void siteFixedWithNoDataEntered() throws CommandException {

		setUser(1);
					
		PivotTableElement element = executeLayout(composer.fixSite(4));
		
		System.out.println(element.getContent());
		
	}
	
	
	@Test
	public void indicatorFixed() throws CommandException {

		setUser(1);
					
		PivotTableElement element = executeLayout(composer.fixSite(1));
		
		System.out.println(element.getContent());
		
	}
	
		
	
	private PivotTableElement executeLayout(PivotTableElement element) throws CommandException {
		PivotContent content = execute(new GenerateElement<PivotContent>(element));
		element.setContent(content);
		return element;
	}
}
