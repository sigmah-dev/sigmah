package org.sigmah.client.page.project.pivot;

import java.util.Date;

import org.sigmah.shared.command.Month;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.date.DateUtil;
import org.sigmah.shared.report.model.DateDimension;
import org.sigmah.shared.report.model.DateRange;
import org.sigmah.shared.report.model.DateUnit;
import org.sigmah.shared.report.model.Dimension;
import org.sigmah.shared.report.model.DimensionType;
import org.sigmah.shared.report.model.PivotTableElement;

public class LayoutComposer {

	private int databaseId;
	private DateRange projectDateRange;
	private DateUtil dateUtil;
	
	public LayoutComposer(DateUtil dateUtil, int databaseId, Date startDate, Date endDate) {
		this.databaseId = databaseId;
		this.dateUtil = dateUtil;
		this.projectDateRange = computeProjectDateRange(startDate, endDate);
	}
	
	/**
	 * Using the project start date as a guideline, generate a date
	 * range of at least six months.
	 * @param date
	 * @param endDate 
	 * @return
	 */
	private DateRange computeProjectDateRange(Date startDate, Date endDate) {
		Month startMonth = dateUtil.monthFromDate(startDate);
		Month endMonth =  endDate == null ? startMonth : dateUtil.monthFromDate(endDate);
		
		if(Month.monthsBetween(startMonth, endMonth) < 6) {
			endMonth = startMonth.plus(6);
		}
		
		return dateUtil.dateRange(startMonth, endMonth);
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
