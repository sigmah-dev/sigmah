package org.sigmah.server.servlet;

import java.io.OutputStream;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sigmah.client.page.RequestParameter;
import org.sigmah.server.servlet.base.AbstractServlet;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.GlobalExportExporter;
import org.sigmah.server.servlet.exporter.LogFrameExporter;
import org.sigmah.server.servlet.exporter.OrgUnitSynthesisExporter;
import org.sigmah.server.servlet.exporter.ProjectReportExporter;
import org.sigmah.server.servlet.exporter.ProjectSynthesisExporter;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.server.servlet.exporter.models.CategoryTypeHandler;
import org.sigmah.server.servlet.exporter.models.ModelHandler;
import org.sigmah.server.servlet.exporter.models.OrgUnitModelHandler;
import org.sigmah.server.servlet.exporter.models.ProjectModelHandler;
import org.sigmah.server.servlet.exporter.models.ProjectReportModelHandler;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * File upload and download servlet.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ExportServlet extends AbstractServlet {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4349939424520611506L;

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ExportServlet.class);

	/**
	 * Injected application injector.
	 */
	private final Injector injector;

	@Inject
	public ExportServlet(Injector injector) {
		this.injector = injector;
	}

	/**
	 * See {@link ServletMethod#EXPORT_GLOBAL} for JavaDoc.
	 * 
	 * @param request
	 *          The HTTP request containing the file id parameter.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @param context
	 *          The execution context.
	 * @throws Exception
	 */
	protected void exportGlobal(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		executeExport(new GlobalExportExporter(injector, request, context), request, response);

	}

	/**
	 * See {@link ServletMethod#EXPORT_ORG_UNIT} for JavaDoc.
	 * 
	 * @param request
	 *          The HTTP request containing the file id parameter.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @param context
	 *          The execution context.
	 * @throws Exception
	 *           If an error occurs during process.
	 */
	protected void exportOrgUnit(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		executeExport(new OrgUnitSynthesisExporter(injector, request, context), request, response);

	}

	/**
	 * See {@link ServletMethod#EXPORT_PROJECT} for JavaDoc.
	 * 
	 * @param request
	 *          The HTTP request containing the file id parameter.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @param context
	 *          The execution context.
	 * @throws Exception
	 *           If an error occurs during process.
	 */
	protected void exportProject(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		executeExport(new ProjectSynthesisExporter(injector, request, context), request, response);

	}

	/**
	 * See {@link ServletMethod#EXPORT_PROJECT_LOGFRAME} for JavaDoc.
	 * 
	 * @param request
	 *          The HTTP request containing the file id parameter.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @param context
	 *          The execution context.
	 * @throws Exception
	 *           If an error occurs during process.
	 */
	protected void exportProjectLogFrame(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context)
			throws Exception {

		executeExport(new LogFrameExporter(injector, request, context), request, response);

	}

	/**
	 * See {@link ServletMethod#EXPORT_REPORT} for JavaDoc.
	 * 
	 * @param request
	 *          The HTTP request containing the file id parameter.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @param context
	 *          The execution context.
	 * @throws Exception
	 *           If an error occurs during process.
	 */
	protected void exportReport(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		executeExport(new ProjectReportExporter(injector, request, context), request, response);

	}

	/**
	 * Executes the given {@code exporter} corresponding export.
	 * 
	 * @param exporter
	 *          The exporter implementation.
	 * @param request
	 *          The HTTP request.
	 * @param response
	 *          The HTTP response.
	 * @throws Exception
	 *           If the export fails.
	 */
	private static void executeExport(final Exporter exporter, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		// Configures response headers.
		if (request.getHeader("User-Agent").indexOf("MSIE") != -1) {
			response.addHeader("Content-Disposition", "attachment; filename=" + exporter.getFileName());

		} else {
			response.addHeader("Content-Disposition", "attachment; filename=" + (exporter.getFileName()).replace(" ", "_"));
		}

		response.setContentType(exporter.getContentType());

		try (final OutputStream outputStream = response.getOutputStream()) {

			// Exports.
			exporter.export(outputStream);

		}
	}

	/**
	 * Export Report Model
	 */
	protected void exportReportModel(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context)
			throws Exception {

		executeExportModel(new ProjectReportModelHandler(), request, response);

	}

	/**
	 * Export OrgUnit Model
	 */
	protected void exportOrgUnitModel(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context)
			throws Exception {

		executeExportModel(new OrgUnitModelHandler(), request, response);

	}

	/**
	 * Export Category Model
	 */
	protected void exportCategoryModel(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context)
			throws Exception {

		executeExportModel(new CategoryTypeHandler(), request, response);

	}

	/**
	 * Export Project Model
	 */
	protected void exportProjectModel(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context)
			throws Exception {

		executeExportModel(new ProjectModelHandler(), request, response);

	}

	/**
	 * Run Export Model
	 * 
	 * @param handler
	 * @param request
	 * @param response
	 * @throws Exception
	 */

	private void executeExportModel(final ModelHandler handler, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		// Export the model

		final String identifier = request.getParameter(RequestParameter.getRequestName(RequestParameter.ID));

		try {

			String fileName = handler.exportModel(response.getOutputStream(), identifier, injector.getInstance(EntityManager.class));

			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition", "attachment; filename=\"" + this.escapeFileName(fileName) + ".dat\"");

		} catch (Exception ex) {

			LOG.error("Model export error : ", ex);
			throw ex;
		}

	}

	/**
	 * Replaces every character not included in [a-z][A-Z][0-9] by a '_'.
	 *
	 * @param fileName
	 *          The name to escape.
	 * @return An escaped file name.
	 */
	private String escapeFileName(String fileName) {
		final StringBuilder fileNameBuilder = new StringBuilder();

		char[] characters = fileName.toCharArray();
		for (char c : characters) {

			if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < '0' || c > '9'))
				c = '_';

			fileNameBuilder.append(c);
		}

		return fileNameBuilder.toString();
	}

}
