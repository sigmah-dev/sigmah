package org.sigmah.offline.fileapi;

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
