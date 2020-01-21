package org.sigmah.server.servlet.exporter.data;

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


import javax.validation.constraints.NotNull;
import org.sigmah.client.ui.view.pivot.LayoutComposer;
import org.sigmah.server.domain.Project;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.server.util.CalendarDates;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.pivot.content.PivotContent;
import org.sigmah.shared.dto.pivot.model.PivotTableElement;

public final class SpreadsheetDataUtil {

	private SpreadsheetDataUtil() {
		// Only provides static methods.
	}

	@NotNull
	public static LogFrameExportData prepareLogFrameData(final Project project, final Exporter exporter) throws Throwable {
		// TODO [INDICATORS] A major redesign of the indicators is in progress... 
		
		final LogFrameExportData data = new LogFrameExportData(project, exporter);
		
		// Get indicators from CommandHanler (only for the purpose to get aggregated(current) values of indicators).
		final IndicatorListResult indicators = exporter.execute(new GetIndicators(project.getId()));
		for (final IndicatorDTO dto : indicators.getData()) {
			data.getIndMap().put(dto.getId(), dto);
		}
		return data;
	}

	@NotNull
	public static IndicatorEntryData prepareIndicatorsData(Integer projectId, final Exporter exporter) throws Throwable {
		// TODO [INDICATORS] A major redesign of the indicators is in progress... 
		
		// Get project
        final ProjectDTO project = exporter.execute(new GetProject(projectId, null));
        final LayoutComposer composer = new LayoutComposer(new CalendarDates(), project);
        
        // Get indicators
        final IndicatorListResult indicators = exporter.execute(new GetIndicators(project));
        
        // Create spreadsheet data
        final IndicatorEntryData data = new IndicatorEntryData(exporter, indicators, project.getName());
        
        // Set indicator groups
        for(final IndicatorGroup group:indicators.getGroups()) {
        	data.getGroupMap().put(group.getId(), group.getName());
        }
        
        // Put indicator entry
        for (final IndicatorDTO indicator : indicators.getData()) {
			final PivotTableElement pivot = composer.fixIndicator(indicator.getId());
			final PivotContent content = exporter.execute(new GenerateElement<PivotContent>(pivot));
			data.getEntryMap().put(indicator.getId(), content.getData());
            final PivotTableElement pivotyear = composer.YearExcel(indicator.getId());
            final PivotContent contentyear = exporter.execute(new GenerateElement<PivotContent>(pivotyear));
            data.getEntryMap().put(indicator.getId()+1, contentyear.getData());
        }
      
        // Put ungrouped indicators
        final IndicatorGroup nonGroup = new IndicatorGroup(exporter.localize("unGrouped"));
        nonGroup.getIndicators().addAll(indicators.getUngroupedIndicators());
        data.getIndicators().getGroups().add(nonGroup);
		
		return data;
	}
}
