package org.sigmah.client.page;

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
	CLOSE_CURRENT_TAB,
    ELEMENTS,
	PROJECT_ID;

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
