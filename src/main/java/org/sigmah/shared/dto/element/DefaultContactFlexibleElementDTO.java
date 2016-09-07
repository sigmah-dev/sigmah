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

import com.google.gwt.dom.client.Style;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.widget.HistoryTokenText;
import org.sigmah.client.ui.widget.form.ButtonFileUploadField;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.ListComboBox;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.ImageProvider;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.command.GetContact;
import org.sigmah.shared.command.GetContacts;
import org.sigmah.shared.command.GetCountry;
import org.sigmah.shared.command.GetOrgUnit;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.history.HistoryTokenDTO;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.file.Cause;
import org.sigmah.shared.file.ProgressListener;
import org.sigmah.shared.util.ValueResultUtils;

import com.allen_sauer.gwt.log.client.Log;

public class DefaultContactFlexibleElementDTO extends AbstractDefaultFlexibleElementDTO {
  private static final long serialVersionUID = -1251850749619288873L;

  private static final String ENTITY_NAME = "element.DefaultContactFlexibleElement";

  private transient DefaultContactFlexibleElementContainer container;
  private transient ListStore<ContactDTO> contactsStore;
  private transient FormPanel formPanel;
  private transient ImageProvider imageProvider;

  @Override
  protected Component getComponent(ValueResult valueResult, boolean enabled) {
    if (currentContainerDTO instanceof DefaultContactFlexibleElementContainer) {
      container = (DefaultContactFlexibleElementContainer) currentContainerDTO;
    }

    boolean enabledAndUpdatable = enabled && getType().isUpdatable();
    if (valueResult != null && valueResult.isValueDefined()) {
      return getComponentWithValue(valueResult, enabledAndUpdatable);
    } else {
      return getComponent(enabledAndUpdatable);
    }
  }

  private Component getComponentWithValue(ValueResult valueResult, boolean enabled) {
    switch (getType()) {
      case COUNTRY:
        return buildCountryField(valueResult.getValueObject(), enabled);
      case CREATION_DATE:
        return buildCreationDateField(valueResult.getValueObject(), enabled);
      case DIRECT_MEMBERSHIP:
        return buildDirectMembershipField(valueResult.getValueObject(), enabled);
      case EMAIL_ADDRESS:
        return buildEmailField(valueResult.getValueObject(), enabled);
      case FAMILY_NAME:
        return buildFamilyNameField(valueResult.getValueObject(), enabled);
      case FIRST_NAME:
        return buildFirstNameField(valueResult.getValueObject(), enabled);
      case LOGIN:
        return buildLoginField(valueResult.getValueObject(), enabled);
      case MAIN_ORG_UNIT:
        return buildMainOrgUnitField(valueResult.getValueObject(), enabled);
      case ORGANIZATION_NAME:
        return buildOrganizationNameField(valueResult.getValueObject(), enabled);
      case PHONE_NUMBER:
        return buildPhoneNumberField(valueResult.getValueObject(), enabled);
      case PHOTO:
        return buildPhotoField(valueResult.getValueObject(), enabled);
      case POSTAL_ADDRESS:
        return buildPostalAddressField(valueResult.getValueObject(), enabled);
      case SECONDARY_ORG_UNITS:
        return buildSecondaryOrgUnitsField(valueResult.getValueObject(), enabled);
      case TOP_MEMBERSHIP:
        return buildTopMembershipField(valueResult.getValueObject());
      default:
        throw new IllegalStateException("Unknown DefaultContactFlexibleElementType : " + getType());
    }
  }

