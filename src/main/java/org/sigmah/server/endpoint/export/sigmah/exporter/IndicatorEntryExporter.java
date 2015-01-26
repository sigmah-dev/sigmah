/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.exporter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.endpoint.export.sigmah.ExportException;
import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.IndicatorEntryData;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.SpreadsheetDataUtil;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.ExportTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.IndicatorEntryCalcTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.IndicatorEntryExcelTemplate;
import org.sigmah.shared.dto.ExportUtils;

import com.google.inject.Injector;



/*
 * Exports indicators list and entries into spreadsheets 
 * @author sherzod
 */
public class IndicatorEntryExporter extends Exporter{

	private static final Log log = LogFactory.getLog(IndicatorEntryExporter.class);
	
	public IndicatorEntryExporter(final Injector injector,final HttpServletRequest req) throws Throwable  {
		super(injector, req);
	}
	
	@Override
	public String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return localize("projectTabDataEntry") + "_" + format.format(new Date()) + getExtention();
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
				 
		try {			
			final IndicatorEntryData data = SpreadsheetDataUtil.prepareIndicatorsData(id, this);				
			ExportTemplate template = null;
			switch (exportFormat) {
			case XLS:
				template = new IndicatorEntryExcelTemplate(data);
				break;
			case ODS:
				template = new IndicatorEntryCalcTemplate(data,null);
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
	
	 
}
