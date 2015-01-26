package org.sigmah.server.servlet.exporter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.server.domain.Project;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.server.servlet.exporter.data.IndicatorEntryData;
import org.sigmah.server.servlet.exporter.data.LogFrameExportData;
import org.sigmah.server.servlet.exporter.data.ProjectSynthesisData;
import org.sigmah.server.servlet.exporter.data.SpreadsheetDataUtil;
import org.sigmah.server.servlet.exporter.template.ExportTemplate;
import org.sigmah.server.servlet.exporter.template.IndicatorEntryCalcTemplate;
import org.sigmah.server.servlet.exporter.template.IndicatorEntryExcelTemplate;
import org.sigmah.server.servlet.exporter.template.LogFrameCalcTemplate;
import org.sigmah.server.servlet.exporter.template.LogFrameExcelTemplate;
import org.sigmah.server.servlet.exporter.template.ProjectSynthesisCalcTemplate;
import org.sigmah.server.servlet.exporter.template.ProjectSynthesisExcelTemplate;
import org.sigmah.shared.util.ExportUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

public class ProjectSynthesisExporter extends Exporter {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ProjectSynthesisExporter.class);

	public ProjectSynthesisExporter(final Injector injector, final HttpServletRequest req, ServletExecutionContext context) throws Exception {
		super(injector, req, context);
	}

	@Override
	public String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return localize("projectSynthesis") + "_" + format.format(new Date()) + getExtention();
	}

	@Override
	public void export(OutputStream output) throws Exception {
		// The project id.
		final String idString = requireParameter(RequestParameter.ID);
		final Integer projectId;

		try {

			projectId = Integer.parseInt(idString);

		} catch (NumberFormatException e) {
			LOG.error("[export] The id '" + idString + "' is invalid.", e);
			throw new Exception("The id '" + idString + "' is invalid.", e);
		}

		try {

			// data
			final ProjectSynthesisData synthesisData = prepareSynthesisData(projectId);
			LogFrameExportData logFrameData = null;
			IndicatorEntryData indicatorData = null;

			// appending options
			final String typeString = requireParameter(RequestParameter.TYPE);
			final ExportUtils.ExportType type = ExportUtils.ExportType.valueOfOrNull(typeString);

			switch (type) {

				case PROJECT_SYNTHESIS_LOGFRAME: {
					final Project project = injector.getInstance(EntityManager.class).find(Project.class, projectId);
					logFrameData = SpreadsheetDataUtil.prepareLogFrameData(project, this);
				}
					break;

				case PROJECT_SYNTHESIS_INDICATORS: {
					indicatorData = SpreadsheetDataUtil.prepareIndicatorsData(projectId, this);
				}
					break;

				case PROJECT_SYNTHESIS_LOGFRAME_INDICATORS: {
					// logframe data
					final Project project = injector.getInstance(EntityManager.class).find(Project.class, projectId);
					logFrameData = SpreadsheetDataUtil.prepareLogFrameData(project, this);
					logFrameData.setIndicatorsSheetExist(true);
					// indicator data
					indicatorData = SpreadsheetDataUtil.prepareIndicatorsData(projectId, this);
				}
					break;

				default:
					// TODO Throw exception ?
					break;
			}

			ExportTemplate template = null;
			switch (exportFormat) {

				case XLS: {
					final HSSFWorkbook wb = new HSSFWorkbook();
					template = new ProjectSynthesisExcelTemplate(synthesisData, wb, getContext(), getI18ntranslator(), getLanguage());
					if (logFrameData != null)
						template = new LogFrameExcelTemplate(logFrameData, wb);
					if (indicatorData != null)
						template = new IndicatorEntryExcelTemplate(indicatorData, wb);
				}
					break;

				case ODS: {
					final SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
					template = new ProjectSynthesisCalcTemplate(synthesisData, doc, getContext(), getI18ntranslator(), getLanguage());
					if (logFrameData != null)
						template = new LogFrameCalcTemplate(logFrameData, doc);
					if (indicatorData != null)
						template = new IndicatorEntryCalcTemplate(indicatorData, doc);
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

	private ProjectSynthesisData prepareSynthesisData(Integer projectId) throws Throwable {
		return new ProjectSynthesisData(this, projectId, injector);
	}

}
