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
}
