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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;

import java.util.EnumMap;
import java.util.Map;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.referential.ContactModelType;

public class ContactModelTypeField extends MultiField<ContactModelType> {

  private final EnumMap<ContactModelType, Radio> radios;
  private final RadioGroup radioGroup;

  /**
   * Initializes the field with the given arguments and default {@link Style.Orientation#HORIZONTAL}.
   *
   * @param fieldLabel
   *          The field label.
   * @param mandatory
   *          Is the field mandatory?
   * @param contactModelTypes
   *          The specific {@link ContactModelType} to show. If {@code null} or empty, all types are shown.
   */
  public ContactModelTypeField(final String fieldLabel, final boolean mandatory, final ContactModelType... contactModelTypes) {
    this(fieldLabel, mandatory, null, contactModelTypes);
  }

  /**
   * Initializes the field with the given arguments.
   *
   * @param fieldLabel
   *          The field label.
   * @param mandatory
   *          Is the field mandatory?
   * @param orientation
   *          The orientation. If {@code null}, default {@link Style.Orientation#HORIZONTAL} is set.
   * @param contactModelTypes
   *          The specific {@link ContactModelType} to show. If {@code null} or empty, all types are shown.
   */
  public ContactModelTypeField(final String fieldLabel, final boolean mandatory, final Style.Orientation orientation, ContactModelType... contactModelTypes) {

    this.radios = new EnumMap<ContactModelType, Radio>(ContactModelType.class);
    this.radioGroup = Forms.radioGroup(null, "contact-model-types", orientation != null ? orientation : Style.Orientation.HORIZONTAL);

    radioGroup.setSelectionRequired(mandatory);
    radioGroup.setFireChangeEventOnSetValue(true);

    if (ClientUtils.isEmpty(contactModelTypes)) {
      contactModelTypes = ContactModelType.values();
    }

    for (final ContactModelType projectModelType : contactModelTypes) {

      final Radio radio = Forms.radio(null, null, null, Boolean.FALSE, "project-model-type-radio");
      radio.setFireChangeEventOnSetValue(true);
      radio.setBoxLabel(ContactModelType.getName(projectModelType));

      radioGroup.add(radio);
      radios.put(projectModelType, radio);
    }

    add(radioGroup);
    setFieldLabel(fieldLabel);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final EventType eventType, final Listener<? extends BaseEvent> listener) {
    radioGroup.addListener(eventType, listener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ContactModelType getValue() {

    final Radio selectedRadio = radioGroup.getValue();

    if (selectedRadio == null) {
      return null;
    }

    for (final Map.Entry<ContactModelType, Radio> entry : radios.entrySet()) {
      if (selectedRadio.equals(entry.getValue())) {
        return entry.getKey();
      }
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValue(final ContactModelType value) {

    if (value == null) {
      radioGroup.setValue(null);
      return;
    }

    radioGroup.setValue(radios.get(value));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid() {
    return radioGroup.isValid();
  }

}
