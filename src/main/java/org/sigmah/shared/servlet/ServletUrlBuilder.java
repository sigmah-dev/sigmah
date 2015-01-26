package org.sigmah.shared.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sigmah.client.page.PageManager;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;

import com.google.gwt.http.client.URL;

/**
 * Convenient servlet URL builder.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class ServletUrlBuilder {

	// Injected resources.
	private final AuthenticationProvider authenticationProvider;
	private final PageManager pageManager;

	// Servlet URL required resources.
	private final Servlet servlet;
	private final ServletMethod servletMethod;
	private final Map<String, List<String>> parameters;

	/**
	 * Builds a new {@code ServletUrlBuilder} with the given parameters.
	 * 
	 * @param authenticationProvider
	 * @param pageManager
	 * @param servlet
	 *          The servlet (required).
	 * @param method
	 *          The servlet method to execute (required).
	 */
	public ServletUrlBuilder(AuthenticationProvider authenticationProvider, PageManager pageManager, final Servlet servlet, final ServletMethod method) {
		this.authenticationProvider = authenticationProvider;
		this.pageManager = pageManager;
		this.servlet = servlet;
		this.servletMethod = method;
		this.parameters = new HashMap<String, List<String>>();
	}

	/**
	 * <p>
	 * Adds a parameter to the request URL.<br/>
	 * If a previous parameter was already present with the given {@code key}, its value is override.
	 * </p>
	 * <p>
	 * Empty and {@code null} values are skipped.<br/>
	 * {@code String.valueOf(value)} is used for each preserved value.
	 * </p>
	 * 
	 * @param key
	 *          The parameter key.
	 * @param values
	 *          The parameter value(s).
	 * @return The current instance of the builder.
	 */
	public ServletUrlBuilder addParameter(final RequestParameter key, final Object... values) {
		return addParameter(key, values != null ? Arrays.asList(values) : null);
	}

	/**
	 * <p>
	 * Adds a parameter to the request URL.<br/>
	 * If a previous parameter was already present with the given {@code key}, its value is override.
	 * </p>
	 * <p>
	 * Empty and {@code null} values are skipped.<br/>
	 * {@code String.valueOf(value)} is used for each preserved value.
	 * </p>
	 * 
	 * @param key
	 *          The parameter key.
	 * @param values
	 *          The parameter value(s).
	 * @return The current instance of the builder.
	 */
	public ServletUrlBuilder addParameter(final RequestParameter key, final List<? extends Object> values) {
		return addParameter(key != null ? key.getRequestName() : null, values);
	}

	/**
	 * <p>
	 * Adds a parameter to the request URL.<br/>
	 * If a previous parameter was already present with the given {@code key}, its value is override.
	 * </p>
	 * <p>
	 * Empty and {@code null} values are skipped.<br/>
	 * {@code String.valueOf(value)} is used for each preserved value.
	 * </p>
	 * 
	 * @param key
	 *          The parameter key.
	 * @param values
	 *          The parameter value(s).
	 * @return The current instance of the builder.
	 */
	ServletUrlBuilder addParameter(final String key, final List<? extends Object> values) {

		if (ClientUtils.isBlank(key) || ClientUtils.isEmpty(values)) {
			return this;
		}

		final List<String> stringValues = new ArrayList<String>();
		for (final Object value : values) {
			if (value == null || ClientUtils.isBlank(value.toString())) {
				continue;
			}
			stringValues.add(String.valueOf(value));
		}

		parameters.put(key, stringValues);

		return this;
	}

	/**
	 * Removes the given parameter {@code key} from the request URL.
	 * 
	 * @param key
	 *          The parameter key.
	 * @return the current instance of the builder.
	 */
	public ServletUrlBuilder removeParameter(final RequestParameter key) {
		if (key != null) {
			parameters.remove(key.getRequestName());
		}
		return this;
	}

	/**
	 * <p>
	 * Builds the URL with all its parameters.
	 * </p>
	 * <p>
	 * Necessary parameters (<em>authentication token</em>, etc.) are automatically added into the request parameters.
	 * </p>
	 * 
	 * @return The generated URL.
	 */
	private String buildUrl() {

		// Adds session token id in parameters map.
		addParameter(ServletConstants.AUTHENTICATION_TOKEN, ClientUtils.toList(authenticationProvider.get().getAuthenticationToken()));
		addParameter(ServletConstants.SERVLET_METHOD, ClientUtils.toList(servletMethod.getName()));
		addParameter(ServletConstants.ORIGIN_PAGE_TOKEN, ClientUtils.toList(pageManager.getCurrentPageToken()));
		addParameter(ServletConstants.RANDOM, ClientUtils.toList(Math.random()));

		final StringBuilder builder = new StringBuilder();

		builder.append(servlet.getUrl()).append('?');
		final Iterator<String> keyIt = parameters.keySet().iterator();

		while (keyIt.hasNext()) {

			final String key = keyIt.next();
			final String encodedKey = URL.encodeQueryString(key);
			final List<String> params = parameters.get(key);

			builder.append(encodedKey).append('=');

			if (ClientUtils.isNotEmpty(params)) {
				// Key value(s).
				final Iterator<String> paramIt = params.iterator();

				while (paramIt.hasNext()) {
					builder.append(URL.encodeQueryString(paramIt.next()));
					if (paramIt.hasNext()) {
						builder.append('&').append(encodedKey).append('=');
					}
				}
			}

			if (keyIt.hasNext()) {
				builder.append('&');
			}
		}

		return builder.toString();
	}

	/**
	 * Displays the full URL with all its parameters.
	 * 
	 * @return The displayed URL.
	 * @see #buildUrl()
	 */
	@Override
	public String toString() {
		return buildUrl();
	}

}
