package org.sigmah.offline.indexeddb;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class CountRequest extends Request {

	CountRequest(IDBRequest request) {
		super(request);
	}
	
	public native int getCount() /*-{
		return this.@org.sigmah.offline.indexeddb.Request::request.result;
	}-*/;
}
