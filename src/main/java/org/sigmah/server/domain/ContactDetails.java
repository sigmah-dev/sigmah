package org.sigmah.server.domain;

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

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.util.EntityConstants;

@Entity
@Table(name = EntityConstants.CONTACT_DETAILS_TABLE)
public class ContactDetails extends AbstractEntityId<Integer> {
  private static final long serialVersionUID = -2655562802757703274L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = EntityConstants.CONTACT_DETAILS_COLUMN_ID)
  private Integer id;

  // --------------------------------------------------------------------------------
  //
  // FOREIGN KEYS.
  //
  // --------------------------------------------------------------------------------

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstants.CONTACT_MODEL_COLUMN_ID)
  private ContactModel contactModel;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstants.LAYOUT_COLUMN_ID, nullable = false)
  @NotNull
  private Layout layout;

  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public void setId(Integer id) {
    this.id = id;
  }

  public ContactModel getContactModel() {
    return contactModel;
  }

  public void setContactModel(ContactModel contactModel) {
    this.contactModel = contactModel;
  }

  public Layout getLayout() {
    return layout;
  }

  public void setLayout(Layout layout) {
    this.layout = layout;
  }

  public void resetImport(final ContactModel contactModel, boolean keepPrivacyGroups) {
    this.id = null;
    this.contactModel = contactModel;
    if (this.layout != null) {
      this.layout.resetImport(keepPrivacyGroups);
    }
  }
}
