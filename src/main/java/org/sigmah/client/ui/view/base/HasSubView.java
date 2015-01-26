package org.sigmah.client.ui.view.base;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/**
 * Each <b>parent</b> presenter's view implementation should implement this interface.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface HasSubView extends ViewInterface {

	/**
	 * Returns the sub-presenters placeholder, where sub-views will be shown.
	 * 
	 * @return The sub-presenters placeholder.
	 */
	LayoutContainer getPlaceHolder();

}
