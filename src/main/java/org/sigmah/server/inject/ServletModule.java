package org.sigmah.server.inject;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
import org.sigmah.client.ui.view.calendar.DesEncrypter;
import org.sigmah.shared.util.DesEncrypterImpl;

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
                serve(ENDPOINT + DesEncrypter.REMOTE_SERVICE_RELATIVE_PATH_ENC).with(DesEncrypterImpl.class);
	}
}
