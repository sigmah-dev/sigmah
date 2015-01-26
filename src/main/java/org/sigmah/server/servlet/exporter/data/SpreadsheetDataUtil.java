package org.sigmah.server.servlet.exporter.data;

import org.sigmah.server.domain.Project;
import org.sigmah.server.servlet.exporter.base.Exporter;

public final class SpreadsheetDataUtil {

	private SpreadsheetDataUtil() {
		// Only provides static methods.
	}

	public static LogFrameExportData prepareLogFrameData(final Project project, final Exporter exporter) throws Throwable {
		/*
		 * final LogFrameExportData data = new LogFrameExportData(project, exporter); // TODO [INDICATORS] A major redesign
		 * of the indicators is in progress... // Get indicators from CommandHanler (only for the purpose to get
		 * aggregated(current) values of indicators). final List<Command<?>> commands = new ArrayList<Command<?>>(1); final
		 * GetIndicators command = new GetIndicators(); command.setUserDatabaseId(project.getId()); commands.add(command);
		 * final IndicatorListResult indicators = (IndicatorListResult) exporter.executeCommands(commands); for (final
		 * IndicatorDTO dto : indicators.getData()) { data.getIndMap().put(dto.getId(), dto); } return data;
		 */
		return null;
	}

	public static IndicatorEntryData prepareIndicatorsData(Integer projectId, final Exporter exporter) throws Throwable {
		/*
		 * // TODO [INDICATORS] A major redesign of the indicators is in progress... final List<Command<Result>> commands =
		 * new ArrayList<Command<Result>>(1); // get project commands.add(new GetProject(projectId)); final ProjectDTO
		 * project = (ProjectDTO) exporter.executeCommands(commands); final LayoutComposer composer = new LayoutComposer(new
		 * DateUtilCalendarImpl(), project); // get indicators commands.clear(); final GetIndicators command = new
		 * GetIndicators(); command.setUserDatabaseId(projectId); commands.add(command); final IndicatorListResult
		 * indicators = (IndicatorListResult) exporter.executeCommands(commands); // create spreadsheet data final
		 * IndicatorEntryData data = new IndicatorEntryData(exporter, indicators, project.getName()); // set indicator
		 * groups for (final IndicatorGroup group : indicators.getGroups()) { data.getGroupMap().put(group.getId(),
		 * group.getName()); } // put indicator entry for (final IndicatorDTO indicator : indicators.getData()) { final
		 * PivotTableElement pivot = composer.fixIndicator(indicator.getId()); commands.clear(); commands.add(new
		 * GenerateElement<PivotContent>(pivot)); final PivotContent content = (PivotContent)
		 * exporter.executeCommands(commands); data.getEntryMap().put(indicator.getId(), content.getData()); } // put
		 * ungrouped indicators final IndicatorGroup nonGroup = new IndicatorGroup(exporter.localize("unGrouped"));
		 * nonGroup.getIndicators().addAll(indicators.getUngroupedIndicators());
		 * data.getIndicators().getGroups().add(nonGroup); return data;
		 */
		return null;
	}
}
