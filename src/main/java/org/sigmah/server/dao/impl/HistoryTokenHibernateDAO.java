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

import javax.persistence.TypedQuery;

import org.sigmah.server.dao.HistoryTokenDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.util.EntityConstants;

public class HistoryTokenHibernateDAO extends AbstractDAO<HistoryToken, Integer> implements HistoryTokenDAO {

  @Override
  public List<HistoryToken> findByContainerIdAndFlexibleElementId(Integer containerId, List<Integer> flexibleElementIds, boolean lastOnly) {
    TypedQuery<HistoryToken> q = em().createQuery("" +
        "SELECT h " +
        "FROM HistoryToken h " +
        "WHERE h.projectId = :containerId " +
        "AND h.elementId in :elementIds " +
        "ORDER BY h.date DESC", HistoryToken.class)
        .setParameter("containerId", containerId)
        .setParameter("elementIds", flexibleElementIds);
    if (lastOnly) {
      q = q.setMaxResults(1);
    }
    return q.getResultList();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<HistoryToken> findByIdInSerializedValueAndElementType(Integer id, String elementTypeTableName, boolean lastOnly) {
    return em().createNativeQuery("" +
        "SELECT ht.* " +
        "FROM " + EntityConstants.HISTORY_TOKEN_TABLE + " ht " +
        "WHERE (ht." + EntityConstants.HISTORY_TOKEN_COLUMN_VALUE + " = :id " +
        "OR ht." + EntityConstants.HISTORY_TOKEN_COLUMN_VALUE + " ~ ('^(.*~)?'||:id||'(~.*)?$') ) " +
        (elementTypeTableName == null ? "" : "AND EXISTS (SELECT * FROM " + elementTypeTableName + ") ") +
        "ORDER BY ht." + EntityConstants.HISTORY_TOKEN_COLUMN_DATE + " DESC" +
        (lastOnly ? " LIMIT 1" : ""), HistoryToken.class)
        .setParameter("id", String.valueOf(id))
        .getResultList();
  }
}
