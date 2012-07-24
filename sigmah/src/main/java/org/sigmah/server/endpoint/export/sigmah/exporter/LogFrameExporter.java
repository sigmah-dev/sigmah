package org.sigmah.server.endpoint.export.sigmah.exporter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.endpoint.export.sigmah.ExportException;
import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.LogFrameExportData;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.ExportTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.LogFrameCalcTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.LogFrameExcelTemplate;
import org.sigmah.server.endpoint.gwtrpc.handler.GetIndicatorsHandler;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.dto.ExportUtils;
import org.sigmah.shared.dto.IndicatorDTO;

import com.google.inject.Injector;

/**
 * Exports logical frameworks.
 * 
 * @author tmi
 */
public class LogFrameExporter extends Exporter {

	/**
	 * Logger.
	 */
	private static final Log log = LogFactory.getLog(LogFrameExporter.class);

	private final Injector injector;

	public LogFrameExporter(final Injector injector,final HttpServletRequest req) throws Throwable  {
		super(injector.getInstance(EntityManager.class), req);
		this.injector = injector;
	}
 

	@Override
	public String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return localize("logFrame") + "_" + format.format(new Date()) + getExtention();
	}

	@Override
	public void export(OutputStream output) throws ExportException {

		// The project id.
		final String idString = requireParameter(ExportUtils.PARAM_EXPORT_PROJECT_ID);
		final Integer id;
		try {
			id = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			log.error("[export] The id '" + idString + "' is invalid.", e);
			throw new ExportException("The id '" + idString + "' is invalid.",e);
		}
		
		// Retrieves the project 
		final Project project = em.find(Project.class, id);

		if (project == null) {
			log.error("[export] The project #" + id + " doesn't exist.");
			throw new ExportException("The project #" + id + " doesn't exist.");
		}
		try {			
			LogFrameExportData data = prepareData(project);				
			ExportTemplate template = null;
			switch (exportFormat) {
			case MS_EXCEL:
				template = new LogFrameExcelTemplate(data);
				break;
			case OPEN_DOCUMENT_SPREADSHEET:
				template = new LogFrameCalcTemplate(data);
				break;
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

	private LogFrameExportData prepareData(final Project project) throws Throwable {
		
		LogFrameExportData data = new LogFrameExportData(project,this);		

		/*
		 * Get indicators from CommandHanler 
		 * (only for the purpose to get aggregated(current)
		 * values of indicators) 
		 */
		GetIndicatorsHandler handler = injector.getInstance(GetIndicatorsHandler.class);
		GetIndicators command = GetIndicators.forDatabase(project.getId());
		IndicatorListResult result = (IndicatorListResult) handler.execute(command, null);
		for (final IndicatorDTO dto : result.getData()) {
			data.getIndMap().put(dto.getId(), dto);
		}
		return data;
	}




}
