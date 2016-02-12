package org.sigmah.shared.dto.referential;

import com.google.gwt.core.client.GWT;
import org.sigmah.client.i18n.I18N;

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

/**
 * Status of an automated import.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public enum AutomatedImportStatus {
	
	UPDATED,
	WAS_LOCKED,
	UNLOCK_FAILED,
	UNLOCKED_AND_UPDATED,
	NOT_FOUND,
	MULTIPLE_MATCHES_FOUND,
	CREATED_AND_UPDATED,
	CREATION_FAILED,
	;
	
	/**
	 * Returns the given {@code elementType} corresponding name value.<br/>
	 * This method should be executed from client-side. If executed from server-side, it returns a default name.
	 * 
	 * @param status
	 *          The {@code ElementTypeEnum} value.
	 * @return the given {@code elementType} corresponding name value, or a default name.
	 */
	public static String getName(final AutomatedImportStatus status) {

		if (status == null) {
			return null;
		} else if (!GWT.isClient()) {
			return status.name();
		}

		switch (status) {
			case CREATED_AND_UPDATED:
				return I18N.CONSTANTS.automatedImportCreatedAndUpdated();
			case CREATION_FAILED:
				return I18N.CONSTANTS.automatedImportCreationFailed();
			case MULTIPLE_MATCHES_FOUND:
				return I18N.CONSTANTS.automatedImportMultipleMatchesFound();
			case NOT_FOUND:
				return I18N.CONSTANTS.automatedImportNotFound();
			case UNLOCKED_AND_UPDATED:
				return I18N.CONSTANTS.automatedImportUnlockedAndUpdated();
			case UNLOCK_FAILED:
				return I18N.CONSTANTS.automatedImportUnlockedFailed();
			case UPDATED:
				return I18N.CONSTANTS.automatedImportUpdated();
			case WAS_LOCKED:
				return I18N.CONSTANTS.automatedImportWasLocked();
			default:
				return status.name();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getName(this);
	}
	
}
