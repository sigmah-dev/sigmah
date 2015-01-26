package org.sigmah.shared.dto.referential;

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
