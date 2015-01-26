package org.sigmah.offline.indexeddb;


/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Cursor {
	private final IDBCursor nativeCursor;

	Cursor(IDBCursor cursor) {
		this.nativeCursor = cursor;
	}
	
	public void next() {
		nativeCursor.next();
	}
	
	public Object getKey() {
		return nativeCursor.getKey();
	}
	
	public <T> T getValue() {
		return (T) nativeCursor.getValue();
	}
	
	public void update(Object object) {
		nativeCursor.update(object);
	}
}
