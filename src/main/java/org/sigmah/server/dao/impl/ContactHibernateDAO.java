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
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Contact;
import org.sigmah.shared.dto.referential.ContactModelType;

public class ContactHibernateDAO extends AbstractDAO<Contact, Integer> implements ContactDAO {
  @Override
  public List<Contact> findContactsByTypeAndContactModels(Integer organizationId, ContactModelType type, Set<Integer> contactModelIds) {
    // Too much nullable parameters, let's use criteria query builder to ease the query creation
    // and to avoid using dangerous string concatenation
    CriteriaBuilder criteriaBuilder = em().getCriteriaBuilder();
    CriteriaQuery<Contact> criteriaQuery = criteriaBuilder.createQuery(Contact.class);
    Root<Contact> contactRoot = criteriaQuery.from(Contact.class);
    Join<Object, Object> contactModelJoin = contactRoot.join("contactModel", JoinType.INNER);
    Join<Object, Object> organizationJoin = contactModelJoin.join("organization", JoinType.INNER);

    List<Predicate> predicates = new ArrayList<>();
    predicates.add(criteriaBuilder.equal(organizationJoin.get("id"), organizationId));
    if (type != null) {
      predicates.add(criteriaBuilder.equal(contactModelJoin.get("type"), type));
    }
    if (contactModelIds != null && !contactModelIds.isEmpty()) {
      predicates.add(contactModelJoin.get("id").in(contactModelIds));
    }
    criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
    criteriaQuery.select(contactRoot);
    criteriaQuery.orderBy(criteriaBuilder.asc(contactRoot.get("name")));

    return em().createQuery(criteriaQuery).getResultList();
  }

  @Override
  public Contact update(Contact contact) {
    return em().merge(contact);
  }
}
