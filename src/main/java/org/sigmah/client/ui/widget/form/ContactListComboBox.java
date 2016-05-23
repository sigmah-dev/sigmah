package org.sigmah.client.ui.widget.form;
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

import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.referential.ContactModelType;

public class ContactListComboBox extends ListComboBox<ContactDTO> {
  private boolean initialized = false;
  private int limit;
  private ContactModelType allowedType;
  private Set<Integer> allowedContactModelIds;
  private ChangeHandler changeHandler;

  public ContactListComboBox() {
    this(0, null, Collections.<Integer>emptySet());
  }

  public ContactListComboBox(int limit, ContactModelType allowedType, Set<Integer> allowedContactModelIds) {
    super(ContactDTO.ID, ContactDTO.FULLNAME);

    this.limit = limit;
    this.allowedType = allowedType;
    this.allowedContactModelIds = allowedContactModelIds;
  }

  public void setChangeHandler(ChangeHandler changeHandler) {
    this.changeHandler = changeHandler;
  }

  @Override
  public void initComponent() {
    super.initComponent();

    applyStoreListeners();
  }

  public void initListStore(List<ContactDTO> contacts) {
    for (ContactDTO contact : contacts) {
      getAvailableValuesStore().remove(contact);
    }

    getListStore().add(contacts);

    initialized = true;
  }

  private void applyStoreListeners() {
    getListStore().addStoreListener(new StoreListener<ContactDTO>() {
      @Override
      public void storeAdd(StoreEvent event) {
        List<ContactDTO> models = event.getModels();
        if (models == null) {
          models = new ArrayList<ContactDTO>(1);
          models.add((ContactDTO) event.getModel());
        }

        if (limit > 0 && getListStore().getCount() > limit) {
          // event.setCancelled(true) doesn't work because the element is already added...
          // Let's remove it
          for (ContactDTO contactDTO : models) {
            getListStore().remove(contactDTO);
            getAvailableValuesStore().add(contactDTO);
          }
        }

        for (ContactDTO contactDTO : models) {
          // Avoid selection of filtered type and models
          if (contactDTO.getContactModel().getType() == allowedType) {
            continue;
          }
          if (allowedContactModelIds == null || allowedContactModelIds.isEmpty() || allowedContactModelIds.contains(contactDTO.getContactModel().getId())) {
            continue;
          }

          getListStore().remove(contactDTO);
          getAvailableValuesStore().add(contactDTO);
        }

        if (initialized) {
          handleChange();
        }
      }

      @Override
      public void storeRemove(StoreEvent se) {
        handleChange();
      }

      @Override
      public void storeClear(StoreEvent se) {
        handleChange();
      }
    });
  }

  private void checkReadWriteStatus() {
    if (getField() == null) {
      return;
    }

    boolean readOnly = limit > 0 && getListStore().getCount() >= limit;
    getField().setReadOnly(readOnly);
  }

  private void handleChange() {
    checkReadWriteStatus();

    if (changeHandler == null) {
      return;
    }

    changeHandler.handleChange(getListStore().getModels());
  }

  public interface ChangeHandler {
    void handleChange(List<ContactDTO> contacts);
  }
}
