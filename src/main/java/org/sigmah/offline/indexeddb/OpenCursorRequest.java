package org.sigmah.offline.indexeddb;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OpenCursorRequest extends Request {

	OpenCursorRequest(IDBRequest request) {
		super(request);
	}

	@Override
	public Cursor getResult() {
		final IDBCursor cursor = (IDBCursor)super.getResult();
		if(cursor != null) {
			return new Cursor(cursor);
		} else {
			return null;
		}
	}
}
