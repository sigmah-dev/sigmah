package org.sigmah.shared.dto.element;
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchQueue;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.event.handler.OfflineHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.FlexibleGrid;
import org.sigmah.client.ui.widget.HistoryTokenText;
import org.sigmah.client.ui.widget.contact.ContactPicker;
import org.sigmah.client.ui.widget.contact.DedupeContactDialog;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.ListComboBox;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.client.util.profiler.Scenario;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.command.CheckContactDuplication;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.DedupeContact;
import org.sigmah.shared.command.GetContactDuplicatedProperties;
import org.sigmah.shared.command.GetContactModels;
import org.sigmah.shared.command.GetContacts;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.result.ContactDuplicatedProperty;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.history.HistoryTokenDTO;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ValueEventChangeType;
import org.sigmah.shared.util.ValueResultUtils;

import com.allen_sauer.gwt.log.client.Log;

public class ContactListElementDTO extends FlexibleElementDTO {
  private static final long serialVersionUID = 646913359144175456L;

  public static final String ENTITY_NAME = "element.ContactListElement";

  public static final String ALLOWED_TYPE = "allowedType";
  public static final String ALLOWED_MODEL_IDS = "allowedModels";
  public static final String LIMIT = "limit";
  public static final String IS_MEMBER = "member";
  public static final String CHECKBOX_ELEMENT = "checkboxElement";

  private static final String STYLE_CONTACT_GRID_NAME = "contact-grid-name";

  private static class OrgUnitSelectionChangedListener extends SelectionChangedListener<OrgUnitDTO> {
    private final AdapterField secondaryOrgUnitsFieldAdapter;

    public OrgUnitSelectionChangedListener(AdapterField secondaryOrgUnitsFieldAdapter) {
      this.secondaryOrgUnitsFieldAdapter = secondaryOrgUnitsFieldAdapter;
    }

    @Override
    public void selectionChanged(SelectionChangedEvent<OrgUnitDTO> se) {
      if (se.getSelectedItem() == null) {
        secondaryOrgUnitsFieldAdapter.setVisible(false);
        return;
      }
      secondaryOrgUnitsFieldAdapter.setVisible(true);
    }
  }

  private static class ContactModelSelectionChangedListener extends SelectionChangedListener<ContactModelDTO> {
    private final TextField<String> firstNameField;
    private final TextField<String> familyNameField;
    private final TextField<String> organizationNameField;

    public ContactModelSelectionChangedListener(TextField<String> firstNameField, TextField<String> familyNameField, TextField<String> organizationNameField) {
      this.firstNameField = firstNameField;
      this.familyNameField = familyNameField;
      this.organizationNameField = organizationNameField;
    }

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
  }

  private class ContactStoreEventListener implements Listener<StoreEvent<ContactDTO>> {
    private final ListStore<ContactDTO> store;
    private final ToolBar actionsToolBar;

    private List<ContactDTO> clearedValues;

    public ContactStoreEventListener(ListStore<ContactDTO> store, ToolBar actionsToolBar) {
      this.store = store;
      this.actionsToolBar = actionsToolBar;
    }

    @Override
    public void handleEvent(StoreEvent<ContactDTO> e) {
      EventType type = e.getType();
      if (type == Store.BeforeAdd) {
        storeBeforeAdd(e);
      } else if (type == Store.Add) {
        storeAdd(e);
      } else if (type == Store.BeforeClear) {
        storeBeforeClear(e);
      } else if (type == Store.Clear) {
        storeClear(e);
      } if (type == Store.Remove) {
        storeRemove(e);
      }
    }

    private void storeBeforeAdd(StoreEvent<ContactDTO> se) {
      if (getLimit() > 0 && store.getCount() >= getLimit()) {
        se.setCancelled(true);
        return;
      }
      assert se.getModels().size() == 1;
      ContactDTO contactDTO = se.getModels().get(0);
      // Avoid selection of filtered type and models
      if (getAllowedType() != null && contactDTO.getContactModel().getType() != getAllowedType()) {
        se.setCancelled(true);
        return;
      }
      if (getAllowedModelIds() != null && !getAllowedModelIds().isEmpty() && !getAllowedModelIds().contains(contactDTO.getContactModel().getId())) {
        se.setCancelled(true);
        return;
      }
      // XXX: verify the contact has TRUE for the checkboxElement defined in the ContactListElement ?
      // (to do that we need to retrive this flexibleElement for all ContactDTO)
      // Not very useful, the availableContactsStore only contains valid values and
      // created contacts have the value set to TRUE
    }

