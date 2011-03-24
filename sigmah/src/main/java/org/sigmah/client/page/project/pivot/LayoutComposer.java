package org.sigmah.client.page.project.pivot;

import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.report.model.DateDimension;
import org.sigmah.shared.report.model.DateRange;
import org.sigmah.shared.report.model.DateUnit;
import org.sigmah.shared.report.model.Dimension;
import org.sigmah.shared.report.model.DimensionType;
import org.sigmah.shared.report.model.PivotTableElement;

public class LayoutComposer {

	private int databaseId;
	private DateRange projectDateRange;
	
	public LayoutComposer(int databaseId, DateRange projectDateRange) {
		this.databaseId = databaseId;
		this.projectDateRange = projectDateRange;
	}
	
	public PivotTableElement fixIndicator(int indicatorId) {
		PivotTableElement pivot = new PivotTableElement();
		pivot.setShowEmptyCells(true);

		pivot.addRowDimension(new Dimension(DimensionType.Site));
		
		//pivot.addColDimension(new DateDimension(DateUnit.YEAR));
		pivot.addColDimension(new DateDimension(DateUnit.MONTH));
		
		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Database, databaseId);
		filter.addRestriction(DimensionType.Indicator, indicatorId );
		filter.setDateRange(projectDateRange);
		pivot.setFilter(filter);
		return pivot;
	}

	public PivotTableElement fixSite(int siteId) {
		PivotTableElement pivot = new PivotTableElement();
		pivot.setShowEmptyCells(true);

		pivot.addRowDimension(new Dimension(DimensionType.IndicatorCategory));
		pivot.addRowDimension(new Dimension(DimensionType.Indicator));
		
		pivot.addColDimension(new DateDimension(DateUnit.YEAR));
		pivot.addColDimension(new DateDimension(DateUnit.MONTH));
		
		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Database, databaseId);
		filter.addRestriction(DimensionType.Site, siteId);
		filter.setDateRange(projectDateRange);
		pivot.setFilter(filter);
		return pivot;
	}
	

	public PivotTableElement fixDateRange(DateRange dateRange) {
		PivotTableElement pivot = new PivotTableElement();
		pivot.setShowEmptyCells(true);

		pivot.addColDimension(new Dimension(DimensionType.IndicatorCategory));
		pivot.addColDimension(new Dimension(DimensionType.Indicator));
		
		pivot.addRowDimension(new Dimension(DimensionType.Site));
		
		
		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Database, databaseId);
		filter.setDateRange(dateRange);
		pivot.setFilter(filter);
		return pivot;
	}
}
