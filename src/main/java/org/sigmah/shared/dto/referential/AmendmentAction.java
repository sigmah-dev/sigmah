package org.sigmah.shared.dto.referential;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.conf.PropertyName;

import com.google.gwt.core.client.GWT;

/**
 * Possible user interaction with an amendment.
 */
public enum AmendmentAction implements Result {

	CREATE,
	LOCK,
	UNLOCK,
	REJECT,
	VALIDATE;

	/**
	 * <p>
	 * Returns the given {@code amendmentAction} corresponding name.
	 * </p>
	 * <p>
	 * If this method is executed from server-side, it returns the given {@code amendmentAction} constant name.
	 * </p>
	 * 
	 * @param amendmentAction
	 *          The amendment action value.
	 * @return the given {@code amendmentAction} corresponding name, or {@code null}.
	 */
	public static String getName(final AmendmentAction amendmentAction) {

		if (amendmentAction == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return amendmentAction.name();
		}

		switch (amendmentAction) {
			case CREATE:
				return I18N.CONSTANTS.amendmentCreate();
			case LOCK:
				return I18N.CONSTANTS.amendmentLock();
			case UNLOCK:
				return I18N.CONSTANTS.amendmentUnlock();
			case REJECT:
				return I18N.CONSTANTS.amendmentReject();
			case VALIDATE:
				return I18N.CONSTANTS.amendmentValidate();
			default:
				return PropertyName.error(amendmentAction.name());
		}
	}

}
