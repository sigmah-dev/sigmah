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
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Contact;
import org.sigmah.shared.dto.referential.ContactModelType;

public class ContactHibernateDAO extends AbstractDAO<Contact, Integer> implements ContactDAO {
  private static final float MIN_SIMILARITY_SCORE = 0.5f;

  @Override
  public List<Contact> findContactsByTypeAndContactModels(Integer organizationId, ContactModelType type, Set<Integer> contactModelIds,
                                                          boolean onlyWithoutUser, boolean withEmailNotNull, Set<Integer> orgUnitsIds) {
    // Too much nullable parameters, let's use criteria query builder to ease the query creation
    // and to avoid using dangerous string concatenation
    CriteriaBuilder criteriaBuilder = em().getCriteriaBuilder();
    CriteriaQuery<Contact> criteriaQuery = criteriaBuilder.createQuery(Contact.class);
    Root<Contact> contactRoot = criteriaQuery.from(Contact.class);
    Join<Object, Object> contactModelJoin = contactRoot.join("contactModel", JoinType.INNER);
    Join<Object, Object> organizationJoin = contactModelJoin.join("organization", JoinType.INNER);
    Join<Object, Object> userJoin = contactRoot.join("user", JoinType.LEFT);
    Join<Object, Object> mainOrgUnitJoin = contactRoot.join("mainOrgUnit", JoinType.LEFT);
    Join<Object, Object> secondaryOrgUnitJoin = contactRoot.join("secondaryOrgUnits", JoinType.LEFT);
    Join<Object, Object> userOrgUnitsJoin = userJoin.join("orgUnitsWithProfiles", JoinType.LEFT);
    Join<Object, Object> organizationOrgUnitsJoin = contactRoot.join("organization", JoinType.LEFT).join("orgUnit", JoinType.LEFT);

    List<Predicate> predicates = new ArrayList<>();
    predicates.add(criteriaBuilder.equal(organizationJoin.get("id"), organizationId));
    if (type != null) {
      predicates.add(criteriaBuilder.equal(contactModelJoin.get("type"), type));
    }
    if (contactModelIds != null && !contactModelIds.isEmpty()) {
      predicates.add(contactModelJoin.get("id").in(contactModelIds));
    }
    if (onlyWithoutUser) {
      predicates.add(userJoin.get("id").isNull());
    }
    if (withEmailNotNull) {
      predicates.add(criteriaBuilder.or(
          contactRoot.get("email").isNotNull(),
          userJoin.get("email").isNotNull()
      ));
    }

    if(orgUnitsIds != null && !orgUnitsIds.isEmpty()) {
      predicates.add(criteriaBuilder.or(
          criteriaBuilder.or(mainOrgUnitJoin.get("id").in(orgUnitsIds), secondaryOrgUnitJoin.get("id").in(orgUnitsIds)),
          criteriaBuilder.or(userOrgUnitsJoin.get("orgUnit").get("id").in(orgUnitsIds), organizationOrgUnitsJoin.get("id").in(orgUnitsIds))
      ));
    }

    criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
    criteriaQuery.select(contactRoot);
    criteriaQuery.orderBy(criteriaBuilder.asc(contactRoot.get("name")));

    return em().createQuery(criteriaQuery).getResultList();
  }

  @Override
  public List<Contact> findContactsByEmailOrSimilarName(Integer organizationId, Integer contactId, String email, String firstName, String name) {
    CriteriaBuilder criteriaBuilder = em().getCriteriaBuilder();
    CriteriaQuery<Contact> criteriaQuery = criteriaBuilder.createQuery(Contact.class);
    Root<Contact> contactRoot = criteriaQuery.from(Contact.class);
    Join<Object, Object> userJoin = contactRoot.join("user", JoinType.LEFT);
    Join<Object, Object> organizationJoin = contactRoot.join("organization", JoinType.LEFT);
    Join<Object, Object> contactModelOrganizationJoin = contactRoot.join("contactModel").join("organization");

    List<Predicate> andPredicates = new ArrayList<>();
    andPredicates.add(criteriaBuilder.equal(contactModelOrganizationJoin.get("id"), organizationId));

    if (contactId != null) {
      // Let's remove the current contact from the result
      andPredicates.add(criteriaBuilder.notEqual(contactRoot.get("id"), contactId));
    }

    List<Predicate> orPredicates = new ArrayList<>();
    if (email != null) {
      orPredicates.add(criteriaBuilder.equal(contactRoot.get("email"), email));
      orPredicates.add(criteriaBuilder.and(
          criteriaBuilder.isNull(contactRoot.get("email")),
          criteriaBuilder.equal(userJoin.get("email"), email)
      ));
    }
    orPredicates.add(criteriaBuilder.and(
        criteriaBuilder.isNotNull(contactRoot.get("name")),
        criteriaBuilder.isNotNull(contactRoot.get("firstname")),
        similarity(criteriaBuilder, contactRoot.get("name").as(String.class), name, contactRoot.get("firstname").as(String.class), firstName)
    ));
    orPredicates.add(criteriaBuilder.and(
        criteriaBuilder.isNull(contactRoot.get("name")),
        criteriaBuilder.isNull(contactRoot.get("firstname")),
        criteriaBuilder.or(
            criteriaBuilder.and(
                criteriaBuilder.isNotNull(userJoin.get("id")),
                similarity(criteriaBuilder, userJoin.get("name").as(String.class), name, userJoin.get("firstName").as(String.class), firstName)
            ),
            criteriaBuilder.and(
                criteriaBuilder.isNotNull(organizationJoin.get("id")),
                similarity(criteriaBuilder, organizationJoin.get("name").as(String.class), name, null, null)
            )
        )
    ));
    andPredicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()])));

    criteriaQuery.where(andPredicates.toArray(new Predicate[andPredicates.size()]));
    criteriaQuery.select(contactRoot);
    criteriaQuery.orderBy(criteriaBuilder.asc(contactRoot.get("name")));

    return em().createQuery(criteriaQuery).getResultList();
  }

  @Override
  public List<Contact> findByDirectMembership(Integer directMembershipId) {
    return em().createQuery("" +
        "SELECT c " +
        "FROM Contact c " +
        "WHERE c.parent.id = :directMembershipId ",
        Contact.class)
        .setParameter("directMembershipId", directMembershipId)
        .getResultList();
  }

  private Predicate similarity(CriteriaBuilder criteriaBuilder,
                               Expression<String> nameExpression, String nameValue,
                               Expression<String> firstNameExpression, String firstNameValue) {
    Expression<String> fullNameExpression;
    String fullnameValue;
    if (firstNameExpression != null && firstNameValue != null) {
      fullNameExpression = criteriaBuilder.concat(criteriaBuilder.concat(nameExpression, " "), firstNameExpression);
      fullnameValue = (nameValue + " " + firstNameValue).toLowerCase();
    } else {
      fullNameExpression = nameExpression;
      fullnameValue = nameValue.toLowerCase();
    }
    return criteriaBuilder.greaterThanOrEqualTo(
        criteriaBuilder.function("similarity", Float.class, criteriaBuilder.lower(fullNameExpression), criteriaBuilder.literal(fullnameValue)),
        MIN_SIMILARITY_SCORE
    );
  }

  @Override
  public Contact update(Contact contact) {
    return em().merge(contact);
  }
}
