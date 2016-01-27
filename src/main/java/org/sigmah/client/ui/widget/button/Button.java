package org.sigmah.client.ui.widget.button;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.Loadable;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Util;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Accessibility;

/**
 * {@link Loadable} button implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see com.extjs.gxt.ui.client.widget.button.Button
 * @see org.sigmah.client.ui.widget.Loadable
 */
public class Button extends com.extjs.gxt.ui.client.widget.button.Button implements Loadable {

	/**
	 * Loading state of the button.
	 */
	private boolean loading;

	/**
	 * Initial enabled state of the button.
	 */
	private boolean initialEnabledState = isEnabled();

	/**
	 * Creates a new button.
	 */
	public Button() {
		super();
	}

	/**
	 * Creates a new button with the given HTML.
	 * 
	 * @param html
	 *          the button text as HTML.
	 */
	public Button(final String html) {
		super(html);
	}

	/**
	 * Creates a new button with the given HTML and icon.
	 * 
	 * @param html
	 *          the button text as HTML.
	 * @param icon
	 *          the icon.
	 */
	public Button(final String html, final AbstractImagePrototype icon) {
		super(html, icon);
	}

	/**
	 * Creates a new button with the given HTML, icon and specified selection listener.
	 * 
	 * @param html
	 *          the button text as HTML.
	 * @param icon
	 *          the icon.
	 * @param listener
	 *          the selection listener.
	 */
	public Button(final String html, final AbstractImagePrototype icon, final SelectionListener<ButtonEvent> listener) {
		super(html, icon, listener);
	}

	/**
	 * Creates a new button with the given HTML and specified selection listener.
	 * 
	 * @param html
	 *          the button's text as HTML.
	 * @param listener
	 *          the selection listener.
	 */
	public Button(final String html, final SelectionListener<ButtonEvent> listener) {
		super(html, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoading(final boolean loading) {

		if (!this.loading && loading) {
            initialEnabledState = isEnabled();
			super.setEnabled(false);
			replaceIcon(IconImageBundle.ICONS.loading());

		} else if (this.loading && !loading) {
			super.setEnabled(initialEnabledState);
			setIcon(getIcon());
		}

		this.loading = loading;
	}

	/**
	 * {@inheritDoc}
	 */
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
        updateInitialEnabldStateWhileLoading(enabled);
	}

    /**
	 * {@inheritDoc}
	 */
    @Override
    public void enable() {
        super.enable();
        updateInitialEnabldStateWhileLoading(true);
    }

    /**
	 * {@inheritDoc}
	 */
    @Override
    public void disable() {
        super.disable();
        updateInitialEnabldStateWhileLoading(false);
    }
    
    /**
     * Changes the saved value to restore correctly the state when the load ends.
     * 
     * @param enabled Enabled state.
     */
    private void updateInitialEnabldStateWhileLoading(boolean enabled) {
        if (loading) {
            this.initialEnabledState = enabled;
        }
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
