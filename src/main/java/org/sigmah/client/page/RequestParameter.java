package org.sigmah.client.page;

import org.sigmah.client.util.ClientUtils;

/**
 * Parameters for {@link PageRequest}.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public enum RequestParameter {

	ID(true),
	TYPE(false),
	HEADER(false),
	CONTENT(false),
	REPORT_ID(false),
	GLOBAL_EXPORT_ID(false),
	LANGUAGE(false),
	DTO(false),
	REQUEST(false),
	VERSION(false),
	FORMAT(false),
	NAME(false),
	SOURCE(false),
	MODEL(false),
	CATEGORY(false),
	IMPORATION_SCHEME(false),
	VARIABLE_IMPORTATION_SCHEME(false), 
	CODE(false),
	TITLE(false),
	BUDGET(false);

	// If the parameter is part of the tab uniqueness logic.
	private final boolean unique;

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
