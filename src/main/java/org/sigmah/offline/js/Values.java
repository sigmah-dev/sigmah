package org.sigmah.offline.js;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsDate;

/**
 * Utility class to create and handle JavaScriptObjects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class Values {
	
	private Values() {
	}
	
	public static native <T> T createJavaScriptObject(Class<T> clazz) /*-{
		return {};
	}-*/;
	
	public static native <T> T createJavaScriptArray(Class<T> clazz) /*-{
		return [];
	}-*/;
	
	public static JsDate toJsDate(Date date) {
		if(date != null) {
			return JsDate.create(date.getTime());
		} else {
			return null;
		}
	}
	
	public static Date toDate(JsDate date) {
		if(date != null) {
			return new Date((long) date.getTime());
		} else {
			return null;
		}
	}
	
	public static native boolean isDefined(JavaScriptObject object, String property) /*-{
		return typeof object[property] != 'undefined';
	}-*/;
	
	public static Integer getInteger(JavaScriptObject object, String property) {
		if(isDefined(object, property)) {
			return getInt(object, property);
		}
		return null;
	}
	
	public static void setInteger(JavaScriptObject object, String property, Integer value) {
		if(value != null) {
			setInt(object, property, value);
		}
	}
	
	public static native int getInt(JavaScriptObject object, String property) /*-{
		return object[property];
	}-*/;
	
	public static native void setInt(JavaScriptObject object, String property, int value) /*-{
		object[property] = value;
	}-*/;
	
	public static <E extends Enum<E>> E getEnum(JavaScriptObject object, String property, Class<E> enumClass) {
		if(isDefined(object, property)) {
			return Enum.valueOf(enumClass, getString(object, property));
		}
		return null;
	}
	
	public static <E extends Enum<E>> void setEnum(JavaScriptObject object, String property, E value) {
		if(value != null) {
			setString(object, property, value.name());
		}
	}
	
	public static native String getString(JavaScriptObject object, String property) /*-{
		return object[property];
	}-*/;
	
	public static native void setString(JavaScriptObject object, String property, String value) /*-{
		object[property] = value;
	}-*/;
	
	public static native <J extends JavaScriptObject> J getJavaScriptObject(JavaScriptObject object, String property) /*-{
		return object[property];
	}-*/;
	
	public static native <J extends JavaScriptObject> void setJavaScriptObject(JavaScriptObject object, String property, J value) /*-{
		object[property] = value;
	}-*/;
	
	public static Date getDate(JavaScriptObject object, String property) {
		final JsDate date = getJavaScriptObject(object, property);
		return toDate(date);
	}
	
	public static void setDate(JavaScriptObject object, String property, Date value) {
		setJavaScriptObject(object, property, toJsDate(value));
	}
}
