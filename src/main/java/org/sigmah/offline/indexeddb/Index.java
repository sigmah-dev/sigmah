package org.sigmah.offline.indexeddb;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Index {
	private final IDBIndex nativeIndex;
	
	Index(IDBIndex index) {
		this.nativeIndex = index;
	}
	
	public Request get(Object key) {
		return new Request(nativeIndex.get(key));
	}
	
	public Request get(int key) {
		return new Request(nativeIndex.get(key));
	}
	
	public Request get(float key) {
		return new Request(nativeIndex.get(key));
	}
	
	public Request get(double key) {
		return new Request(nativeIndex.get(key));
	}
	
	public Request get(char key) {
		return new Request(nativeIndex.get(key));
	}
	
	public CountRequest count() {
		return new CountRequest(nativeIndex.count());
	}
	
	public CountRequest count(Object value) {
		return new CountRequest(nativeIndex.count(value));
	}
	
	public CountRequest count(double value) {
		return new CountRequest(nativeIndex.count(value));
	}
	
	public CountRequest count(float value) {
		return new CountRequest(nativeIndex.count(value));
	}
	
	public CountRequest count(char value) {
		return new CountRequest(nativeIndex.count(value));
	}
	
	public CountRequest count(boolean value) {
		return new CountRequest(nativeIndex.count(value));
	}
	
	public CountRequest count(IDBKeyRange range) {
		return new CountRequest(nativeIndex.count(range));
	}
	
	public OpenCursorRequest openCursor() {
		return new OpenCursorRequest(nativeIndex.openCursor());
	}
	
	public OpenCursorRequest openCursor(IDBKeyRange range) {
		return new OpenCursorRequest(nativeIndex.openCursor(range));
	}
	
	public OpenCursorRequest openCursor(IDBKeyRange range, Order order) {
		return new OpenCursorRequest(nativeIndex.openCursor(range, order.toString()));
	}
	
	public OpenCursorRequest openKeyCursor() {
		return new OpenCursorRequest(nativeIndex.openKeyCursor());
	}
	
	public OpenCursorRequest openKeyCursor(IDBKeyRange range) {
		return new OpenCursorRequest(nativeIndex.openKeyCursor(range));
	}
	
	public OpenCursorRequest openKeyCursor(IDBKeyRange range, Order order) {
		return new OpenCursorRequest(nativeIndex.openKeyCursor(range, order.toString()));
	}
}
