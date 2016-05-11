package org.sigmah.server.dao.impl;
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

import java.util.Arrays;
import java.util.List;

import org.sigmah.server.dao.ContactModelDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

public class ContactModelHibernateDAO extends AbstractDAO<ContactModel, Integer> implements ContactModelDAO {
  @Override
  public List<ContactModel> findByOrganization(Integer organizationId) {
    return em()
        .createQuery(
            "SELECT cm " +
            "FROM ContactModel cm " +
            "WHERE cm.organization.id = :organizationId ",
            ContactModel.class
        )
        .setParameter("organizationId", organizationId)
        .getResultList();
  }

  @Override
  public List<ContactModel> findByOrganizationAndType(Integer organizationId, ContactModelType type, boolean onlyAvailable) {
    return em()
        .createQuery(
            "SELECT cm " +
            "FROM ContactModel cm " +
            "WHERE cm.organization.id = :organizationId " +
            "AND cm.type = :type " +
            "AND cm.status IN (:availableStatus) ",
            ContactModel.class
        )
        .setParameter("organizationId", organizationId)
        .setParameter("type", type)
        .setParameter("availableStatus", onlyAvailable ? Arrays.asList(ProjectModelStatus.READY, ProjectModelStatus.USED) : ProjectModelStatus.values())
        .getResultList();
  }
}
