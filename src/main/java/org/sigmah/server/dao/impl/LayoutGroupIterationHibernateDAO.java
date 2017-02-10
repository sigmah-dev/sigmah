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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dao.LayoutGroupIterationDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.IterationHistoryToken;
import org.sigmah.server.domain.layout.LayoutGroupIteration;

/**
 * {@link LayoutGroupIterationDAO} implementation.
 */
public class LayoutGroupIterationHibernateDAO extends AbstractDAO<LayoutGroupIteration, Integer> implements LayoutGroupIterationDAO {
  public List<LayoutGroupIteration> findByLayoutGroupAndContainer(Integer layoutGroupId, Integer containerId, Integer amendmentId) {

    if(amendmentId == -1) {
      // Builds query.
      final TypedQuery<LayoutGroupIteration> query = em().createQuery("SELECT i FROM LayoutGroupIteration i WHERE i.layoutGroup.id = :layoutGroupId AND i.containerId = :containerId ORDER BY i.id ASC", LayoutGroupIteration.class);
      query.setParameter("layoutGroupId", layoutGroupId);
      query.setParameter("containerId", containerId);

      // Retrieves query results and map results.
      return query.getResultList();
    }

    // Builds query.
    final TypedQuery<IterationHistoryToken> query = em().createQuery("SELECT i FROM IterationHistoryToken i WHERE i.layoutGroup.id = :layoutGroupId AND i.projectId = :containerId AND i.coreVersion.id = :amendmentId ORDER BY i.id ASC", IterationHistoryToken.class);
    query.setParameter("layoutGroupId", layoutGroupId);
    query.setParameter("containerId", containerId);
    query.setParameter("amendmentId", amendmentId);

    // Retrieves query results and map results.
    List<LayoutGroupIteration> iterations = new ArrayList<LayoutGroupIteration>();
    for(IterationHistoryToken i : query.getResultList()) {
      LayoutGroupIteration iteration = new LayoutGroupIteration();
      iteration.setContainerId(containerId);
      iteration.setLayoutGroup(i.getLayoutGroup());
      iteration.setName(i.getName());
      iteration.setId(i.getLayoutGroupIterationId());

      iterations.add(iteration);
    }

    return iterations;
  }
}
