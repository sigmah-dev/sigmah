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


import java.util.Date;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.button.SplitButton;
import org.sigmah.client.util.ClientUtils;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import org.sigmah.client.ui.res.icon.IconImageBundle;

/**
 * <p>
 * Forms utility class providing utility methods for forms.
 * </p>
 * <p>
 * Useful default values:
 * <ul>
 * <li><b>Form padding:</b> 10px (see {@link #DEFAULT_PADDING}).</li>
 * <li><b>Label width:</b> 100px (see {@link #DEFAULT_LABEL_WIDTH}).</li>
 * <li><b>Field width:</b> 250px (see {@link #DEFAULT_FIELD_WIDTH}).</li>
 * </ul>
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.sigmah.client.ui.widget.form.FormPanel
 */
public final class Forms {

	/**
	 * <p>
	 * Default forms fields width (in pixels).
	 * </p>
	 * <p>
	 * Instead of specifying field width manually, use {@link #data()} to generate a {@code 100%} width.
	 * </p>
	 */
	private static final int DEFAULT_FIELD_WIDTH = 250;

	/**
	 * Default forms padding (in pixels).
	 */
	private static final int DEFAULT_PADDING = 10;

	/**
	 * <p>
	 * Default forms panels right padding (in pixels).<br/>
	 * This specific right padding is used to properly display fields error icons.
	 * </p>
	 */
	static final int DEFAULT_RIGHT_PADDING = 25;

	/**
	 * Builds a new {@link org.sigmah.client.ui.widget.form.FormPanel} with default {@link FormLayout}.
	 * 
	 * @param stylenames
	 *          (optional) Style names added to the panel component.<br/>
	 *          {@code null} values are ignored.
	 * @return The {@link org.sigmah.client.ui.widget.form.FormPanel} instance.
	 */
	public static org.sigmah.client.ui.widget.form.FormPanel panel(final String... stylenames) {

		return panel(null, false, null, null, stylenames);
	}

	/**
	 * Builds a new {@link org.sigmah.client.ui.widget.form.FormPanel} with default {@link FormLayout}.
	 * 
	 * @param labelWidth
	 *          The width of the form fields labels.
	 * @param stylenames
	 *          (optional) Style names added to the panel component.<br/>
	 *          {@code null} values are ignored.
	 * @return The {@link org.sigmah.client.ui.widget.form.FormPanel} instance.
	 */
	public static org.sigmah.client.ui.widget.form.FormPanel panel(final Integer labelWidth, final String... stylenames) {

		return panel(null, false, labelWidth, null, stylenames);
	}

	/**
	 * Builds a new {@link org.sigmah.client.ui.widget.form.FormPanel} with default {@link FormLayout}.
	 * 
	 * @param labelWidth
	 *          The width of the form fields labels.
	 * @param fieldWidth
	 *          The width of the form fields. Set a default value if {@code null}.
	 * @param stylenames
	 *          (optional) Style names added to the panel component.<br/>
	 *          {@code null} values are ignored.
	 * @return The {@link org.sigmah.client.ui.widget.form.FormPanel} instance.
	 */
	public static org.sigmah.client.ui.widget.form.FormPanel panel(final Integer labelWidth, final Integer fieldWidth, final String... stylenames) {

		return panel(null, false, labelWidth, fieldWidth, stylenames);
	}

