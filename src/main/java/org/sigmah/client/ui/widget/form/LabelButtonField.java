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

import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;

/**
 * <p>
 * Custom field implementation composed of one {@link LabelField} and a {@link Button} (horizontally
 * aligned).
 * </p>
 *
 * @author tde (tde@atolcd.com)
 */
public class LabelButtonField extends MultiField<String> implements Loadable {

	private final LabelField labelField;
	private final Button button;
	private final AdapterField buttonAdapter;

	/**
	 * Initializes a new {@code TextButtonField} field instance with multiple text fields.
	 *
	 * @param fieldLabel
	 *          The field label.
	 */
	public LabelButtonField(String fieldLabel) {

		labelField = new LabelField();

		button = Forms.button(I18N.CONSTANTS.formWindowListEmptyText());
		button.setAutoWidth(true);
		buttonAdapter = Forms.adapter(fieldLabel, button);

		if (ClientUtils.isNotBlank(fieldLabel)) {
			setFieldLabel(fieldLabel);
		} else {
			setLabelSeparator("");
		}

		setSpacing(7);

		add(labelField);
		add(buttonAdapter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
		button.setVisible(enabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResize(int width, int height) {

		super.onResize(width, height);

		int buttonWidth = buttonAdapter.el().getParent().getWidth();
		if (buttonWidth <= 0) {
			buttonWidth = button.getWidth();
		}
		final int textFieldWidth = width - buttonWidth - spacing;

		labelField.setSize(textFieldWidth, height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(String value) {
		labelField.setValue(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue() {
		return String.valueOf(labelField.getValue());
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
	 * Returns the inner {@link Button}.
	 *
	 * @return The inner {@link Button} (never {@code null}).
	 */
	public Button getButton() {
		return button;
	}

}
