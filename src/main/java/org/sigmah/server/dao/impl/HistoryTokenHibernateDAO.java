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

import java.util.List;

import org.sigmah.server.dao.HistoryTokenDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.value.Value;

public class HistoryTokenHibernateDAO extends AbstractDAO<HistoryToken, Integer> implements HistoryTokenDAO {
  @Override
  public List<HistoryToken> findByContainerIdAndFlexibleElementId(Integer containerId, Integer flexibleElementId) {
    return em().createQuery("" +
        "SELECT h " +
        "FROM HistoryToken h " +
        "WHERE h.projectId = :containerId " +
        "AND h.elementId = :elementId " +
        "ORDER BY h.date ASC", HistoryToken.class)
        .setParameter("containerId", containerId)
        .setParameter("elementId", flexibleElementId)
        .getResultList();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<HistoryToken> findByIdInSerializedValue(Integer id) {
    return em().createNativeQuery("" +
        "SELECT ht.* " +
        "FROM history_token ht " +
        "WHERE ht.value = :id " +
        "OR ht.value ~ ('^(.*~)?'||:id||'(~.*)?$') " +
        "ORDER BY ht.id_element ", HistoryToken.class)
        .setParameter("id", String.valueOf(id))
        .getResultList();
  }
}
