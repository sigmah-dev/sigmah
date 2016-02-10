package org.sigmah.server.servlet.base;

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
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.server.conf.Properties;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.util.DomainFilters;
import org.sigmah.server.inject.ServletModule;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.security.SecureSessionValidator;
import org.sigmah.server.security.SecureSessionValidator.Access;
import org.sigmah.server.servlet.util.Servlets;
import org.sigmah.shared.conf.PropertyKey;
import org.sigmah.shared.security.InvalidSessionException;
import org.sigmah.shared.security.UnauthorizedAccessException;
import org.sigmah.shared.servlet.ServletConstants;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.util.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.http.client.Response;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * Abstract additional servlet which secures access to its methods.<br/>
 * All additional <em>secured</em> servlet should inherit this abstract layer.
 * </p>
 * The declared child servlet methods must have this signature:
 * 
 * <pre>
 * [method_name] (HttpServletRequest request, HttpServletResponse response, ServletExecutionContext context) throws [any exception(s)];
 * </pre>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public abstract class AbstractServlet extends HttpServlet {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 301456647415093255L;

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractServlet.class);

	/**
	 * HTML servlet error page filename.
	 */
	private static final String ERROR_PAGE_NAME = "servlet-error.html";

	/**
	 * Injected secure session validator.
	 */
	@Inject
	private SecureSessionValidator secureSessionValidator;

	/**
	 * Injected application properties service.
	 */
	@Inject
	private Properties properties;

	/**
	 * Injected {@link EntityManager} provider.
	 */
	@Inject
	private Provider<EntityManager> entityManagerProvider;

	/**
	 * Injected {@link Mapper}.
	 */
	@Inject
	private Mapper mapper;

	/**
	 * HTML error page template.
	 */
	private String template;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void init(final ServletConfig config) throws ServletException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Reading HTML error page template.");
		}

		try (final InputStream is = getClass().getResourceAsStream(ERROR_PAGE_NAME)) {

			template = Servlets.readAll(is);

			// Replaces tags.
			template = template.replaceAll(Pattern.quote("<!-- ${AppName} -->"), Matcher.quoteReplacement(properties.getProperty(PropertyKey.APP_NAME)));

		} catch (final IOException e) {
			throw new ServletException("Cannot read the HTML page template.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void log(final String msg) {
		this.log(msg, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void log(final String message, final Throwable t) {
		if (t != null) {
			if (LOG.isErrorEnabled()) {
				LOG.error(message, t);
			}
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug(message);
			}
		}
	}

	/**
	 * Secures the given {@code servletMethod} execution.
	 * 
	 * @param request
	 *          The HTTP request.
	 * @param response
	 *          The HTTP response.
	 * @param servletMethod
	 *          Java servlet method to execute once user session has been secured.
	 * @throws ServletException
	 *           If the servlet execution fails.
	 */
	private void secureServlet(final HttpServletRequest request, final HttpServletResponse response, final Method servletMethod) throws ServletException {

		if (servletMethod == null) {
			if (LOG.isErrorEnabled()) {
				LOG.error("The given servlet method {} is null.", servletMethod);
			}
			throw new IllegalArgumentException("Servlet method is required.");
		}

		User user = null;

		try {

			// Validates the user session and user access.
			final String authenticationToken = request.getParameter(ServletConstants.AUTHENTICATION_TOKEN);
			final String originPageToken = request.getParameter(ServletConstants.ORIGIN_PAGE_TOKEN);

			final String servletPath = request.getRequestURI().replaceFirst(ServletModule.ENDPOINT, "");
			final Servlet servletEnum = Servlet.fromPathName(servletPath);
			final ServletMethod servletMethodEnum = ServletMethod.fromMethodName(servletMethod.getName());

			final Access access = secureSessionValidator.validate(authenticationToken, servletEnum, servletMethodEnum, originPageToken);
			user = access.getUser();

			switch (access.getAccessType()) {

				case INVALID_SESSION:

					if (LOG.isDebugEnabled()) {
						LOG.debug("SERVLET METHOD EXECUTION FAILED - Servlet method: '{}' ; User: '{}' ; Error: Invalid auth token '{}'.", servletMethod,
							Servlets.logUser(user), authenticationToken);
					}

					throw new InvalidSessionException("Your session is no longer valid.");

				case UNAUTHORIZED_ACCESS:

					if (LOG.isDebugEnabled()) {
						LOG.debug("SERVLET METHOD EXECUTION FAILED - Servlet method: '{}' ; User: '{}' ; Error: Unauthorized process.", servletMethod,
							Servlets.logUser(user));
					}

					throw new UnauthorizedAccessException("You are not authorized to execute this process.");

				default:

					// Access granted, executes servlet method.
					if (LOG.isDebugEnabled()) {
						LOG.debug("SERVLET METHOD EXECUTION GRANTED - Servlet method: '{}' ; User: '{}'.", servletMethod, Servlets.logUser(user));
					}

					// Activate filters into hibernate session.
					DomainFilters.applyUserFilter(user, entityManagerProvider.get());

					final StopWatch chrono = new StopWatch();
					chrono.start();

					servletMethod.setAccessible(true);
					servletMethod.invoke(this, request, response, new ServletExecutionContext(access.getUser(), request, originPageToken));

					if (LOG.isDebugEnabled()) {
						LOG.debug("SERVLET METHOD '{}' EXECUTED IN {} MS.", servletMethod, chrono.getTime());
					}
			}

		} catch (final InvocationTargetException e) {

			// NO NEED TO LOG EXCEPTION HERE.

			if (e.getTargetException() instanceof ServletException) {
				// Servlet exception.
				throw (ServletException) e.getTargetException();

			} else if (e.getTargetException() instanceof ConstraintViolationException) {
				// Bean validation failed.
				final ConstraintViolationException cve = (ConstraintViolationException) e.getTargetException();

				if (LOG.isErrorEnabled()) {
					LOG.error("SERVLET METHOD EXECUTION FAILED - Servlet method: '"
						+ servletMethod
						+ "' ; User: '"
						+ Servlets.logUser(user)
						+ "' ; Error: A bean validation failed during servlet method execution. Consider performing the validation on client-side.\n"
						+ Servlets.logConstraints(cve.getConstraintViolations()));
				}

				throw new ServletException(e.getCause().getMessage(), cve);

			} else {
				throw new ServletException(e.getCause().getMessage(), e.getTargetException());
			}

		} catch (final Throwable e) {
			// Server unknown error.
			throw new ServletException(e.getMessage(), e);
		}
	}

	/**
	 * Retrieves {@code java} method to execute from {@code request} and calls
	 * {@link #secureServlet(HttpServletRequest, HttpServletResponse, Method)}.
	 * 
	 * @param servletMethodName
	 *          The real servlet method name ({@code doGet}, {@code doPost}, etc.).
	 * @param request
	 *          The HTTP request.
	 * @param response
	 *          The HTTP response.
	 * @throws ServletException
	 *           If an error occurs while executing servlet process.
	 */
	private void secureServletMethod(final String servletMethodName, final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Executing specific '{}' servlet method.", servletMethodName);
		}

		// Retrieving method name from request.
		final String methodName = request.getParameter(ServletConstants.SERVLET_METHOD);

		boolean popupDestination = false;

		try {

			if (LOG.isDebugEnabled()) {
				LOG.debug("Retrieving by reflection the given servlet method '{}'.", methodName);
			}

			if (StringUtils.isBlank(methodName)) {
				return;
			}

			// Retrieving servlet method.
			final Method servletMethod = getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class, ServletExecutionContext.class);

			final ServletMethod servletMethodEnum = ServletMethod.fromMethodName(servletMethod.getName());
			popupDestination = servletMethodEnum != null && servletMethodEnum.isPopup();

			// Secure servlet method.
			secureServlet(request, response, servletMethod);

		} catch (final StatusServletException e) {
			handleException(request, response, servletMethodName, popupDestination, e, e.getStatusCode());

		} catch (final Throwable caught) {
			handleException(request, response, servletMethodName, popupDestination, caught, Response.SC_INTERNAL_SERVER_ERROR);
		}
	}

	// ---------------------------------------------------------------------------------------
	//
	// SECURED DEFAULT SERVLET METHODS.
	//
	// ---------------------------------------------------------------------------------------

	/**
	 * Servlet {@code GET} method name.
	 */
	private static final String DO_GET_METHOD_NAME = "doGet";

	@Override
	final protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		secureServletMethod("doPost", request, response);
	}

	@Override
	final protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		secureServletMethod(DO_GET_METHOD_NAME, request, response);
	}

	@Override
	final protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		secureServletMethod("doDelete", request, response);
	}

	@Override
	final protected void doOptions(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		secureServletMethod("doOptions", request, response);
	}

	@Override
	final protected void doHead(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		secureServletMethod("doHead", request, response);
	}

	@Override
	final protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		secureServletMethod("doPut", request, response);
	}

	@Override
	final protected void doTrace(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		secureServletMethod("doTrace", request, response);
	}

	// ---------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------

	/**
	 * Returns the application {@link Properties} service.
	 * 
	 * @return the application {@link Properties} service, never {@code null}.
	 */
	protected final Properties prop() {
		return properties;
	}

	/**
	 * Returns the application {@link Mapper} service.
	 * 
	 * @return the application {@link Mapper} service, never {@code null}.
	 */
	protected final Mapper mapper() {
		return mapper;
	}

	/**
	 * Returns the given {@code paramKey} corresponding value from the {@code request}.
	 * 
	 * @param request
	 *          The HTTP request.
	 * @param paramKey
	 *          The {@link RequestParameter} key.
	 * @param acceptNull
	 *          {@code true} to accept a {@code null} value result, {@code false} to throw a
	 *          {@link StatusServletException} if the value is {@code null}.
	 * @return The given {@code paramKey} corresponding value from the {@code request}.
	 * @throws StatusServletException
	 *           If the parameter value is {@code null} <b>and</b> {@code acceptNull} is set to {@code false}.
	 */
	protected static final String getParameter(final HttpServletRequest request, final RequestParameter paramKey, boolean acceptNull)
			throws StatusServletException {

		final String value = ClientUtils.deletePreTags(request.getParameter(paramKey.getRequestName()));

		if (StringUtils.isBlank(value) || "null".equals(value)) {

			if (acceptNull) {
				return null;
			}

			if (LOG.isWarnEnabled()) {
				LOG.warn("No value for parameter key '{}'.", paramKey);
			}

			throw new StatusServletException(Response.SC_BAD_REQUEST);
		}

		return value;
	}

	/**
	 * Returns the given {@code paramKey} corresponding {@link Integer} value from the {@code request}.
	 * 
	 * @param request
	 *          The HTTP request.
	 * @param paramKey
	 *          The {@link RequestParameter} key.
	 * @param acceptNull
	 *          {@code true} to accept a {@code null} value result, {@code false} to throw a
	 *          {@link StatusServletException} if the value is {@code null}.
	 * @return The given {@code paramKey} corresponding {@link Integer} value from the {@code request}.
	 * @throws StatusServletException
	 *           If the parameter value is {@code null} <b>and</b> {@code acceptNull} is set to {@code false}.
	 */
	protected static final Integer getIntegerParameter(final HttpServletRequest request, final RequestParameter paramKey, boolean acceptNull)
			throws StatusServletException {

		final String intValue = getParameter(request, paramKey, acceptNull);

		if (StringUtils.isBlank(intValue)) {
			if (acceptNull) {
				return null;
			}
			throw new StatusServletException(Response.SC_BAD_REQUEST);
		}

		try {

			return Integer.parseInt(intValue);

		} catch (final NumberFormatException e) {
			LOG.error("Error while parsing the integer parameter '" + intValue + "'.", e);
			throw new StatusServletException(Response.SC_BAD_REQUEST);
		}
	}

	/**
	 * Returns the given {@code paramKey} corresponding {@link Boolean} value from the {@code request}.
	 * 
	 * @param request
	 *          The HTTP request.
	 * @param paramKey
	 *          The {@link RequestParameter} key.
	 * @param acceptNull
	 *          {@code true} to accept a {@code null} value result, {@code false} to throw a
	 *          {@link StatusServletException} if the value is {@code null}.
	 * @return The given {@code paramKey} corresponding {@link Boolean} value from the {@code request}.
	 * @throws StatusServletException
	 *           If the parameter value is {@code null} <b>and</b> {@code acceptNull} is set to {@code false}.
	 */
	protected static final Boolean getBooleanParameter(final HttpServletRequest request, final RequestParameter paramKey, boolean acceptNull)
			throws StatusServletException {

		final String booleanValue = getParameter(request, paramKey, acceptNull);

		if (StringUtils.isBlank(booleanValue)) {
			if (acceptNull) {
				return null;
			}
			throw new StatusServletException(Response.SC_BAD_REQUEST);
		}

		try {

			return Boolean.parseBoolean(booleanValue);

		} catch (final NumberFormatException e) {
			throw new StatusServletException(Response.SC_BAD_REQUEST);
		}
	}

	/**
	 * <p>
	 * Handles the {@code caught} exception.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>If {@code GET} access (direct access and not ajax call), writes into the {@code response} the HTML error page
	 * content.</li>
	 * <li>Else, writes into the {@code response} the given {@code errorCode} as header and
	 * {@link ServletConstants#ERROR_RESPONSE_CONTENT} as content.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param request
	 *          The HTTP request.
	 * @param response
	 *          The HTTP response.
	 * @param servletMethodName
	 *          The {@link ServletMethod} value.
	 * @param popupDestination
	 *          Is the servlet process destined to be displayed into a pop-up window?
	 * @param caught
	 *          The throwable.
	 * @param errorCode
	 *          The error code set on the {@code response}.
	 */
	private void handleException(final HttpServletRequest request, final HttpServletResponse response, final String servletMethodName,
			final boolean popupDestination, final Throwable caught, final int errorCode) {

		if (LOG.isErrorEnabled()) {
			LOG.error("Exception while executing '" + getClass().getName() + '#' + servletMethodName + "' servlet method.", caught);
		}

		try {
			response.setContentType(FileType.HTML.getContentType());

			final String htmlMessage = caught.getClass().getSimpleName() + " : " + caught.getMessage();
			final boolean ajaxCall = ClientUtils.isTrue(request.getParameter(ServletConstants.AJAX));

			if (DO_GET_METHOD_NAME.equals(servletMethodName) && !ajaxCall) {

				// If the servlet method is executed using HTTP {@code GET} method.
				String html = template;
				html = html.replaceAll(Pattern.quote("<!-- ${MessageContent} -->"), Matcher.quoteReplacement(htmlMessage));
				html = html.replaceAll(Pattern.quote("<!-- ${ButtonDisplay} -->"), Servlets.cssDisplay(popupDestination));

				response.setCharacterEncoding(Servlets.UTF8_CHARSET);
				response.getWriter().write(html);

			} else {
				// Other method.
				response.setStatus(errorCode);
				response.getWriter().write(ServletConstants.buildErrorResponse(errorCode));
			}

		} catch (final IOException ioe) {
			// Nothing to do ; 'getWriter()' has just failed.
			if (LOG.isErrorEnabled()) {
				LOG.error("'getWriter()' method has raised an exception.", ioe);
			}
		}
	}

}
