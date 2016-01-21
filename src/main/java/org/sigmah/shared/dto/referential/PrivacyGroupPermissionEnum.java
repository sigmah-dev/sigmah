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
 * List of the permissions linked to a privacy group.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum PrivacyGroupPermissionEnum implements Result {

	/**
	 * Forbids all actions.
	 */
	NONE,

	/**
	 * Allows to view.
	 */
	READ,

	/**
	 * Allows to view and edit.
	 */
	WRITE;

	/**
	 * <p>
	 * Returns the given {@code privacyGroupPermission} corresponding name.
	 * </p>
	 * <p>
	 * If this method is executed from server-side, it returns the given {@code privacyGroupPermission} constant name.
	 * </p>
	 * 
	 * @param privacyGroupPermission
	 *          The privacy group permission.
	 * @return the given {@code privacyGroupPermission} corresponding name, or {@code null}.
	 */
	public static String getName(final PrivacyGroupPermissionEnum privacyGroupPermission) {

		if (privacyGroupPermission == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return privacyGroupPermission.name();
		}

		switch (privacyGroupPermission) {
			case NONE:
				return I18N.CONSTANTS.none();
			case READ:
				return I18N.CONSTANTS.view();
			case WRITE:
				return I18N.CONSTANTS.edit();
			default:
				return privacyGroupPermission.name();
		}
	}

	/**
	 * Returns the given {@code privacyGroupPermission} corresponding {@code PrivacyGroupPermissionEnum} instance.
	 * 
	 * @param privacyGroupPermission
	 *          The privacy group permission text.
	 * @return the given {@code privacyGroupPermission} corresponding {@code PrivacyGroupPermissionEnum} instance, or
	 *         {@code null}.
	 */
	public static PrivacyGroupPermissionEnum translatePGPermission(final String privacyGroupPermission) {

		final PrivacyGroupPermissionEnum pgName;

		if (I18N.CONSTANTS.none().equals(privacyGroupPermission)) {
			pgName = PrivacyGroupPermissionEnum.NONE;

		} else if (I18N.CONSTANTS.view().equals(privacyGroupPermission)) {
			pgName = PrivacyGroupPermissionEnum.READ;

		} else if (I18N.CONSTANTS.edit().equals(privacyGroupPermission)) {
			pgName = PrivacyGroupPermissionEnum.WRITE;

		} else {
			pgName = null;
		}

		return pgName;
	}

	private PrivacyGroupPermissionEnum() {
	}
}
