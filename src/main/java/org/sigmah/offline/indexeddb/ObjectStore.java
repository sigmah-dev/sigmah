package org.sigmah.offline.indexeddb;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ObjectStore {
	private final IDBObjectStore nativeObjectStore;

	ObjectStore(IDBObjectStore objectStore) {
		this.nativeObjectStore = objectStore;
	}
	
	public Request add(Object object) {
		return new Request(this.nativeObjectStore.add(object));
	}
	
	public Request add(Object object, int key) {
		return new Request(this.nativeObjectStore.add(object, key));
	}
	
	public Request add(Object object, float key) {
		return new Request(this.nativeObjectStore.add(object, key));
	}
	
	public Request add(Object object, double key) {
		return new Request(this.nativeObjectStore.add(object, key));
	}
	
	public Request add(Object object, char key) {
		return new Request(this.nativeObjectStore.add(object, key));
	}
	
	public Request put(Object object) {
		return new Request(this.nativeObjectStore.put(object));
	}
	
	public Request put(Object object, int key) {
		return new Request(this.nativeObjectStore.put(object, key));
	}
	
	public Request put(Object object, float key) {
		return new Request(this.nativeObjectStore.put(object, key));
	}
	
	public Request put(Object object, double key) {
		return new Request(this.nativeObjectStore.put(object, key));
	}
	
	public Request put(Object object, char key) {
		return new Request(this.nativeObjectStore.put(object, key));
	}
	
	public Request get(Object key) {
		return new Request(nativeObjectStore.get(key));
	}
	
	public Request get(int key) {
		return new Request(nativeObjectStore.get(key));
	}
	
	public Request get(float key) {
		return new Request(nativeObjectStore.get(key));
	}
	
	public Request get(double key) {
		return new Request(nativeObjectStore.get(key));
	}
	
	public Request get(char key) {
		return new Request(nativeObjectStore.get(key));
	}
	
	public CountRequest count() {
		return new CountRequest(nativeObjectStore.count());
	}
	
	public CountRequest count(Object value) {
		return new CountRequest(nativeObjectStore.count(value));
	}
	
	public CountRequest count(double value) {
		return new CountRequest(nativeObjectStore.count(value));
	}
	
	public CountRequest count(float value) {
		return new CountRequest(nativeObjectStore.count(value));
	}
	
	public CountRequest count(char value) {
		return new CountRequest(nativeObjectStore.count(value));
	}
	
	public CountRequest count(boolean value) {
		return new CountRequest(nativeObjectStore.count(value));
	}
	
	public CountRequest count(IDBKeyRange range) {
		return new CountRequest(nativeObjectStore.count(range));
	}
	
	public Request delete(Object key) {
		return new Request(nativeObjectStore.delete(key));
	}
	
	public Request delete(int key) {
		return new Request(nativeObjectStore.delete(key));
	}
	
	public Request delete(double key) {
		return new Request(nativeObjectStore.delete(key));
	}
	
	public Request delete(float key) {
		return new Request(nativeObjectStore.delete(key));
	}
	
	public Request delete(char key) {
		return new Request(nativeObjectStore.delete(key));
	}
	
	public OpenCursorRequest openCursor() {
		return new OpenCursorRequest(nativeObjectStore.openCursor());
	}
	
	public OpenCursorRequest openCursor(IDBKeyRange range) {
		return new OpenCursorRequest(nativeObjectStore.openCursor(range));
	}
	
	public OpenCursorRequest openCursor(IDBKeyRange range, Order order) {
		return new OpenCursorRequest(nativeObjectStore.openCursor(range, order.toString()));
	}

	public void createIndex(String name, String keypath) {
		nativeObjectStore.createIndex(name, keypath);
	}

	public void createIndex(String name, String keypath, boolean unique, boolean multiEntry) {
		nativeObjectStore.createIndex(name, keypath, unique, multiEntry);
	}
	
	public Index index(String name) {
		return new Index(nativeObjectStore.index(name));
	}
}
