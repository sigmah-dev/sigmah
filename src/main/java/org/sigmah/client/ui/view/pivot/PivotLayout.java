package org.sigmah.client.ui.view.pivot;


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
