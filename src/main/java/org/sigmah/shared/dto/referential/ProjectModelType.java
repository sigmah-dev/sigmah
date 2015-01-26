package org.sigmah.shared.dto.referential;

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
