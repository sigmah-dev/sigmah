package org.sigmah.client.page.project.pivot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sigmah.client.util.DateUtilGWTImpl;
import org.sigmah.shared.command.Month;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.date.DateUtil;
import org.sigmah.shared.dto.AdminLevelDTO;
import org.sigmah.shared.dto.CountryDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.report.model.AdminDimension;
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
	private List<Dimension> adminDimensions;
	
	public LayoutComposer(DateUtil dateUtil, ProjectDTO project) {
		this.databaseId = project.getId();
		this.dateUtil = dateUtil;
		this.projectDateRange = computeProjectDateRange(project.getStartDate(), project.getEndDate());
		this.adminDimensions = getAdminDimensions(project.getCountry());
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

		pivot.addRowDimensions(adminDimensions);
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
	

	public PivotTableElement fixDateRange(DateRange dateRange, boolean indicatorsInRows) {
		PivotTableElement pivot = new PivotTableElement();
		pivot.setShowEmptyCells(true);

		if(indicatorsInRows) {
			pivot.addColDimensions(adminDimensions);
			pivot.addColDimension(new Dimension(DimensionType.Site));	

			pivot.addRowDimension(new Dimension(DimensionType.IndicatorCategory));
			pivot.addRowDimension(new Dimension(DimensionType.Indicator));
			
		} else {
			pivot.addColDimension(new Dimension(DimensionType.IndicatorCategory));
			pivot.addColDimension(new Dimension(DimensionType.Indicator));
			
			pivot.addRowDimensions(adminDimensions);
			pivot.addRowDimension(new Dimension(DimensionType.Site));
		}
		
		
		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Database, databaseId);
		filter.setDateRange(dateRange);
		pivot.setFilter(filter);
		return pivot;
	}
	
	private List<Dimension> getAdminDimensions(CountryDTO country) {
		List<Dimension> dims = new ArrayList<Dimension>();
		Integer parentId = null;
		AdminLevelDTO level;
		while ( (level=getFirstChild(country, parentId)) != null) {
			dims.add(new AdminDimension(level.getId()));
			parentId = level.getId();
		}
		return dims;
	}
	
	private AdminLevelDTO getFirstChild(CountryDTO country, Integer parentId) {
		for(AdminLevelDTO level : country.getAdminLevels()) {
			if( (level.getParentLevelId() == null && parentId == null) ||
			    (level.getParentLevelId() != null && level.getParentLevelId().equals(parentId))) {
				return level;
			}
		}
		return null;
	}
}
