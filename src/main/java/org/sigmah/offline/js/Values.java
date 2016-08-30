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
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsDate;
import java.util.Collection;
import org.sigmah.shared.dto.base.EntityDTO;

/**
 * Utility class to create and handle JavaScriptObjects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class Values {
	
	/**
	 * Private constructor.
	 */
	private Values() {
		// Empty.
	}
	
	public static native <T> T createJavaScriptObject() /*-{
		return {};
	}-*/;
	
	public static native <T> T createJavaScriptObject(final Class<T> clazz) /*-{
		return {};
	}-*/;
	
	public static native <T> T createJavaScriptArray(final Class<T> clazz) /*-{
		return [];
	}-*/;
	
	public static native <T extends JavaScriptObject> JsArray<T> createTypedJavaScriptArray(final Class<T> clazz) /*-{
		return [];
	}-*/;
	
	public static JsDate toJsDate(final Date date) {
		if(date != null) {
			return JsDate.create(date.getTime());
		} else {
			return null;
		}
	}
	
	public static Date toDate(final JsDate date) {
		if(date != null) {
			return new Date((long) date.getTime());
		} else {
			return null;
		}
	}
	
	public static native boolean isDefined(final JavaScriptObject object, final String property) /*-{
		return typeof object[property] !== 'undefined';
	}-*/;
	
	public static native boolean isObject(final JavaScriptObject object, final String property) /*-{
		return typeof object[property] === 'object';
	}-*/;
	
	public static native boolean isNumber(final JavaScriptObject object, final String property) /*-{
		return typeof object[property] === 'number';
	}-*/;
	
	public static Integer getInteger(final JavaScriptObject object, final String property) {
		if(isDefined(object, property)) {
			return getInt(object, property);
		}
		return null;
	}
	
	public static void setInteger(final JavaScriptObject object, final String property, final Integer value) {
		if(value != null) {
			setInt(object, property, value);
		}
	}
	
	public static native int getInt(final JavaScriptObject object, final String property) /*-{
		return object[property];
	}-*/;
	
	public static native void setInt(final JavaScriptObject object, final String property, final int value) /*-{
		object[property] = value;
	}-*/;
	
	public static <E extends Enum<E>> E getEnum(final JavaScriptObject object, final String property, final Class<E> enumClass) {
		if(isDefined(object, property)) {
			return Enum.valueOf(enumClass, getString(object, property));
		}
		return null;
	}
	
	public static <E extends Enum<E>> void setEnum(final JavaScriptObject object, final String property, final E value) {
		if(value != null) {
			setString(object, property, value.name());
		}
	}
	
	public static native String getString(final JavaScriptObject object, final String property) /*-{
		return object[property];
	}-*/;
	
	public static native void setString(final JavaScriptObject object, final String property, final String value) /*-{
		object[property] = value;
	}-*/;
	
	public static native <J extends JavaScriptObject> J getJavaScriptObject(final JavaScriptObject object, final String property) /*-{
		return object[property];
	}-*/;
	
	public static native <J extends JavaScriptObject> void setJavaScriptObject(final JavaScriptObject object, final String property, final J value) /*-{
		object[property] = value;
	}-*/;
	
	public static Date getDate(final JavaScriptObject object, final String property) {
		final JsDate date = getJavaScriptObject(object, property);
		return toDate(date);
	}
	
	public static void setDate(final JavaScriptObject object, final String property, final Date value) {
		setJavaScriptObject(object, property, toJsDate(value));
	}
	
	public static native boolean isDeleted(final JavaScriptObject object) /*-{
		return typeof object != 'undefined' && object['deleted'] == true;
	}-*/;
	
	public static void setArrayOfIdentifiers(final JavaScriptObject object, final String property, final Collection<? extends EntityDTO<Integer>> dtos) {
		if (dtos == null) {
			return;
		}
		final JsArrayInteger array = createJavaScriptArray(JsArrayInteger.class);
		for(final EntityDTO<Integer> dto : dtos) {
			array.push(dto.getId());
		}
		setJavaScriptObject(object, property, array);
	}
	
}
