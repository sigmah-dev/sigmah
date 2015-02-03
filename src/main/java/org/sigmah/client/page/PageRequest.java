package org.sigmah.client.page;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.util.Pair;

/**
 * Page request.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public final class PageRequest {

	/**
	 * Character used to reference a page token.
	 */
	public static final String URL_TOKEN = "#";

	/**
	 * Character used to separate optional params in the URL.
	 */
	private static final String PARAM_SEPARATOR = "&";
	private static final String PARAM_PATTERN = PARAM_SEPARATOR + "(?!" + PARAM_SEPARATOR + ")";
	private static final String PARAM_ESCAPE = PARAM_SEPARATOR + PARAM_SEPARATOR;

	/**
	 * Character used to assign a param value.
	 */
	private static final String VALUE_SEPARATOR = "=";
	private static final String VALUE_PATTERN = VALUE_SEPARATOR + "(?!" + VALUE_SEPARATOR + ")";
	private static final String VALUE_ESCAPE = VALUE_SEPARATOR + VALUE_SEPARATOR;

	/**
	 * The {@link Page} instance.
	 */
	private final Page page;

	/**
	 * Page request <b>URL</b> parameters.<br/>
	 * <em>They can be considered as HTTP {@code GET} parameters.</em>
	 */
	private final Map<RequestParameter, String> parameters;

	/**
	 * Page request <b>data object</b> parameters.<br/>
	 * <em>They can be considered as HTTP {@code POST} parameters.</em>
	 */
	private final Map<RequestParameter, Object> dataParameters;

	/**
	 * Initializes a new {@code PageRequest} instance.
	 * 
	 * @param page
	 *          The page associated to the page request.
	 */
	public PageRequest(final Page page) {
		this(page, null, null);
	}

	/**
	 * Initializes a new {@code PageRequest} instance, clone of the given {@code request}.
	 * 
	 * @param request
	 *          The {@code PageRequest} instance to clone.
	 */
	public PageRequest(final PageRequest request) {
		this(request != null ? request.page : null, request != null ? request.parameters : null, request != null ? request.dataParameters : null);
	}

	/**
	 * Initializes a new {@code PageRequest} instance with the given arguments.
	 * 
	 * @param page
	 *          The page.
	 * @param parameters
	 *          The URL parameters. If {@code null}, an empty map is initialized.
	 * @param dataParameters
	 *          The data parameters. If {@code null}, an empty map is initialized.
	 */
	private PageRequest(final Page page, final Map<RequestParameter, String> parameters, final Map<RequestParameter, Object> dataParameters) {
		this.page = page;
		this.parameters = parameters != null ? new HashMap<RequestParameter, String>(parameters) : new HashMap<RequestParameter, String>(1);
		this.dataParameters = dataParameters != null ? new HashMap<RequestParameter, Object>(dataParameters) : new HashMap<RequestParameter, Object>(1);
	}

	/**
	 * Returns the {@link Page} associated to the current request.
	 * 
	 * @return The {@link Page} associated to the current request.
	 */
	public Page getPage() {
		return page;
	}

	/**
	 * Returns all the request parameters map.
	 * 
	 * @return The request parameters map (never {@code null}).
	 */
	public Map<RequestParameter, String> getParameters() {
		return getParameters(false);
	}

	/**
	 * Returns the request parameters map.
	 * 
	 * @param onlyUniqueParameters
	 *          If {@code true}, returns only the {@link RequestParameter} with {@code unique} flag. If {@code false},
	 *          returns all parameters.
	 * @return The request parameters map (never {@code null}).
	 */
	public Map<RequestParameter, String> getParameters(final boolean onlyUniqueParameters) {

		if (!onlyUniqueParameters) {
			return parameters;
		}

		final Map<RequestParameter, String> uniqueParams = new HashMap<RequestParameter, String>();

		for (final Map.Entry<RequestParameter, String> entry : parameters.entrySet()) {
			if (entry.getKey().isUnique()) {
				uniqueParams.put(entry.getKey(), entry.getValue());
			}
		}

		return uniqueParams;
	}

	/**
	 * Returns the request parameters names set.
	 * 
	 * @return the request parameters names set (never {@code null}).
	 */
	public Set<RequestParameter> getParameterKeys() {
		if (parameters != null) {
			return parameters.keySet();
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Gets the parameter value corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The parameter key.
	 * @return the parameter value corresponding to the given {@code key}, or {@code null} if the parameter is not present
	 *         inside the request.
	 */
	public String getParameter(final RequestParameter key) {
		return getParameter(key, null);
	}

	/**
	 * Gets the parameter value corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The parameter key.
	 * @param defaultValue
	 *          The value returned if the parameter is not found.
	 * @return the parameter value corresponding to the given {@code key}, or given {@code defaultValue} if the parameter
	 *         is not present inside the request.
	 */
	public String getParameter(final RequestParameter key, final String defaultValue) {
		String value = null;

		if (parameters != null) {
			value = parameters.get(key);
		}

		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	/**
	 * Gets the parameter <b>integer</b> value corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The parameter key.
	 * @return The parameter <b>integer</b> value corresponding to the given {@code key}, or {@code null} if the parameter
	 *         is not present inside the request or is not a valid integer.
	 */
	public Integer getParameterInteger(final RequestParameter key) {
		return ClientUtils.asInt(getParameter(key, null));
	}

	/**
	 * <p>
	 * Adds a new parameter to the current {@code PageRequest} with the given parameter {@code name} and {@code value}.
	 * </p>
	 * <p>
	 * If a parameter with the same {@code name} was previously specified, the returned request contains the new value.
	 * </p>
	 * 
	 * @param name
	 *          The new parameter name.
	 * @param value
	 *          The new parameter value ({@code value.toString()} is used).
	 * @return The {@code PageRequest} instance with its new parameter.
	 */
	public PageRequest addParameter(final RequestParameter name, final Object value) {
		parameters.put(name, String.valueOf(value));
		return this;
	}

	/**
	 * <p>
	 * Adds all the given {@code parameters} to the current {@code PageRequest} (does not remove existing parameters).
	 * </p>
	 * <p>
	 * Replaces existing parameters with same key.
	 * </p>
	 * 
	 * @param parameters
	 *          The parameters map.
	 * @return The {@code PageRequest} instance with its new parameter(s).
	 */
	public PageRequest addAllParameters(final Map<RequestParameter, ?> parameters) {

		if (parameters == null) {
			return this;
		}

		for (final Entry<RequestParameter, ?> parameter : parameters.entrySet()) {
			addParameter(parameter.getKey(), parameter.getValue());
		}

		return this;
	}
	
	/**
	 * <p>
	 * Remove an existing parameter to the current {@code PageRequest}.
	 * </p>
	 * 
	 * @param name
	 *          The name of the parameter to remove.
	 * @return The {@code PageRequest} instance without the given parameter.
	 */
	public PageRequest removeParameter(final RequestParameter name) {
		parameters.remove(name);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PageRequest) {
			final PageRequest req = (PageRequest) obj;
			if (!page.equals(req.page)) {
				return false;
			}

			if (parameters == null) {
				return req.parameters == null;
			} else {
				return parameters.equals(req.parameters);
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return 11 * (page.hashCode() + (parameters == null ? 0 : parameters.hashCode()));
	}

	/**
	 * Outputs the place as a GWT history token.
	 * 
	 * @return the place as a GWT history token including optional parameters.
	 */
	@Override
	public String toString() {

		final StringBuilder out = new StringBuilder();
		out.append(page.toString());

		if (ClientUtils.isNotEmpty(parameters)) {
			for (final Map.Entry<RequestParameter, String> entry : parameters.entrySet()) {
				out.append(PARAM_SEPARATOR);
				out.append(escape(entry.getKey().getRequestName())).append(VALUE_SEPARATOR).append(escape(entry.getValue()));
			}
		}

		return out.toString();
	}

	/**
	 * <p>
	 * Adds a new <b>data object</b> parameter to the current {@code PageRequest} with the given parameter {@code key} and
	 * {@code value}.
	 * </p>
	 * <p>
	 * If a <b>data object</b> parameter with the same {@code key} was previously specified, the returned request contains
	 * the new value.
	 * </p>
	 * 
	 * @param key
	 *          The new data parameter key.
	 * @param value
	 *          The new data parameter value.
	 * @return The {@code PageRequest} instance with its new <b>data object</b> parameter.
	 */
	public PageRequest addData(final RequestParameter key, final Object value) {
		dataParameters.put(key, value);
		return this;
	}

	/**
	 * Returns the <b>data object</b> contained in the page request instance.
	 * 
	 * @param <T>
	 *          Data type.
	 * @param key
	 *          The data parameter key.
	 * @return The given {@code key} corresponding data contained in the page request, or {@code null} if the data doesn't
	 *         exist or if its type is not {@code T}.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getData(final RequestParameter key) {
		try {

			return (T) dataParameters.get(key);

		} catch (final ClassCastException e) {
			return null;
		}
	}

	// ---------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------

	/**
	 * Parses a GWT history token into a {@link PageRequest} instance.
	 * 
	 * @param token
	 *          The token (with optional URL parameters).
	 * @param pages
	 *          The pages map storing pages tokens with their corresponding {@link Page}.
	 * @return The page request, or <code>null</code> if the token could not be parsed.
	 * @throws PageParsingException
	 *           If the given {@code token} is invalid.
	 */
	static PageRequest fromString(final String token, final Map<String, Pair<Page, Boolean>> pages) throws PageParsingException {
		return fromString(token, pages, null);
	}

	/**
	 * Parses a GWT history token into a {@link PageRequest} instance.
	 * 
	 * @param token
	 *          The token (with optional URL parameters).
	 * @param pages
	 *          The pages map storing pages tokens with their corresponding {@link Page}.
	 * @param dataParameters
	 *          (optional) Data parameters (object(s)) to include into returned page request instance.
	 * @return The page request, or <code>null</code> if the token could not be parsed.
	 * @throws PageParsingException
	 *           If the given {@code token} is invalid.
	 */
	static PageRequest fromString(final String token, final Map<String, Pair<Page, Boolean>> pages, final Map<RequestParameter, Object> dataParameters)
			throws PageParsingException {

		PageRequest req = null;

		final int split = token.indexOf(PARAM_SEPARATOR);

		// Invalid token.
		if (split == 0) {
			throw new PageParsingException("Page token is missing.");
		}
		// No URL parameters.
		else if (split == -1) {
			final Page page = pages.get(token) != null ? pages.get(token).left : null;
			if (page == null) {
				throw new PageParsingException("Page token '" + token + "' is not registered among application presenters.");
			} else {
				req = new PageRequest(page);
			}
		}
		// URL parameters detected.
		else if (split >= 0) {
			final String pageToken = token.substring(0, split);
			final Page page = pages.get(pageToken) != null ? pages.get(pageToken).left : null;
			if (page == null) {
				throw new PageParsingException("Page token '" + token + "' is not registered among application presenters.");
			} else {
				req = new PageRequest(page);
			}
			final String paramsChunk = token.substring(split + 1);
			final String[] paramTokens = paramsChunk.split(PARAM_PATTERN);
			for (final String paramToken : paramTokens) {
				final String[] param = paramToken.split(VALUE_PATTERN);
				if (param.length != 2) {
					throw new PageParsingException("Bad parameter: Parameters require a single '" + VALUE_SEPARATOR + "' between the key and value.");
				}
				req = req.addParameter(RequestParameter.fromRequestName(unescape(param[0])), unescape(param[1]));
			}
		}

		if (req != null && dataParameters != null) {
			for (final Entry<RequestParameter, Object> dataParameter : dataParameters.entrySet()) {
				req.addData(dataParameter.getKey(), dataParameter.getValue());
			}
		}

		return req;
	}

	/**
	 * Escapes the given {@code value} as a page request parameter.
	 * 
	 * @param value
	 *          The page request parameter.
	 * @return the given {@code value} escaped as a page request parameter (if necessary).
	 */
	private static String escape(final String value) {
		return value.replaceAll(PARAM_SEPARATOR, PARAM_ESCAPE).replaceAll(VALUE_SEPARATOR, VALUE_ESCAPE);
	}

	/**
	 * Unescapes the given {@code value} as a page request parameter.
	 * 
	 * @param value
	 *          The page request parameter.
	 * @return the given {@code value} unescaped as a page request parameter (if necessary).
	 */
	private static String unescape(final String value) {
		return value.replaceAll(PARAM_ESCAPE, PARAM_SEPARATOR).replaceAll(VALUE_ESCAPE, VALUE_SEPARATOR);
	}

	/**
	 * Returns the given arguments corresponding URL string.
	 * 
	 * @param pageToken
	 *          The URL page token.
	 * @param parameters
	 *          The URL parameters.
	 * @return the given arguments corresponding URL string.
	 */
	public static String toUrl(final String pageToken, final Map<RequestParameter, String> parameters) {

		if (ClientUtils.isBlank(pageToken)) {
			throw new IllegalArgumentException("URL page token is required.");
		}

		final StringBuilder builder = new StringBuilder();
		builder.append(URL_TOKEN).append(pageToken);

		if (ClientUtils.isNotEmpty(parameters)) {
			for (final Entry<RequestParameter, String> param : parameters.entrySet()) {
				if (param == null || param.getKey() == null) {
					continue;
				}
				builder.append(PARAM_SEPARATOR);
				builder.append(escape(param.getKey().getRequestName())).append(VALUE_SEPARATOR).append(escape(param.getValue()));
			}
		}

		return builder.toString();
	}

}
