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
 * Useful internal class to manage the required elements completion.
 */
final class RequiredListValueState {

	private final HashMap<Integer, RequiredValueState> iterations;

	public RequiredListValueState() {
		iterations = new HashMap<Integer, RequiredValueState>();
	}

	public RequiredValueState get(Integer iterationId) {
		return this.iterations.get(iterationId);
	}

	public void put(Integer iterationId, RequiredValueState value) {
		this.iterations.put(iterationId, value);
	}

	public void saveState() {
		for(RequiredValueState state : iterations.values()) {
			state.saveState();
		}
	}

	public void clearState() {
		for(RequiredValueState state : iterations.values()) {
			state.clearState();
		}
	}

	public boolean isTrue() {
		for (final RequiredValueState state : iterations.values()) {
			if (!state.isTrue()) {
				return false;
			}
		}
		return true;
	}

	public boolean isActuallyTrue() {
		for (final RequiredValueState state : iterations.values()) {
			if (!state.isActuallyTrue()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return this.iterations.toString();
	}
}
