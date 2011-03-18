package org.sigmah.client.page.project.pivot;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.report.content.PivotContent;
import org.sigmah.shared.report.model.AdminDimension;
import org.sigmah.shared.report.model.Dimension;
import org.sigmah.shared.report.model.DimensionType;
import org.sigmah.shared.report.model.PivotElement;
import org.sigmah.shared.report.model.PivotTableElement;

import com.extjs.gxt.ui.client.event.Observable;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Layout which crosses Sites by Indicators, 
 * optionally filtered by time.
 * 
 * @author alexander
 *
 */
class SiteByIndicatorLayout extends PivotLayout {

	public static String LAYOUT_ID = "SxI";
	
	private final Dispatcher dispatcher;
	private final HasValue<PivotElement> pivotGrid;
	
	private int databaseId;
		
	public SiteByIndicatorLayout(Dispatcher dispatcher, Observable dateRange, HasValue<PivotElement> gridPanel) {
		this.dispatcher = dispatcher;
		this.pivotGrid = gridPanel;
	}

	@Override
	public void activate(int databaseId) {
		this.databaseId = databaseId;
		load();
	}

	private void load() {
		final PivotTableElement pivot = new PivotTableElement();
		pivot.setShowEmptyCells(true);
		pivot.addColDimension(new Dimension(DimensionType.IndicatorCategory));
		pivot.addColDimension(new Dimension(DimensionType.Indicator));
		pivot.addRowDimension(new Dimension(DimensionType.Site));
		
		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Database, databaseId);
		pivot.setFilter(filter);
		
		dispatcher.execute(new GenerateElement<PivotContent>(pivot), null, new AsyncCallback<PivotContent>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Pivot", "Pivot failed: " + caught.getMessage(), null);
				
			}

			@Override
			public void onSuccess(PivotContent content) {
				pivot.setContent(content);
				pivotGrid.setValue(pivot);
			}
		});
	}
	

}
