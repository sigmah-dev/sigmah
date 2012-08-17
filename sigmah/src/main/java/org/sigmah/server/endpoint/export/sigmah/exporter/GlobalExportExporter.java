package org.sigmah.server.endpoint.export.sigmah.exporter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.endpoint.export.sigmah.ExportException;
import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.GlobalExportDataProvider;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.GlobalExportData;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.LogFrameExportData;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.SpreadsheetDataUtil;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.ExportTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.GlobalExportExcelTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.LogFrameCalcTemplate;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.template.LogFrameExcelTemplate;
import org.sigmah.shared.dto.ExportUtils;

import com.google.inject.Injector;

/*
 * Exporter for Projects list exportation
 * 
 * @author sherzod
 */
public class GlobalExportExporter extends Exporter{
 
	private static final Log log = LogFactory.getLog(GlobalExportExporter.class);
	
	public GlobalExportExporter(final Injector injector,final HttpServletRequest req) throws Throwable  {
		super(injector, req);
	}
	
	@Override
	public String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return localize("globalExport") + "_" + format.format(new Date()) + getExtention();
	}

	@Override
	public void export(OutputStream output) throws ExportException {					
		try {												
			final GlobalExportData data = prepareData();			
			
			ExportTemplate template = null;
			switch (exportFormat) {
			case MS_EXCEL:
				template = new GlobalExportExcelTemplate(data);
				break;
			case OPEN_DOCUMENT_SPREADSHEET:
				template = new GlobalExportExcelTemplate(data);
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
	
	private GlobalExportData prepareData() throws ExportException {

		Map<String, List<String[]>> exportData = null;
		final GlobalExportDataProvider dataProvider = 
			injector.getInstance(GlobalExportDataProvider.class);
		final EntityManager em = injector.getInstance(EntityManager.class);

		// data version
		final String versionStr = requireParameter(ExportUtils.PARAM_EXPORT_DATA_VERSION);
		final ExportUtils.ExportDataVersion version = 
			ExportUtils.ExportDataVersion.valueOfOrNull(versionStr);

		switch (version) {
		case LIVE_DATA: {
			try {
				Integer organizationId =
					Integer.parseInt(requireParameter(ExportUtils.PARAM_EXPORT_ORGANIZATION_ID));
				exportData = dataProvider.generateGlobalExportData(
						organizationId, em, locale.getLanguage());
			} catch (Exception e) {
				log.error("[export] Failed to generate global export data.", e);
				throw new ExportException(
						"Failed to generate global export data.");
			}
		}
			break;

		case BACKED_UP_DATA: {
			Integer globalExportId = 
				Integer.parseInt(requireParameter(ExportUtils.PARAM_EXPORT_GLOBAL_EXPORT_ID));
			exportData = dataProvider.getBackedupGlobalExportData(
					em,globalExportId);
		}
			break;
		}

		return new GlobalExportData(exportData);

	}

}
