package org.sigmah.server.endpoint.export.sigmah.spreadsheet.data;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.page.project.pivot.LayoutComposer;
import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.server.util.DateUtilCalendarImpl;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.report.content.PivotContent;
import org.sigmah.shared.report.model.PivotTableElement;

public final class SpreadsheetDataUtil {
	private SpreadsheetDataUtil(){}
	
	public static LogFrameExportData prepareLogFrameData(
			final Project project,
			final Exporter exporter) throws Throwable {
		
		LogFrameExportData data = new LogFrameExportData(project,exporter);		

		/*
		 * Get indicators from CommandHanler 
		 * (only for the purpose to get aggregated(current)
		 * values of indicators) 
		 */	 
		List<Command> commands= new ArrayList<Command>(1);
		GetIndicators command = new GetIndicators();
		command.setUserDatabaseId(project.getId());		
		commands.add(command);
	 
        IndicatorListResult indicators = (IndicatorListResult) exporter.executeCommands(commands);		
		for (final IndicatorDTO dto : indicators.getData()) {
			data.getIndMap().put(dto.getId(), dto);
		}
		return data;
	}
	
public static IndicatorEntryData prepareIndicatorsData(
										Integer projectId,
										final Exporter exporter)throws Throwable {
		
 		final List<Command> commands= new ArrayList<Command>(1);
		
		//get project
		commands.add(new GetProject(projectId));
        final ProjectDTO project=(ProjectDTO)exporter.executeCommands(commands);
        final LayoutComposer composer = new LayoutComposer(new DateUtilCalendarImpl(), project);
        
        //get indicators
        commands.clear();
        final GetIndicators command = new GetIndicators();
		command.setUserDatabaseId(projectId);		
		commands.add(command);		  
        final IndicatorListResult indicators = 
        	(IndicatorListResult) exporter.executeCommands(commands);
        
        //create spreadsheet data
        final IndicatorEntryData data=new IndicatorEntryData(exporter,indicators,project.getName());
        
        //set indicator groups
        for(final IndicatorGroup group:indicators.getGroups()){
        	data.getGroupMap().put(group.getId(), group.getName());
        }
        
        //put indicator entry
        for (final IndicatorDTO indicator : indicators.getData()) {
			final PivotTableElement pivot = composer.fixIndicator(indicator.getId());
			commands.clear();
			commands.add(new GenerateElement<PivotContent>(pivot));
			final PivotContent content=(PivotContent)exporter.executeCommands(commands);           
			data.getEntryMap().put(indicator.getId(), content.getData());			
        }
      
        //put ungrouped indicators
        final IndicatorGroup nonGroup=new IndicatorGroup(exporter.localize("unGrouped"));
        nonGroup.getIndicators().addAll(indicators.getUngroupedIndicators());
        data.getIndicators().getGroups().add(nonGroup);
		return data;
	}
 


}