	/**
	 * Builds a new {@link org.sigmah.client.ui.widget.form.FormPanel} with default {@link FormLayout}.
	 * 
	 * @param title
	 *          The panel header title (html is supported).<br/>
	 *          If {@code null}, header is disabled and automatically hidden.
	 * @param collapsible
	 *          {@code true} to set the panel collapsible (expand/collapse toggle button).
	 * @param labelWidth
	 *          The width of the form fields labels.
	 * @param fieldWidth
	 *          The width of the form fields. Set a default value if {@code null}.
	 * @param stylenames
	 *          (optional) Style names added to the panel component.<br/>
	 *          {@code null} values are ignored.
	 * @return The {@link org.sigmah.client.ui.widget.form.FormPanel} instance.
	 */
	public static org.sigmah.client.ui.widget.form.FormPanel panel(final String title, final boolean collapsible, final Integer labelWidth,
			final Integer fieldWidth, final String... stylenames) {

		final org.sigmah.client.ui.widget.form.FormPanel panel = new org.sigmah.client.ui.widget.form.FormPanel();

		panel.setLayout(layout(labelWidth, fieldWidth, stylenames));
		panel.setHeadingHtml(ClientUtils.isNotBlank(title) ? title : null);
		panel.setHeaderVisible(ClientUtils.isNotBlank(title));
		panel.setCollapsible(collapsible);
		panel.setMethod(Method.POST);
		panel.setFieldWidth(fieldWidth != null ? fieldWidth : DEFAULT_FIELD_WIDTH);
		panel.setButtonAlign(HorizontalAlignment.RIGHT);
		panel.setPadding(DEFAULT_PADDING);

		if (ClientUtils.isNotEmpty(stylenames)) {
			for (final String stylename : stylenames) {
				if (ClientUtils.isBlank(stylename)) {
					continue;
				}
				panel.addStyleName(stylename);
			}
		}

		return panel;
	}

	// --------------------------------------------------------------------------------
	//
	// LAYOUT.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Default forms labels width (in pixels).
	 */
	private static final int DEFAULT_LABEL_WIDTH = 100;

	/**
	 * Default space between forms labels and fields (in pixels).
	 */
	private static final int DEFAULT_LABEL_PAD = 5;

	/**
	 * Builds a new {@link FormLayout}.
	 * 
	 * @param labelWidth
	 *          The width of the form fields labels. Set a default value if {@code null}.
	 * @param fieldWidth
	 *          The width of the form fields. Set a default value if {@code null}.
	 * @param stylenames
	 *          (optional) Style names added to the layout.<br/>
	 *          {@code null} values are ignored.
	 * @return The {@link FormLayout} instance.
	 */
	public static FormLayout layout(final Integer labelWidth, final Integer fieldWidth, final String... stylenames) {

		final FormLayout layout = new FormLayout() {

			@Override
			protected void initTarget() {
				super.initTarget();
				target.addStyleName(stylenames);
			}
		};

		layout.setLabelWidth(labelWidth != null ? labelWidth : DEFAULT_LABEL_WIDTH);
		layout.setDefaultWidth(fieldWidth != null ? fieldWidth : DEFAULT_FIELD_WIDTH);
		layout.setLabelPad(DEFAULT_LABEL_PAD);
		layout.setLabelSeparator(I18N.CONSTANTS.form_label_separator());
		layout.setLabelAlign(LabelAlign.LEFT);

		return layout;
	}

	/**
	 * Builds a new {@link FormData} configured with width {@code 100%}.
	 * 
	 * @return The {@link FormData} instance.
	 */
	public static FormData data() {
		return new FormData("100%");
	}

	// --------------------------------------------------------------------------------
	//
	// FIELDS.
	//
	// --------------------------------------------------------------------------------

	// --
	// -- TextField
	// --

