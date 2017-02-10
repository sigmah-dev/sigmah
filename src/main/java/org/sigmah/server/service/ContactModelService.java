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

import com.google.inject.Inject;

import java.util.Arrays;
import java.util.Date;

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dao.ContactModelDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.ContactCard;
import org.sigmah.server.domain.ContactDetails;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.DefaultContactFlexibleElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.ModelUtil;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

public class ContactModelService extends AbstractEntityService<ContactModel, Integer, ContactModelDTO> {
  @Inject
  private ContactModelDAO contactModelDAO;
  @Inject
  private Mapper mapper;
  @Inject
  private ModelUtil modelUtil;

  @Override
  public ContactModel create(PropertyMap properties, UserDispatch.UserExecutionContext context) throws CommandException {

    final ContactModelDTO contactModelDTO = (ContactModelDTO) properties.get(AdminUtil.ADMIN_CONTACT_MODEL);

    // Only draft models can be changed.
    if (contactModelDTO == null) {
      return null;
    }

    if (contactModelDTO.getId() != null) {
      // Properties can only contain actual changes between old version and new one as verification has already been
      // done.
      return update(contactModelDTO.getId(), properties, context);
    }

    // Create new draft ContactModel
    ContactModel contactModel = createContactModel(null, properties, context.getUser());

    // Contact model details
    ContactDetails contactDetails = new ContactDetails();
    Layout detailsLayout = new Layout();
    detailsLayout.setColumnsCount(1);
    detailsLayout.setRowsCount(1);
    contactDetails.setLayout(detailsLayout);
    contactDetails.setContactModel(contactModel);

    ContactCard contactCard = new ContactCard();
    Layout cardLayout = new Layout();
    cardLayout.setColumnsCount(2);
    cardLayout.setRowsCount(1);
    contactCard.setLayout(cardLayout);
    contactCard.setContactModel(contactModel);

    LayoutGroup detailsGroup = new LayoutGroup();
    detailsGroup.setTitle("Default details group");
    detailsGroup.setColumn(0);
    detailsGroup.setRow(0);
    detailsGroup.setParentLayout(detailsLayout);

    LayoutGroup avatarGroup = new LayoutGroup();
    avatarGroup.setTitle("Avatar group");
    avatarGroup.setColumn(1);
    avatarGroup.setRow(0);
    avatarGroup.setParentLayout(cardLayout);
    LayoutGroup cardGroup = new LayoutGroup();
    cardGroup.setTitle("Default card group");
    cardGroup.setColumn(0);
    cardGroup.setRow(1);
    cardGroup.setParentLayout(cardLayout);

    // Default flexible elements all in default details group
    int order = 0;
    for (DefaultContactFlexibleElementType e : DefaultContactFlexibleElementType.values()) {
      DefaultContactFlexibleElement defaultElement = new DefaultContactFlexibleElement();
      defaultElement.setType(e);
      defaultElement.setValidates(false);
      defaultElement.setAmendable(true);
      defaultElement.setExportable(true);
      em().persist(defaultElement);

      // Details
      LayoutConstraint detailLayoutConstraint = new LayoutConstraint();
      detailLayoutConstraint.setParentLayoutGroup(detailsGroup);
      detailLayoutConstraint.setElement(defaultElement);
      detailLayoutConstraint.setSortOrder(order);
      detailsGroup.addConstraint(detailLayoutConstraint);

      // Card
      LayoutConstraint cardLayoutConstraint = new LayoutConstraint();
      cardLayoutConstraint.setElement(defaultElement);
      if (e == DefaultContactFlexibleElementType.PHOTO) {
        cardLayoutConstraint.setSortOrder(0);
        cardLayoutConstraint.setParentLayoutGroup(avatarGroup);
        avatarGroup.addConstraint(cardLayoutConstraint);
      } else if (e.getDefaultCardOrder() != null) {
        cardLayoutConstraint.setSortOrder(e.getDefaultCardOrder());
        cardLayoutConstraint.setParentLayoutGroup(cardGroup);
        cardGroup.addConstraint(cardLayoutConstraint);
      }
      order++;
    }

    detailsLayout.setGroups(Arrays.asList(detailsGroup));
    cardLayout.setGroups(Arrays.asList(avatarGroup, cardGroup));

    contactModel.setDetails(contactDetails);
    contactModel.setCard(contactCard);

    return contactModelDAO.persist(contactModel, context.getUser());
  }

  @Override
  public ContactModel update(Integer entityId, PropertyMap changes, UserDispatch.UserExecutionContext context) throws CommandException {
    ContactModel contactModel = em().find(ContactModel.class, entityId);

    if (contactModel == null) {
      throw new IllegalArgumentException("No ContactModel found for id #" + entityId);
    }

    if (changes.get(AdminUtil.PROP_CM_NAME) != null) {
      contactModel = createContactModel(contactModel, changes, context.getUser());
      return em().merge(contactModel);
    }

    if (changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT) != null) {
      modelUtil.persistFlexibleElement(changes, contactModel);
      return em().find(ContactModel.class, contactModel.getId());
    }

    em().flush();
    return null;
  }

  private static ContactModel createContactModel(ContactModel oldContactModel, PropertyMap properties, User user) {
    ContactModel updatedContactModel = oldContactModel;
    if (updatedContactModel == null) {
      updatedContactModel = new ContactModel();
      updatedContactModel.setStatus(ProjectModelStatus.DRAFT);
      updatedContactModel.setOrganization(user.getOrganization());
    } else {
      if (properties.get(AdminUtil.PROP_CM_STATUS) != null) {
        updatedContactModel.setStatus((ProjectModelStatus) properties.get(AdminUtil.PROP_CM_STATUS));
      }
      if (properties.containsKey(AdminUtil.PROP_CM_MAINTENANCE_DATE)) {
        Object maintenanceDate = properties.get(AdminUtil.PROP_CM_MAINTENANCE_DATE);
        if (maintenanceDate instanceof Date) {
          updatedContactModel.setDateMaintenance((Date) maintenanceDate);
        } else {
          updatedContactModel.setDateMaintenance(null);
        }
      }
    }

    if (properties.get(AdminUtil.PROP_CM_NAME) != null) {
      updatedContactModel.setName((String) properties.get(AdminUtil.PROP_CM_NAME));
    }
    if (properties.get(AdminUtil.PROP_CM_TYPE) != null) {
      updatedContactModel.setType((ContactModelType) properties.get(AdminUtil.PROP_CM_TYPE));
    }

    return updatedContactModel;
  }
}
