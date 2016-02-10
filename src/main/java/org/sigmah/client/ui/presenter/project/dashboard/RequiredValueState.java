package org.sigmah.client.ui.presenter.project.dashboard;

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
 * Useful internal class to manage the required elements completion.
 *
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class RequiredValueState {

	/**
	 * The required element saved value completion (in db).
	 */
	private Boolean savedState;

	/**
	 * The actual element saved value completion (in local).
	 */
	private Boolean actualState;

	public void setSavedState(Boolean savedState) {
		this.savedState = savedState;
	}

	public void setActualState(Boolean actualState) {
		this.actualState = actualState;
	}

	/**
	 * Informs that the actual value completion has been saved to the data layer.
	 */
	public void saveState() {
		if (actualState != null) {
			savedState = actualState;
			actualState = null;
		}
	}

	/**
	 * Informs that the actual value completion has been discarded.
	 */
	public void clearState() {
		actualState = null;
	}

	/**
	 * Returns if the saved value completion is valid.
	 *
	 * @return If the saved value completion is valid.
	 */
	public boolean isTrue() {
		return !Boolean.FALSE.equals(savedState);
	}

	/**
	 * Returns if the actual value completion is valid.
	 *
	 * @return If the actual value completion is valid.
	 */
	public boolean isActuallyTrue() {
		return !Boolean.FALSE.equals(actualState);
	}

	@Override
	public String toString() {
		return "saved: " + savedState + " ; actual: " + actualState;
	}
}
