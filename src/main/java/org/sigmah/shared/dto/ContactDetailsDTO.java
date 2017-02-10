package org.sigmah.shared.dto;
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

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

public class ContactDetailsDTO extends AbstractModelDataEntityDTO<Integer> {
  private static final long serialVersionUID = 6352776463120837691L;

  public static final String ENTITY_NAME = "ContactDetails";

  public static final String ID = "id";
  public static final String CONTACT_MODEL = "contactModel";
  public static final String LAYOUT = "layout";
  public static final String NAME = "name";

  @Override
  public String getEntityName() {
    return ENTITY_NAME;
  }

  @Override
  public Integer getId() {
    return (Integer) get(ID);
  }

  public void setId(Integer id) {
    set(ID, id);
  }

  public String getName() {
    return I18N.CONSTANTS.Admin_CONTACT_DETAILS();
  }

  public void setName() {
    set(NAME, I18N.CONSTANTS.Admin_CONTACT_DETAILS());
  }

  public LayoutDTO getLayout() {
    return get(LAYOUT);
  }

  public void setLayout(LayoutDTO layout) {
    set(LAYOUT, layout);
  }

  public ContactModelDTO getContactModel() {
    return get(CONTACT_MODEL);
  }

  public void setContactModel(ContactModelDTO contactModel) {
    set(CONTACT_MODEL, contactModel);
  }
}
