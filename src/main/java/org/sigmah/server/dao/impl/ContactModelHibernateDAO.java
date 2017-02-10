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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.sigmah.server.dao.ContactModelDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

public class ContactModelHibernateDAO extends AbstractDAO<ContactModel, Integer> implements ContactModelDAO {
  @Override
  public List<ContactModel> findByOrganizationAndTypeAndIds(Integer organizationId, ContactModelType type,
                                                            Set<Integer> contactModelIds, boolean onlyAvailable) {
    // Too much nullable parameters, let's use criteria query builder to ease the query creation
    // and to avoid using dangerous string concatenation
    CriteriaBuilder criteriaBuilder = em().getCriteriaBuilder();
    CriteriaQuery<ContactModel> criteriaQuery = criteriaBuilder.createQuery(ContactModel.class);
    Root<ContactModel> contactModelRoot = criteriaQuery.from(ContactModel.class);
    Join<Object, Object> organizationJoin = contactModelRoot.join("organization", JoinType.INNER);

    List<Predicate> predicates = new ArrayList<>();
    predicates.add(criteriaBuilder.equal(organizationJoin.get("id"), organizationId));
    if (type != null) {
      predicates.add(criteriaBuilder.equal(contactModelRoot.get("type"), type));
    }
    if (contactModelIds != null && !contactModelIds.isEmpty()) {
      predicates.add(contactModelRoot.get("id").in(contactModelIds));
    }
    if (onlyAvailable) {
      predicates.add(contactModelRoot.get("status").in(Arrays.asList(ProjectModelStatus.READY, ProjectModelStatus.USED)));
    }
    criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
    criteriaQuery.select(contactModelRoot);
    criteriaQuery.orderBy(criteriaBuilder.asc(contactModelRoot.get("name")));

    return em().createQuery(criteriaQuery).getResultList();
  }
}