  private Component getComponent(boolean enabled) {
    switch (getType()) {
      case COUNTRY:
        return buildCountryField(container.getCountry(), enabled);
      case CREATION_DATE:
        return buildCreationDateField(container.getDateCreated(), enabled);
      case DIRECT_MEMBERSHIP:
        return buildDirectMembershipField(container.getParent(), enabled);
      case EMAIL_ADDRESS:
        return buildEmailField(container.getEmail(), enabled);
      case FAMILY_NAME:
        return buildFamilyNameField(container.getFamilyName(), enabled);
      case FIRST_NAME:
        return buildFirstNameField(container.getFirstname(), enabled);
      case LOGIN:
        return buildLoginField(container.getLogin(), enabled);
      case MAIN_ORG_UNIT:
        return buildMainOrgUnitField(container.getMainOrgUnit(), enabled);
      case ORGANIZATION_NAME:
        return buildOrganizationNameField(container.getOrganizationName(), enabled);
      case PHONE_NUMBER:
        return buildPhoneNumberField(container.getPhoneNumber(), enabled);
      case PHOTO:
        return buildPhotoField(container.getPhoto(), enabled);
      case POSTAL_ADDRESS:
        return buildPostalAddressField(container.getPostalAddress(), enabled);
      case SECONDARY_ORG_UNITS:
        return buildSecondaryOrgUnitsField(container.getSecondaryOrgUnits(), enabled);
      case TOP_MEMBERSHIP:
        return buildTopMembershipField(container.getRoot());
      default:
        throw new IllegalStateException("Unknown DefaultContactFlexibleElementType : " + getType());
    }
  }

  private Field<?> buildDirectMembershipField(ContactDTO directMembership, boolean enabled) {
    Field<?> field;

    if (enabled) {
      ensureContactsStore();

      ComboBox<ContactDTO> comboBox = new ComboBox<ContactDTO>();
      comboBox.setStore(contactsStore);
      comboBox.setDisplayField(ContactDTO.FULLNAME);
      comboBox.setValueField(ContactDTO.ID);
      comboBox.setTriggerAction(ComboBox.TriggerAction.ALL);
      comboBox.setEditable(true);
      comboBox.setAllowBlank(true);
      comboBox.setValue(directMembership);
      addContactSelectionChangedListener(comboBox);

      field = comboBox;
    } else {
      LabelField labelField = createLabelField();
      if (directMembership != null) {
        labelField.setValue(directMembership.getFullName());
      } else {
        labelField.setValue(EMPTY_VALUE);
      }

      field = labelField;
    }

    // Sets the field label.
    setLabel(I18N.CONSTANTS.contactDirectMembership());
    field.setFieldLabel(getLabel());

    return field;
  }

  private Field<?> buildDirectMembershipField(final String directMembershipId, boolean enabled) {
    Field<?> field;

    if (enabled) {
      ensureContactsStore();

      final ComboBox<ContactDTO> comboBox = new ComboBox<ContactDTO>();
      comboBox.setStore(contactsStore);
      comboBox.setDisplayField(ContactDTO.FULLNAME);
      comboBox.setValueField(ContactDTO.ID);
      comboBox.setTriggerAction(ComboBox.TriggerAction.ALL);
      comboBox.setEditable(true);
      comboBox.setAllowBlank(true);
      dispatch.execute(new GetContact(Integer.parseInt(directMembershipId), ContactDTO.Mode.BASIC_INFORMATION), new AsyncCallback<ContactDTO>() {
        @Override
        public void onFailure(Throwable caught) {
          Log.error("Error while getting contact " + directMembershipId + ".", caught);
        }

        @Override
        public void onSuccess(ContactDTO contactDTO) {
          comboBox.setValue(contactDTO);
          addContactSelectionChangedListener(comboBox);
        }
      });

      field = comboBox;
    } else {
      final LabelField labelField = createLabelField();
      dispatch.execute(new GetContact(Integer.parseInt(directMembershipId), ContactDTO.Mode.BASIC_INFORMATION), new AsyncCallback<ContactDTO>() {
        @Override
        public void onFailure(Throwable caught) {
          Log.error("Error while getting contact " + directMembershipId + ".", caught);
        }

        @Override
        public void onSuccess(ContactDTO contactDTO) {
          if (contactDTO != null) {
            labelField.setValue(contactDTO.getFullName());
          } else {
            labelField.setValue(EMPTY_VALUE);
          }
        }
      });

      field = labelField;
    }

    // Sets the field label.
    setLabel(I18N.CONSTANTS.contactTopMembership());
    field.setFieldLabel(getLabel());

    return field;
  }

  private Field<?> buildTopMembershipField(ContactDTO topMembership) {
    LabelField labelField = createLabelField();
    if (topMembership != null) {
      labelField.setValue(topMembership.getFullName());
    } else {
      labelField.setValue(EMPTY_VALUE);
    }

    // Sets the field label.
    setLabel(I18N.CONSTANTS.contactTopMembership());
    labelField.setFieldLabel(getLabel());

    return labelField;
  }

