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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.N10N;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * Custom {@link com.extjs.gxt.ui.client.widget.form.FormPanel} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.sigmah.client.ui.widget.form.Forms
 */
public class FormPanel extends com.extjs.gxt.ui.client.widget.form.FormPanel {

	/**
	 * Set to {@code true} if one (at least) of the form fields value has changed.
	 */
	private boolean valueHasChanged;

	/**
	 * Set to {@code true} to enable the validation icon right padding.<br>
	 * Default to {@code true}.
	 */
	private boolean validationPaddingEnabled = true;

	public FormPanel() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		valueHasChanged = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onRender(final Element target, final int index) {
		super.onRender(target, index);
		if (validationPaddingEnabled) {
			getLayoutTarget().setStyleAttribute("paddingRight", Forms.DEFAULT_RIGHT_PADDING + Unit.PX.getType());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(Widget widget) {
		return this.add(widget, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(final Widget widget, final LayoutData layoutData) {

		addChangeEventListener(widget);

		return super.add(widget, layoutData != null ? layoutData : Forms.data());
	}

	/**
	 * Adds a change event listener to the given {@code widget} inner field(s).<br>
	 * If the widget is a {@link Container}, the method is executed recursively to retrieve the inner field(s).
	 * 
	 * @param widget
	 *          The widget.
	 */
	private void addChangeEventListener(final Widget widget) {

		if (widget instanceof Field) {

			final Field<?> field = (Field<?>) widget;

			field.addListener(Events.Change, new Listener<FieldEvent>() {

				@Override
				public void handleEvent(final FieldEvent be) {
					valueHasChanged = true;
				}

			});

		} else if (widget instanceof Container) {

			@SuppressWarnings("unchecked")
			final Container<Component> container = (Container<Component>) widget;

			for (final Component component : container.getItems()) {
				addChangeEventListener(component);
			}
		}
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 * <p>
	 * Automatically displays a warning message if validation fails.
	 * </p>
	 */
	@Override
	public boolean isValid() {
		return super.isValid();
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 * <p>
	 * Automatically displays a warning message if validation fails.
	 * </p>
	 */
	@Override
	public boolean isValid(boolean preventMark) {
		return isValid(preventMark, true);
	}

	/**
	 * <p>
	 * Returns the form's valid state by querying all child fields.
	 * </p>
	 * <p>
	 * Automatically displays a warning message if validation fails, unless the {@code displayMessage} flag is set to
	 * {@code false}.
	 * </p>
	 * 
	 * @param preventMark
	 *          {@code true} for silent validation (no invalid event and field is not marked invalid).
	 * @param displayMessage
	 *          {@code true} to display a validation message, {@code false} to disable this message.
	 */
	public boolean isValid(boolean preventMark, boolean displayMessage) {

		final boolean valid = super.isValid(preventMark);

		if (!valid && displayMessage) {
			displayValidationMessage();
		}

		return valid;
	}

	/**
	 * Returns if one (at least) of the form fields value has changed.
	 * 
	 * @return {@code true} if one (at least) of the form fields value has changed.
	 */
	public boolean isValueHasChanged() {
		return valueHasChanged;
	}

	/**
	 * <p>
	 * Resets the flag that detects changes in field values.
	 * </p>
	 * <p>
	 * This method should be called:
	 * <ul>
	 * <li><strike>once form has been loaded</strike> (automatically handled during {@link #onAttach()} method execution),
	 * </li>
	 * <li>or once its has been saved.</li>
	 * </ul>
	 * </p>
	 */
	public void resetValueHasChanged() {
		this.valueHasChanged = false;
	}

	/**
	 * Clears all values from all fields and remove all values in {@link Store}.
	 */
	public void clearAll() {
		for (Field<?> f : getFields()) {

			if (f instanceof TimeField) {
				// TimeField store is only populated once, it should not be cleared.
				f.setValue(null);
				continue;
			}

			f.clear();
			if (f instanceof ComboBox) {
				((ComboBox<?>) f).getStore().removeAll();
			}
		}
	}

	/**
	 * Sets the form panel padding value.<br>
	 * The {@code validationPaddingEnabled} argument sets the validation padding enabled state.
	 * 
	 * @param padding
	 *          The padding.
	 * @param validationPaddingEnabled
	 *          {@code true} to enable the validation right padding used to display the validation icon, {@code false} to
	 *          disable it.
	 */
	public void setPadding(int padding, boolean validationPaddingEnabled) {
		super.setPadding(padding);
		this.validationPaddingEnabled = validationPaddingEnabled;
	}

	/**
	 * Displays the failed validation message.
	 */
	private static void displayValidationMessage() {
		N10N.warn(I18N.CONSTANTS.form_validation_ko());
	}

	/**
	 * Validates the given {@code forms} and ensures that only <b>one</b> validation message appears (if validation
	 * fails).
	 * 
	 * @param forms
	 *          The form(s).
	 * @return The form(s) validation result ({@code true} if validation succeeds, {@code false} if it fails).
	 */
	public static boolean valid(final FormPanel... forms) {

		boolean valid = true;

		for (final FormPanel form : forms) {
			valid &= form.isValid(false, false); // Validation without warning message.
		}

		if (!valid) {
			displayValidationMessage();
		}

		return valid;
	}

}
