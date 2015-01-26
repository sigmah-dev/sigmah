package org.sigmah.offline.event;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ProgressEvent extends JavaScriptObject {
	protected ProgressEvent() {
	}

	public native boolean isLengthComputable() /*-{
		return this.lengthComputable;
	}-*/;

	public native int getLoaded() /*-{
		return this.loaded;
	}-*/;

	public native int getTotal() /*-{
		return this.total;
	}-*/;
}
