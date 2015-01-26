package org.sigmah.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sigmah.server.conf.Properties;
import org.sigmah.server.servlet.util.Servlets;
import org.sigmah.server.util.Languages;
import org.sigmah.shared.conf.PropertyKey;
import org.sigmah.shared.util.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Serve the Sigmah main page (that handles both the login and the application).
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class SigmahHostController extends HttpServlet {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7520225767501595508L;

	/**
	 * Log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SigmahHostController.class);

	/**
	 * HTML page template filename.
	 */
	private static final String WELCOME_PAGE_NAME = "index.html";

	/**
	 * HTML page template.
	 */
	private String template;

	/**
	 * Sigmah properties.
	 */
	@Inject
	private Properties properties;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Reading HTML page template.");
		}

		try (final InputStream is = getClass().getResourceAsStream(WELCOME_PAGE_NAME)) {

			template = Servlets.readAll(is);

			// Replaces tags.
			template = template.replaceAll(Pattern.quote("<!-- ${AppName} -->"), Matcher.quoteReplacement(properties.getProperty(PropertyKey.APP_NAME)));
			template = template.replaceAll(Pattern.quote("<!-- ${AppVersion} -->"), Matcher.quoteReplacement(properties.getProperty(PropertyKey.VERSION_NUMBER)));
			template = template.replaceAll(Pattern.quote("<!-- ${AppMapsKey} -->"), Matcher.quoteReplacement(properties.getProperty(PropertyKey.MAPS_KEY)));

		} catch (IOException e) {
			throw new ServletException("Cannot read the HTML page template.", e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Get the Sigmah welcome page.");
		}
		
		resp.setContentType(FileType.HTML.getContentType());
		resp.setCharacterEncoding(Servlets.UTF8_CHARSET);
		resp.getWriter().write(template.replaceAll(Pattern.quote("<!-- ${AppLocale} -->"), Matcher.quoteReplacement(getLocaleTag(req))));

	}

	/**
	 * Gets the HTML meta tag for gwt locale porperty.
	 * 
	 * @param request
	 *          The HTTP request.
	 * @return The tag.
	 */
	private static String getLocaleTag(final HttpServletRequest request) {
		return "<meta name='gwt:property' content='locale=" + Languages.getLanguage(request).getLocale() + "'>";
	}

}
