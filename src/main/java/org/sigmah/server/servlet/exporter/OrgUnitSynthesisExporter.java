package org.sigmah.server.servlet.exporter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.server.servlet.exporter.data.OrgUnitSynthesisData;
import org.sigmah.server.servlet.exporter.template.ExportTemplate;
import org.sigmah.server.servlet.exporter.template.OrgUnitSynthesisCalcTemplate;
import org.sigmah.server.servlet.exporter.template.OrgUnitSynthesisExcelTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

public class OrgUnitSynthesisExporter extends Exporter {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(OrgUnitSynthesisExporter.class);

	public OrgUnitSynthesisExporter(final Injector injector, final HttpServletRequest req, ServletExecutionContext context) throws Exception {
		super(injector, req, context);
	}

	@Override
	public String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return localize("orgUnitSynthesis") + "_" + format.format(new Date()) + getExtention();
	}

	@Override
	public void export(OutputStream output) throws Exception {
		// The project id.
		final String idString = requireParameter(RequestParameter.ID);
		final Integer orgunitId;
		try {
			orgunitId = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			LOG.error("[export] The id '" + idString + "' is invalid.", e);
			throw new Exception("The id '" + idString + "' is invalid.", e);
		}

		try {
			// data
			final OrgUnitSynthesisData synthesisData = prepareSynthesisData(orgunitId);

			ExportTemplate template = null;

			switch (exportFormat) {

				case XLS: {
					final HSSFWorkbook wb = new HSSFWorkbook();
					template = new OrgUnitSynthesisExcelTemplate(synthesisData, wb, getContext(), getI18ntranslator(), getLanguage());
				}
					break;

				case ODS: {
					final SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
					template = new OrgUnitSynthesisCalcTemplate(synthesisData, doc, getContext(), getI18ntranslator(), getLanguage());
				}
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

	private OrgUnitSynthesisData prepareSynthesisData(Integer orgunitId) throws Throwable {
		return new OrgUnitSynthesisData(this, orgunitId, injector);
	}

}
