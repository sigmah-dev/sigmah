package org.sigmah.client.ui.widget.form;

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

import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * GXT style form field that contains a functionnal GWT widget.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class WidgetField<W extends Widget, D> extends Field<D> {

	private W widget;

	public WidgetField(W widget) {
		setWidth(150); // Default width for GXT fields. Taken from TextField<D>.
		this.widget = widget;
	}

	public W getWidget() {
		return widget;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBrowserEvent(Event event) {
		int mouseX = event.getClientX();
		int mouseY = event.getClientY();

		int top = widget.getAbsoluteTop();
		int left = widget.getAbsoluteLeft();

		int buttonWidth = getButtonWidth(widget.getElement().getId());
		int buttonHeight = getButtonHeight(widget.getElement().getId());

		if ((mouseX > left && mouseX < left + buttonWidth) && (mouseY > top && mouseY < top + buttonHeight)) {
			widget.onBrowserEvent(event);
		}
	}

	private native int getButtonWidth(String id) /*-{
		var element = $wnd.document.getElementById(id);
		var style = $wnd.getComputedStyle(element, null);
		return parseInt(style.width);
	}-*/;

	private native int getButtonHeight(String id) /*-{
		var element = $wnd.document.getElementById(id);
		var style = $wnd.getComputedStyle(element, null);
		return parseInt(style.height);
	}-*/;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onRender(Element parent, int index) {
		if (el() == null) {
			setElement(widget.getElement(), parent, index);
		}

		if (widget.getElement().getId() == null)
			widget.getElement().setId("element-" + (int) (Math.random() * 9999) + '-' + (int) (Math.random() * 9999));

		super.onRender(parent, index);
	}
}
