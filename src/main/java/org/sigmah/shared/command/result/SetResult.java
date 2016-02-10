package org.sigmah.shared.command.result;

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

import java.util.Set;

import org.sigmah.client.util.ClientUtils;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * An action result which returns a set or a size.
 * 
 * @author Raphaël GRENIER (rgrenier@ideia.fr)
 * @param <E>
 *          The type of the entities.
 */
public class SetResult<E extends IsSerializable> implements Result {

	/**
	 * The set.
	 */
	private Set<E> set;

	/**
	 * The size.
	 */
	private int size;

	public SetResult() {
		// Serialization.
	}

	public SetResult(final Set<E> entities) {
		this(entities, ClientUtils.isEmpty(entities) ? 0 : entities.size());
	}

	public SetResult(final int size) {
		this(null, size);
	}

	public SetResult(final Set<E> entities, final int size) {
		this.set = entities;
		this.size = size;
	}

	public Set<E> get() {
		return set;
	}

	public void set(final Set<E> set) {
		this.set = set;
		this.size = set != null ? set.size() : 0;
	}

	public int size() {
		return size;
	}

	public void set(int size) {
		this.size = size;
	}

	// --------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Returns if the inner set is {@code null} or empty.
	 * 
	 * @return {@code true} if the inner set is {@code null} or empty, {@code false} otherwise.
	 */
	public boolean isEmpty() {
		return ClientUtils.isEmpty(set);
	}

	/**
	 * Returns if the inner set is <b>not</b> {@code null} or empty.
	 * 
	 * @return {@code true} if the inner set is <b>not</b> {@code null} or empty, {@code false} otherwise.
	 */
	public boolean isNotEmpty() {
		return !isEmpty();
	}

}
