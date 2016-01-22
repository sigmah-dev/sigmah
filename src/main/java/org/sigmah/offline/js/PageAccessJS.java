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
