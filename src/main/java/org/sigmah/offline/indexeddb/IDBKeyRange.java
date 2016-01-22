package org.sigmah.offline.indexeddb;

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
