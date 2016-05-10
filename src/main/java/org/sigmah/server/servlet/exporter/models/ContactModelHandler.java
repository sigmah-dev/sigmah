package org.sigmah.server.servlet.exporter.models;

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

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

import javax.persistence.EntityManager;

import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.service.LayoutGroupService;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Exports and imports contact models.
 */
public class ContactModelHandler implements ModelHandler {
  private final LayoutGroupService layoutGroupService;

  @Inject
  public ContactModelHandler(LayoutGroupService layoutGroupService) {
    this.layoutGroupService = layoutGroupService;
  }

  @Override
  public void importModel(InputStream inputStream, EntityManager em, User user) throws Exception {
    ObjectInputStream objectInputStream;
    em.getTransaction().begin();
    try {
      objectInputStream = new ObjectInputStream(inputStream);
      ContactModel contactModel = (ContactModel) objectInputStream.readObject();
      contactModel.resetImport(true);
      saveContactFlexibleElement(contactModel, em);

      // Set the status to DRAFT
      contactModel.setStatus(ProjectModelStatus.DRAFT);
      contactModel.setOrganization(user.getOrganization());
      em.persist(contactModel);
      em.getTransaction().commit();
    } catch (Exception e) {
      throw e;
    }
  }

  @Override
  public String exportModel(OutputStream outputStream, String identifier, EntityManager em) throws Exception {
    // TODO
    return null;
  }

  /**
   * Save the flexible elements of imported contact model
   *
   * @param contactModel the imported contact model
   * @param em           the entity manager
   */
  private void saveContactFlexibleElement(ContactModel contactModel, EntityManager em) {
    // ContactModel --> Banner --> Layout --> Groups --> Constraints
    if (contactModel.getCard() != null && contactModel.getCard().getLayout() != null) {
      layoutGroupService.saveLayoutGroups(contactModel.getCard().getLayout().getGroups());
    }
    // ContactModel --> Detail --> Layout --> Groups --> Constraints
    if (contactModel.getDetails() != null && contactModel.getDetails().getLayout() != null) {
      layoutGroupService.saveLayoutGroups(contactModel.getDetails().getLayout().getGroups());
    }
  }
}
