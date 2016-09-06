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
	}
	
	/**
	 * Creates a new javascript object.
	 * 
	 * @param <T>
	 *			Type of the object to create.
	 * @return A new javascript object.
	 */
	public static native <T> T createJavaScriptObject() /*-{
		return {};
	}-*/;
	
	/**
	 * Creates a new javascript object.
	 * 
	 * @param <T>
	 *			Type of the object to create.
	 * @param clazz
	 *			Class of the type of the object to create.
	 * @return A new javascript object.
	 */
	public static native <T> T createJavaScriptObject(final Class<T> clazz) /*-{
		return {};
	}-*/;
	
	/**
	 * Creates a new javascript array with the given type.
	 * <p>
	 * Type may be <code>JsArrayString</code>, <code>JsArrayInt</code> and the
	 * likes. To create a typed <code>JsArray</code> see 
	 * {@link #createTypedJavaScriptArray(java.lang.Class)}.
	 * 
	 * @param <T>
	 *			Type of the array to create.
	 * @param clazz
	 *			Class of the array to create.
	 * @return A new and empty javascript array.
	 */
	public static native <T> T createJavaScriptArray(final Class<T> clazz) /*-{
		return [];
	}-*/;
	
	/**
	 * Creates a new typed javascript array.
	 * 
	 * @param <T>
	 *			Type of the content of the array.
	 * @param clazz
	 *			Class of the content of the array.
	 * @return A new and empty typed javascript array.
	 */
	public static native <T extends JavaScriptObject> JsArray<T> createTypedJavaScriptArray(final Class<T> clazz) /*-{
		return [];
	}-*/;
	
	/**
	 * Convert the given java date to a javascript one.
	 * 
	 * @param date
	 *			Date to convert (can be <code>null</code>).
	 * @return A corresponding javascript date object.
	 */
	public static JsDate toJsDate(final Date date) {
		if (date != null) {
			return JsDate.create(date.getTime());
		} else {
			return null;
		}
	}
	
	/**
	 * Convert the given javascript date to a java one.
	 * 
	 * @param date
	 *			Date to convert (can be <code>null</code>).
	 * @return A corresponding java date object.
	 */
	public static Date toDate(final JsDate date) {
		if (date != null) {
			return new Date((long) date.getTime());
		} else {
			return null;
		}
	}
	
	/**
	 * Returns <code>true</code> if the given property is defined on the given
	 * javascript object.
	 * 
	 * @param object
	 *			Object to test (should not be <code>null</code>).
	 * @param property
	 *			Property to search.
	 * @return <code>true</code> if the given property is defined, <code>false</code> otherwise.
	 */
	public static native boolean isDefined(final JavaScriptObject object, final String property) /*-{
		return typeof object[property] !== 'undefined';
	}-*/;
	
	public static native boolean isObject(final JavaScriptObject object, final String property) /*-{
		return typeof object[property] === 'object';
	}-*/;
	
	public static native boolean isNumber(final JavaScriptObject object, final String property) /*-{
		return typeof object[property] === 'number';
	}-*/;
	
	/**
	 * Returns <code>true</code> if the given object is not <code>null</code>
	 * and if its <code>deleted</code> property is <code>true</code>.
	 * 
	 * @param object
	 *			Object to test (can be <code>null</code>).
	 * @return <code>true</code> if the given object is not <code>null</code>
	 * and if its <code>deleted</code> property is <code>true</code>,
	 * <code>false</code> otherwise.
	 */
	public static native boolean isDeleted(final JavaScriptObject object) /*-{
		return typeof object != 'undefined' && object['deleted'] == true;
	}-*/;
	
	/**
	 * Returns the value of the given property for the given javascript object
	 * as an <code>Integer</code>.
	 * 
	 * @param object
	 *			Javascript object to read.
	 * @param property
	 *			Property to get.
	 * @return An <code>Integer</code> value or <code>null</code>.
	 */
	public static Integer getInteger(final JavaScriptObject object, final String property) {
		if (isDefined(object, property)) {
			return getInt(object, property);
		}
		return null;
	}
	
	/**
	 * Defines the value of the given property for the given javascript object.
	 * 
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @param value 
	 *			<code>Integer</code> value to set.
	 */
	public static void setInteger(final JavaScriptObject object, final String property, final Integer value) {
		if(value != null) {
			setInt(object, property, value);
		}
	}
	
	/**
	 * Returns the value of the given property for the given javascript object
	 * as an <code>int</code>.
	 * 
	 * @param object
	 *			Javascript object to read.
	 * @param property
	 *			Property to get.
	 * @return An <code>int</code> value.
	 * A <code>JavaScriptException</code> will be thrown if the property is <code>null</code> or <code>undefined</code>.
	 */
	public static native int getInt(final JavaScriptObject object, final String property) /*-{
		return object[property];
	}-*/;
	
	/**
	 * Defines the value of the given property for the given javascript object.
	 * 
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @param value 
	 *			<code>int</code> value to set.
	 */
	public static native void setInt(final JavaScriptObject object, final String property, final int value) /*-{
		object[property] = value;
	}-*/;
	
	/**
	 * Returns the value of the given property for the given javascript object
	 * as a <code>Double</code>.
	 * 
	 * @param object
	 *			Javascript object to read.
	 * @param property
	 *			Property to get.
	 * @return A <code>Double</code> value or <code>null</code>.
	 */
	public static Double getDouble(final JavaScriptObject object, final String property) {
		if (isDefined(object, property)) {
			return getNativeDouble(object, property);
		}
		return null;
	}
	
	/**
	 * Defines the value of the given property for the given javascript object.
	 * 
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @param value 
	 *			<code>Double</code> value to set.
	 */
	public static void setDouble(final JavaScriptObject object, final String property, final Double value) {
		if (value != null) {
			setNativeDouble(object, property, value);
		}
	}
	
	/**
	 * Returns the value of the given property for the given javascript object
	 * as a <code>double</code>.
	 * 
	 * @param object
	 *			Javascript object to read.
	 * @param property
	 *			Property to get.
	 * @return A <code>double</code> value.
	 * A <code>JavaScriptException</code> will be thrown if the property is <code>null</code> or <code>undefined</code>.
	 */
	public static native double getNativeDouble(final JavaScriptObject object, final String property) /*-{
		return object[property];
	}-*/;
	
	/**
	 * Defines the value of the given property for the given javascript object.
	 * 
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @param value 
	 *			<code>double</code> value to set.
	 */
	public static native void setNativeDouble(final JavaScriptObject object, final String property, final double value) /*-{
		object[property] = value;
	}-*/;
	
	/**
	 * Returns the enum value of the given property for the given javascript
	 * object.
	 * 
	 * @param <E>
	 *			Enum type.
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @param enumClass
	 *			Class of the enum.
	 * @return An <code>enum</code> value or <code>null</code>.
	 */
	public static <E extends Enum<E>> E getEnum(final JavaScriptObject object, final String property, final Class<E> enumClass) {
		if (isDefined(object, property)) {
			return Enum.valueOf(enumClass, getString(object, property));
		}
		return null;
	}
	
	/**
	 * Defines the value of the given property for the given javascript object.
	 * 
	 * @param <E>
	 *			Enum type.
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @param value 
	 *			<code>enum</code> value to set.
	 */
	public static <E extends Enum<E>> void setEnum(final JavaScriptObject object, final String property, final E value) {
		if (value != null) {
			setString(object, property, value.name());
		}
	}
	
	/**
	 * Returns the <code>String</code> value of the given property for the given
	 * javascript object.
	 * 
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @return A <code>String</code> value or <code>null</code>.
	 */
	public static native String getString(final JavaScriptObject object, final String property) /*-{
		return object[property];
	}-*/;
	
	/**
	 * Defines the value of the given property for the given javascript object.
	 * 
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @param value 
	 *			<code>String</code> value to set.
	 */
	public static native void setString(final JavaScriptObject object, final String property, final String value) /*-{
		object[property] = value;
	}-*/;
	
	/**
	 * Returns the <code>JavaScriptObject</code> value of the given property for
	 * the given javascript object.
	 * 
	 * @param <J>
	 *			Type of JavaScriptObject.
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @return An <code>JavaScriptObject</code> value or <code>null</code>.
	 */
	public static native <J extends JavaScriptObject> J getJavaScriptObject(final JavaScriptObject object, final String property) /*-{
		return object[property];
	}-*/;
	
	/**
	 * Defines the value of the given property for the given javascript object.
	 * 
	 * @param <J>
	 *			Type of JavaScriptObject.
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @param value 
	 *			<code>JavaScriptObject</code> value to set.
	 */
	public static native <J extends JavaScriptObject> void setJavaScriptObject(final JavaScriptObject object, final String property, final J value) /*-{
		object[property] = value;
	}-*/;
	
	/**
	 * Returns the <code>Date</code> value of the given property for the given
	 * javascript object.
	 * 
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @return A <code>Date</code> value or <code>null</code>.
	 */
	public static Date getDate(final JavaScriptObject object, final String property) {
		final JsDate date = getJavaScriptObject(object, property);
		return toDate(date);
	}
	
	/**
	 * Defines the value of the given property for the given javascript object.
	 * 
	 * @param object
	 *			Javascript object to edit.
	 * @param property
	 *			Property to set.
	 * @param value 
	 *			<code>Date</code> value to set.
	 */
	public static void setDate(final JavaScriptObject object, final String property, final Date value) {
		setJavaScriptObject(object, property, toJsDate(value));
	}
	
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
