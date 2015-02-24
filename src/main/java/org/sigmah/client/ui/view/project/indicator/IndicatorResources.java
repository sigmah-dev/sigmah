package org.sigmah.client.ui.view.project.indicator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * CSS and images used to display indicators.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface IndicatorResources extends ClientBundle {
	
	/**
	 * Shared instance.
	 */
	public static final IndicatorResources INSTANCE = GWT.create(IndicatorResources.class);
	
	ImageResource star();
	ImageResource emptyStar();
	ImageResource map();
	ImageResource emptyMap();
	
	@Source("IndicatorResources.css")
	Style css();
	
	public interface Style extends CssResource {
		String mapIcon();
		String emptyMapIcon();
		String starIcon();
		String emptyStarIcon();
		String indicatorLabel();
		String indicatorLabelInactive();
	}
}
