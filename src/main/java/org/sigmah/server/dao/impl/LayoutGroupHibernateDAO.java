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

import javax.persistence.NonUniqueResultException;

import org.sigmah.server.dao.LayoutGroupDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;

public class LayoutGroupHibernateDAO extends AbstractDAO<LayoutGroup, Integer> implements LayoutGroupDAO {
  @Override
  public LayoutGroup getByElementId(Integer elementId) {
    try {
      return em().createQuery("" +
          "SELECT DISTINCT lg " +
          "FROM LayoutGroup lg " +
          "JOIN lg.constraints lc " +
          "WHERE lc.element.id = :elementId", LayoutGroup.class)
          .setParameter("elementId", elementId)
          .getSingleResult();
    } catch (NonUniqueResultException e) {
      return null;
    }
  }

  @Override
  public LayoutGroup getGroupOfDirectMembershipElementByContact(Integer contactId) {
    try {
      return (LayoutGroup) em().createNativeQuery("" +
          "SELECT lg.* " +
          "FROM contact c " +
          "JOIN contact_details cd ON (cd.id_contact_model = c.id_contact_model) " +
          "JOIN layout_group lg ON (lg.id_layout = cd.id_layout) " +
          "JOIN layout_constraint lc ON (lc.id_layout_group = lg.id_layout_group) " +
          "JOIN default_contact_flexible_element fe ON (fe.id_flexible_element = lc.id_flexible_element) " +
          "WHERE c.id_contact = :contactId " +
          "AND fe.type = 'DIRECT_MEMBERSHIP'", LayoutGroup.class)
          .setParameter("contactId", contactId)
          .getSingleResult();
    } catch (NonUniqueResultException e) {
      return null;
    }
  }
}
