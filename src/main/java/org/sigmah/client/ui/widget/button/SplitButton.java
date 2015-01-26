package org.sigmah.client.ui.widget.button;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Util;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Accessibility;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.Loadable;

/**
 * {@link Loadable} split button implementation.
 * 
 * @see com.extjs.gxt.ui.client.widget.button.SplitButton
 * @see org.sigmah.client.ui.widget.Loadable
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SplitButton extends com.extjs.gxt.ui.client.widget.button.SplitButton implements Loadable {

	/**
	 * Loading state of the button.
	 */
	private boolean loading;

	/**
	 * Initial enabled state of the button.
	 */
	private boolean initialEnabledState = isEnabled();

	/**
	 * Creates a new split button.
	 */
	public SplitButton() {
		super();
	}

	/**
	 * Creates a new split button with the given HTML.
	 * 
	 * @param html
	 *          the button label as HTML.
	 */
	public SplitButton(final String html) {
		super(html);
	}
	
	/**
	 * Creates a new split button with the given HTML and icon.
	 * 
	 * @param html
	 *          the button label as HTML.
	 * @param icon
	 *          the icon.
	 */
	public SplitButton(final String html, final AbstractImagePrototype icon) {
		super(html);
		setIcon(icon);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoading(final boolean loading) {

		if (!this.loading && loading) {
			super.setEnabled(false);
			replaceIcon(IconImageBundle.ICONS.loading());

		} else if (this.loading && !loading) {
			super.setEnabled(initialEnabledState);
			setIcon(getIcon());
		}

		this.loading = loading;
	}

	@Override
	public boolean isLoading() {
		return loading;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		initialEnabledState = enabled;
	}
	
	/**
	 * Replaces the button icon <b>but</b> does not save icon reference into parent {@code icon} attribute.
	 * 
	 * @param icon
	 *          The button icon to display.
	 */
	private void replaceIcon(final AbstractImagePrototype icon) {

		if (rendered) {
			El oldIcon = buttonEl.selectNode("." + baseStyle + "-image");
			if (oldIcon != null) {
				oldIcon.remove();
				el().removeStyleName(baseStyle + "-text-icon", baseStyle + "-icon", baseStyle + "-noicon");
			}
			el()
				.addStyleName((icon != null ? (!Util.isEmptyString(html) ? " " + baseStyle + "-text-icon" : " " + baseStyle + "-icon") : " " + baseStyle + "-noicon"));
			Element e = null;

			if (icon != null) {
				e = (Element) icon.createElement().cast();

				Accessibility.setRole(e, "presentation");
				fly(e).addStyleName(baseStyle + "-image");

				buttonEl.insertFirst(e);
				El.fly(e).makePositionable(true);

			}
			autoWidth();
			alignIcon(e);
		}
	}
}
