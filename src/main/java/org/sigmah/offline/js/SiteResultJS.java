package org.sigmah.offline.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import org.sigmah.shared.command.result.SiteResult;

/**
 * Javascript version of {@link SiteResult}.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class SiteResultJS extends JavaScriptObject {
	
	protected SiteResultJS() {
	}
	
	public static SiteResultJS toJavaScript(SiteResult siteResult) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	public SiteResult toSiteResult() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	public native int getOffset() /*-{
		return this.offset;
	}-*/;

	public native void setOffset(int offset) /*-{
		this.offset = offset;
	}-*/;

	public native int getSiteCount() /*-{
		return this.siteCount;
	}-*/;

	public native void setSiteCount(int siteCount) /*-{
		this.siteCount = siteCount;
	}-*/;

	public native JsArray<SiteJS> getList() /*-{
		return this.list;
	}-*/;

	public native void setList(JsArray<SiteJS> list) /*-{
		this.list = list;
	}-*/;
	
}
