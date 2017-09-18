package org.sigmah.server.domain.element;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.ContactModelType;

@Entity
@Table(name = EntityConstants.CONTACT_LIST_ELEMENT_TABLE)
public class ContactListElement extends FlexibleElement {
  private static final long serialVersionUID = 2306257721319749960L;

  @Column(name = EntityConstants.CONTACT_LIST_ELEMENT_COLUMN_ALLOWED_TYPE)
  @Enumerated(EnumType.STRING)
  private ContactModelType allowedType;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = EntityConstants.CONTACT_LIST_ELEMENT_ALLOWED_MODEL_LINK_TABLE,
      joinColumns = @JoinColumn(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_ID, nullable = false),
      inverseJoinColumns = @JoinColumn(name = EntityConstants.CONTACT_MODEL_COLUMN_ID, nullable = false),
      uniqueConstraints = @UniqueConstraint(columnNames = {
          EntityConstants.FLEXIBLE_ELEMENT_COLUMN_ID,
          EntityConstants.CONTACT_MODEL_COLUMN_ID
      })
  )
  private List<ContactModel> allowedModels;

  @Column(name = EntityConstants.CONTACT_LIST_ELEMENT_COLUMN_LIMIT, nullable = false)
  private int limit;

  @Column(name = EntityConstants.CONTACT_LIST_ELEMENT_COLUMN_IS_MEMBER, nullable = false)
  private boolean member;

  // TODO: Add isMemberOfOrganization parameter

  public ContactModelType getAllowedType() {
    return allowedType;
  }

  public void setAllowedType(ContactModelType allowedType) {
    this.allowedType = allowedType;
  }

  public List<ContactModel> getAllowedModels() {
    return allowedModels;
  }

  public Set<Integer> getAllowedModelIds() {
	Set<Integer> ids = new HashSet<>();
	if (allowedModels != null) {
		for (ContactModel allowedModel : allowedModels) {
			ids.add(allowedModel.getId());
		}
	}
	return ids;
  }

  public void setAllowedModels(List<ContactModel> allowedModels) {
    this.allowedModels = allowedModels;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public boolean isMember() {
    return member;
  }

  public void setMember(boolean member) {
    this.member = member;
  }

  @Override
  public boolean isHistorable() {
    return true;
  }
}