	/**
	 * Builds a {@link TextField}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static <D> TextField<D> text(String label, boolean mandatory, String... stylenames) {
		return text(label, mandatory, null, stylenames);
	}

	/**
	 * Builds a {@link TextField}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param maxLength
	 *          Max length of the value.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static <D> TextField<D> text(String label, boolean mandatory, Integer maxLength, String... stylenames) {

		final TextField<D> field = new TextField<D>();

		// Label.
		if (ClientUtils.isNotBlank(label)) {
			field.setFieldLabel(label);
		}

		// Constraints.
		field.setAllowBlank(!mandatory);
		if (maxLength != null) {
			maxLength = maxLength >= 0 ? maxLength : 0;
			field.setMaxLength(maxLength);
		}

		return addStyles(field, stylenames);

	}

	// --
	// -- TextArea
	// --

	/**
	 * Builds a {@link TextArea}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static TextArea textarea(String label, boolean mandatory, String... stylenames) {
		return textarea(label, mandatory, null, stylenames);
	}

	/**
	 * Builds a {@link TextArea}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param maxLength
	 *          Max length of the value.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static TextArea textarea(String label, boolean mandatory, Integer maxLength, String... stylenames) {

		final TextArea field = new TextArea();

		// Label.
		if (ClientUtils.isNotBlank(label)) {
			field.setFieldLabel(label);
		}

		// Constraints.
		field.setAllowBlank(!mandatory);
		if (maxLength != null) {
			maxLength = maxLength >= 0 ? maxLength : 0;
			field.setMaxLength(maxLength);
		}

		return addStyles(field, stylenames);

	}

	// --
	// -- NumberField
	// --

	/**
	 * Builds a {@link NumberField}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static NumberField number(String label, boolean mandatory, String... stylenames) {
		return number(label, mandatory, true, stylenames);
	}

	/**
	 * Builds a {@link NumberField}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param allowNegative
	 *          Sets whether negative value are allowed.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static NumberField number(String label, boolean mandatory, boolean allowNegative, String... stylenames) {
		return number(label, mandatory, allowNegative, true, stylenames);
	}

	/**
	 * Builds a {@link NumberField}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param allowNegative
	 *          Sets whether negative value are allowed.
	 * @param allowDecimals
	 *          Sets whether decimal value are allowed.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static NumberField number(String label, boolean mandatory, boolean allowNegative, boolean allowDecimals, String... stylenames) {
		return number(label, mandatory, allowNegative, allowDecimals, null, stylenames);
	}

	/**
	 * Builds a {@link NumberField}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param allowNegative
	 *          Sets whether negative value are allowed.
	 * @param allowDecimals
	 *          Sets whether decimal value are allowed.
	 * @param format
	 *          The number format.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static NumberField number(String label, boolean mandatory, boolean allowNegative, boolean allowDecimals, final NumberFormat format,
			String... stylenames) {

		final NumberField field = new NumberField();

		// Label.
		if (ClientUtils.isNotBlank(label)) {
			field.setFieldLabel(label);
		}

		// Format.
		if (format != null) {
			field.setFormat(format);
			field.addListener(Events.Blur, new Listener<FieldEvent>() {

				@Override
				public void handleEvent(FieldEvent be) {
					final Number value = field.getValue();
					field.setFormat(format);
					field.setValue(value);
				}

			});
			field.addListener(Events.Focus, new Listener<FieldEvent>() {

				@Override
				public void handleEvent(FieldEvent be) {
					final Number value = field.getValue();
					field.setFormat(null);
					field.setValue(value);
				}

			});
		}

		// Constraints.
		field.setAllowBlank(!mandatory);
		field.setAllowNegative(allowNegative);
		field.setAllowDecimals(allowDecimals);

		return addStyles(field, stylenames);

	}

	// --
	// -- LabelField
	// --

	/**
	 * Builds a {@link LabelField}.
	 * 
	 * @param label
	 *          The field label (automatically includes proper form label separator).
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static LabelField label(String label, String... stylenames) {

		final LabelField field = new LabelField();

		// Label.
		if (ClientUtils.isNotBlank(label)) {
			// Note: GXT does not set a form label separator on LabelField (?).
			field.setFieldLabel(label + I18N.CONSTANTS.form_label_separator());
		}

		return addStyles(field, stylenames);

	}

	// --
	// -- ComboBox
	// --

	/**
	 * Builds a {@link ComboBox} with a default empty {@link ListStore} and default {@link TriggerAction#ALL}.<br>
	 * Generated field is not editable by default.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param valueField
	 *          The value field name.
	 * @param displayField
	 *          The display field.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static <D extends ModelData> ComboBox<D> combobox(String label, boolean mandatory, String valueField, String displayField, String... stylenames) {
		return combobox(label, mandatory, valueField, displayField, null, null, stylenames);
	}

	/**
	 * Builds a {@link ComboBox} with default {@link TriggerAction#ALL}.<br>
	 * Generated field is not editable by default.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param valueField
	 *          The value field name.
	 * @param displayField
	 *          The display field.
	 * @param store
	 *          The values store.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static <D extends ModelData> ComboBox<D> combobox(String label, boolean mandatory, String valueField, String displayField, ListStore<D> store,
			String... stylenames) {
		return combobox(label, mandatory, valueField, displayField, null, store, stylenames);
	}

	/**
	 * Builds a {@link ComboBox} with default {@link TriggerAction#ALL}.<br>
	 * Generated field is not editable by default.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param valueField
	 *          The value field name.
	 * @param displayField
	 *          The display field.
	 * @param emptyText
	 *          The empty text. If blank, a default empty text is set.
	 * @param store
	 *          The values store. If {@code null}, an empty {@link ListStore} is set.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static <D extends ModelData> ComboBox<D> combobox(String label, boolean mandatory, String valueField, String displayField, String emptyText,
			ListStore<D> store, String... stylenames) {

		final ComboBox<D> field = new ComboBox<D>();
		field.setTriggerAction(TriggerAction.ALL);
		field.setEditable(false);

		// Label.
		if (ClientUtils.isNotBlank(label)) {
			field.setFieldLabel(label);
		}
		if (ClientUtils.isNotBlank(emptyText)) {
			field.setEmptyText(emptyText);
		} else {
			field.setEmptyText(I18N.CONSTANTS.formWindowListEmptyText());
		}

		// Value.
		field.setValueField(valueField);
		field.setDisplayField(displayField);

		// Store.
		field.setStore(store != null ? store : new ListStore<D>());

		// Constraints.
		field.setAllowBlank(!mandatory);

		return addStyles(field, stylenames);

	}

	// --
	// -- SimpleComboBox
	// --

	/**
	 * Builds a {@link SimpleComboBox} with default {@link TriggerAction#ALL}.<br>
	 * Generated field is not editable by default.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static <T> SimpleComboBox<T> simpleCombobox(String label, boolean mandatory, String... stylenames) {
		return simpleCombobox(label, mandatory, null, stylenames);
	}

	/**
	 * Builds a {@link SimpleComboBox} with default {@link TriggerAction#ALL}.<br>
	 * Generated field is not editable by default.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param emptyText
	 *          The empty text. If blank, a default empty text is set.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static <T> SimpleComboBox<T> simpleCombobox(String label, boolean mandatory, String emptyText, String... stylenames) {

		final SimpleComboBox<T> field = new SimpleComboBox<T>();
		field.setTriggerAction(TriggerAction.ALL);
		field.setEditable(false);

		// Label.
		if (ClientUtils.isNotBlank(label)) {
			field.setFieldLabel(label);
		}
		if (ClientUtils.isNotBlank(emptyText)) {
			field.setEmptyText(emptyText);
		} else {
			field.setEmptyText(I18N.CONSTANTS.formWindowListEmptyText());
		}

		// Constraints.
		field.setAllowBlank(!mandatory);

		return addStyles(field, stylenames);

	}

	// --
	// -- Radio / RadioGroup
	// --

	/**
	 * Builds a {@link Radio}.
	 * 
	 * @param boxLabel
	 *          The field <b>radio</b> label.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static Radio radio(String boxLabel, String... stylenames) {

		return radio(boxLabel, null, null, stylenames);

	}

	/**
	 * Builds a {@link Radio}.
	 * 
	 * @param boxLabel
	 *          The field <b>radio</b> label.
	 * @param value
	 *          The field value ({@code null} means {@code false}).
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static Radio radio(String boxLabel, Boolean value, String... stylenames) {

		return radio(boxLabel, null, value, stylenames);

	}

	/**
	 * Builds a {@link Radio}.
	 * 
	 * @param boxLabel
	 *          The field <b>radio</b> label.
	 * @param name
	 *          The radio field name property (recommended for {@link RadioGroup} fields).
	 * @param value
	 *          The field value ({@code null} means {@code false}).
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static Radio radio(String boxLabel, String name, Boolean value, String... stylenames) {

		return radio(boxLabel, name, null, value, stylenames);

	}

	/**
	 * Builds a {@link Radio}.
	 * 
	 * @param boxLabel
	 *          The field <b>radio</b> label.
	 * @param name
	 *          The radio field name property (recommended for {@link RadioGroup} fields).
	 * @param fieldLabel
	 *          The field label.
	 * @param value
	 *          The field value ({@code null} means {@code false}).
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static Radio radio(String boxLabel, String name, String fieldLabel, Boolean value, String... stylenames) {

		final Radio field = new Radio();

		if (ClientUtils.isNotBlank(boxLabel)) {
			field.setBoxLabel(boxLabel);
		}

		if (ClientUtils.isNotBlank(name)) {
			field.setName(name);
		}

		if (ClientUtils.isNotBlank(fieldLabel)) {
			field.setFieldLabel(fieldLabel);
		}

		field.setValue(value);

		return addStyles(field, stylenames);

	}

	/**
	 * Builds a {@link RadioGroup} with auto-generated name property and default horizontal orientation.
	 * 
	 * @param label
	 *          The field label.
	 * @param radios
	 *          The {@link Radio} fields that belong to the group.
	 * @return The field.
	 */
	public static RadioGroup radioGroup(String label, Radio... radios) {

		return radioGroup(label, null, null, radios);

	}

