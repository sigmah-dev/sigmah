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

import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * An upload field represented by a simple button. This component is based on {@link FileUploadField}.
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @see com.extjs.gxt.ui.client.widget.form.FileUploadField
 */
public class ButtonFileUploadField extends TextField<String> implements Loadable {

	private final BaseEventPreview focusPreview;
	private El file;
	private Button button;
	private String buttonCaption;
	private String accept;
	private AbstractImagePrototype buttonIcon;

	/**
	 * Creates a new file upload field.
	 */
	public ButtonFileUploadField() {
		focusPreview = new BaseEventPreview();
		focusPreview.setAutoHide(false);
		ensureVisibilityOnSizing = true;
		setWidth(150);
	}

	public String getButtonCaption() {
		return buttonCaption;
	}

	public void setButtonCaption(String buttonCaption) {
		this.buttonCaption = buttonCaption;
	}

	/**
	 * A comma-separated list of content types that a server processing this form will handle correctly.
	 * 
	 * @return The comma-separated list of content types.
	 */
	public String getAccept() {
		if (rendered) {
			return getFileInput().getAccept();
		}
		return accept;
	}

	/**
	 * A comma-separated list of content types that a server processing this form will handle correctly.
	 * 
	 * @param accept
	 *          The comma-separated list of content types.
	 */
	public void setAccept(String accept) {
		this.accept = accept;
		if (rendered) {
			getFileInput().setAccept(accept);
		}
	}

	/**
	 * Returns the button icon.
	 * 
	 * @return The button icon.
	 */
	public AbstractImagePrototype getButtonIconStyle() {
		return buttonIcon;
	}

	/**
	 * Sets the button icon.
	 * 
	 * @param buttonIcon
	 *          The button icon.
	 */
	public void setButtonIcon(AbstractImagePrototype buttonIcon) {
		this.buttonIcon = buttonIcon;
	}

	/**
	 * Returns the file input element. You should not store a reference to this. When resetting this field the file input
	 * will change.
	 * 
	 * @return The underlying input element.
	 */
	public InputElement getFileInput() {
		return (InputElement) file.dom.cast();
	}

	/**
	 * Callback executed on {@code OnChange} event.
	 */
	private void onChange() {
		setValue(getFileInput().getValue());
	}

	/**
	 * Creates the underlying file input.
	 */
	private void createFileInput() {

		if (file != null) {
			el().removeChild(file.dom);
		}

		final InputElement fileElement = Document.get().createFileInputElement();

		file = new El((Element) fileElement.cast());
		file.addEventsSunk(Event.ONCHANGE | Event.FOCUSEVENTS);
		file.setId(XDOM.getUniqueId());
		file.addStyleName("x-form-file");
		file.setTabIndex(-1);
		getFileInput().setName(name);
		getFileInput().setAccept(accept);
		file.insertInto(getElement(), 1);
		if (file != null) {
			file.setEnabled(isEnabled());
		}
	}

	// -------------------------------------------------------------------------------------------
	//
	// Loadable implementation.
	//
	// -------------------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoading(boolean loading) {
		button.setLoading(loading);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLoading() {
		return button.isLoading();
	}

	// -------------------------------------------------------------------------------------------
	//
	// TextField overridden methods.
	//
	// -------------------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		if (rendered) {
			final String n = file.dom.getAttribute("name");
			if (!n.equals("")) {
				return n;
			}
		}
		return super.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		if ((event.getTypeInt() != Event.ONCLICK) && ((Element) event.getEventTarget().cast()).isOrHasChild(file.dom)) {
			button.onBrowserEvent(event);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onComponentEvent(ComponentEvent ce) {
		super.onComponentEvent(ce);
		switch (ce.getEventTypeInt()) {
			case Event.ONCHANGE:
				onChange();
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		super.reset();
		createFileInput();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		this.name = name;
		if (rendered) {
			file.dom.removeAttribute("name");
			if (name != null) {
				((InputElement) file.dom.cast()).setName(name);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		if (button != null) {
			button.setEnabled(!readOnly);
		}
		if (file != null) {
			file.setEnabled(!readOnly);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void afterRender() {
		super.afterRender();
		el().removeStyleName(fieldStyle);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doAttachChildren() {
		super.doAttachChildren();
		ComponentHelper.doAttach(button);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doDetachChildren() {
		super.doDetachChildren();
		ComponentHelper.doDetach(button);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected El getFocusEl() {
		return el();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected El getInputEl() {
		return el();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected El getStyleEl() {
		return el();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onBlur(ComponentEvent ce) {
		final Rectangle rec = button.el().getBounds();
		if (rec.contains(BaseEventPreview.getLastXY())) {
			ce.stopEvent();
			return;
		}
		super.onBlur(ce);
		focusPreview.remove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDetach() {
		super.onDetach();
		if (focusPreview != null) {
			focusPreview.remove();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onFocus(ComponentEvent ce) {
		focusPreview.add();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onRender(Element target, int index) {

		final El wrap = new El(DOM.createDiv());
		wrap.addStyleName("x-form-field-wrap");
		wrap.addStyleName("x-form-file-wrap");

		setElement(wrap.dom, target, index);

		createFileInput();

		button = Forms.button(buttonCaption != null ? buttonCaption : "...");
		button.addStyleName("x-form-file-btn");
		button.setIcon(buttonIcon);
		button.render(wrap.dom);

		super.onRender(target, index);
		super.setReadOnly(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		el().setWidth(button.getWidth());
	}

}
