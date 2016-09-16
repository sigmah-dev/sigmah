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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.shared.command.GetContactModels;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ValueEventChangeType;

import com.allen_sauer.gwt.log.client.Log;

public class ContactListComboBox extends ListComboBox<ContactDTO> {
  private boolean initialized = false;
  private int limit;
  private ContactModelType allowedType;
  private Set<Integer> allowedContactModelIds;
  private ChangeHandler changeHandler;
  private CreateContactHandler createContactHandler;
  private Button createButton;
  private DispatchAsync dispatch;

  public ContactListComboBox(DispatchAsync dispatch) {
    this(0, null, Collections.<Integer>emptySet(), dispatch);
  }

  public ContactListComboBox(int limit, ContactModelType allowedType, Set<Integer> allowedContactModelIds, DispatchAsync dispatch) {
    super(ContactDTO.ID, ContactDTO.FULLNAME);

    this.limit = limit;
    this.allowedType = allowedType;
    this.allowedContactModelIds = allowedContactModelIds;
    this.dispatch = dispatch;
  }

  public void setChangeHandler(ChangeHandler changeHandler) {
    this.changeHandler = changeHandler;
  }

  public void setCreateContactHandler(CreateContactHandler createContactHandler) {
    this.createContactHandler = createContactHandler;
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

  @Override
  protected void buildComponent() {
    super.buildComponent();

    createButton = Forms.button(I18N.CONSTANTS.createContact(), IconImageBundle.ICONS.create());
    getButtonPanel().add(createButton);

    applyButtonListeners();
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
          handleChange(models, ValueEventChangeType.ADD);
        }
      }

      @Override
      public void storeRemove(StoreEvent event) {
        List<ContactDTO> models = event.getModels();
        if (models == null) {
          models = new ArrayList<ContactDTO>(1);
          models.add((ContactDTO) event.getModel());
        }

        handleChange(models, ValueEventChangeType.REMOVE);
      }