	/**
	 * Builds a {@link RadioGroup} with auto-generated name property.
	 * 
	 * @param label
	 *          The field label.
	 * @param orientation
	 *          The radios orientation.
	 * @param radios
	 *          The {@link Radio} fields that belong to the group.
	 * @return The field.
	 */
	public static RadioGroup radioGroup(String label, Orientation orientation, Radio... radios) {

		return radioGroup(label, null, orientation, radios);

	}

	/**
	 * Builds a {@link Radio}.
	 * 
	 * @param label
	 *          The field label.
	 * @param name
	 *          The radios group name property. If {@code null}, default auto-generated name is set.
	 * @param orientation
	 *          The radios orientation. If {@code null}, default horizontal orientation is set.
	 * @param radios
	 *          The {@link Radio} fields that belong to the group.
	 * @return The field.
	 */
	public static RadioGroup radioGroup(String label, String name, Orientation orientation, Radio... radios) {

		final RadioGroup field = new RadioGroup();

		if (ClientUtils.isNotBlank(label)) {
			field.setFieldLabel(label);
		}

		field.setName(ClientUtils.isNotBlank(name) ? name : Math.random() + "_" + new Date().getTime());

		if (orientation != null) {
			field.setOrientation(orientation);
		}

		if (ClientUtils.isNotEmpty(radios)) {
			for (final Radio radio : radios) {
				if (radio == null) {
					continue;
				}
				field.add(radio);
			}
		}

		return field;

	}

