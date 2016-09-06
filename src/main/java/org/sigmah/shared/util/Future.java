package org.sigmah.shared.util;

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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Reference to a not yet initialized value.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @param <V> 
 *          Type of the future value.
 */
public class Future<V> {

	/**
	 * Future value.
	 */
	private V value;
	
	/**
	 * Exception thrown during the call.
	 */
	private Throwable caught;

	/**
	 * Creates a new instance with an empty value.
	 */
	public Future() {
	}

	/**
	 * Creates a new instance and sets an initial value.
	 * 
	 * @param value
	 *          Initial value.
	 */
	public Future(V value) {
		this.value = value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *          Value to set.
	 */
	public void set(V value) {
		this.value = value;
	}
	
	/**
	 * Retrieves the current value.
	 * 
	 * Should not be used if the {@link #defer()} method was used to set the
	 * value.
	 * 
	 * @return The referenced value or <code>null</code> if undefined.
	 */
	public V get() {
		return value;
	}

	/**
	 * Retrieves the current value or throw the exception that happened during
	 * the callback or if .
	 * 
	 * @return The referenced value or <code>null</code> if undefined.
	 * @throws java.lang.Throwable If an error happened during the retrieval.
	 */
	public V getOrThrow() throws Throwable {
		if (caught != null) {
			throw caught;
		}
		return value;
	}
	
	/**
	 * Creates a callback that will wait for this value.
	 * 
	 * @return A new callback.
	 */
	public AsyncCallback<V> defer() {
		this.caught = new IllegalStateException("Response to the callback has not been received yet.");
		
		return new AsyncCallback<V>() {
			
			@Override
			public void onFailure(final Throwable caught) {
				Future.this.value = null;
				Future.this.caught = caught;
			}

			@Override
			public void onSuccess(final V result) {
				Future.this.value = result;
				Future.this.caught = null;
			}
			
		};
	}
	
}

