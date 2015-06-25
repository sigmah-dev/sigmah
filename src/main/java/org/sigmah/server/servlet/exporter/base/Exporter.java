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
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dispatch.DispatchException;

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
	 *  The dispatch instance.
	 */
	private final UserDispatch dispatch;

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
		this.dispatch = injector.getInstance(UserDispatch.class);

		// set up user's Language

		this.language = context.getLanguage();
		i18ntranslator = injector.getInstance(I18nServer.class);

		// Set the export format
		// BUGFIX #800: Fixed format reading method.
		final String[] formatArray = (String[]) parametersMap.get(RequestParameter.getRequestName(RequestParameter.FORMAT));
		final String formatString = formatArray != null && formatArray.length == 1 ? formatArray[0] : null;

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
	 * Execute the given command.
	 * 
	 * @param <C> 
	 *			Command type.
	 * @param <R> 
	 *			Result type.
	 * @param command 
	 *			Command to execute.
	 * @return 
	 *			Result of the command.
	 * @throws DispatchException 
	 *			If the command execution fails.
	 */
	public <C extends Command<R>, R extends Result> R execute(C command) throws DispatchException {
		return dispatch.execute(command, context);
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