      @Override
      public void storeClear(StoreEvent event) {

        List<ContactDTO> models = event.getModels();
        if (models == null) {
          models = new ArrayList<ContactDTO>(1);
          models.add((ContactDTO) event.getModel());
        }

        handleChange(models, ValueEventChangeType.REMOVE);
      }
    });
  }

  private void applyButtonListeners() {
    createButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        showContactCreator();
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

  private void handleChange(List<ContactDTO> contacts, ValueEventChangeType changeType) {
    checkReadWriteStatus();

    if (changeHandler == null) {
      return;
    }

    changeHandler.handleChange(contacts, changeType);
  }

  private void showContactCreator() {
    final Window window = new Window();
    window.setPlain(true);
    window.setModal(true);
    window.setBlinkModal(true);
    window.setLayout(new FitLayout());
    window.setSize(700, 300);
    window.setHeadingHtml(I18N.CONSTANTS.createContactDialogTitle());

    final ComboBox<ContactModelDTO> contactModelComboBox = Forms.combobox(I18N.CONSTANTS.contactModelLabel(), true, ContactModelDTO.ID, ContactModelDTO.NAME);
    final TextField<String> emailField = Forms.text(I18N.CONSTANTS.contactEmailAddress(), false);

    final TextField<String> firstNameField = Forms.text(I18N.CONSTANTS.contactFirstName(), false);
    final TextField<String> familyNameField = Forms.text(I18N.CONSTANTS.contactFamilyName(), false);
    final TextField<String> organizationNameField = Forms.text(I18N.CONSTANTS.contactOrganizationName(), false);
    firstNameField.setVisible(false);
    familyNameField.setVisible(false);
    organizationNameField.setVisible(false);

    final ComboBox<OrgUnitDTO> mainOrgUnitComboBox = Forms.combobox(I18N.CONSTANTS.contactMainOrgUnit(), false, OrgUnitDTO.ID, OrgUnitDTO.FULL_NAME);
    final ListComboBox<OrgUnitDTO> secondaryOrgUnitsComboBox = new ListComboBox<OrgUnitDTO>(OrgUnitDTO.ID, OrgUnitDTO.FULL_NAME);
    secondaryOrgUnitsComboBox.initComponent();
    final AdapterField secondaryOrgUnitsFieldAdapter = Forms.adapter(I18N.CONSTANTS.contactSecondaryOrgUnits(), secondaryOrgUnitsComboBox);
    secondaryOrgUnitsFieldAdapter.setVisible(false);

    dispatch.execute(new GetContactModels(allowedType, allowedContactModelIds, true), new AsyncCallback<ListResult<ContactModelDTO>>() {
      @Override
      public void onFailure(Throwable caught) {
        Log.error("Error while retrieving contact models for contact creation dialog.");
      }

      @Override
      public void onSuccess(ListResult<ContactModelDTO> result) {
        contactModelComboBox.getStore().add(result.getList());
      }
    });

    dispatch.execute(new GetOrgUnits(OrgUnitDTO.Mode.WITH_TREE), new AsyncCallback<ListResult<OrgUnitDTO>>() {
      @Override
      public void onFailure(Throwable caught) {
        Log.error("Error while retrieving org units for contact creation dialog.");
      }

      @Override
      public void onSuccess(ListResult<OrgUnitDTO> result) {

        for (OrgUnitDTO orgUnitDTO : result.getData()) {
          fillOrgUnitsComboboxes(orgUnitDTO, mainOrgUnitComboBox, secondaryOrgUnitsComboBox);
        }
      }
    });

    mainOrgUnitComboBox.addSelectionChangedListener(new SelectionChangedListener<OrgUnitDTO>() {
      @Override
      public void selectionChanged(SelectionChangedEvent<OrgUnitDTO> se) {
        if (se.getSelectedItem() == null) {
          secondaryOrgUnitsFieldAdapter.setVisible(false);
          return;
        }
        secondaryOrgUnitsFieldAdapter.setVisible(true);
      }
    });
    contactModelComboBox.addSelectionChangedListener(new SelectionChangedListener<ContactModelDTO>() {
      @Override
      public void selectionChanged(SelectionChangedEvent<ContactModelDTO> event) {
        ContactModelType currentType = null;
        if (event.getSelectedItem() != null) {
          currentType = event.getSelectedItem().getType();
        }
        firstNameField.setVisible(currentType == ContactModelType.INDIVIDUAL);
        familyNameField.setVisible(currentType == ContactModelType.INDIVIDUAL);
        organizationNameField.setVisible(currentType == ContactModelType.ORGANIZATION);
        firstNameField.setAllowBlank(currentType != ContactModelType.INDIVIDUAL);
        familyNameField.setAllowBlank(currentType != ContactModelType.INDIVIDUAL);
        organizationNameField.setAllowBlank(currentType != ContactModelType.ORGANIZATION);
      }
    });
    org.sigmah.client.ui.widget.button.Button button = Forms.button(I18N.CONSTANTS.createContact());

    final FormPanel formPanel = Forms.panel(200);
    formPanel.add(contactModelComboBox);
    formPanel.add(emailField);
    formPanel.add(firstNameField);
    formPanel.add(familyNameField);
    formPanel.add(organizationNameField);
    formPanel.add(mainOrgUnitComboBox);
    formPanel.add(secondaryOrgUnitsFieldAdapter);
    formPanel.getButtonBar().add(button);

    button.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent event) {
        if (!formPanel.isValid()) {
          return;
        }

        createContactHandler.handleContactCreation(contactModelComboBox.getValue(), emailField.getValue(),
            firstNameField.getValue(), familyNameField.getValue(), organizationNameField.getValue(),
            mainOrgUnitComboBox.getValue(), secondaryOrgUnitsComboBox.getListStore().getModels());
        window.hide();
      }
    });

    window.add(formPanel);
    window.show();
  }

  private void fillOrgUnitsComboboxes(OrgUnitDTO unit, final ComboBox<OrgUnitDTO> mainOrgUnitComboBox, final ListComboBox<OrgUnitDTO> secondaryOrgUnitsComboBox) {

    mainOrgUnitComboBox.getStore().add(unit);
    secondaryOrgUnitsComboBox.getAvailableValuesStore().add(unit);

    final Set<OrgUnitDTO> children = unit.getChildrenOrgUnits();
    if (children != null && !children.isEmpty()) {
      for (final OrgUnitDTO child : children) {
        fillOrgUnitsComboboxes(child, mainOrgUnitComboBox, secondaryOrgUnitsComboBox);
      }
    }

  }

  private boolean isFormValid(ContactModelDTO contactModelDTO, OrgUnitDTO mainOrgUnitDTO, List<OrgUnitDTO> secondaryOrgUnits) {
    if (contactModelDTO == null) {
      return false;
    }
    // a contact cannot only have secondary org units
    return mainOrgUnitDTO != null || secondaryOrgUnits == null || secondaryOrgUnits.isEmpty();
  }

  public interface CreateContactHandler {
    void handleContactCreation(ContactModelDTO contactModelDTO, String email, String firstName, String familyName, String organizationName, OrgUnitDTO mainOrgUnit, List<OrgUnitDTO> secondaryOrgUnits);
  }

  public interface ChangeHandler {
    void handleChange(List<ContactDTO> contacts, ValueEventChangeType changeType);
  }
}
