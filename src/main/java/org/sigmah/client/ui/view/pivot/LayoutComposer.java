package org.sigmah.client.ui.view.pivot;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sigmah.shared.dto.pivot.model.AdminDimension;
import org.sigmah.shared.dto.pivot.model.DateDimension;
import org.sigmah.shared.dto.pivot.model.DateUnit;
import org.sigmah.shared.dto.pivot.model.Dimension;
import org.sigmah.shared.dto.pivot.model.PivotTableElement;
import org.sigmah.shared.dto.AdminLevelDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.pivot.content.Filter;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.DateRange;
import org.sigmah.shared.util.Dates;
import org.sigmah.shared.util.Month;

public class LayoutComposer {

	private final int databaseId;
	private final DateRange projectDateRange;
	private final Dates dates;
	private final List<Dimension> adminDimensions;
	
	public LayoutComposer(Dates dates, ProjectDTO project) {
		this.databaseId = project.getId();
		this.dates = dates;
		this.projectDateRange = computeProjectDateRange(project.getStartDate(), project.getEndDate());
		this.adminDimensions = getAdminDimensions(project.getCountry());
	}
	

	/**
	 * Using the project start date as a guideline, generate a date
	 * range of at least six months.
	 * 
	 * @param startDate
	 *			Start date of the project.
	 * @param endDate 
	 *			End date of the project.
	 * @return
	 */
	private DateRange computeProjectDateRange(final Date startDate, final Date endDate) {
		final Date notNullStartDate = startDate != null ? startDate : new Date();
				
		final Month startMonth = dates.monthFromDate(notNullStartDate);
		Month endMonth =  endDate == null ? startMonth : dates.monthFromDate(endDate);
		
		if (Month.monthsBetween(startMonth, endMonth) < 6) {
			endMonth = startMonth.plus(6);
		}
		
		return dates.dateRange(startMonth, endMonth);
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

		pivot.addRowDimension(new Dimension(DimensionType.Activity));
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

			pivot.addRowDimension(new Dimension(DimensionType.Activity));
			pivot.addRowDimension(new Dimension(DimensionType.Indicator));
			
		} else {
			pivot.addColDimension(new Dimension(DimensionType.Activity));
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
