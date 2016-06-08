package org.sigmah.shared.conf;

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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Configuration properties keys.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public enum PropertyKey implements IsSerializable {

	// --------------------------------------------------------------------------------
	//
	// Version properties.
	//
	// --------------------------------------------------------------------------------

	APP_NAME(PropertyName.n("app", "name")),
	VERSION_NAME(PropertyName.n(PropertyName.PREFIX_VERSION, "name")),
	VERSION_NUMBER(PropertyName.n(PropertyName.PREFIX_VERSION, "number")),
	VERSION_DATE(PropertyName.n(PropertyName.PREFIX_VERSION, "date")),
	VERSION_REFERENCE(PropertyName.n(PropertyName.PREFIX_VERSION, "reference")),
	VERSION_MANAGERS(PropertyName.n(PropertyName.PREFIX_VERSION, "managers")),
	VERSION_PARTNERS(PropertyName.n(PropertyName.PREFIX_VERSION, "partners")),
	VERSION_DEVELOPERS(PropertyName.n(PropertyName.PREFIX_VERSION, "developers")),
	VERSION_CONTRIBUTORS(PropertyName.n(PropertyName.PREFIX_VERSION, "contributors")),

	// --------------------------------------------------------------------------------
	//
	// Mails properties.
	//
	// --------------------------------------------------------------------------------
	MAIL_HOSTNAME(PropertyName.n(PropertyName.PREFIX_MAIL, "hostname")),
	MAIL_PORT(PropertyName.n(PropertyName.PREFIX_MAIL, "port")),
	MAIL_FROM_ADDRESS(PropertyName.n(PropertyName.PREFIX_MAIL, "from", "address")),
	MAIL_FROM_NAME(PropertyName.n(PropertyName.PREFIX_MAIL, "from", "name")),
	MAIL_AUTH_USERNAME(PropertyName.n(PropertyName.PREFIX_MAIL, "auth", "username")),
	MAIL_AUTH_PASSWORD(PropertyName.n(PropertyName.PREFIX_MAIL, "auth", "password")),
	MAIL_ENCODING(PropertyName.n(PropertyName.PREFIX_MAIL, "encoding")),
	MAIL_CONTENT_TYPE(PropertyName.n(PropertyName.PREFIX_MAIL, "contentType")),
	MAIL_SUPPORT_TO(PropertyName.n(PropertyName.PREFIX_MAIL, "support", "to")),
	MAIL_OPTIMISATION_TO_ADDRESS(PropertyName.n(PropertyName.PREFIX_MAIL,"optimisation", "to", "address")),
	MAIL_OPTIMISATION_COPY_ADDRESS(PropertyName.n(PropertyName.PREFIX_MAIL,"optimisation", "copy", "address")),
	MAIL_OPTIMISATION_MARKDOWN_FILE_NAME(PropertyName.n(PropertyName.PREFIX_MAIL,"optimisation", "markdownfile","name")),
	MAIL_OPTIMISATION_JSON_FILE_NAME(PropertyName.n(PropertyName.PREFIX_MAIL,"optimisation", "jsonfile","name")),
	// --------------------------------------------------------------------------------
	//
	// Maps API properties.
	//
	// --------------------------------------------------------------------------------

	MAPS_KEY(PropertyName.n(PropertyName.PREFIX_MAPS, "key")),

	// --------------------------------------------------------------------------------
	//
	// File storage properties.
	//
	// --------------------------------------------------------------------------------

	FILE_REPOSITORY_NAME(PropertyName.n("files", "repository", "name")),
	FILE_UPLOAD_MAX_SIZE(PropertyName.n("files", "upload", "maxSize")),
	ARCHIVE_REPOSITORY_NAME(PropertyName.n("archives", "repository", "name")),
	

	;

	private final String key;

	private PropertyKey(String key) {
		this.key = key;
	}

	public String getName() {
		return key;
	}

}
