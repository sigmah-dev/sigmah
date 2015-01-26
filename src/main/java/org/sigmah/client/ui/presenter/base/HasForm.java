package org.sigmah.client.ui.presenter.base;

import org.sigmah.client.ui.widget.form.FormPanel;

/**
 * Interface implemented by presenters managing one or several form(s).
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface HasForm {

	/**
	 * Returns the {@link FormPanel}(s) managed by the component.
	 * 
	 * @return The {@link FormPanel}(s) managed by the component ({@code null} value(s) are ignored).
	 */
	FormPanel[] getForms();

}
