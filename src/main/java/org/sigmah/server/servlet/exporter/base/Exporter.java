package org.sigmah.server.servlet.exporter.base;

import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.sigmah.client.page.RequestParameter;
import org.sigmah.server.dao.impl.GlobalExportSettingsHibernateDAO;
import org.sigmah.server.domain.export.GlobalExportSettings;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.shared.Language;
import org.sigmah.shared.util.ExportUtils;

import com.google.inject.Injector;

/**
 * Represents an exporter.
 * 
 * @author tmi (v1.3)
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 */
public abstract class Exporter {

	/**
	 * The export parameters.
	 */
	protected final Map<RequestParameter, Object> parametersMap;

	/**
	 * The injector.
	 */
	protected final Injector injector;

	/**
	 * Export format
	 */
	protected ExportUtils.ExportFormat exportFormat;

	/**
	 * Language
	 */
	private Language language;

	/**
	 * The {@code i18n} server translator service.
	 */
	private final I18nServer i18ntranslator;

	/**
	 * ServletExecutionContext
	 */
	private final ServletExecutionContext context;

	/**
	 * Builds an new exporter.
	 * 
	 * @param injector
	 *          The application injector.
	 * @param req
	 *          The HTTP request.
	 * @param context
	 *          The execution context.
	 */
	@SuppressWarnings("unchecked")
	public Exporter(final Injector injector, final HttpServletRequest req, ServletExecutionContext context) throws Exception {
		this.context = context;
		this.injector = injector;
		this.parametersMap = req.getParameterMap();

		// set up user's Language

		this.language = context.getLanguage();
		i18ntranslator = injector.getInstance(I18nServer.class);

		// Set the export format
		final String formatString = req.getParameter(RequestParameter.FORMAT.name());

		if (formatString != null) {

			this.exportFormat = ExportUtils.ExportFormat.valueOfOrNull(formatString);

		} else {
			final Integer organizationId = context.getUser().getOrganization().getId();

			final GlobalExportSettingsHibernateDAO exportSettingDao = injector.getInstance(GlobalExportSettingsHibernateDAO.class);
			final GlobalExportSettings exportSettings = exportSettingDao.getGlobalExportSettingsByOrganization(organizationId);

			this.exportFormat = exportSettings.getDefaultOrganizationExportFormat();

		}
	}

	/**
	 * Retrieves a parameter with the given name. If the parameter doesn't exist, an exception is thrown.
	 * 
	 * @param parameter
	 *          The parameter name.
	 * @return The parameter value.
	 * @throws Exception
	 *           If the parameter doesn't exists.
	 */
	protected final String requireParameter(final RequestParameter parameter) throws Exception {

		final String[] param = (String[]) parametersMap.get(RequestParameter.getRequestName(parameter));
		if (param == null) {
			throw new Exception("The parameter '" + parameter + "' 'is missing.");
		}
		return param[0];
	}

	/**
	 * Gets the exported file name.
	 * 
	 * @return The exported file name.
	 */
	public abstract String getFileName();

	/**
	 * Returns document's MIME type
	 */
	public String getContentType() {
		return ExportUtils.getContentType(exportFormat);
	}

	/**
	 * Returns file extension
	 */
	public String getExtention() {
		return ExportUtils.getExtension(exportFormat);
	}

	/**
	 * Returns localized version of a key
	 */
	public String localize(String key) {
		return i18ntranslator.t(language, key);
	}

	/**
	 * Performs the export into the output stream.
	 * 
	 * @param output
	 *          The output stream.
	 * @throws Exception
	 *           If an error occurs during the export.
	 */
	public abstract void export(OutputStream output) throws Exception;

	/**
	 * get the ServletExecutionContext
	 * 
	 * @return ServletExecutionContext
	 */
	public ServletExecutionContext getContext() {
		return context;
	}

	public Language getLanguage() {
		return language;
	}

	public I18nServer getI18ntranslator() {
		return i18ntranslator;
	}

}
