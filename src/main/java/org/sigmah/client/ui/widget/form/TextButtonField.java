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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;

import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * <p>
 * Custom field implementation composed of one (or multiple) {@link TextField} and a {@link Button} (horizontally
 * aligned).
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class TextButtonField extends MultiField<String> implements Loadable {

	private final List<TextField<String>> textFields;
	private final Button button;
	private final AdapterField buttonAdapter;

	/**
	 * Initializes a new {@code TextButtonField} field instance with <b>one</b> text field.<br>
	 * To initialize multiple text fields, see {@link #TextButtonField(String, int)} constructor.
	 * 
	 * @param fieldLabel
	 *          The field label.
	 */
	public TextButtonField(String fieldLabel) {
		this(fieldLabel, 1);
	}

	/**
	 * Initializes a new {@code TextButtonField} field instance with multiple text fields.
	 * 
	 * @param fieldLabel
	 *          The field label.
	 * @param fieldsNumber
	 *          The number of text fields.
	 */
	public TextButtonField(String fieldLabel, int fieldsNumber) {

		if (fieldsNumber < 1) {
			fieldsNumber = 1;
		}

		textFields = new ArrayList<TextField<String>>(fieldsNumber);
		button = Forms.button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add()); // Default configuration.
		buttonAdapter = Forms.adapter(fieldLabel, button);

		if (ClientUtils.isNotBlank(fieldLabel)) {
			setFieldLabel(fieldLabel);
		} else {
			setLabelSeparator("");
		}

		setSpacing(7);

		for (int i = 0; i < fieldsNumber; i++) {
			final TextField<String> textField = Forms.text(null, false);
			textFields.add(textField);
			add(textField);
		}

		add(buttonAdapter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResize(int width, int height) {

		super.onResize(width, height);

		int buttonWidth = buttonAdapter.el().getParent().getWidth();
		if (buttonWidth <= 0) {
			// TODO Try to 'always' determine dynamic button width (see 'EditFlexibleElementAdminView' problem).
			buttonWidth = 70; // Arbitrary width considering all i18n translations widths.
			button.setWidth(buttonWidth);
		}
		final int textFieldsNumber = textFields.size();
		final int textFieldsTotalWidth = width - buttonWidth - (textFieldsNumber * spacing);
		final int textFieldWidth = textFieldsTotalWidth / textFieldsNumber;

		for (final TextField<String> textField : textFields) {
			textField.setSize(textFieldWidth, height);
		}
	}

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

	/**
	 * Returns the given {@code index} corresponding inner {@link TextField} field.
	 * 
	 * @param index
	 *          The text field index (starting at {@code 0}).
	 * @return The inner {@link TextField} field.
	 * @throws IndexOutOfBoundsException
	 *           If the given {@code index} is out of bounds.
	 */
	public TextField<String> getTextField(final int index) {
		return textFields.get(index);
	}

	/**
	 * Returns the inner {@link Button}.
	 * 
	 * @return The inner {@link Button} (never {@code null}).
	 */
	public Button getButton() {
		return button;
	}

}
