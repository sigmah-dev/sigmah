package org.sigmah.client.page.project.pivot;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface ProjectPivotResources extends ClientBundle {
	
	static ProjectPivotResources INSTANCE = GWT.create(ProjectPivotResources.class);
	
	@Source("ProjectPivot.css")
	Style style();
	
	interface Style extends CssResource {
		String toolbar();
	}

}
