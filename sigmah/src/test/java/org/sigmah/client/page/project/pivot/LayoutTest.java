package org.sigmah.client.page.project.pivot;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;

import org.easymock.Capture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.server.endpoint.gwtrpc.CommandTestCase;
import org.sigmah.shared.report.model.PivotElement;
import org.sigmah.test.InjectionSupport;

import com.google.gwt.user.client.ui.HasValue;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/project-indicator.db.xml")
public class LayoutTest extends CommandTestCase {

	
	@Test
	public void siteByIndicators() {

		setUser(1);
					
		Capture<PivotElement> element = new Capture<PivotElement>();
		
		HasValue gridPanel = createMock(HasValue.class);
		gridPanel.setValue(capture(element));
		replay(gridPanel);
		
		SiteByIndicatorLayout layout = new SiteByIndicatorLayout(dispatcher, null, gridPanel);
		layout.activate(1);
		
		System.out.println(element.getValue().getContent());
		
	}
	
	@Test
	public void indicatorsByTime() {

		setUser(1);
					
		Capture<PivotElement> element = new Capture<PivotElement>();
		
		HasValue gridPanel = createMock(HasValue.class);
		gridPanel.setValue(capture(element));
		replay(gridPanel);
		
		IndicatorByTimeLayout layout = new IndicatorByTimeLayout(dispatcher, null, gridPanel);
		layout.activate(1);
		
		System.out.println(element.getValue().getContent());
		
	}
}
