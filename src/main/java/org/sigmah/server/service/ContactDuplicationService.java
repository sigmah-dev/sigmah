package org.sigmah.server.service;
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

import com.google.gwt.thirdparty.guava.common.base.Strings;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.sigmah.server.dao.ValueDAO;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.element.DefaultContactFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.value.Value;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.result.ContactDuplicatedProperty;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;

public class ContactDuplicationService {
  private final ValueDAO valueDAO;
  private final ModelPropertyService modelPropertyService;

  @Inject
  public ContactDuplicationService(ValueDAO valueDAO, ModelPropertyService modelPropertyService) {
    this.valueDAO = valueDAO;
    this.modelPropertyService = modelPropertyService;
  }

  public List<ContactDuplicatedProperty> extractProperties(Contact newContact, Contact oldContact, Language language) {
    if (!Objects.equals(newContact.getContactModel().getId(), oldContact.getContactModel().getId())) {
      throw new IllegalStateException("Uncompatible contact models : " + newContact.getContactModel().getId() + " and " + oldContact.getContactModel().getId());
    }

    List<ContactDuplicatedProperty> properties = new ArrayList<>();
    for (LayoutGroup group : newContact.getContactModel().getDetails().getLayout().getGroups()) {

      if (group.getHasIterations()) {
        // iterative groups are not handled by the dedupe mechanism
        continue;
      }

      for (LayoutConstraint layoutConstraint : group.getConstraints()) {
        FlexibleElement element = layoutConstraint.getElement();

        String serializedNewValue;
        String serializedOldValue;
        String formattedNewValue;
        String formattedOldValue;
        ContactDuplicatedProperty.ValueType valueType = ContactDuplicatedProperty.ValueType.STRING;

        if (element instanceof DefaultContactFlexibleElement) {
          DefaultContactFlexibleElement defaultContactFlexibleElement = (DefaultContactFlexibleElement) element;
          serializedNewValue = defaultContactFlexibleElement.getSerializedValue(newContact);
          serializedOldValue = defaultContactFlexibleElement.getSerializedValue(oldContact);
          formattedNewValue = defaultContactFlexibleElement.getFormattedValue(newContact);
          formattedOldValue = defaultContactFlexibleElement.getFormattedValue(oldContact);
          if (((DefaultContactFlexibleElement) element).getType() == DefaultContactFlexibleElementType.PHOTO) {
            valueType = ContactDuplicatedProperty.ValueType.IMAGE;
          }
        } else {
          Value newValue = valueDAO.getValueByElementAndContainer(element.getId(), newContact.getId());
          Value oldValue = valueDAO.getValueByElementAndContainer(element.getId(), oldContact.getId());

          serializedNewValue = newValue.getValue();
          serializedOldValue = oldValue.getValue();
          formattedNewValue = modelPropertyService.getFormattedValue(element, newValue.getValue(), language);
          formattedOldValue = modelPropertyService.getFormattedValue(element, oldValue.getValue(), language);
        }

        if (Strings.isNullOrEmpty(serializedOldValue)) {
          serializedOldValue = null;
        }
        if (Strings.isNullOrEmpty(serializedNewValue)) {
          serializedNewValue = null;
        }

        if (Objects.equals(serializedNewValue, serializedOldValue)) {
          continue;
        }

        String propertyLabel = element.getLabel();
        if (propertyLabel == null && element instanceof DefaultContactFlexibleElement) {
          propertyLabel = modelPropertyService.getDefaultContactPropertyLabel(((DefaultContactFlexibleElement) element).getType(), language);
        }
        ContactDuplicatedProperty contactDuplicatedProperty = new ContactDuplicatedProperty();
        contactDuplicatedProperty.setPropertyLabel(propertyLabel);
        contactDuplicatedProperty.setSerializedNewValue(serializedNewValue);
        contactDuplicatedProperty.setSerializedOldValue(serializedOldValue);
        contactDuplicatedProperty.setFormattedNewValue(formattedNewValue);
        contactDuplicatedProperty.setFormattedOldValue(formattedOldValue);
        contactDuplicatedProperty.setFlexibleElementId(element.getId());
        contactDuplicatedProperty.setValueType(valueType);
        properties.add(contactDuplicatedProperty);
      }
    }
    return properties;
  }
}
