package org.sigmah.client.ui.widget.toolbar;

import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;

import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Convenience subclass for the GXT {@link ToolBar} that offers a method to easily add button(s).
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ActionToolBar extends ToolBar {

	/**
	 * Adds a new {@link org.sigmah.client.ui.widget.button.Button} to the ToolBar.
	 * 
	 * @param label
	 *          The button label (may contain HTML).
	 * @param icon
	 *          The button icon. See {@link org.sigmah.client.ui.res.icon.IconImageBundle}.
	 * @return The added button.
	 */
	public Button addButton(final String label, final AbstractImagePrototype icon) {
		final Button button = Forms.button(label, icon);
		add(button);
		return button;
	}

}
