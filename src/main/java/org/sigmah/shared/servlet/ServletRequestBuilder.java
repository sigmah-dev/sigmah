package org.sigmah.shared.servlet;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

/**
 * Custom servlet request builder.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class ServletRequestBuilder {

	/**
	 * Void request callback.
	 */
	public static final RequestCallback Void = new RequestCallback() {

		@Override
		public void onResponseReceived(Request request, Response response) {
			// Does nothing.
		}

		@Override
		public void onError(Request request, Throwable exception) {
			// Does nothing.
		}

	};

	/**
	 * Default {@link RequestCallback} implementation with default {@code onError()} method.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static abstract class RequestCallbackAdapter implements RequestCallback {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onError(final Request request, final Throwable exception) {
			// Network error.
			if (Log.isErrorEnabled()) {
				Log.error("An unexpected error occured while executing the servlet request.", exception);
			}
		}

	}

	private final Method requestMethod;
	private final ServletUrlBuilder urlBuilder;
	private final Map<String, String> requestAttributes;

	/**
	 * Builds a new {@code ServletRequestBuilder} with the given parameters.
	 * 
	 * @param injector
	 *          The client-side injector.
	 * @param requestMethod
	 *          request method.
	 * @param servlet
	 *          The servlet.
	 * @param method
	 *          The servlet method to execute.
	 */
	public ServletRequestBuilder(final Injector injector, final Method requestMethod, final Servlet servlet, final ServletMethod method) {
		this.requestMethod = requestMethod;
		this.urlBuilder = new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), servlet, method);
		this.requestAttributes = new HashMap<String, String>();
		this.urlBuilder.addParameter(ServletConstants.AJAX, ClientUtils.toList(true));
	}

	/**
	 * Adds a parameter to the request URL.
	 * 
	 * @param key
	 *          The parameter key.
	 * @param values
	 *          The parameter value(s).
	 * @return the current instance of the builder.
	 */
	public ServletRequestBuilder addParameter(final RequestParameter key, final Object... values) {
		urlBuilder.addParameter(key, values);
		return this;
	}

	/**
	 * Adds a parameter to the request URL.
	 * 
	 * @param key
	 *          The parameter key.
	 * @param values
	 *          The parameter value(s).
	 * @return the current instance of the builder.
	 */
	public ServletRequestBuilder addParameter(final RequestParameter key, final List<? extends Object> values) {
		urlBuilder.addParameter(key, values);
		return this;
	}

	/**
	 * Removes the given parameter {@code key} from the request URL.
	 * 
	 * @param key
	 *          The parameter key.
	 * @return the current instance of the builder.
	 */
	public ServletRequestBuilder removeParameter(final RequestParameter key) {
		urlBuilder.removeParameter(key);
		return this;
	}

	/**
	 * <p>
	 * Adds a {@code POST} request parameter.<br/>
	 * The {@link #requestMethod} must be set to {@link RequestBuilder#POST}. Otherwise, the method does nothing.
	 * </p>
	 * <p>
	 * Empty or {@code null} value is skipped.<br/>
	 * {@code String.valueOf(value)} is used for given {@code value}.
	 * </p>
	 * 
	 * @param key
	 *          The POST parameter key.
	 * @param value
	 *          The POST parameter value.
	 * @return The current instance of the builder.
	 */
	public ServletRequestBuilder addPostParameter(final RequestParameter key, final Object value) {
		if (key != null && value != null && isPostMethod()) {
			requestAttributes.put(key.getRequestName(), String.valueOf(value));
		}
		return this;
	}

	/**
	 * Sends the request with its optional parameter(s) (including {@code POST} parameters).
	 * 
	 * @param callback
	 *          The {@code RequestCallback}.
	 * @throws ServletRequestException
	 *           If an error occurs during request call.
	 */
	public void send(final RequestCallback callback) throws ServletRequestException {

		final RequestBuilder requestBuilder = new RequestBuilder(requestMethod, urlBuilder.toString());

		requestBuilder.setCallback(callback != null ? callback : Void);

		final StringBuilder builder = new StringBuilder();

		if (ClientUtils.isNotEmpty(requestAttributes)) {

			final Iterator<String> iterator = requestAttributes.keySet().iterator();

			while (iterator.hasNext()) {
				final String next = iterator.next();
				final String attribute = requestAttributes.get(next);

				if (attribute != null) {
					builder.append(URL.encodeQueryString(next));
					builder.append('=');
					builder.append(URL.encodeQueryString(attribute));
					if (iterator.hasNext()) {
						builder.append('&');
					}
				}
			}
		}

		if (isPostMethod()) {
			requestBuilder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			requestBuilder.setRequestData(builder.length() > 0 ? builder.toString() : null);
		}

		try {

			requestBuilder.send();

		} catch (final RequestException e) {
			throw new ServletRequestException("Servlet request '" + builder + "' execution fails.", e);
		}
	}

	/**
	 * Returns if the current {@link #requestMethod} is set to {@code POST}.
	 * 
	 * @return {@code true} if the current {@link #requestMethod} is set to {@code POST}.
	 */
	private boolean isPostMethod() {
		return RequestBuilder.POST.equals(requestMethod);
	}

	/**
	 * Returns the current builder corresponding {@code GET} URL.<br/>
	 * If the builder contains {@code POST} parameters, they will <b>not</b> be included in the result.
	 * 
	 * @return the current builder corresponding {@code GET} URL.
	 */
	@Override
	public String toString() {
		return urlBuilder.toString();
	}

}
