package org.sigmah.client.ui.res.icon.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

/**
 * Dashboard images bundle.
 * 
 * @author rca
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@SuppressWarnings("deprecation")
public interface DashboardImageBundle extends ImageBundle {

	/**
	 * Bundle implementation.
	 */
	DashboardImageBundle ICONS = GWT.create(DashboardImageBundle.class);

	@Resource(value = "star.png")
	AbstractImagePrototype star();

	@Resource(value = "emptyStar.png")
	AbstractImagePrototype emptyStar();

}