	// --
	// -- CheckBox / CheckBoxGroup
	// --

	/**
	 * Builds a {@link CheckBox}.
	 * 
	 * @param boxLabel
	 *          The field <b>checkbox</b> label. None if {@code null}, but <em>empty</em> will generate one.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static CheckBox checkbox(String boxLabel, String... stylenames) {

		return checkbox(boxLabel, null, null, stylenames);

	}

	/**
	 * Builds a {@link CheckBox}.
	 * 
	 * @param boxLabel
	 *          The field <b>checkbox</b> label. None if {@code null}, but <em>empty</em> will generate one.
	 * @param value
	 *          The field value ({@code null} means {@code false}).
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static CheckBox checkbox(String boxLabel, Boolean value, String... stylenames) {

		return checkbox(boxLabel, null, value, stylenames);

	}

	/**
	 * Builds a {@link CheckBox}.
	 * 
	 * @param boxLabel
	 *          The field <b>checkbox</b> label. None if {@code null}, but <em>empty</em> will generate one.
	 * @param name
	 *          The field name property (recommended for {@link CheckBoxGroup} fields).
	 * @param value
	 *          The field value ({@code null} means {@code false}).
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static CheckBox checkbox(String boxLabel, String name, Boolean value, String... stylenames) {

		return checkbox(boxLabel, name, null, value, stylenames);

	}

	/**
	 * Builds a {@link CheckBox}.
	 * 
	 * @param boxLabel
	 *          The field <b>checkbox</b> label. None if {@code null}, but <em>empty</em> will generate one.
	 * @param name
	 *          The field name property (recommended for {@link CheckBoxGroup} fields).
	 * @param fieldLabel
	 *          The field label.
	 * @param value
	 *          The field value ({@code null} means {@code false}).
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static CheckBox checkbox(String boxLabel, String name, String fieldLabel, Boolean value, String... stylenames) {

		final CheckBox field = new CheckBox();

		// Should not process a 'null/blank' control to let the user set an empty boxLabel.
		field.setBoxLabel(boxLabel);

		if (ClientUtils.isNotBlank(name)) {
			field.setName(name);
		}

		if (ClientUtils.isNotBlank(fieldLabel)) {
			field.setFieldLabel(fieldLabel);
		}

		field.setValue(value);

		return addStyles(field, stylenames);

	}

	/**
	 * Builds a {@link CheckBoxGroup} with auto-generated name property and default horizontal orientation.
	 * 
	 * @param label
	 *          The field label.
	 * @param checkBoxes
	 *          The {@link CheckBox} fields that belong to the group.
	 * @return The field.
	 */
	public static CheckBoxGroup checkBoxGroup(String label, CheckBox... checkBoxes) {

		return checkBoxGroup(label, null, null, checkBoxes);

	}

