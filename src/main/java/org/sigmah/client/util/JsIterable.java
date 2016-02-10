package org.sigmah.client.util;

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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import java.util.Iterator;

/**
 * Utility object that wrap a <code>JsArray</code> to make it implements the
 * <code>Iterable</code> iterface.
 * 
 * @param <T> Element type.
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class JsIterable<T extends JavaScriptObject> implements Iterable<T> {
	
	/**
	 * Wrapped JS array.
	 */
	private final JsArray<T> array;

	/**
	 * Creates a new iterable over the given array.
	 * 
	 * @param array Array to wrap.
	 */
	public JsIterable(JsArray<T> array) {
		this.array = array;
	}

	/**
	 * Returns a new iterator over the wrapped array.
	 * 
	 * @return A new iterator.
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			
			private int index;

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {
				return index < array.length();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public T next() {
				return array.get(index++);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void remove() {
				throw new UnsupportedOperationException("Not supported.");
			}
			
		};
	}
	
}
