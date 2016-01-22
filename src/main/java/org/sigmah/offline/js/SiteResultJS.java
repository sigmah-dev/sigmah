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
