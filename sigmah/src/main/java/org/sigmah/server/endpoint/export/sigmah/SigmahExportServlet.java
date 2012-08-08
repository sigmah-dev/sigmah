package org.sigmah.server.endpoint.export.sigmah;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.endpoint.export.sigmah.exporter.IndicatorEntryExporter;
import org.sigmah.server.endpoint.export.sigmah.exporter.LogFrameExporter;
import org.sigmah.server.endpoint.export.sigmah.exporter.ProjectReportExporter;
import org.sigmah.server.endpoint.export.sigmah.exporter.ProjectSynthesisExporter;
import org.sigmah.shared.dto.ExportUtils;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * Manages exports.
 * 
 * @author tmi
 * 
 */
@Singleton
public class SigmahExportServlet extends HttpServlet {

    private static final long serialVersionUID = 8161472823847155801L;

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(SigmahExportServlet.class);

    /**
     * The entity manager.
     */
    private final Injector injector;

    @Inject
    public SigmahExportServlet(Injector injector) {
        this.injector = injector;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {

        try {

            //TODO: Verify the identity and the rights of the current user.

            // The exported entity type.
            final String typeString = request.getParameter(ExportUtils.PARAM_EXPORT_TYPE);
            final ExportUtils.ExportType type = ExportUtils.ExportType.valueOfOrNull(typeString);

            if (type == null) {
                log.error("[doGet] The export type '" + typeString + "' is unknown.");
                throw new ServletException("The export type '" + typeString + "' is unknown.");
            }

            // Builds the exporter.
            final Exporter exporter;
            switch (type) {
	            case PROJECT_SYNTHESIS:
	            case PROJECT_SYNTHESIS_LOGFRAME:
	            case PROJECT_SYNTHESIS_INDICATORS:		           	
	            case PROJECT_SYNTHESIS_LOGFRAME_INDICATORS:	
	                exporter = new ProjectSynthesisExporter(injector,request);
	                break;
	            case PROJECT_LOG_FRAME:
	                exporter = new LogFrameExporter(injector,request);
	                break;
	            case PROJECT_INDICATOR_LIST:
	                exporter = new IndicatorEntryExporter(injector,request);
	                break;           
	            case PROJECT_REPORT:
	                exporter = new ProjectReportExporter(injector,request);
	                break;
	            default:
	                log.error("[doGet] The export type '" + type + "' is unknown.");
	                throw new ServletException("The export type '" + type + "' is unknown.");
            }

            // Configures response.
            resp.setContentType(exporter.getContentType());
            if (request.getHeader("User-Agent").indexOf("MSIE") != -1) {
                resp.addHeader("Content-Disposition", "attachment; filename=" + exporter.getFileName());
            } else {
                resp.addHeader("Content-Disposition",
                        "attachment; filename=" + (exporter.getFileName()).replace(" ", "_"));
            }

            // Exports.
            try {
                final OutputStream outputStream = resp.getOutputStream();
                exporter.export(outputStream);
                outputStream.close();

            } catch (ExportException e) {
                log.error("[doGet] An error occurred during the export", e);
                throw new ServletException("An error occurred during the export.", e);
            }

        } catch (Throwable e) {
            resp.setStatus(500);
            log.error("[doGet] An error occurred during the export", e);
            throw new ServletException("An error occurred during the export.", e);
        }
    }
}