  private Field<?> buildTopMembershipField(final String topMembershipId) {
    final LabelField labelField = createLabelField();
    dispatch.execute(new GetContact(Integer.parseInt(topMembershipId), ContactDTO.Mode.BASIC_INFORMATION), new AsyncCallback<ContactDTO>() {
      @Override
      public void onFailure(Throwable caught) {
        Log.error("Error while getting contact " + topMembershipId + ".", caught);
      }

      @Override
      public void onSuccess(ContactDTO contactDTO) {
        if (contactDTO != null) {
          labelField.setValue(contactDTO.getFullName());
        } else {
          labelField.setValue(EMPTY_VALUE);
        }
      }
    });
    // Sets the field label.
    setLabel(I18N.CONSTANTS.contactTopMembership());
    labelField.setFieldLabel(getLabel());

    return labelField;
  }

  private Field<?> buildMainOrgUnitField(OrgUnitDTO orgUnitDTO, boolean enabled) {
    return buildOrgUnitField(I18N.CONSTANTS.contactMainOrgUnit(), orgUnitDTO, enabled);
  }

  private Field<?> buildMainOrgUnitField(String orgUnitId, boolean enabled) {
    return buildOrgUnitField(I18N.CONSTANTS.contactMainOrgUnit(), orgUnitId, enabled);
  }

  private Field<?> buildSecondaryOrgUnitsField(List<OrgUnitDTO> orgUnits, boolean enabled) {
    ensureOrgUnitStore();

    final ListComboBox comboBox = new ListComboBox(OrgUnitDTO.ID, OrgUnitDTO.COMPLETE_NAME);
    comboBox.setEnabled(enabled);
    comboBox.copyAvailableValueStore(orgUnitsStore);
    comboBox.getListStore().add(orgUnits);
    comboBox.initComponent();
    return Forms.adapter(I18N.CONSTANTS.contactSecondaryOrgUnits(), comboBox);
  }

  private Field<?> buildSecondaryOrgUnitsField(Set<Integer> orgUnitIds, boolean enabled) {
    ensureOrgUnitStore();

    final ListComboBox comboBox = new ListComboBox(OrgUnitDTO.ID, OrgUnitDTO.COMPLETE_NAME);
    comboBox.setEnabled(enabled);
    comboBox.copyAvailableValueStore(orgUnitsStore);
    comboBox.initComponent();

    for (Integer orgUnitId : orgUnitIds) {
      cache.getOrganizationCache().get(orgUnitId, new AsyncCallback<OrgUnitDTO>() {

        @Override
        public void onSuccess(final OrgUnitDTO orgUnitDTO) {
          comboBox.getListStore().add(orgUnitDTO);
        }

        @Override
        public void onFailure(final Throwable caught) {
          Log.error("Error while getting org units.", caught);
        }
      });
    }
    return Forms.adapter(I18N.CONSTANTS.contactSecondaryOrgUnits(), comboBox);
  }

  private Field<?> buildSecondaryOrgUnitsField(String serializedOrgUnits, boolean enabled) {
    HashSet<Integer> orgUnitIds = new HashSet<Integer>();
    for (String orgUnitId : serializedOrgUnits.split(",")) {
      orgUnitIds.add(Integer.parseInt(orgUnitId));
    }
    return buildSecondaryOrgUnitsField(orgUnitIds, enabled);
  }

  private Field<?> buildCreationDateField(Date date, boolean enabled) {
    return buildDateField(I18N.CONSTANTS.contactCreationDate(), date, enabled);
  }

  private Field<?> buildCreationDateField(String date, boolean enabled) {
    return buildDateField(I18N.CONSTANTS.contactCreationDate(), new Date(Long.parseLong(date)), enabled);
  }

