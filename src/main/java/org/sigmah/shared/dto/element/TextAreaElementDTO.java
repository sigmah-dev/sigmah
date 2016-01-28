package org.sigmah.shared.dto.element;

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
import org.sigmah.client.ui.widget.HistoryTokenText;
import org.sigmah.client.util.DateUtils;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DatePickerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * TextAreaElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class TextAreaElementDTO extends FlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;
    
    public static final String ENTITY_NAME = "element.TextAreaElement";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		// Gets the entity name mapped by the current DTO starting from the "server.domain" package name.
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("type", getType());
		builder.append("minValue", getMinValue());
		builder.append("maxValue", getMaxValue());
		builder.append("length", getLength());
		builder.append("isDecimal", getIsDecimal());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {

		final DateTimeFormat DATE_FORMAT = DateUtils.DATE_SHORT;

		final TextField<?> field;

		// Checks the type of the expected value to build the corrected
		// component.
		if (getType() != null) {

			switch (getType()) {
			// Number
				case 'N': {

					final NumberField numberField = new NumberField();
					final boolean isDecimal = Boolean.TRUE.equals(getIsDecimal());

					numberField.setAllowDecimals(isDecimal);
					numberField.setAllowNegative(true);
					preferredWidth = 120;

					// Decimal value
					if (isDecimal) {
						numberField.setFormat(NumberFormat.getDecimalFormat());

						// Sets the value to the field.
						if (valueResult != null && valueResult.isValueDefined()) {
							numberField.setValue(Double.parseDouble(valueResult.getValueObject()));
						}
					}
					// Non-decimal value
					else {
						numberField.setFormat(NumberFormat.getFormat("#"));

						// Sets the value to the field.
						if (valueResult != null && valueResult.isValueDefined()) {
							numberField.setValue(Long.parseLong(valueResult.getValueObject()));
						}
					}

					// Sets the min value.
					final Long minValue;
					if (getMinValue() != null) {
						minValue = getMinValue();
						numberField.setMinValue(minValue);
					} else {
						minValue = null;
					}

					// Sets the min value.
					final Long maxValue;
					if (getMaxValue() != null) {
						maxValue = getMaxValue();
						numberField.setMaxValue(maxValue);
					} else {
						maxValue = null;
					}

					// Sets tooltip.
					numberField.setToolTip(I18N.MESSAGES.flexibleElementTextAreaNumberRange(
						isDecimal ? I18N.CONSTANTS.flexibleElementDecimalValue() : I18N.CONSTANTS.flexibleElementNonDecimalValue(),
						minValue != null ? String.valueOf(minValue) : "-", maxValue != null ? String.valueOf(maxValue) : "-"));

					// Adds listeners.
					numberField.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {

							final Number number = numberField.getValue();
							final Double asDouble = number != null ? number.doubleValue() : null;

							// The number is invalid, fires only a required event to invalidate some previously valid number.
							if (asDouble == null) {

								// Required element ?
								if (getValidates()) {
									handlerManager.fireEvent(new RequiredValueEvent(false));
								}

								return;
							}

							// The number is saved as a double (decimal) or a long (integer).
							final String rawValue = isDecimal ? String.valueOf(asDouble) : String.valueOf(asDouble.longValue());
							// Checks the number interval.
							final boolean isValueOn = !((minValue != null && asDouble < minValue) || (maxValue != null && asDouble > maxValue));

							fireEvents(rawValue, isValueOn);
						}
					});

					field = numberField;
				}
					break;
				// Date
				case 'D': {

					// Creates a date field which manages date picker selections and
					// manual selections.
					final DateField dateField = new DateField();
					dateField.getPropertyEditor().setFormat(DATE_FORMAT);
					dateField.setEditable(true);
					dateField.setAllowBlank(true);
					preferredWidth = 120;

					// Sets the min date value.
					final Date minDate;
					if (getMinValue() != null) {
						minDate = new Date(getMinValue());
						dateField.setMinValue(minDate);
					} else {
						minDate = null;
					}

					// Sets the max date value.
					final Date maxDate;
					if (getMaxValue() != null) {
						maxDate = new Date(getMaxValue());
						dateField.setMaxValue(maxDate);
					} else {
						maxDate = null;
					}

					// Sets tooltip.
					dateField.setToolTip(I18N.MESSAGES.flexibleElementTextAreaDateRange(minDate != null ? DATE_FORMAT.format(minDate) : "-",
						maxDate != null ? DATE_FORMAT.format(maxDate) : "-"));

					// Adds the listeners.

					dateField.getDatePicker().addListener(Events.Select, new Listener<DatePickerEvent>() {

						@Override
						public void handleEvent(DatePickerEvent be) {

							// The date is saved as a timestamp.
							final String rawValue = String.valueOf(be.getDate().getTime());
							// The date picker always returns a valid date.
							final boolean isValueOn = true;

							fireEvents(rawValue, isValueOn);
						}
					});

					dateField.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {

							final Date date = dateField.getValue();

							// The date is invalid, fires only a required event to
							// invalidate some previously valid date.
							if (date == null || (minDate != null && date.before(minDate)) || (maxDate != null && date.after(maxDate))) {

								// Required element ?
								if (getValidates()) {
									handlerManager.fireEvent(new RequiredValueEvent(false));
								}

								return;
							}

							// The date is saved as a timestamp.
							final String rawValue = String.valueOf(date.getTime());
							// The date is valid here.
							final boolean isValueOn = true;

							fireEvents(rawValue, isValueOn);
						}
					});

					// Sets the value to the field.
					if (valueResult != null && valueResult.isValueDefined()) {
						dateField.setValue(new Date(Long.parseLong(valueResult.getValueObject())));
					}

					field = dateField;
				}
					break;
				// Text
				case 'T': {

					final TextField<String> textField = new TextField<String>();

					// Sets the max length.
					final Integer length = getLength();
					if (length != null) {
						textField.setMaxLength(length);
						textField.setToolTip(I18N.MESSAGES.flexibleElementTextAreaTextLength(String.valueOf(length)));
					}

					// Adds the listeners.
					textField.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {

							String rawValue = textField.getValue();

							if (rawValue == null) {
								rawValue = "";
							}

							// The value is valid if it contains at least one
							// non-blank character.
							final boolean isValueOn = !rawValue.trim().equals("") && !(length != null && rawValue.length() > length);

							fireEvents(rawValue, isValueOn);
						}
					});

					// Sets the value to the field.
					if (valueResult != null && valueResult.isValueDefined()) {
						textField.setValue(valueResult.getValueObject());
					}

					field = textField;
				}
					break;
				// Paragraph
				case 'P':
				default: {

					final TextArea textArea = new TextArea();
					textArea.addStyleName("flexibility-textarea");

					final Integer length = getLength();
					// Sets the max length.
					if (length != null) {
						textArea.setMaxLength(length);
						textArea.setToolTip(I18N.MESSAGES.flexibleElementTextAreaTextLength(String.valueOf(length)));
					}

					// Adds the listeners.
					textArea.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {

							String rawValue = textArea.getValue();

							if (rawValue == null) {
								rawValue = "";
							}

							// The value is valid if it contains at least one
							// non-blank character.
							final boolean isValueOn = !rawValue.trim().equals("") && !(length != null && rawValue.length() > length);

							fireEvents(rawValue, isValueOn);
						}
					});

					// Sets the value to the field.
					if (valueResult != null && valueResult.isValueDefined()) {
						textArea.setValue(valueResult.getValueObject());
					}

					field = textArea;
				}
					break;
			}
		}
		// No specific type specified.
		else {

			final TextArea textArea = new TextArea();

			final Integer length = getLength();
			// Sets the max length.
			if (length != null) {
				textArea.setMaxLength(length);
				textArea.setToolTip(I18N.MESSAGES.flexibleElementTextAreaTextLength(String.valueOf(length)));
			}

			// Adds the listeners.
			textArea.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent be) {

					String rawValue = textArea.getValue();

					if (rawValue == null) {
						rawValue = "";
					}

					// The value is valid if it contains at least one
					// non-blank character.
					final boolean isValueOn = !rawValue.trim().equals("") && !(length != null && rawValue.length() > length);

					fireEvents(rawValue, isValueOn);
				}
			});

			// Sets the value to the field.
			if (valueResult != null && valueResult.isValueDefined()) {
				textArea.setValue(valueResult.getValueObject());
			}

			field = textArea;
		}

		// Sets the global properties.
		field.setAllowBlank(true);
		field.setFieldLabel(getLabel());

		field.setEnabled(enabled);

		return field;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {

		if (result == null || !result.isValueDefined()) {
			return false;
		}

		try {

			final String value = (result.getValueObject());
			final boolean isValueOn;

			if (getType() != null) {

				switch (getType()) {
				// Number
					case 'N': {

						final boolean isDecimal = Boolean.TRUE.equals(getIsDecimal());

						// Gets the min value.
						final Long minValue;
						if (getMinValue() != null) {
							minValue = getMinValue();
						} else {
							minValue = null;
						}

						// Gets the min value.
						final Long maxValue;
						if (getMaxValue() != null) {
							maxValue = getMaxValue();
						} else {
							maxValue = null;
						}

						// Checks the number range.
						if (isDecimal) {
							final double d = Double.parseDouble(value);
							isValueOn = !((minValue != null && d < minValue) || (maxValue != null && d > maxValue));
						} else {
							final long l = Long.parseLong(value);
							isValueOn = !((minValue != null && l < minValue) || (maxValue != null && l > maxValue));
						}
					}
						break;
					// Date
					case 'D': {

						// Gets the min date value.
						final Date minDate;
						if (getMinValue() != null) {
							minDate = new Date(getMinValue());
						} else {
							minDate = null;
						}

						// Gets the max date value.
						final Date maxDate;
						if (getMaxValue() != null) {
							maxDate = new Date(getMaxValue());
						} else {
							maxDate = null;
						}

						final Date date = new Date(Long.parseLong(value));
						isValueOn = !((minDate != null && date.before(minDate)) || (maxDate != null && date.after(maxDate)));
					}
						break;
					// Text
					case 'T': {
						final Integer length = getLength();
						isValueOn = !"".equals(value.trim()) && !(length != null && value.length() > length);
					}
						break;
					// Paragraph
					case 'P':
					default: {
						final Integer length = getLength();
						isValueOn = !"".equals(value.trim()) && !(length != null && value.length() > length);
					}
						break;
				}
			} else {
				final Integer length = getLength();
				isValueOn = !"".equals(value.trim()) && !(length != null && value.length() > length);
			}

			return isValueOn;

		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * Method in charge of firing value events.
	 * 
	 * @param value
	 *          The raw value which is serialized to the server and saved to the data layer.
	 * @param isValueOn
	 *          If the value is correct.
	 */
	private void fireEvents(String value, boolean isValueOn) {

		handlerManager.fireEvent(new ValueEvent(TextAreaElementDTO.this, value));

		// Required element ?
		if (getValidates()) {
			handlerManager.fireEvent(new RequiredValueEvent(isValueOn));
		}
	}

	private String formatDate(String value) {
		if (value != null) {
			try {
				final DateTimeFormat formatter = DateUtils.DATE_SHORT;
				return formatter.format(new Date(Long.parseLong(value)));
			} catch(NumberFormatException e) {
				return "";
			}
		} else {
			return "";
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object renderHistoryToken(HistoryTokenListDTO token) {

		if (getType() != null && getType() == 'D') {
			return new HistoryTokenText(formatDate(token.getTokens().get(0).getValue()));
		} else {
			return super.renderHistoryToken(token);
		}
	}

	@Override
	public String toHTML(String value) {
		if(value == null || value.length() == 0) {
			return "";
		}
		
		if (getType() != null && getType() == 'D') {
			return formatDate(value);
		} else {
			return value.replace("\n", "<br>");
		}
	}

	// Expected value type
	public Character getType() {
		return get("type");
	}

	public void setType(Character type) {
		set("type", type);
	}

	// Expected min value
	public Long getMinValue() {
		return get("minValue");
	}

	public void setMinValue(Long minValue) {
		set("minValue", minValue);
	}

	// Expected max value
	public Long getMaxValue() {
		return get("maxValue");
	}

	public void setMaxValue(Long maxValue) {
		set("maxValue", maxValue);
	}

	// Expected decimal value ?
	public Boolean getIsDecimal() {
		return get("isDecimal");
	}

	public void setIsDecimal(Boolean isDecimal) {
		set("isDecimal", isDecimal);
	}

	// Expected value's length
	public Integer getLength() {
		return get("length");
	}

	public void setLength(Integer length) {
		set("length", length);
	}

}
