package org.sigmah.client.page.project.pivot;

import org.sigmah.client.dispatch.Dispatcher;

import com.google.gwt.user.client.rpc.AsyncCallback;



/**
 * Encapsulates the state of a pivot table layout.
 * 
 * @author alexander
 *
 */
abstract class PivotLayout {
	
	
	public abstract String serialize();
	
	
	public static void deserialize(Dispatcher dispatcher, int projectId, String text, AsyncCallback<PivotLayout> callback) {
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
