package org.sigmah.offline.js;

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

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsDate;

/**
 * Utility class to create and handle JavaScriptObjects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class Values {
	
	private Values() {
	}
	
	public static native <T> T createJavaScriptObject() /*-{
		return {};
	}-*/;
	
	public static native <T> T createJavaScriptObject(Class<T> clazz) /*-{
		return {};
	}-*/;
	
	public static native <T> T createJavaScriptArray(Class<T> clazz) /*-{
		return [];
	}-*/;
	
	public static native <T extends JavaScriptObject> JsArray<T> createTypedJavaScriptArray(Class<T> clazz) /*-{
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
	
	public static native boolean isDeleted(JavaScriptObject object) /*-{
		return typeof object != 'undefined' && object['deleted'] == true;
	}-*/;
	
}
