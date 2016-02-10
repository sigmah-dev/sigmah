package org.sigmah.offline.fileapi;

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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Utility class to handle files from client-side.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class Files {
	
	private Files() {
		// Private.
	}
	
	/**
	 * Creates a data URL from the given content and type.
	 * 
	 * @param mimeType Type of the content.
	 * @param content Content.
	 * @return A data URL.
	 */
	public static String toDataURL(String mimeType, String content) {
		return "data:" + mimeType + ',' + URL.encodePathSegment(content);
	}
	
	/**
     * Ask the browser to download the given file.
     * 
     * @param fileName Name of the file to download.
     * @param dataUrl Content of the file as a data URL.
     */
	public static void startDownload(String fileName, String dataUrl) {
		final Element anchorElement = DOM.createAnchor();
		anchorElement.setAttribute("href", dataUrl);
		anchorElement.setAttribute("download", fileName);
		anchorElement.getStyle().setDisplay(Style.Display.NONE);
		RootPanel.getBodyElement().appendChild(anchorElement);
		click(anchorElement);
		anchorElement.removeFromParent();
	}
    
	/**
	 * Generate a click event on a given element.
	 * 
	 * @param e Element to click on.
	 */
    private static native void click(Element e) /*-{
		e.click();
	}-*/;
	
}