  private Field<?> buildEmailField(String email, boolean enabled) {
    Field<?> emailField = buildTextField(I18N.CONSTANTS.contactEmailAddress(), email, 50, enabled, true);
    if (emailField instanceof TextField) {
      // Very basic email regexp as an email field shouldn't be too restrictive
      ((TextField) emailField).setValidator(new Validator() {
        @Override
        public String validate(Field<?> field, String value) {
          if (!RegExp.compile("^.+@.+\\..+$").test(value)) {
            return I18N.CONSTANTS.emailNotValidError();
          }
          return null;
        }
      });
      ((TextField) emailField).setRegex("^.+@.+\\..+$");
    }
    return emailField;
  }

  private Field<?> buildFamilyNameField(String name, boolean enabled) {
    return buildTextField(I18N.CONSTANTS.contactFamilyName(), name, 50, enabled, false);
  }

  private Field<?> buildFirstNameField(String name, boolean enabled) {
    return buildTextField(I18N.CONSTANTS.contactFirstName(), name, 50, enabled, true);
  }

  private Field<?> buildOrganizationNameField(String name, boolean enabled) {
    return buildTextField(I18N.CONSTANTS.contactOrganizationName(), name, 50, enabled, false);
  }

  private Field<?> buildLoginField(String login, boolean enabled) {
    return buildTextField(I18N.CONSTANTS.contactLogin(), login, 50, enabled, true);
  }

  private Field<?> buildPhoneNumberField(String phoneNumber, boolean enabled) {
    return buildTextField(I18N.CONSTANTS.contactPhoneNumber(), phoneNumber, 50, enabled, true);
  }

  private Field<?> buildPostalAddressField(String postalAddress, boolean enabled) {
    return buildTextField(I18N.CONSTANTS.contactPostalAddress(), postalAddress, 50, enabled, true);
  }

  private Field<?> buildPhotoField(String imageId, boolean enabled) {
    final Image image = new Image();
    image.setVisible(false);
    image.addStyleName("contact-details-photo");

    setLabel(I18N.CONSTANTS.contactPhoto());

    if (imageId != null && imageId.length() > 0 && imageProvider != null) {
      imageProvider.provideDataUrl(imageId, new AsyncCallback<String>() {
        @Override
        public void onFailure(Throwable caught) {
          // noop
        }

        @Override
        public void onSuccess(String url) {
          image.setVisible(true);
          image.setUrl(url);
          image.getElement().getStyle().setPaddingTop(10, Style.Unit.PX);
        }
      });
    }

    if (!enabled) {
      // Not really useful to display the image as it's already displayed in contact card
      return Forms.adapter(I18N.CONSTANTS.contactPhoto(), image);
    }

    final ButtonFileUploadField buttonUploadField = new ButtonFileUploadField();
    buttonUploadField.setButtonCaption(I18N.CONSTANTS.contactPhotoUpload());
    buttonUploadField.setName(FileUploadUtils.DOCUMENT_CONTENT);
    buttonUploadField.setAccept("image/*");

    FlowPanel panel = new FlowPanel();
    panel.add(buttonUploadField);
    panel.add(image);

    final AdapterField field = Forms.adapter(I18N.CONSTANTS.contactPhoto(), panel);
    field.setFireChangeEventOnSetValue(true);

    buttonUploadField.addListener(Events.OnChange, new Listener<DomEvent>() {

      @Override
      public void handleEvent(DomEvent event) {
        event.getEvent().stopPropagation();

        if (!transfertManager.canUpload()) {
          N10N.warn(I18N.CONSTANTS.flexibleElementFilesListUploadUnable());
          return;
        }

        buttonUploadField.mask();
        image.setVisible(false);

        // Submits the form.
        transfertManager.uploadAvatar(formPanel, createUploadProgressListener(buttonUploadField, image, field));
      }
    });

    return field;
  }

  @Override
  public boolean isCorrectRequiredValue(ValueResult result) {
    return false;
  }

  public DefaultContactFlexibleElementType getType() {
    return get("type");
  }

  public void setType(DefaultContactFlexibleElementType type) {
    set("type", type);
  }

  @Override
  public String getEntityName() {
    return ENTITY_NAME;
  }

  @Override
  public String getFormattedLabel() {
    return getLabel() != null ? getLabel() : DefaultContactFlexibleElementType.getName(getType());
  }