	/**
	 * Builds a {@link CheckBoxGroup} with auto-generated name property.
	 * 
	 * @param label
	 *          The field label.
	 * @param orientation
	 *          The radios orientation.
	 * @param checkBoxes
	 *          The {@link CheckBox} fields that belong to the group.
	 * @return The field.
	 */
	public static CheckBoxGroup checkBoxGroup(String label, Orientation orientation, CheckBox... checkBoxes) {

		return checkBoxGroup(label, null, orientation, checkBoxes);

	}

	/**
	 * Builds a {@link CheckBoxGroup}.
	 * 
	 * @param label
	 *          The field label.
	 * @param name
	 *          The checkboxes group name property. If {@code null}, default auto-generated name is set.
	 * @param orientation
	 *          The checkboxes orientation. If {@code null}, default horizontal orientation is set.
	 * @param checkBoxes
	 *          The {@link CheckBox} fields that belong to the group.
	 * @return The field.
	 */
	public static CheckBoxGroup checkBoxGroup(String label, String name, Orientation orientation, CheckBox... checkBoxes) {

		final CheckBoxGroup field = new CheckBoxGroup();

		if (ClientUtils.isNotBlank(label)) {
			field.setFieldLabel(label);
		}

		field.setName(ClientUtils.isNotBlank(name) ? name : Math.random() + "_" + new Date().getTime());

		if (orientation != null) {
			field.setOrientation(orientation);
		}

		if (ClientUtils.isNotEmpty(checkBoxes)) {
			for (final CheckBox checkBox : checkBoxes) {
				if (checkBoxes == null) {
					continue;
				}
				field.add(checkBox);
			}
		}

		return field;

	}

	// --
	// -- FileUpload
	// --

	/**
	 * Builds a {@link FileUploadField}.
	 * 
	 * @param label
	 *          The field label.
	 * @return The field.
	 */
	public static FileUploadField upload(String label) {

		return upload(label, null);

	}

	/**
	 * Builds a {@link FileUploadField}.
	 * 
	 * @param label
	 *          The field label.
	 * @param name
	 *          The field name property.
	 * @return The field.
	 */
	public static FileUploadField upload(String label, String name) {

		final FileUploadField field = new FileUploadField();

		if (ClientUtils.isNotBlank(label)) {
			field.setFieldLabel(label);
		}

		if (ClientUtils.isNotBlank(name)) {
			field.setName(name);
		}

		return field;

	}

