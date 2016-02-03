package org.sigmah.client.util;

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