  private ProgressListener createUploadProgressListener(final ButtonFileUploadField uploadField, final Image image, final Field<Object> field) {
    return new ProgressListener() {

      @Override
      public void onProgress(double progress, double speed) {
      }

      @Override
      public void onFailure(Cause cause) {
        uploadField.unmask();

        // Displaying an error message.
        final StringBuilder errorMessageBuilder = new StringBuilder();
        errorMessageBuilder.append(I18N.CONSTANTS.flexibleElementFilesListUploadErrorDetails()).append("\n");

        switch (cause) {
          case FILE_TOO_LARGE:
            errorMessageBuilder.append(I18N.CONSTANTS.flexibleElementFilesListUploadErrorTooBig());
            break;
          default:
            errorMessageBuilder.append(I18N.CONSTANTS.flexibleElementFilesListUploadErrorEmpty());
            break;
        }

        N10N.warn(I18N.CONSTANTS.flexibleElementFilesListUploadError(), errorMessageBuilder.toString());
      }

      @Override
      public void onLoad(final String result) {
        // result = <pre>imageId</pre> => let's remove these annoying html tags around the image id
        final String imageId = result.replaceAll("<(\\/)?pre( style=\"[a-z ;:-]*\")?>", "");
        uploadField.unmask();
        handlerManager.fireEvent(new RequiredValueEvent(true, true));

        if (ClientUtils.isBlank(result)) {
          N10N.errorNotif(I18N.CONSTANTS.flexibleElementFilesListUploadError(), I18N.CONSTANTS.flexibleElementFilesListUploadErrorDetails());
          return;
        }

        // Displaying a notification of success
        N10N.validNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.flexibleElementFilesListUploadFileConfirm());

        imageProvider.provideDataUrl(imageId, new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable caught) {
            image.setUrl("");
            image.getElement().getStyle().clearPaddingTop();
          }

          @Override
          public void onSuccess(String url) {
            image.setVisible(true);
            image.setUrl(url);
            image.getElement().getStyle().setPaddingTop(10, Style.Unit.PX);

            field.setValue(imageId);

            fireEvents(imageId, true);
          }
        });
      }
    };
  }

  @Override
  protected void addOrgUnitSelectionChangedListener(ComboBox<OrgUnitDTO> comboBox) {
    comboBox.addSelectionChangedListener(new SelectionChangedListener<OrgUnitDTO>() {

      @Override
      public void selectionChanged(final SelectionChangedEvent<OrgUnitDTO> se) {
        String value = null;
        final boolean isValueOn;

        // Gets the selected choice.
        final OrgUnitDTO choice = se.getSelectedItem();

        // Checks if the choice isn't the default empty choice.
        isValueOn = choice != null && choice.getId() != null && choice.getId() != -1;

        if (choice != null) {
          value = String.valueOf(choice.getId());
        }

        if (value != null) {
          // Fires value change event.
          handlerManager.fireEvent(new ValueEvent(DefaultContactFlexibleElementDTO.this, value));
        }

        // Required element ?
        if (getValidates()) {
          handlerManager.fireEvent(new RequiredValueEvent(isValueOn));
        }
      }
    });
  }

  protected void addContactSelectionChangedListener(ComboBox<ContactDTO> comboBox) {
    comboBox.addSelectionChangedListener(new SelectionChangedListener<ContactDTO>() {

      @Override
      public void selectionChanged(final SelectionChangedEvent<ContactDTO> se) {
        String value = null;
        final boolean isValueOn;

        // Gets the selected choice.
        final ContactDTO choice = se.getSelectedItem();

        // Checks if the choice isn't the default empty choice.
        isValueOn = choice != null && choice.getId() != null && choice.getId() != -1;

        if (choice != null) {
          value = String.valueOf(choice.getId());
        }

        if (value != null) {
          // Fires value change event.
          handlerManager.fireEvent(new ValueEvent(DefaultContactFlexibleElementDTO.this, value));
        }

        // Required element ?
        if (getValidates()) {
          handlerManager.fireEvent(new RequiredValueEvent(isValueOn));
        }
      }
    });
  }

  protected void ensureContactsStore() {
    if (contactsStore != null) {
      return;
    }

    contactsStore = new ListStore<ContactDTO>();
    dispatch.execute(new GetContacts(ContactModelType.ORGANIZATION), new AsyncCallback<ListResult<ContactDTO>>() {
      @Override
      public void onFailure(Throwable caught) {
        Log.error("[getComponent] Error while getting users info.", caught);
      }

      @Override
      public void onSuccess(ListResult<ContactDTO> contacts) {
        contactsStore.add(contacts.getList());
      }
    });
  }

  public void setFormPanel(FormPanel formPanel) {
    this.formPanel = formPanel;
  }

  public void setImageProvider(ImageProvider imageProvider) {
    this.imageProvider = imageProvider;
  }

  @Override
  public Object renderHistoryToken(HistoryTokenListDTO historyTokenListDTO) {
    switch (getType()) {
      case FAMILY_NAME: // fall through
      case FIRST_NAME: // fall through
      case ORGANIZATION_NAME: // fall through
      case EMAIL_ADDRESS: // fall through
      case PHONE_NUMBER: // fall through
      case POSTAL_ADDRESS: // fall through
        return super.renderHistoryToken(historyTokenListDTO);
      case PHOTO:
        return renderPhotoHistory(historyTokenListDTO);
      case COUNTRY:
        return renderCountryHistory(historyTokenListDTO);
      case DIRECT_MEMBERSHIP:
        return renderContactHistory(historyTokenListDTO);
      case LOGIN: // fall through
      case MAIN_ORG_UNIT: // fall through
      case SECONDARY_ORG_UNITS: // fall through
      case CREATION_DATE: // fall through
      case TOP_MEMBERSHIP: // fall through
      default:
        throw new IllegalStateException("Unknown DefaultContactFlexibleElementType : " + getType());
    }
  }

  private HistoryTokenText renderCountryHistory(HistoryTokenListDTO historyTokenListDTO) {
    final HistoryTokenText historyTokenText = new HistoryTokenText();
    final List<String> formattedValues = new ArrayList<String>();
    for (HistoryTokenDTO historyTokenDTO : historyTokenListDTO.getTokens()) {
      String serializedValue = historyTokenDTO.getValue();
      if (serializedValue == null || serializedValue.isEmpty()) {
        continue;
      }

      dispatch.execute(new GetCountry(Integer.parseInt(serializedValue)), new CommandResultHandler<CountryDTO>() {
        @Override
        protected void onCommandSuccess(CountryDTO countryDTO) {
          formattedValues.add(countryDTO.getCompleteName());
          historyTokenText.setHistoryTokenValue(formattedValues);
        }
      });
    }
    return historyTokenText;
  }

  private Widget renderPhotoHistory(HistoryTokenListDTO historyTokenListDTO) {
    final FlowPanel flowPanel = new FlowPanel();
    for (HistoryTokenDTO historyTokenDTO : historyTokenListDTO.getTokens()) {
      String value = historyTokenDTO.getValue();
      if (value == null || value.isEmpty()) {
        continue;
      }

      final Image image = new Image();
      image.setHeight("100px");
      imageProvider.provideDataUrl(value, new SuccessCallback<String>() {
        @Override
        public void onSuccess(String dataUrl) {
          image.setUrl(dataUrl);
        }
      });
      flowPanel.add(image);
    }
    return flowPanel;
  }

  private HistoryTokenText renderContactHistory(HistoryTokenListDTO historyTokenListDTO) {
    final HistoryTokenText historyTokenText = new HistoryTokenText();
    final List<String> formattedValues = new ArrayList<String>();
    for (HistoryTokenDTO historyTokenDTO : historyTokenListDTO.getTokens()) {
      String serializedValue = historyTokenDTO.getValue();
      if (serializedValue == null || serializedValue.isEmpty()) {
        continue;
      }

      dispatch.execute(new GetContact(Integer.parseInt(serializedValue), ContactDTO.Mode.BASIC_INFORMATION), new CommandResultHandler<ContactDTO>() {
        @Override
        protected void onCommandSuccess(ContactDTO contactDTO) {
          formattedValues.add(contactDTO.getFullName());
          historyTokenText.setHistoryTokenValue(formattedValues);
        }
      });
    }
    return historyTokenText;
  }
}