	// --
	// -- DateField
	// --

	/**
	 * Builds a {@link DateField}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static DateField date(String label, boolean mandatory, String... stylenames) {
		return date(label, mandatory, null, null, stylenames);
	}

	/**
	 * Builds a {@link DateField}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param minValue
	 *          The min value date.
	 * @param maxValue
	 *          The max value date.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static DateField date(String label, boolean mandatory, Date minValue, Date maxValue, String... stylenames) {

		final DateField field = new DateField();

		// Label.
		if (ClientUtils.isNotBlank(label)) {
			field.setFieldLabel(label);
		}

		// Constraints.
		field.setAllowBlank(!mandatory);

		if (minValue != null) {
			field.setMinValue(minValue);
		}

		if (maxValue != null) {
			field.setMaxValue(maxValue);
		}

		return addStyles(field, stylenames);

	}

	// --
	// -- TimeField
	// --

	/**
	 * Builds a {@link TimeField}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static TimeField time(String label, boolean mandatory, String... stylenames) {
		return time(label, mandatory, null, null, stylenames);
	}

	/**
	 * Builds a {@link TimeField}.
	 * 
	 * @param label
	 *          The field label.
	 * @param mandatory
	 *          <code>true</code> if the field doesn't allow blanks, <code>false</code> otherwise.
	 * @param minValue
	 *          The min value date.
	 * @param maxValue
	 *          The max value date.
	 * @param stylenames
	 *          (optional) Style name(s) added to the field. Blank values are ignored.
	 * @return The field.
	 */
	public static TimeField time(String label, boolean mandatory, Date minValue, Date maxValue, String... stylenames) {

		final TimeField field = new TimeField();

		// Label.
		if (ClientUtils.isNotBlank(label)) {
			field.setFieldLabel(label);
		}

		// Constraints.
		field.setAllowBlank(!mandatory);

		if (minValue != null) {
			field.setMinValue(minValue);
		}

		if (maxValue != null) {
			field.setMaxValue(maxValue);
		}

		// Necessary to avoid NPE in 'clear()' method.
		field.setStore(new ListStore<Time>());
		
		field.setTriggerAction(TriggerAction.ALL);

		return addStyles(field, stylenames);

	}

	// --
	// -- AdapterField
	// --
	
