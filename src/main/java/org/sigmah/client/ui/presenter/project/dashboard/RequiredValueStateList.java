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

import java.util.HashMap;

/**
 * Useful internal class to manage the required elements completions list.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class RequiredValueStateList {

	/**
	 * Map the required element, its saved value completion (in db) and its actual value completion (not yet saved).
	 */
	private final HashMap<Integer, RequiredListValueState> list;

	public RequiredValueStateList() {
		list = new HashMap<Integer, RequiredListValueState>();
	}

	/**
	 * Clears the list of required elements.
	 */
	public void clear() {
		list.clear();
	}

	/**
	 * Copies a list of required elements from another manager.
	 * 
	 * @param other
	 *          The other required elements manager.
	 */
	public void putAll(RequiredValueStateList other) {
		list.putAll(other.list);
	}

	/**
	 * Sets a required element saved value completion (in db). Adds it if necessary.
	 * 
	 * @param elementDTOId
	 *          The required element id.
	 * @param savedState
	 *          The saved value completion.
	 */
	public void putSaved(Integer iterationId, Integer elementDTOId, Boolean savedState) {

		RequiredValueState state = retrieveOrCreateState(iterationId, elementDTOId);

		state.setSavedState(savedState);

		putActual(iterationId, elementDTOId, savedState);
	}

	private RequiredValueState retrieveOrCreateState(Integer iterationId, Integer elementDTOId) {

		RequiredListValueState listState = list.get(elementDTOId);

		if (listState == null) {
			listState = new RequiredListValueState();
			list.put(elementDTOId, listState);
		}

		RequiredValueState state = list.get(elementDTOId).get(iterationId);

		if (state == null) {
			state = new RequiredValueState();
			listState.put(iterationId, state);
		}

		return state;
	}

	/**
	 * Sets a required element actual value completion (in local). Adds it if necessary.
	 * 
	 * @param elementDTOId
	 *          The required element id.
	 * @param actualState
	 *          The actual value completion.
	 */
	public void putActual(Integer iterationId, Integer elementDTOId, Boolean actualState) {

	RequiredValueState state = retrieveOrCreateState(iterationId, elementDTOId);

		state.setActualState(actualState);
	}

	/**
	 * Informs that all actual values completions has been saved to the data layer.
	 */
	public void saveState() {
		for (final RequiredListValueState state : list.values()) {
			state.saveState();
		}
	}

	/**
	 * Informs that all actual values completions has been discarded.
	 */
	public void clearState() {
		for (final RequiredListValueState state : list.values()) {
			state.clearState();
		}
	}

	/**
	 * Returns if all saved values completions are valid.
	 * 
	 * @return If all saved values completions are valid.
	 */
	public boolean isTrue() {
		for (final RequiredListValueState state : list.values()) {
			if (!state.isTrue()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns if all actual values completions are valid.
	 *
	 * @return If all actual values completions are valid.
	 */
	public boolean isActuallyTrue(Integer elementDTOId) {
		RequiredListValueState state = list.get(elementDTOId);
		if(state == null) {
			return true;
		}

		return state.isActuallyTrue();
	}

	/**
	 * Returns if all actual values completions are valid.
	 * 
	 * @return If all actual values completions are valid.
	 */
	public boolean isActuallyTrue() {
		for (final RequiredListValueState state : list.values()) {
			if (!state.isActuallyTrue()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return list.toString();
	}
}
