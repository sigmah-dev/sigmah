package org.sigmah.client.page.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface PivotResources extends ClientBundle {

	public static PivotResources INSTANCE = GWT.create(PivotResources.class);
	
	ImageResource pencil();
	ImageResource zoom();
	ImageResource swap();
	
	@Source("pivot.css")
	Style css();
	
	interface Style extends CssResource {
		String editIcon();
		String zoomIcon();
		String swapIcon();
		String pivotTable();
	}
	
}
