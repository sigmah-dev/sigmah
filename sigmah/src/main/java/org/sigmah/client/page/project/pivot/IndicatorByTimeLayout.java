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
 * Layout which crosses indicators by time,
 * optionally filtered by site
 * 
 * 
 * @author alexander
 *
 */
public class IndicatorByTimeLayout extends PivotLayout {

	public static final String LAYOUT_ID = "IxT";
	
	private final Dispatcher dispatcher;
	private final HasValue<PivotElement> pivotGrid;
	
	public IndicatorByTimeLayout(Dispatcher dispatcher, Observable siteCombo, HasValue<PivotElement> gridPanel) {
		this.dispatcher = dispatcher;
		this.pivotGrid = gridPanel;
	}

	@Override
	public void activate(int databaseId) {
		load(databaseId);
	}
	

	private void load(int databaseId) {
		final PivotTableElement pivot = new PivotTableElement();
		pivot.setShowEmptyCells(true);
		pivot.addRowDimension(new Dimension(DimensionType.IndicatorCategory));
		pivot.addRowDimension(new Dimension(DimensionType.Indicator));
		
		pivot.addColDimension(new DateDimension(DateUnit.YEAR));
		//pivot.addColDimension(new DateDimension(DateUnit.MONTH));
		
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
