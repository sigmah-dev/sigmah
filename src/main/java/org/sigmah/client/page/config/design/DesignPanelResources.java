package org.sigmah.client.page.config.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface DesignPanelResources extends ClientBundle {
	
	public static final DesignPanelResources INSTANCE = GWT.create(DesignPanelResources.class);
	
	
	ImageResource star();
	ImageResource emptyStar();
	ImageResource map();
	ImageResource emptyMap();
	
	@Source("DesignPanel.css")
	Style css();
	
	public interface Style extends CssResource {
		String mapIcon();
		String emptyMapIcon();
		String starIcon();
		String emptyStarIcon();
		String indicatorLabel();
		
	}
}
