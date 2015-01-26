package org.sigmah.server.servlet.exporter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.sigmah.client.page.RequestParameter;
import org.sigmah.server.domain.Project;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.server.servlet.exporter.data.LogFrameExportData;
import org.sigmah.server.servlet.exporter.data.SpreadsheetDataUtil;
import org.sigmah.server.servlet.exporter.template.ExportTemplate;
import org.sigmah.server.servlet.exporter.template.LogFrameCalcTemplate;
import org.sigmah.server.servlet.exporter.template.LogFrameExcelTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 * Exports logical frameworks.
 * 
 * @author tmi (v1.3)
 */
public class LogFrameExporter extends Exporter {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(LogFrameExporter.class);

	public LogFrameExporter(final Injector injector, final HttpServletRequest req, ServletExecutionContext context) throws Exception {
		super(injector, req, context);
	}

	@Override
	public String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return localize("logFrame") + "_" + format.format(new Date()) + getExtention();
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

		// Retrieves the project
		final Project project = injector.getInstance(EntityManager.class).find(Project.class, id);

		if (project == null) {
			LOG.error("[export] The project #" + id + " doesn't exist.");
			throw new Exception("The project #" + id + " doesn't exist.");
		}
		try {
			final LogFrameExportData data = SpreadsheetDataUtil.prepareLogFrameData(project, this);
			ExportTemplate template = null;
			switch (exportFormat) {
				case XLS:
					template = new LogFrameExcelTemplate(data);
					break;
				case ODS:
					template = new LogFrameCalcTemplate(data, null);
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
