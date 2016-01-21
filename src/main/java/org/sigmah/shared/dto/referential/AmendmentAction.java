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
