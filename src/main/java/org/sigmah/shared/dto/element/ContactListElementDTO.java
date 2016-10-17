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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchQueue;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.widget.HistoryTokenText;
import org.sigmah.client.ui.widget.contact.DedupeContactDialog;
import org.sigmah.client.ui.widget.form.ContactListComboBox;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.ListComboBox;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.command.CheckContactDuplication;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.DedupeContact;
import org.sigmah.shared.command.GetContactDuplicatedProperties;
import org.sigmah.shared.command.GetContacts;
import org.sigmah.shared.command.result.ContactDuplicatedProperty;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.ContactModelDTO;
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
  public static final String IS_MEMBER = "isMember";

  @Override
  @SuppressWarnings("unchecked")
  protected Component getComponent(final ValueResult valueResult, final boolean enabled) {
    final ContactListComboBox listComboBox = new ContactListComboBox(getLimit(), getAllowedType(), getAllowedModelIds(), dispatch);
    listComboBox.setEnabled(enabled);
    listComboBox.setChangeHandler(new ContactListComboBox.ChangeHandler() {
      @Override
      public void handleChange(List<ContactDTO> contacts, ValueEventChangeType changeType) {
        fireEvents(serializeValue(contacts), changeType);
      }
    });
    listComboBox.setCreateContactHandler(new ContactListComboBox.CreateContactHandler() {
      @Override
      public void handleContactCreation(final ContactModelDTO contactModelDTO, final String email, final String firstName, final String familyName, final String organizationName, final OrgUnitDTO mainOrgUnit, final List<OrgUnitDTO> secondaryOrgUnits) {
        CheckContactDuplication checkContactDuplication;
        if (contactModelDTO.getType() == ContactModelType.INDIVIDUAL) {
          checkContactDuplication = new CheckContactDuplication(null, email, familyName, firstName);
        } else {
          checkContactDuplication = new CheckContactDuplication(null, email, familyName, null);
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
              createEntity(properties, listComboBox);
              return;
            }

            final DedupeContactDialog dedupeContactDialog = new DedupeContactDialog(true);
            dedupeContactDialog.getPossibleDuplicatesGrid().getStore().add(result.getList());
            dedupeContactDialog.getFirstStepMainButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
              @Override
              public void componentSelected(ButtonEvent ce) {
                createEntity(properties, listComboBox);
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
                    eventBus.navigateRequest(Page.CONTACT_DASHBOARD.requestWith(RequestParameter.ID, targetedContactId));
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
    });

    listComboBox.initComponent();

    // TODO: Filter contacts following user choice
    dispatch.execute(new GetContacts(getAllowedType(), getAllowedModelIds()), new AsyncCallback<ListResult<ContactDTO>>() {
      @Override
      public void onFailure(Throwable caught) {
        Log.error("Error while trying to get contacts for a contact list element.", caught);
      }

      @Override
      public void onSuccess(ListResult<ContactDTO> result) {
        listComboBox.getAvailableValuesStore().add(result.getList());

        Set<Integer> contactIds = parseValue(valueResult);
        List<ContactDTO> contacts = new ArrayList<ContactDTO>();
        for (ContactDTO contactDTO : result.getList()) {
          if (contactIds.contains(contactDTO.getId())) {
            contacts.add(contactDTO);
          }
        }
        listComboBox.initListStore(contacts);
      }
    });

    return Forms.adapter(getLabel(), listComboBox);
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

  public void setMember(boolean isMember) {
    set(IS_MEMBER, isMember);
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

  private void createEntity(HashMap<String, Object> properties, final ListComboBox<ContactDTO> listComboBox) {
    dispatch.execute(new CreateEntity(ContactDTO.ENTITY_NAME, properties), new AsyncCallback<CreateResult>() {
      @Override
      public void onFailure(Throwable caught) {
        Log.error("Error while creating a new Contact from contact creation dialog.");
      }

      @Override
      public void onSuccess(CreateResult result) {
        listComboBox.getListStore().add((ContactDTO) result.getEntity());
      }
    });
  }

  private HashMap<String, Object> buildPropertyMap(ContactModelDTO contactModelDTO, String email, String firstName, String familyName, String organizationName, OrgUnitDTO mainOrgUnit, List<OrgUnitDTO> secondaryOrgUnits) {
    HashMap<String, Object> properties = new HashMap<String, Object>();
    properties.put(ContactDTO.CONTACT_MODEL, contactModelDTO.getId());
    properties.put(ContactDTO.EMAIL, email);
    properties.put(ContactDTO.FIRSTNAME, contactModelDTO.getType() == ContactModelType.INDIVIDUAL ? firstName : null);
    properties.put(ContactDTO.NAME, contactModelDTO.getType() == ContactModelType.INDIVIDUAL ? familyName : organizationName);
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

  private void fireEvents(Set<Integer> value, ValueEventChangeType changeType) {
    handlerManager.fireEvent(new ValueEvent(ContactListElementDTO.this, value, changeType));
  }
}