	/**
	 * Wraps the given {@code widget} with a ScollPanel and builds an 
	 * {@link AdapterField} for it.
	 * 
	 * @param label
	 *			The field label. If {@code blank}, label separator is automatically disabled.
	 * @param widget
	 *			The widget wrapped into the scroll panel and into the adapter field.
	 * @param width
	 *			Width of the given widget.
	 * @param height
	 *			Height of the scroll panel.
	 * @return The field.
	 */
	public static AdapterField adapterWithScrollbars(final String label, final IsWidget widget, final int width, final int height) {
		
		final ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setWidget(Widget.asWidgetOrNull(widget));
		scrollPanel.addAttachHandler(new AttachEvent.Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				scrollPanel.setWidth(width + "px");
			}
		});
		scrollPanel.setHeight(height + "px");
		
		return adapter(label, scrollPanel);
		
	}

	/**
	 * Builds an {@link AdapterField} for the given {@code widget}.
	 * 
	 * @param label
	 *          The field label. If {@code blank}, label separator is automatically disabled.
	 * @param widget
	 *          The widget wrapped into the adapter field.
	 * @return The field.
	 */
	public static AdapterField adapter(final String label, final IsWidget widget) {

		return adapter(label, widget, null);

	}

	/**
	 * Builds an {@link AdapterField} for the given {@code widget}.
	 * 
	 * @param label
	 *          The field label. If {@code blank}, label separator is automatically disabled.
	 * @param widget
	 *          The widget wrapped into the adapter field.
	 * @param resizeWidget
	 *          {@code true} to resize the widget.
	 * @return The field.
	 */
	public static AdapterField adapter(final String label, final IsWidget widget, final Boolean resizeWidget) {

		final AdapterField field = new AdapterField(Widget.asWidgetOrNull(widget));

		// Label.
		if (ClientUtils.isNotBlank(label)) {
			field.setFieldLabel(label);
		} else {
			field.setLabelSeparator("");
		}

		// Resize widget?
		if (resizeWidget != null) {
			field.setResizeWidget(resizeWidget.booleanValue());
		}

		return field;

	}
	
	// --
	// -- HiddenField
	// --

	/**
	 * Builds a {@link HiddenField}.
	 * 
	 * @param <D>
	 *          The hidden field value type.
	 * @param name
	 *          The field name property.
	 * @return The field.
	 */
	public static <D> HiddenField<D> hidden(final String name) {

		final HiddenField<D> field = new HiddenField<D>();

		// Name.
		if (ClientUtils.isNotBlank(name)) {
			field.setName(name);
		}

		return field;

	}

	// --
	// -- Button
	// --

	/**
	 * Creates a new button.
	 * 
	 * @return A new button.
	 */
	public static Button button() {
		return new Button();
	}

	/**
	 * Creates a new button with the given HTML.
	 * 
	 * @param html
	 *          the button text as HTML.
	 * 
	 * @return A new button with the given label.
	 */
	public static Button button(final String html) {
		return new Button(html);
	}

	/**
	 * Creates a new button with the given HTML and icon.
	 * 
	 * @param html
	 *          the button text as HTML.
	 * @param icon
	 *          the icon.
	 * 
	 * @return A new button with the given label and the given icon.
	 */
	public static Button button(final String html, final AbstractImagePrototype icon) {
		return new Button(html, icon);
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
	 * 
	 * @return A new button with the given listener.
	 */
	public static Button button(final String html, final AbstractImagePrototype icon, final SelectionListener<ButtonEvent> listener) {
		return new Button(html, icon, listener);
	}

	/**
	 * Creates a new button with the given HTML and specified selection listener.
	 * 
	 * @param html
	 *          the button's text as HTML.
	 * @param listener
	 *          the selection listener.
	 * 
	 * @return A new button with the given listener.
	 */
	public static Button button(final String html, final SelectionListener<ButtonEvent> listener) {
		return new Button(html, listener);
	}
	
	/**
	 * Creates a new button with the given HTML and icon.
	 * 
	 * @param html
	 *          the button text as HTML.
	 * @param icon
	 *          the icon.
	 * 
	 * @return A new button with the given label and the given icon.
	 */
	public static SplitButton splitButton(final String html, final AbstractImagePrototype icon) {
		return new SplitButton(html, icon);
	}
	
	/**
	 * Creates a new save split button and its menu composed of a save item and a discard changes item.
	 * 
	 * @return A new save split button.
	 */
	public static SplitButton saveSplitButton() {
		final SplitButton saveButton = Forms.splitButton(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		
		// Menu attached to the save button.
		final Menu menu = new Menu();
		menu.add(new MenuItem(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save()));
		menu.add(new MenuItem(I18N.CONSTANTS.discardChanges(), IconImageBundle.ICONS.cancel()));
		saveButton.setMenu(menu);
		
		return saveButton;
	}

	// --
	// -- Utility methods.
	// --

	/**
	 * Adds the given {@code stylenames} to the given {@code ui}.
	 * 
	 * @param ui
	 *          The UI object. Does nothing if {@code null}.
	 * @param stylenames
	 *          The style name(s). Does nothing if {@code null}.
	 * @return The given {@code ui} with added style(s).
	 */
	private static <U extends UIObject> U addStyles(final U ui, String... stylenames) {

		if (ui == null || ClientUtils.isEmpty(stylenames)) {
			return ui;
		}

		for (final String stylename : stylenames) {
			if (ClientUtils.isBlank(stylename)) {
				continue;
			}
			ui.addStyleName(stylename);
		}

		return ui;
	}

	/**
	 * Private constructor.
	 */
	private Forms() {
		// Factory pattern.
	}

}
