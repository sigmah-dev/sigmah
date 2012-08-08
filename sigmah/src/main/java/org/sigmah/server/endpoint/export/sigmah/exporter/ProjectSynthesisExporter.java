package org.sigmah.server.endpoint.export.sigmah.exporter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.server.endpoint.export.sigmah.ExportException;
import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.IndicatorEntryData;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.LogFrameExportData;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.ProjectSynthesisData;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.SpreadsheetDataUtil;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.ExportTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.IndicatorEntryCalcTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.IndicatorEntryExcelTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.LogFrameCalcTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.LogFrameExcelTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.ProjectSynthesisCalcTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.ProjectSynthesisExcelTemplate;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.dto.ExportUtils;
import org.sigmah.shared.dto.ProjectDTO;

import com.google.inject.Injector;

public class ProjectSynthesisExporter extends Exporter{
 
	private static final Log log = LogFactory.getLog(ProjectSynthesisExporter.class);
	
	public ProjectSynthesisExporter(final Injector injector,final HttpServletRequest req) throws Throwable  {
		super(injector, req);
	}
	
	@Override
	public String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return localize("projectSynthesis") + "_" + format.format(new Date()) + getExtention();
	}

	@Override
	public void export(OutputStream output) throws ExportException {
		// The project id.
		final String idString = requireParameter(ExportUtils.PARAM_EXPORT_PROJECT_ID);
		final Integer projectId;
		try {
			projectId = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			log.error("[export] The id '" + idString + "' is invalid.", e);
			throw new ExportException("The id '" + idString + "' is invalid.",e);
		}
				 
		try {
			//data
			final ProjectSynthesisData synthesisData = prepareSynthesisData(projectId);
			LogFrameExportData logFrameData=null;
			IndicatorEntryData indicatorData=null;
			
			//appending options 
			final String typeString = requireParameter(ExportUtils.PARAM_EXPORT_TYPE);
			final ExportUtils.ExportType type= ExportUtils.ExportType.valueOfOrNull(typeString);
			 switch (type) {
	            case PROJECT_SYNTHESIS_LOGFRAME:{
	        		final Project project = injector.getInstance(EntityManager.class).find(Project.class, projectId);
	        		logFrameData=SpreadsheetDataUtil.prepareLogFrameData(project, this);
	            }break;
	            case PROJECT_SYNTHESIS_INDICATORS:{
	            	indicatorData = SpreadsheetDataUtil.prepareIndicatorsData(projectId, this);
	            }break;
	            case PROJECT_SYNTHESIS_LOGFRAME_INDICATORS:{
	            	//logframe data
	            	final Project project = injector.getInstance(EntityManager.class).find(Project.class, projectId);
	        		logFrameData=SpreadsheetDataUtil.prepareLogFrameData(project, this);
	        		logFrameData.setIndicatorsSheetExist(true);
	        		//indicator data
	        		indicatorData = SpreadsheetDataUtil.prepareIndicatorsData(projectId, this);	        			            	
	            }break;
			 }
			ExportTemplate template = null;
			switch (exportFormat) {
			case MS_EXCEL:{
				final HSSFWorkbook wb=new HSSFWorkbook();
				template = new ProjectSynthesisExcelTemplate(synthesisData,wb);
				if(logFrameData!=null)
					template=new LogFrameExcelTemplate(logFrameData,wb);
				if(indicatorData!=null)
					template=new IndicatorEntryExcelTemplate(indicatorData,wb);
			}break;
			case OPEN_DOCUMENT_SPREADSHEET:{
				final SpreadsheetDocument doc=SpreadsheetDocument.newSpreadsheetDocument();
				template = new ProjectSynthesisCalcTemplate(synthesisData,doc);
				if(logFrameData!=null)
					template=new LogFrameCalcTemplate(logFrameData,doc);
				if(indicatorData!=null)
					template=new IndicatorEntryCalcTemplate(indicatorData,doc);
			}break;
			default:
				log.error("[export] The export format '" + exportFormat + "' is unknown.");
                throw new ServletException("The export format '" + exportFormat + "' is unknown.");
			}			
			template.write(output);
		} catch (Throwable e) {
			log.error("[export] Error during the workbook writing.", e);
			throw new ExportException("Error during the workbook writing.");
		}
	}
	
 	@SuppressWarnings("rawtypes")
	private ProjectSynthesisData prepareSynthesisData(Integer projectId)throws Throwable {
		 		
  		final List<Command> commands= new ArrayList<Command>(1);
		
		//get project
		commands.add(new GetProject(projectId));
        final ProjectDTO project=(ProjectDTO)executeCommands(commands);                
		return new ProjectSynthesisData(this,project,injector.getInstance(EntityManager.class));
	}

}
