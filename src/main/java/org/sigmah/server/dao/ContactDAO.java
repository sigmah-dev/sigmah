package org.sigmah.server.dao;
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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.shared.dto.referential.ContactModelType;

public interface ContactDAO extends DAO<Contact, Integer> {
  /**
   * Return contacts from a targeted organization filtered by type and models
   *
   * @param organizationId
   * @param type Filter the contacts by the type of the model, if provided.
   * @param contactModelIds Filter the contacts by the id of the model, if provided.
   */
  List<Contact> findContactsByTypeAndContactModels(Integer organizationId, ContactModelType type, Set<Integer> contactModelIds,
                                                   boolean onlyWithoutUsers, boolean withEmailNotNull, Set<Integer> orgUnitsIds,
                                                   Integer checkboxElementId);

  List<Contact> findContactsByEmailOrSimilarName(Integer organizationId, Integer contactId, String email, String firstName, String name);

  List<Contact> findContactsByNameOrEmail(Integer organizationId, String search, boolean withDeleted, boolean onlyWithoutUser, ContactModelType allowedType, Set<Integer> allowedModelIds, Set<Integer> excludedIds, Integer checkboxElementId);

  List<Contact> getContacts(final Collection<ContactModel> cmodels);

  List<Contact> findByDirectMembership(Integer directMembershipId);

  Contact update(Contact contact);
}
