package org.sigmah.offline.indexeddb;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class IDBKeyRange extends JavaScriptObject {
	protected IDBKeyRange() {
	}
	
	public static native IDBKeyRange only(Object value) /*-{
		return $wnd.IDBKeyRange.only(value);
	}-*/;
	
	public static native IDBKeyRange only(int value) /*-{
		return $wnd.IDBKeyRange.only(value);
	}-*/;
	
	public static native IDBKeyRange only(double value) /*-{
		return $wnd.IDBKeyRange.only(value);
	}-*/;
	
	public static native IDBKeyRange only(float value) /*-{
		return $wnd.IDBKeyRange.only(value);
	}-*/;
	
	public static native IDBKeyRange only(char value) /*-{
		return $wnd.IDBKeyRange.only(value);
	}-*/;
	
	public static native IDBKeyRange only(boolean value) /*-{
		return $wnd.IDBKeyRange.only(value);
	}-*/;
	
	public static native IDBKeyRange lowerBound(Object lower, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(lower, open);
	}-*/;
	
	public static native IDBKeyRange lowerBound(int lower, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(lower, open);
	}-*/;
	
	public static native IDBKeyRange lowerBound(double lower, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(lower, open);
	}-*/;
	
	public static native IDBKeyRange lowerBound(float lower, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(lower, open);
	}-*/;
	
	public static native IDBKeyRange lowerBound(char lower, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(lower, open);
	}-*/;
	
	public static native IDBKeyRange lowerBound(boolean lower, boolean open) /*-{
		return $wnd.IDBKeyRange.lowerBound(lower, open);
	}-*/;
	
	public static native IDBKeyRange upperBound(Object upper, boolean open) /*-{
		return $wnd.IDBKeyRange.upperBound(upper, open);
	}-*/;
	
	public static native IDBKeyRange upperBound(int upper, boolean open) /*-{
		return $wnd.IDBKeyRange.upperBound(upper, open);
	}-*/;
	
	public static native IDBKeyRange upperBound(double upper, boolean open) /*-{
		return $wnd.IDBKeyRange.upperBound(upper, open);
	}-*/;
	
	public static native IDBKeyRange upperBound(float upper, boolean open) /*-{
		return $wnd.IDBKeyRange.upperBound(upper, open);
	}-*/;
	
	public static native IDBKeyRange upperBound(char upper, boolean open) /*-{
		return $wnd.IDBKeyRange.upperBound(upper, open);
	}-*/;
	
	public static native IDBKeyRange upperBound(boolean upper, boolean open) /*-{
		return $wnd.IDBKeyRange.upperBound(upper, open);
	}-*/;
	
	public static native IDBKeyRange bound(Object lower, Object upper, boolean lowerOpen, boolean upperOpen) /*-{
		return $wnd.IDBKeyRange.bound(lower, upper, lowerOpen, upperOpen);
	}-*/;
	
	public static native IDBKeyRange bound(int lower, int upper, boolean lowerOpen, boolean upperOpen) /*-{
		return $wnd.IDBKeyRange.bound(lower, upper, lowerOpen, upperOpen);
	}-*/;
	
	public static native IDBKeyRange bound(double lower, double upper, boolean lowerOpen, boolean upperOpen) /*-{
		return $wnd.IDBKeyRange.bound(lower, upper, lowerOpen, upperOpen);
	}-*/;
	
	public static native IDBKeyRange bound(float lower, float upper, boolean lowerOpen, boolean upperOpen) /*-{
		return $wnd.IDBKeyRange.bound(lower, upper, lowerOpen, upperOpen);
	}-*/;
	
	public static native IDBKeyRange bound(char lower, char upper, boolean lowerOpen, boolean upperOpen) /*-{
		return $wnd.IDBKeyRange.bound(lower, upper, lowerOpen, upperOpen);
	}-*/;
	
	public static native IDBKeyRange bound(boolean lower, boolean upper, boolean lowerOpen, boolean upperOpen) /*-{
		return $wnd.IDBKeyRange.bound(lower, upper, lowerOpen, upperOpen);
	}-*/;
}
