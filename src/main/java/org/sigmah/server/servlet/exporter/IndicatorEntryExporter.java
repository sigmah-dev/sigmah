package org.sigmah.server.servlet.exporter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.sigmah.client.page.RequestParameter;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.server.servlet.exporter.data.IndicatorEntryData;
import org.sigmah.server.servlet.exporter.data.SpreadsheetDataUtil;
import org.sigmah.server.servlet.exporter.template.ExportTemplate;
import org.sigmah.server.servlet.exporter.template.IndicatorEntryCalcTemplate;
import org.sigmah.server.servlet.exporter.template.IndicatorEntryExcelTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 * Exports indicators list and entries into spreadsheets
 * 
 * @author sherzod (v1.3)
 */
public class IndicatorEntryExporter extends Exporter {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(IndicatorEntryExporter.class);

	public IndicatorEntryExporter(final Injector injector, final HttpServletRequest req, ServletExecutionContext context) throws Exception {
		super(injector, req, context);
	}

	@Override
	public String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return localize("projectTabDataEntry") + "_" + format.format(new Date()) + getExtention();
	}

	@Override
	public void export(OutputStream output) throws Exception {
		// The project id.
		final String idString = requireParameter(RequestParameter.ID);
		final Integer id;

		try {
			id = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			LOG.error("[export] The id '" + idString + "' is invalid.", e);
			throw new Exception("The id '" + idString + "' is invalid.", e);
		}

		try {
			final IndicatorEntryData data = SpreadsheetDataUtil.prepareIndicatorsData(id, this);
			ExportTemplate template = null;
			switch (exportFormat) {
				case XLS:
					template = new IndicatorEntryExcelTemplate(data);
					break;
				case ODS:
					template = new IndicatorEntryCalcTemplate(data, null);
					break;
				default:
					LOG.error("[export] The export format '" + exportFormat + "' is unknown.");
					throw new ServletException("The export format '" + exportFormat + "' is unknown.");
			}
			template.write(output);
		} catch (Throwable e) {
			LOG.error("[export] Error during the workbook writing.", e);
			throw new Exception("Error during the workbook writing.");
		}
	}

}
