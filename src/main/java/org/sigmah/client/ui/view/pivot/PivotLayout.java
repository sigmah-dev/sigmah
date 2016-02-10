package org.sigmah.client.ui.view.pivot;

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


import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.dispatch.DispatchAsync;



/**
 * Encapsulates the state of a pivot table layout.
 * 
 * @author alexander
 *
 */
abstract class PivotLayout {
	
	
	public abstract String serialize();
	
	
	public static void deserialize(DispatchAsync dispatcher, int projectId, String text, AsyncCallback<PivotLayout> callback) {
		switch(text.charAt(0)) {
		case 'I':
			IndicatorLayout.deserializeIndicator(dispatcher, projectId, text.substring(1), callback);
			return;
		case 'S':
			SiteLayout.deserializeSite(dispatcher, text.substring(1), callback);
			return;
		case 'D':
			DateLayout.deserializeDate(text.substring(1), callback);
			return;
		
		}
		throw new IllegalArgumentException(text);
	}

}