    private void storeAdd(StoreEvent<ContactDTO> se) {
      handleChange(se.getModels(), ValueEventChangeType.ADD);
    }

    private void storeBeforeClear(StoreEvent<ContactDTO> se) {
      assert clearedValues == null;
      clearedValues = store.getRange(0, store.getCount() - 1);
    }

    private void storeClear(StoreEvent<ContactDTO> se) {
      assert clearedValues != null;
      handleChange(clearedValues, ValueEventChangeType.REMOVE);
      clearedValues = null;
    }

    private void storeRemove(StoreEvent<ContactDTO> se) {
      handleChange(Collections.singletonList(se.getModel()), ValueEventChangeType.REMOVE);
    }

    private void handleChange(List<ContactDTO> contacts, ValueEventChangeType changeType) {
      actionsToolBar.setEnabled(!Profiler.INSTANCE.isOfflineMode() && !isReadOnly(store));

      handlerManager.fireEvent(new ValueEvent(ContactListElementDTO.this, serializeValue(contacts), changeType));
      handlerManager.fireEvent(new RequiredValueEvent(store.getCount() > 0, true));
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Component getComponent(final ValueResult valueResult, boolean enabled) {
    final ContentPanel mainPanel = new ContentPanel();
    mainPanel.setHeaderVisible(true);
    mainPanel.setBorders(true);
    if (getLimit() > 0) {
      mainPanel.setHeadingHtml(getLabel() + " (" + I18N.MESSAGES.flexibleElementContactListLimitReached(String.valueOf(getLimit())) + ")");
    } else {
      mainPanel.setHeadingHtml(getLabel());
    }

    final ListStore<ContactDTO> store = new ListStore<ContactDTO>();

    final FlexibleGrid<ContactDTO> contactsGrid = new FlexibleGrid<ContactDTO>(store, null, 5, getColumnModel(enabled));
    contactsGrid.setAutoExpandColumn(ContactDTO.NAME);

    mainPanel.add(contactsGrid);

    final Set<Integer> contactIds = parseValue(valueResult);

    Runnable afterGetContacts = null;

    if (enabled) {
      afterGetContacts = prepareAfterGetContacts(mainPanel, store);
    }

    if (!contactIds.isEmpty()) {
      final Runnable afterGetContactsFinal = afterGetContacts;
      dispatch.execute(new GetContacts(contactIds), new AsyncCallback<ListResult<ContactDTO>>() {
        @Override
        public void onFailure(Throwable caught) {
          Log.error("Error while trying to get contacts for a contact list element.", caught);
        }

        @Override
        public void onSuccess(ListResult<ContactDTO> contactDTOListResult) {
          store.add(contactDTOListResult.getList());
          if (afterGetContactsFinal != null) {
            afterGetContactsFinal.run();
          }
        }
      });
    } else if (afterGetContacts != null) {
      afterGetContacts.run();
    }

    return mainPanel;
  }

  private Runnable prepareAfterGetContacts(ContentPanel mainPanel, final ListStore<ContactDTO> store) {
    Runnable afterGetContacts;
    final ToolBar actionsToolBar = new ToolBar();
    actionsToolBar.setAlignment(HorizontalAlignment.LEFT);

    actionsToolBar.add(Forms.button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add(), new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        showContactSelector(store);
      }
    }));

    final Label offlineLabel = new Label(I18N.CONSTANTS.sigmahContactsOfflineUnavailable());
    actionsToolBar.add(offlineLabel);

    mainPanel.setTopComponent(actionsToolBar);

    // if offline mode, no contact can be used
    actionsToolBar.setEnabled(!Profiler.INSTANCE.isOfflineMode());
    offlineLabel.setVisible(Profiler.INSTANCE.isOfflineMode());
    if (eventBus != null) {
      eventBus.addHandler(OfflineEvent.getType(), new OfflineHandler() {
        @Override
        public void handleEvent(OfflineEvent event) {
          final boolean isOffline = ApplicationState.OFFLINE == event.getState();
          actionsToolBar.setEnabled(!isOffline && !isReadOnly(store));
          offlineLabel.setVisible(isOffline);
        }
      });
    }

