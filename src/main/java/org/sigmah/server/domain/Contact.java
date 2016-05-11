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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;

@Entity
@Table(name = EntityConstants.CONTACT_TABLE)
public class Contact extends AbstractEntityId<Integer> implements Deleteable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = EntityConstants.CONTACT_COLUMN_ID)
  private Integer id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstants.CONTACT_MODEL_COLUMN_ID, nullable = false)
  private ContactModel contactModel;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstants.CONTACT_COLUMN_USER)
  private User user;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID)
  private Organization organization;

  @Column(name = EntityConstants.CONTACT_COLUMN_NAME)
  private String name;

  @Column(name = EntityConstants.CONTACT_COLUMN_FIRSTNAME)
  private String firstname;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstants.CONTACT_COLUMN_MAIN_ORG_UNIT)
  private OrgUnit mainOrgUnit;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = EntityConstants.CONTACT_ORG_UNIT_LINK_TABLE,
      joinColumns = @JoinColumn(name = EntityConstants.CONTACT_COLUMN_ID),
      inverseJoinColumns = @JoinColumn(name = EntityConstants.CONTACT_ORG_UNIT_COLUMN_ORG_UNIT),
      uniqueConstraints = @UniqueConstraint(columnNames = {
          EntityConstants.CONTACT_COLUMN_ID,
          EntityConstants.CONTACT_ORG_UNIT_COLUMN_ORG_UNIT
      })
  )
  private List<OrgUnit> secondaryOrgUnits;

  @Column(name = EntityConstants.CONTACT_COLUMN_LOGIN)
  private String login;

  @Column(name = EntityConstants.CONTACT_COLUMN_EMAIL)
  private String email;

  @Column(name = EntityConstants.CONTACT_COLUMN_PHONE_NUMBER)
  private String phoneNumber;

  @Column(name = EntityConstants.CONTACT_COLUMN_POSTAL_ADDRESS)
  private String postalAddress;

  @Column(name = EntityConstants.CONTACT_COLUMN_PHOTO)
  private String photo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstants.CONTACT_COLUMN_COUNTRY)
  private Country country;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = EntityConstants.CONTACT_COLUMN_PARENT)
  private Contact parent;

  @Column(name = EntityConstants.CONTACT_COLUMN_DATE_CREATED)
  @Temporal(value = TemporalType.TIMESTAMP)
  private Date dateCreated;

  @Column(name = EntityConstants.CONTACT_COLUMN_DATE_DELETED)
  @Temporal(value = TemporalType.TIMESTAMP)
  private Date dateDeleted;

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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public String getName() {
    // Take the name from the organization or from the user if available
    if (organization != null) {
      return organization.getName();
    }

    if (user != null) {
      return user.getName();
    }

    return name;
  }

  public void setName(String name) {
    if (organization != null) {
      organization.setName(name);
      return;
    }
    if (user != null) {
      user.setName(name);
      return;
    }
    this.name = name;
  }

  public String getFirstname() {
    // Take the firstname from the user if available
    if (user != null) {
      return user.getFirstName();
    }

    return firstname;
  }

  public void setFirstname(String firstname) {
    if (user != null) {
      user.setFirstName(firstname);
      return;
    }
    this.firstname = firstname;
  }

  public OrgUnit getMainOrgUnit() {
    // Take the main org unit from the organization or from the user if available
    if (organization != null) {
      return organization.getRoot();
    }

    if (user != null) {
      return user.getMainOrgUnitWithProfiles().getOrgUnit();
    }

    return mainOrgUnit;
  }

  public void setMainOrgUnit(OrgUnit mainOrgUnit) {
    if (user != null) {
      // XXX: A user org unit shouldn't be updated from contact forms because OrgUnits alone doesn't provide permission information
      return;
    }
    this.mainOrgUnit = mainOrgUnit;
  }

  public List<OrgUnit> getSecondaryOrgUnits() {
    // Take the secondary org units from the user if available
    if (user != null) {
      return user.getSecondaryOrgUnits();
    }

    return secondaryOrgUnits;
  }

  public void setSecondaryOrgUnits(List<OrgUnit> secondaryOrgUnits) {
    if (user != null) {
      // XXX: A user org unit shouldn't be updated from contact forms because OrgUnits alone doesn't provide permission information
      return;
    }
    this.secondaryOrgUnits = secondaryOrgUnits;
  }

  // Dozer can't transform a Collection<OrgUnit> to a Collection<Integer> by itself
  // Let's help it by providing directly a function that give the result of the transformation
  public Set<Integer> getSecondaryOrgUnitIds() {
    // getSecondaryOrgUnits() instead of secondaryOrgUnits to force data recuperation from the database
    List<OrgUnit> secondaryOrgUnits = getSecondaryOrgUnits();
    if (secondaryOrgUnits == null) {
      return Collections.emptySet();
    }

    Set<Integer> ids = new HashSet<>(secondaryOrgUnits.size());
    for (OrgUnit secondaryOrgUnit : secondaryOrgUnits) {
      ids.add(secondaryOrgUnit.getId());
    }
    return ids;
  }

  public String getLogin() {
    // contact login can be different from user email
    // take it from user in fallback
    if (login == null && user != null) {
      return user.getEmail();
    }

    return login;
  }

  public void setLogin(String login) {
    // Do not update user.login as it is used during authentication process
    this.login = login;
  }

  public String getEmail() {
    // contact email can be different from user email
    // take it from user in fallback
    if (email == null && user != null) {
      return user.getEmail();
    }

    return email;
  }

  public void setEmail(String email) {
    // As emails between user and the related contact can be different, just ignore the email from user entity
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(String postalAddress) {
    this.postalAddress = postalAddress;
  }

  public String getPhoto() {
    // Use the organization logo in fallback
    if (photo == null && organization != null) {
      return organization.getLogo();
    }

    return photo;
  }

  public void setPhoto(String photo) {
    // Do not update the organization logo from the contact
    this.photo = photo;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public Contact getParent() {
    return parent;
  }

  public void setParent(Contact parent) {
    this.parent = parent;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Date getDateDeleted() {
    return dateDeleted;
  }

  public void setDateDeleted(Date dateDeleted) {
    this.dateDeleted = dateDeleted;
  }

  @Override
  public void delete() {
    this.dateDeleted = new Date();
  }

  @Override
  public boolean isDeleted() {
    return dateDeleted != null;
  }
}
