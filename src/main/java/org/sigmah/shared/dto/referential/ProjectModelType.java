package org.sigmah.shared.dto.referential;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.core.client.GWT;

/**
 * The different types of projects.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum ProjectModelType implements Result {

	NGO,
	FUNDING,
	LOCAL_PARTNER;

	/**
	 * <p>
	 * Gets the translation value for the given type.
	 * </p>
	 * <p>
	 * To use only on the client-side. If used on server-side, the method returns the given {@code type} enum name.
	 * </p>
	 * 
	 * @param type
	 *          The type.
	 * @return The translation value for the given type.
	 */
	public static String getName(final ProjectModelType type) {

		if (type == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return type.name();
		}

		switch (type) {
			case NGO:
				return I18N.CONSTANTS.createProjectTypeNGO();
			case FUNDING:
				return I18N.CONSTANTS.createProjectTypeFunding2();
			case LOCAL_PARTNER:
				return I18N.CONSTANTS.createProjectTypePartner2();
			default:
				return type.name();
		}
	}

	private ProjectModelType() {
	}
}
