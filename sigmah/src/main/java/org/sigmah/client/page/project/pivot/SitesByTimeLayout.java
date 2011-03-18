package org.sigmah.client.page.project.pivot;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.report.content.PivotContent;
import org.sigmah.shared.report.model.DateDimension;
import org.sigmah.shared.report.model.DateUnit;
import org.sigmah.shared.report.model.Dimension;
import org.sigmah.shared.report.model.DimensionType;
import org.sigmah.shared.report.model.PivotElement;
import org.sigmah.shared.report.model.PivotTableElement;

import com.extjs.gxt.ui.client.event.Observable;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;


/**
 * Pivot Layout that crosses Sites with Time, 
 * filtered by Indicator.
 */
class SitesByTimeLayout extends PivotLayout {

	public static String LAYOUT_ID = "SxT";
	

	private final Dispatcher dispatcher;
	private final HasValue<PivotElement> pivotGrid;
	
	private int databaseId;
		
	public SitesByTimeLayout(Dispatcher dispatcher, Observable indicatorFilter, HasValue<PivotElement> gridPanel) {
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

		pivot.addRowDimension(new Dimension(DimensionType.Site));
		
		//pivot.addColDimension(new DateDimension(DateUnit.YEAR));
		pivot.addColDimension(new DateDimension(DateUnit.MONTH));
		
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
