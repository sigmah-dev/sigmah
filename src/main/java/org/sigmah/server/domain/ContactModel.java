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

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;

@Entity
@Table(name = EntityConstants.CONTACT_MODEL_TABLE)
@FilterDefs(@FilterDef(name = EntityFilters.HIDE_DELETED))
@Filters(@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.CONTACT_MODEL_HIDE_DELETED_CONDITION))
public class ContactModel extends AbstractEntityId<Integer> implements Deleteable, HasMaintenance {
  private static final long serialVersionUID = 7541931269392949400L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = EntityConstants.CONTACT_MODEL_COLUMN_ID)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID)
  private Organization organization;

  @Column(name = EntityConstants.CONTACT_MODEL_COLUMN_TYPE, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private ContactModelType type;

  @Column(name = EntityConstants.CONTACT_MODEL_COLUMN_NAME, nullable = false, length = EntityConstants.CONTACT_MODEL_NAME_MAX_LENGTH)
  @NotNull
  @Size(max = EntityConstants.CONTACT_MODEL_NAME_MAX_LENGTH)
  private String name;

  @Column(name = EntityConstants.CONTACT_MODEL_COLUMN_STATUS, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private ProjectModelStatus status;

  @Column(name = EntityConstants.CONTACT_MODEL_COLUMN_DATE_DELETED)
  @Temporal(value = TemporalType.TIMESTAMP)
  private Date dateDeleted;

  @Column(name = EntityConstants.CONTACT_MODEL_COLUMN_DATE_MAINTENANCE)
  @Temporal(value = TemporalType.TIMESTAMP)
  private Date dateMaintenance;

  @OneToOne(mappedBy = "contactModel", cascade = CascadeType.ALL)
  private ContactCard card;

  @OneToOne(mappedBy = "contactModel", cascade = CascadeType.ALL)
  private ContactDetails details;

  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public void setId(Integer id) {
    this.id = id;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public ContactModelType getType() {
    return type;
  }

  public void setType(ContactModelType type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public ProjectModelStatus getStatus() {
    return status;
  }

  @Override
  public void setStatus(ProjectModelStatus status) {
    this.status = status;
  }

  public Date getDateDeleted() {
    return dateDeleted;
  }

  public void setDateDeleted(Date dateDeleted) {
    this.dateDeleted = dateDeleted;
  }

  @Override
  public void delete() {
    setDateDeleted(new Date());
  }

  @Override
  @Transient
  public boolean isDeleted() {
    return getDateDeleted() != null;
  }

  @Override
  public Date getDateMaintenance() {
    return dateMaintenance;
  }

  @Override
  public void setDateMaintenance(Date dateMaintenance) {
    this.dateMaintenance = dateMaintenance;
  }

  public ContactCard getCard() {
    return card;
  }

  public void setCard(ContactCard card) {
    this.card = card;
  }

  public ContactDetails getDetails() {
    return details;
  }

  public void setDetails(ContactDetails details) {
    this.details = details;
  }
}
