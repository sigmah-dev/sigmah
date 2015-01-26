package org.sigmah.server.inject;

import org.sigmah.client.security.SecureDispatchService;
import org.sigmah.server.dispatch.SecureDispatchServlet;
import org.sigmah.server.servlet.ExportServlet;
import org.sigmah.server.servlet.FileServlet;
import org.sigmah.server.servlet.HealthCheckServlet;
import org.sigmah.server.servlet.ImportServlet;
import org.sigmah.server.servlet.ManifestServlet;
import org.sigmah.server.servlet.SigmahHostController;
import org.sigmah.server.servlet.filter.CacheFilter;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.PersistFilter;

/**
 * Module to serves the servlets.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ServletModule extends com.google.inject.servlet.ServletModule {

	/**
	 * Log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ServletModule.class);

	/**
	 * Servlet remote service endpoint.
	 */
	public static final String ENDPOINT = "/sigmah/";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureServlets() {

		if (LOG.isInfoEnabled()) {
			LOG.info("Installing servlets module.");
		}

		// Filters.
		filter("/*").through(PersistFilter.class);
		filter("/*").through(CacheFilter.class);

		// Servlets.
		serve(ENDPOINT + SecureDispatchService.REMOTE_SERVICE_RELATIVE_PATH).with(SecureDispatchServlet.class);
		serve("/").with(SigmahHostController.class);
		serve("/healthcheck").with(HealthCheckServlet.class);
		serve(ENDPOINT + Servlet.FILE.getPathName()).with(FileServlet.class);
		serve(ENDPOINT + Servlet.MANIFEST.getPathName()).with(ManifestServlet.class);
		serve(ENDPOINT + Servlet.EXPORT.getPathName()).with(ExportServlet.class);
		serve(ENDPOINT + Servlet.IMPORT.getPathName()).with(ImportServlet.class);
	}
}