    final Listener<StoreEvent<ContactDTO>> listener = new ContactStoreEventListener(store, actionsToolBar);

    afterGetContacts = new Runnable() {
      @Override
      public void run() {
        actionsToolBar.setEnabled(!Profiler.INSTANCE.isOfflineMode() && !isReadOnly(store));

        store.addListener(Store.BeforeAdd, listener);
        store.addListener(Store.Add, listener);
        store.addListener(Store.BeforeClear, listener);
        store.addListener(Store.Clear, listener);
        store.addListener(Store.Remove, listener);

        handlerManager.fireEvent(new RequiredValueEvent(store.getCount() > 0, true));
      }
    };
    return afterGetContacts;
  }

  private boolean isReadOnly(ListStore<ContactDTO> store) {
    return getLimit() > 0 && store.getCount() >= getLimit();
  }

  private ColumnConfig[] getColumnModel(boolean enabled) {
    // Type
    final ColumnConfig typeColumn = new ColumnConfig(ContactDTO.TYPE, I18N.CONSTANTS.contactTypeLabel(), 75);
    typeColumn.setRenderer(new GridCellRenderer<ContactDTO>() {
      @Override
      public Object render(final ContactDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
                           final ListStore<ContactDTO> store, final Grid<ContactDTO> grid) {

        ContactModelType type = model.get(property);

        String typeLabel = I18N.CONSTANTS.contactTypeIndividualLabel();

        if (type == ContactModelType.ORGANIZATION) {
          typeLabel = I18N.CONSTANTS.contactTypeOrganizationLabel();
        }

        return typeLabel;
      }
    });

    // Name
    final ColumnConfig nameColumn = new ColumnConfig(ContactDTO.NAME, I18N.CONSTANTS.contactName(), 100);
    nameColumn.setRenderer(new GridCellRenderer<ContactDTO>() {

      @Override
      public Object render(final ContactDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
                           final ListStore<ContactDTO> store, final Grid<ContactDTO> grid) {

        final Anchor nameLink = new Anchor((String) model.get(property));

        nameLink.addClickHandler(new ClickHandler() {

          @Override
          public void onClick(ClickEvent event) {
            if (eventBus != null) {
              Profiler.INSTANCE.startScenario(Scenario.OPEN_CONTACT);
              eventBus.navigateRequest(Page.CONTACT_DASHBOARD.requestWith(RequestParameter.ID, model.getId()));
            }
          }
        });

        final com.google.gwt.user.client.ui.Grid panel = new com.google.gwt.user.client.ui.Grid(1, 1);
        panel.setCellPadding(0);
        panel.setCellSpacing(0);

        panel.setWidget(0, 0, nameLink);
        panel.getCellFormatter().addStyleName(0, 0, STYLE_CONTACT_GRID_NAME);

        return panel;
      }
    });

    // Firstname
    final ColumnConfig firstnameColumn = new ColumnConfig(ContactDTO.FIRSTNAME, I18N.CONSTANTS.contactFirstName(), 75);

    // Email
    final ColumnConfig emailColumn = new ColumnConfig(ContactDTO.EMAIL, I18N.CONSTANTS.contactEmailAddress(), 150);
    emailColumn.setHidden(true);

    // Id
    final ColumnConfig idColumn = new ColumnConfig(ContactDTO.ID, I18N.CONSTANTS.contactId(), 100);
    idColumn.setHidden(true);

    if (!enabled) {
      return new ColumnConfig[] {
              typeColumn, nameColumn, firstnameColumn, emailColumn, idColumn
      };
    }

    // Remove.
    final ColumnConfig removeColumn = new ColumnConfig();
    removeColumn.setId("remove");
    removeColumn.setHeaderText(null);
    removeColumn.setWidth(10);
    removeColumn.setSortable(false);
    removeColumn.setRenderer(new GridCellRenderer<ContactDTO>() {

      @Override
      public Object render(final ContactDTO model, String property, ColumnData config, int rowIndex, int colIndex, final ListStore<ContactDTO> store,
                           Grid<ContactDTO> grid) {

        final Image image = IconImageBundle.ICONS.deleteIcon().createImage();
        image.setTitle(I18N.CONSTANTS.remove());
        image.addStyleName("flexibility-action");
        image.addClickHandler(new ClickHandler() {

          @Override
          public void onClick(final ClickEvent event) {
            store.remove(model);
          }
        });

        return image;
      }
    });

    return new ColumnConfig[] {
            typeColumn, nameColumn, firstnameColumn, emailColumn, idColumn, removeColumn
    };
  }

  private void showContactSelector(final ListStore<ContactDTO> store) {
    final Set<Integer> selectedContactIds = new HashSet<Integer>(store.getCount(), 1f);
    for (int i = 0,
         l = store.getCount(); i < l; i++) {
      selectedContactIds.add(store.getAt(i).getId());
    }

    final Window window = new Window();
    window.setPlain(true);
    window.setModal(true);
    window.setBlinkModal(true);
    window.setLayout(new FitLayout());
    window.setSize(700, 300);
    window.setHeadingHtml(I18N.CONSTANTS.selectContactDialogTitle());

    final ContactPicker contactPicker = new ContactPicker(getAllowedType(), false, getAllowedModelIds(), getCheckboxElementId(), selectedContactIds, dispatch);

    final FormPanel formPanel = Forms.panel(500);
    formPanel.add(contactPicker);
    formPanel.getButtonBar().add(Forms.button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add(), new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        ContactDTO value = contactPicker.getSelectedItem();
        if (value == null) {
          return;
        }

        if (store.findModel(ContactDTO.ID, value.getId()) != null) {
          return;
        }
        store.add(value);

        window.hide();
      }
    }));
    formPanel.getButtonBar().add(Forms.button(I18N.CONSTANTS.createContact(), IconImageBundle.ICONS.create(), new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        window.hide();
        showContactCreator(store);
      }
    }));

    window.add(formPanel);

    window.show();
  }

  private void showContactCreator(final ListStore<ContactDTO> store) {
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

    final ComboBox<OrgUnitDTO> mainOrgUnitComboBox = Forms.combobox(I18N.CONSTANTS.contactMainOrgUnit(), true, OrgUnitDTO.ID, OrgUnitDTO.FULL_NAME);
    final ListComboBox<OrgUnitDTO> secondaryOrgUnitsComboBox = new ListComboBox<OrgUnitDTO>(OrgUnitDTO.ID, OrgUnitDTO.FULL_NAME);
    secondaryOrgUnitsComboBox.initComponent();
    final AdapterField secondaryOrgUnitsFieldAdapter = Forms.adapter(I18N.CONSTANTS.contactSecondaryOrgUnits(), secondaryOrgUnitsComboBox);
    secondaryOrgUnitsFieldAdapter.setVisible(false);

    getContactModels(contactModelComboBox);

    getOrgUnits(mainOrgUnitComboBox, secondaryOrgUnitsComboBox);

    mainOrgUnitComboBox.addSelectionChangedListener(new OrgUnitSelectionChangedListener(secondaryOrgUnitsFieldAdapter));
    contactModelComboBox.addSelectionChangedListener(new ContactModelSelectionChangedListener(firstNameField, familyNameField, organizationNameField));
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

        handleContactCreation(store, contactModelComboBox.getValue(), emailField.getValue(),
                firstNameField.getValue(), familyNameField.getValue(), organizationNameField.getValue(),
                mainOrgUnitComboBox.getValue(), secondaryOrgUnitsComboBox.getListStore().getModels());
        window.hide();
      }
    });

    window.add(formPanel);
    window.show();
  }

  private void getOrgUnits(final ComboBox<OrgUnitDTO> mainOrgUnitComboBox, final ListComboBox<OrgUnitDTO> secondaryOrgUnitsComboBox) {
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
  }

  private void getContactModels(final ComboBox<ContactModelDTO> contactModelComboBox) {
    dispatch.execute(new GetContactModels(getAllowedType(), getAllowedModelIds(), true), new AsyncCallback<ListResult<ContactModelDTO>>() {
      @Override
      public void onFailure(Throwable caught) {
        Log.error("Error while retrieving contact models for contact creation dialog.");
      }

      @Override
      public void onSuccess(ListResult<ContactModelDTO> result) {
        contactModelComboBox.getStore().add(result.getList());
      }
    });
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

  private void handleContactCreation(final ListStore<ContactDTO> store, final ContactModelDTO contactModelDTO, final String email, final String firstName, final String familyName, final String organizationName, final OrgUnitDTO mainOrgUnit, final List<OrgUnitDTO> secondaryOrgUnits) {
    CheckContactDuplication checkContactDuplication;
    if (contactModelDTO.getType() == ContactModelType.INDIVIDUAL) {
      checkContactDuplication = new CheckContactDuplication(null, email, familyName, firstName, contactModelDTO);
    } else {
      checkContactDuplication = new CheckContactDuplication(null, email, familyName, null, contactModelDTO);
    }
    dispatch.execute(checkContactDuplication, new AsyncCallback<ListResult<ContactDTO>>() {
      @Override
      public void onFailure(Throwable caught) {
        Log.error("Error while checking contact duplicates.");
      }

      @Override
      public void onSuccess(ListResult<ContactDTO> result) {
        final HashMap<String, Object> properties = buildPropertyMap(contactModelDTO, email, firstName, familyName, organizationName, mainOrgUnit, secondaryOrgUnits);
        if (result == null || result.getSize() == 0) {
          createEntity(properties, store);
          return;
        }

        final DedupeContactDialog dedupeContactDialog = new DedupeContactDialog(true);
        dedupeContactDialog.getPossibleDuplicatesGrid().getStore().add(result.getList());
        dedupeContactDialog.getFirstStepMainButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
          @Override
          public void componentSelected(ButtonEvent ce) {
            createEntity(properties, store);
            dedupeContactDialog.hide();
          }
        });
        dedupeContactDialog.setSecondStepHandler(new DedupeContactDialog.SecondStepHandler() {
          @Override
          public void initialize(final Integer contactId, final ListStore<ContactDuplicatedProperty> propertiesStore) {
            dispatch.execute(new GetContactDuplicatedProperties(contactId, null, properties), new CommandResultHandler<ListResult<ContactDuplicatedProperty>>() {
              @Override
              protected void onCommandSuccess(ListResult<ContactDuplicatedProperty> result) {
                propertiesStore.add(result.getList());
              }
            }, new LoadingMask(dedupeContactDialog));
          }

          @Override
          public void downloadImage(String id, final Image image) {
            imageProvider.provideDataUrl(id, new SuccessCallback<String>() {
              @Override
              public void onSuccess(String dataUrl) {
                image.setUrl(dataUrl);
              }
            });
          }

          @Override
          public void handleDedupeContact(final Integer targetedContactId, List<ContactDuplicatedProperty> selectedProperties) {
            dispatch.execute(new DedupeContact(selectedProperties, targetedContactId), new CommandResultHandler<ContactDTO>() {
              @Override
              protected void onCommandSuccess(ContactDTO targetedContactDTO) {
                dedupeContactDialog.hide();
                store.add(targetedContactDTO);
              }
            });
          }

          @Override
          public void handleCancel() {
            dedupeContactDialog.hide();
          }
        });
        dedupeContactDialog.show();
      }
    });
  }

  @Override
  public boolean isCorrectRequiredValue(ValueResult result) {
    return !parseValue(result).isEmpty();
  }

  public ContactModelType getAllowedType() {
    return get(ALLOWED_TYPE);
  }

  public void setAllowedType(ContactModelType allowedType) {
    set(ALLOWED_TYPE, allowedType);
  }

  public Set<Integer> getAllowedModelIds() {
    return get(ALLOWED_MODEL_IDS);
  }

  public void setAllowedModelIds(Set<Integer> allowedModels) {
    set(ALLOWED_MODEL_IDS, allowedModels);
  }

  public int getLimit() {
    return get(LIMIT);
  }

  public void setLimit(int limit) {
    set(LIMIT, limit);
  }

  public boolean isMember() {
    return get(IS_MEMBER);
  }

  public void setMember(boolean member) {
    set(IS_MEMBER, member);
  }

  public CheckboxElementDTO getCheckboxElement() {
    return get(CHECKBOX_ELEMENT);
  }

  public Integer getCheckboxElementId() {
    return getCheckboxElement() == null ? null : getCheckboxElement().getId();
  }

  public void setCheckboxElement(CheckboxElementDTO checkboxElement) {
    set(CHECKBOX_ELEMENT, checkboxElement);
  }


  @Override
  public String getEntityName() {
    return ENTITY_NAME;
  }

  public static Set<Integer> parseValue(ValueResult result) {
    if (result == null || result.getValueObject() == null) {
      return Collections.emptySet();
    }

    Set<Integer> ids = new HashSet<Integer>();
    for (String serializedId : result.getValueObject().split(ValueResultUtils.DEFAULT_VALUE_SEPARATOR)) {
      ids.add(Integer.parseInt(serializedId));
    }
    return ids;
  }

  private static Set<Integer> serializeValue(List<ContactDTO> contacts) {
    Set<Integer> ids = new HashSet<Integer>();
    for (ContactDTO contact : contacts) {
      ids.add(contact.getId());
    }
    return ids;
  }

  private void createEntity(HashMap<String, Object> properties, final ListStore<ContactDTO> store) {
    dispatch.execute(new CreateEntity(ContactDTO.ENTITY_NAME, properties), new AsyncCallback<CreateResult>() {
      @Override
      public void onFailure(Throwable caught) {
        Log.error("Error while creating a new Contact from contact creation dialog.");
      }

      @Override
      public void onSuccess(CreateResult result) {
        store.add((ContactDTO) result.getEntity());
      }
    });
  }

  private HashMap<String, Object> buildPropertyMap(ContactModelDTO contactModelDTO, String email, String firstName, String familyName, String organizationName, OrgUnitDTO mainOrgUnit, List<OrgUnitDTO> secondaryOrgUnits) {
    HashMap<String, Object> properties = new HashMap<String, Object>();
    properties.put(ContactDTO.CONTACT_MODEL, contactModelDTO.getId());
    properties.put(ContactDTO.EMAIL, email);
    properties.put(ContactDTO.FIRSTNAME, contactModelDTO.getType() == ContactModelType.INDIVIDUAL ? firstName : null);
    properties.put(ContactDTO.NAME, contactModelDTO.getType() == ContactModelType.INDIVIDUAL ? familyName : organizationName);
    if (getCheckboxElement() != null) {
      properties.put(ContactDTO.CHECKBOX_ELEMENT_TO_SET_TO_TRUE, getCheckboxElementId());
    }
    if (mainOrgUnit != null) {
      properties.put(ContactDTO.MAIN_ORG_UNIT, mainOrgUnit.getId());
    }
    if (secondaryOrgUnits != null) {
      HashSet<Integer> secondaryOrgUnitIds = new HashSet<Integer>();
      for (OrgUnitDTO secondaryOrgUnit : secondaryOrgUnits) {
        secondaryOrgUnitIds.add(secondaryOrgUnit.getId());
      }
      properties.put(ContactDTO.SECONDARY_ORG_UNITS, secondaryOrgUnitIds);
    }
    return properties;
  }

  @Override
  public Object renderHistoryToken(HistoryTokenListDTO historyTokenListDTO) {
    final List<String> formattedTokens = new ArrayList<String>();
    final HistoryTokenText historyTokenText = new HistoryTokenText();
    DispatchQueue dispatchQueue = new DispatchQueue(dispatch);
    for (final HistoryTokenDTO historyTokenDTO : historyTokenListDTO.getTokens()) {
      // Contact list element value in a HistoryToken is always monovalued and combined to a ADD or REMOVED action
      dispatchQueue.add(new GetContacts(new HashSet<Integer>(ValueResultUtils.splitValuesAsInteger(historyTokenDTO.getValue()))), new CommandResultHandler<ListResult<ContactDTO>>() {
        @Override
        protected void onCommandSuccess(ListResult<ContactDTO> result) {
          StringBuilder stringBuilder = new StringBuilder();
          for (int i = 0; i < result.getList().size(); i++) {
            if (i != 0) {
              stringBuilder.append(", ");
            }
            stringBuilder.append(result.getList().get(i).getFullName());
          }
          switch (historyTokenDTO.getType()) {
            case ADD:
              formattedTokens.add(I18N.MESSAGES.contactListHistoryAdded(stringBuilder.toString()));
              break;
            case REMOVE:
              formattedTokens.add(I18N.MESSAGES.contactListHistoryRemoved(stringBuilder.toString()));
              break;
            case EDIT:
              formattedTokens.add(stringBuilder.toString());
              break;
            default:
              throw new IllegalStateException("Unknown ValueEventChangeType : " + historyTokenDTO.getType());
          }
          historyTokenText.setHistoryTokenValue(formattedTokens);
        }
      });
    }

    dispatchQueue.start();
    return historyTokenText;
  }
}
