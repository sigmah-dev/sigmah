package org.sigmah.server.servlet;

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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sigmah.server.dao.UserDatabaseDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Simple servlet called by the load balancer to verify that everything is correctly operating.
 * 
 * @author alexander
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class HealthCheckServlet extends HttpServlet {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4784106212676050668L;

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(HealthCheckServlet.class);

	/**
	 * The injected {@code UserDatabaseDAO}.
	 */
	private final UserDatabaseDAO userDatabaseDAO;

	@Inject
	public HealthCheckServlet(final UserDatabaseDAO userDatabaseDAO) {
		super();
		this.userDatabaseDAO = userDatabaseDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {

			final int count = userDatabaseDAO.countAll(); // Dumb query to ensure database is OK.

			if (LOG.isInfoEnabled()) {
				LOG.info("Healthcheck call succeed ; {} UserDatabase(s) found.", count);
			}

			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().print("OK");

		} catch (final Throwable caught) {

			if (LOG.isInfoEnabled()) {
				LOG.info("Healthcheck call failed.", caught);
			}

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection problem.");
		}
	}
}
