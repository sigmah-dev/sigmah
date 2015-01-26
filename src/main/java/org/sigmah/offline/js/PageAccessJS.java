package org.sigmah.offline.js;

import com.google.gwt.core.client.JavaScriptObject;
import org.sigmah.client.page.Page;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class PageAccessJS extends JavaScriptObject {
	
	protected PageAccessJS() {
	}
	
	public static PageAccessJS createPageAccessJS(Page page, boolean granted) {
		final PageAccessJS pageAccessJS = Values.createJavaScriptObject(PageAccessJS.class);
		
		pageAccessJS.setPage(page);
		pageAccessJS.setGranted(granted);
		
		return pageAccessJS;
	}
	
	public Page getPage() {
		return Values.getEnum(this, "id", Page.class);
	}
	
	public void setPage(Page page) {
		Values.setEnum(this, "id", page);
	}
	
	public native boolean isGranted() /*-{
		return this.granted;
	}-*/;

	public native void setGranted(boolean granted) /*-{
		this.granted = granted;
	}-*/;
}
