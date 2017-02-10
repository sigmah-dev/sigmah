package org.sigmah.shared.dto;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;
import org.sigmah.shared.dto.element.DefaultContactFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

public class ContactModelDTO extends AbstractModelDataEntityDTO<Integer> implements IsModel {
  private static final long serialVersionUID = -250800101882782573L;

  public static final String ENTITY_NAME = "ContactModel";

  public static final String ID = "id";
  public static final String NAME = "name";
  public static final String STATUS = "status";
  public static final String TYPE = "contactModelType";
  public static final String MAINTENANCE_DATE = "dateMaintenance";
  public static final String UNDER_MAINTENANCE = "underMaintenance";

  public static final String CARD = "card";
  public static final String DETAILS = "details";

  public enum Mode implements IsMappingMode {
    WITHOUT_LAYOUTS(new MappingField("card"), new MappingField("details"));

    private final CustomMappingField[] customFields;
    private final MappingField[] excludedFields;

    Mode(final MappingField... excludedFields) {
      this(null, excludedFields);
    }

    Mode(final CustomMappingField... customFields) {
      this(customFields, (MappingField[]) null);
    }

    Mode(final CustomMappingField[] customFields, final MappingField... excludedFields) {
      this.customFields = customFields;
      this.excludedFields = excludedFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMapId() {
      return name();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomMappingField[] getCustomFields() {
      return customFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingField[] getExcludedFields() {
      return excludedFields;
    }
  }


  public static class LocalizedElement {

    private final FlexibleElementDTO element;

    public LocalizedElement(FlexibleElementDTO element) {
      this.element = element;
    }

    /**
     * Gets the flexible element.
     *
     * @return The flexible element.
     */
    public FlexibleElementDTO getElement() {
      return element;
    }
  }

  /**
   * Localized flexible elements.
   */
  private transient HashMap<Class<? extends FlexibleElementDTO>, List<ContactModelDTO.LocalizedElement>> localizedElements;

  /**
   * {@inheritDoc}
   */
  @Override
  public String getEntityName() {
    return ENTITY_NAME;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ModelType getModelType() {
    return ModelType.ContactModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<AbstractModelDataEntityDTO<?>> getHasLayoutElements() {
    final List<AbstractModelDataEntityDTO<?>> hasLayoutElements = new ArrayList<AbstractModelDataEntityDTO<?>>();
    hasLayoutElements.add(getDetails());
    return hasLayoutElements;
  }

  public Integer getId() {
    return get(ID);
  }

  public void setId(String id) {
    set(ID, id);
  }

  @Override
  public String getName() {
    return get(NAME);
  }

  public void setName(String name) {
    set(NAME, name);
  }

  public ContactModelType getType() {
    return get(TYPE);
  }

  public void setType(ContactModelType contactModelType) {
    set(TYPE, contactModelType);
  }

  // Card
  public ContactCardDTO getCard() {
    return get(CARD);
  }

  public void setCard(ContactCardDTO card) {
    set(CARD, card);
  }

  // Details
  public ContactDetailsDTO getDetails() {
    return get(DETAILS);
  }

  public void setDetails(ContactDetailsDTO details) {
    set(DETAILS, details);
  }

  @Override
  public ProjectModelStatus getStatus() {
    return (ProjectModelStatus) get(STATUS);
  }

  public void setStatus(ProjectModelStatus status) {
    set(STATUS, status);
  }

  // Maintenance.
  @Override
  public boolean isUnderMaintenance() {
    final Boolean underMaintenance = get(UNDER_MAINTENANCE);
    return underMaintenance != null && underMaintenance;
  }

  public void setUnderMaintenance(boolean underMaintenance) {
    set(UNDER_MAINTENANCE, underMaintenance);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isEditable() {
    return getStatus().isEditable() || isUnderMaintenance();
  }

  // Maintenance start date.
  @Override
  public Date getDateMaintenance() {
    return get(MAINTENANCE_DATE);
  }

  public void setDateMaintenance(Date date) {
    set(MAINTENANCE_DATE, date);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<FlexibleElementDTO> getAllElements() {
    List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();
    List<FlexibleElementDTO> cardElements = new ArrayList<FlexibleElementDTO>();

    // Card
    if (this.getCard().getLayout() != null) {
      for (LayoutGroupDTO layoutGroupDTO : getCard().getLayout().getGroups()) {
        for (LayoutConstraintDTO layoutConstraintDTO : layoutGroupDTO.getConstraints()) {
          FlexibleElementDTO flexibleElementDTO = layoutConstraintDTO.getFlexibleElementDTO();
          // If the current element is a default contact flexible element not compatible with the current contact model type, do not add it
          if (isElementIncompatibleWithCurrentType(flexibleElementDTO)) {
            continue;
          }
          flexibleElementDTO.setBannerConstraint(layoutConstraintDTO);
          cardElements.add(flexibleElementDTO);
        }
      }
    }

    // Details
    ContactDetailsDTO contactDetailsDTO = getDetails();
    contactDetailsDTO.setName();
    setDetails(contactDetailsDTO);
    if (getDetails().getLayout() != null) {
      for (LayoutGroupDTO layoutGroupDTO : getDetails().getLayout().getGroups()) {
        for (LayoutConstraintDTO layoutConstraintDTO : layoutGroupDTO.getConstraints()) {
          FlexibleElementDTO flexibleElementDTO = layoutConstraintDTO.getFlexibleElementDTO();
          if (isElementIncompatibleWithCurrentType(flexibleElementDTO)) {
            continue;
          }
          flexibleElementDTO.setGroup(layoutGroupDTO);
          flexibleElementDTO.setConstraint(layoutConstraintDTO);
          flexibleElementDTO.setContainerModel(getDetails());
          for (FlexibleElementDTO cardElement : cardElements) {
            if (flexibleElementDTO.getId().equals(cardElement.getId())) {
              flexibleElementDTO.setBannerConstraint(cardElement.getBannerConstraint());
            }
          }
          allElements.add(flexibleElementDTO);
        }
      }
    }

    return allElements;
  }

  /**
   * Gets all the flexible elements instances of the given class in this model (details page). The card is ignored
   * cause the elements in it are read-only.
   *
   * @param clazz
   *          The class of the searched flexible elements.
   * @return The elements localized for the given class, or <code>null</code> if there is no element of this class.
   */
  public List<ContactModelDTO.LocalizedElement> getLocalizedElements(Class<? extends FlexibleElementDTO> clazz) {

    if (localizedElements == null) {

      localizedElements = new HashMap<Class<? extends FlexibleElementDTO>, List<ContactModelDTO.LocalizedElement>>();

      // Details
      for (final LayoutGroupDTO group : getDetails().getLayout().getGroups()) {

        // For each constraint
        for (final LayoutConstraintDTO constraint : group.getConstraints()) {

          // Gets the element and its class
          final FlexibleElementDTO flexibleElementDTO = constraint.getFlexibleElementDTO();
          if (isElementIncompatibleWithCurrentType(flexibleElementDTO)) {
            continue;
          }
          List<ContactModelDTO.LocalizedElement> elements = localizedElements.get(flexibleElementDTO.getClass());

          // First element for this class
          if (elements == null) {
            elements = new ArrayList<ContactModelDTO.LocalizedElement>();
            localizedElements.put(flexibleElementDTO.getClass(), elements);
          }

          // Maps the element.
          elements.add(new ContactModelDTO.LocalizedElement(flexibleElementDTO));
        }
      }
    }
    return localizedElements.get(clazz);
  }

  private boolean isElementIncompatibleWithCurrentType(FlexibleElementDTO flexibleElementDTO) {
    return flexibleElementDTO instanceof DefaultContactFlexibleElementDTO &&
        !((DefaultContactFlexibleElementDTO) flexibleElementDTO).getType().isVisibleForType(getType());
  }

  /**
   * Returns the current contact model corresponding global export elements.<br>
   * Only the following types of elements are returned:
   * <ul>
   * <li>{@link ElementTypeEnum#DEFAULT_CONTACT}</li>
   * <li>{@link ElementTypeEnum#CHECKBOX}</li>
   * <li>{@link ElementTypeEnum#TEXT_AREA}</li>
   * <li>{@link ElementTypeEnum#TRIPLETS}</li>
   * <li>{@link ElementTypeEnum#QUESTION}</li>
   * </ul>
   *
   * @return The current contact model corresponding global export elements.
   */
  public List<FlexibleElementDTO> getGlobalExportElements() {

    final List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();

    // add details groups
    final ContactDetailsDTO p = getDetails();
    p.setName();
    setDetails(p);
    if (getDetails().getLayout() != null) {
      for (final LayoutGroupDTO lg : getDetails().getLayout().getGroups()) {
        for (final LayoutConstraintDTO lc : lg.getConstraints()) {
          final FlexibleElementDTO element = lc.getFlexibleElementDTO();
          element.setGroup(lg);
          element.setConstraint(lc);
          element.setContainerModel(getDetails());

          final ElementTypeEnum type = element.getElementType();
          if (ElementTypeEnum.DEFAULT_CONTACT == type
              || ElementTypeEnum.CHECKBOX == type
              || ElementTypeEnum.TEXT_AREA == type
              || ElementTypeEnum.TRIPLETS == type
              || ElementTypeEnum.QUESTION == type
              || ElementTypeEnum.CONTACT_LIST == type) {
            allElements.add(element);
          }
        }
      }
    }

    return allElements;
  }
}
