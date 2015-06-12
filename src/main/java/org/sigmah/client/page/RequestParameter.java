package org.sigmah.client.page;

import org.sigmah.client.util.ClientUtils;

/**
 * Parameters for {@link PageRequest}.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public enum RequestParameter {

	ID(true),
	TYPE,
	HEADER,
	CONTENT,
	REPORT_ID,
	GLOBAL_EXPORT_ID,
	LANGUAGE,
	DTO,
	REQUEST,
	VERSION,
	FORMAT,
	NAME,
	SOURCE,
	MODEL,
	CATEGORY,
	IMPORTATION_SCHEME,
	VARIABLE_IMPORTATION_SCHEME,
	IMPORTATION_SCHEME_MODEL,
	CODE,
	TITLE,
	BUDGET,
	NO_REFRESH,
	FOR_KEY,
	SHOW_BRIEFLY,
	PULL_DATABASE,
	CLOSE_CURRENT_TAB;

	// If the parameter is part of the tab uniqueness logic.
	private final boolean unique;

	private RequestParameter() {
		this(false);
	}

	private RequestParameter(boolean unique) {
		this.unique = unique;
	}

	public boolean isUnique() {
		return unique;
	}

	public static String getRequestName(RequestParameter requestParameter) {
		return ClientUtils.toLowerCase(requestParameter.name());
	}

	public String getRequestName() {
		return ClientUtils.toLowerCase(name());
	}

	public static RequestParameter fromRequestName(String requestName) {

		if (ClientUtils.isNotBlank(requestName)) {
			for (final RequestParameter e : values()) {
				if (e.getRequestName().equals(requestName)) {
					return e;
				}
			}
		}

		return null;

	}

}
