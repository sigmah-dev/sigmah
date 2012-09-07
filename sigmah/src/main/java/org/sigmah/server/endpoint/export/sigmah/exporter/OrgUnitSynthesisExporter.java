package org.sigmah.server.endpoint.export.sigmah.exporter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.server.endpoint.export.sigmah.ExportException;
import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.OrgUnitSynthesisData;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.ExportTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.OrgUnitSynthesisCalcTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.OrgUnitSynthesisExcelTemplate;
import org.sigmah.shared.dto.ExportUtils;

import com.google.inject.Injector;

public class OrgUnitSynthesisExporter extends Exporter{
 
	private static final Log log = LogFactory.getLog(OrgUnitSynthesisExporter.class);
	
	public OrgUnitSynthesisExporter(final Injector injector,final HttpServletRequest req) throws Throwable  {
		super(injector, req);
	}
	
	@Override
	public String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return localize("orgUnitSynthesis") + "_" + format.format(new Date()) + getExtention();
	}

	@Override
	public void export(OutputStream output) throws ExportException {
		// The project id.
		final String idString = requireParameter(ExportUtils.PARAM_EXPORT_ORGUNIT_ID);
		final Integer orgunitId;
		try {
			orgunitId = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			log.error("[export] The id '" + idString + "' is invalid.", e);
			throw new ExportException("The id '" + idString + "' is invalid.",e);
		}
				 
		try {
			//data
			final OrgUnitSynthesisData synthesisData = prepareSynthesisData(orgunitId);
			 
			ExportTemplate template = null;
			switch (exportFormat) {
			case XLS:{
				final HSSFWorkbook wb=new HSSFWorkbook();
				template = new OrgUnitSynthesisExcelTemplate(synthesisData,wb);				 
			}break;
			case ODS:{
				final SpreadsheetDocument doc=SpreadsheetDocument.newSpreadsheetDocument();
				template = new OrgUnitSynthesisCalcTemplate(synthesisData,doc);				 
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
	private OrgUnitSynthesisData prepareSynthesisData(Integer orgunitId)throws Throwable {
 		return new OrgUnitSynthesisData(this,orgunitId,injector,locale);
	}

}
