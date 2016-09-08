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

import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DatePickerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;

import java.util.Date;
import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.command.GetCountries;
import org.sigmah.shared.command.GetCountry;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.allen_sauer.gwt.log.client.Log;

public abstract class AbstractDefaultFlexibleElementDTO extends FlexibleElementDTO {
  protected static final String EMPTY_VALUE = "-";

  protected transient ListStore<CountryDTO> countriesStore;
  protected transient ListStore<OrgUnitDTO> orgUnitsStore;

  protected void fireEvents(String value, boolean isValueOn) {

    Log.debug("raw Value is : " + value + "  isValueOn is :" + isValueOn);

    handlerManager.fireEvent(new ValueEvent(this, value));

    // Required element ?
    if (getValidates()) {
      handlerManager.fireEvent(new RequiredValueEvent(isValueOn));
    }
  }

  /**
   * Create a text field to represent a default flexible element.
   *
   * @param length     The max length of the field.
   * @param allowBlank If the field allow blank value.
   * @return The text field.
   */
  protected TextField<String> createStringField(final int length, final boolean allowBlank) {

    final TextField<String> textField = new TextField<String>();
    textField.setAllowBlank(allowBlank);

    // Sets the max length.
    textField.setMaxLength(length);

    // Adds the listeners.
    textField.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

      @Override
      public void handleEvent(BaseEvent be) {

        String rawValue = textField.getValue();

        if (rawValue == null) {
          rawValue = "";
        }

        // The value is valid if it contains at least one non-blank
        // character.
        final boolean isValueOn = !rawValue.trim().equals("") && !(rawValue.length() > length);

        if (!(!allowBlank && !isValueOn)) {
          fireEvents(rawValue, isValueOn);
        }
      }
    });

    return textField;
  }

  /**
   * Create a date field to represent a default flexible element.
   *
   * @param allowBlank If the field allow blank value.
   * @return The date field.
   */
  protected DateField createDateField(final boolean allowBlank) {

    final DateTimeFormat dateFormat = DateUtils.DATE_SHORT;

    // Creates a date field which manages date picker selections and
    // manual selections.
    final DateField dateField = new DateField();
    dateField.getPropertyEditor().setFormat(dateFormat);
    dateField.setEditable(allowBlank);
    dateField.setAllowBlank(allowBlank);
    preferredWidth = FlexibleElementDTO.NUMBER_FIELD_WIDTH;

    // Adds the listeners.

    dateField.getDatePicker().addListener(Events.Select, new Listener<DatePickerEvent>() {

      @Override
      public void handleEvent(DatePickerEvent be) {

        // The date is saved as a timestamp.
        final String rawValue = String.valueOf(be.getDate().getTime());
        // The date picker always returns a valid date.
        final boolean isValueOn = true;

        fireEvents(rawValue, isValueOn);
      }
    });

    dateField.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

      @Override
      public void handleEvent(BaseEvent be) {

        final Date date = dateField.getValue();

        // The date is invalid, fires only a required event to
        // invalidate some previously valid date.
        if (date == null) {

          // Required element ?
          if (getValidates()) {
            handlerManager.fireEvent(new RequiredValueEvent(false));
          }

          if (allowBlank) {
            fireEvents("", false);
          }

          return;
        }

        // The date is saved as a timestamp.
        final String rawValue = String.valueOf(date.getTime());
        // The date is valid here.
        final boolean isValueOn = true;

        if (!(!allowBlank && !isValueOn)) {
          fireEvents(rawValue, isValueOn);
        }
      }
    });

    return dateField;
  }

  /**
   * Create a number field to represent a default flexible element.
   *
   * @param allowBlank If the field allow blank value.
   * @return The number field.
   */
  protected NumberField createNumberField(final boolean allowBlank) {

    final NumberField numberField = new NumberField();
    numberField.setAllowDecimals(true);
    numberField.setAllowNegative(false);
    numberField.setAllowBlank(allowBlank);
    preferredWidth = FlexibleElementDTO.NUMBER_FIELD_WIDTH;

    // Decimal format
    final NumberFormat format = NumberFormat.getDecimalFormat();
    numberField.setFormat(format);

    // Sets the min value.
    final Number minValue = 0.0;
    numberField.setMinValue(minValue);

    return numberField;
  }

  /**
   * Create a label field to represent a default flexible element.
   *
   * @return The label field.
   */
  protected LabelField createLabelField() {

    final LabelField labelField = new LabelField();
    labelField.setLabelSeparator(":");

    return labelField;
  }

  /**
   * Create a label field and sets its value.
   *
   * @param value Value to set.
   * @return The label field.
   */
  protected LabelField createLabelField(String value) {
    final LabelField labelField = createLabelField();
    labelField.setValue(value);
    return labelField;
  }

  /**
   * Creates a text field. <br/>
   * This method is shared between the code and the title fields.
   *
   * @param label   Label of the field.
   * @param value   Current value.
   * @param size    Maximum number of characters allowed.
   * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
   * @return A new text field.
   */
  protected Field<?> buildTextField(String label, String value, int size, boolean enabled, boolean allowBlank) {
    final Field<?> field;

    // Builds the field and sets its value.
    if (enabled) {
      final TextField<String> textField = createStringField(size, allowBlank);
      textField.setValue(value);
      field = textField;

    } else {
      field = createLabelField(value);
    }

    // Sets the field label.
    setLabel(label);
    field.setFieldLabel(getLabel());

    return field;
  }

  protected Field<?> buildParagraphField(final String label, final String value, final int size, final boolean enabled, boolean allowBlank) {

    // Builds the field and sets its value.
    final TextField<String> textArea = new TextArea();
    textArea.addStyleName("flexibility-textarea");
    textArea.setAllowBlank(allowBlank);

    // Sets the max length.
      textArea.setMaxLength(size);
      textArea.setToolTip(I18N.MESSAGES.flexibleElementTextAreaTextLength(String.valueOf(size)));

    // Adds the listeners.
    textArea.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

      @Override
      public void handleEvent(BaseEvent be) {

        String rawValue = textArea.getValue();

        if (rawValue == null) {
          rawValue = "";
        }

        // The value is valid if it contains at least one
        // non-blank character.
        final boolean isValueOn = !rawValue.trim().equals("") && !(rawValue.length() > size);

        fireEvents(rawValue, isValueOn);
      }
    });

    // Sets the value to the field.
    if (value != null) {
      textArea.setValue(value);
    }

    setLabel(label);
    textArea.setFieldLabel(getLabel());
    textArea.setEnabled(enabled);

    return textArea;
  }

  /**
   * Creates a date field. <br/>
   * This method is shared between the start date and the end date fields.
   *
   * @param label   Label of the field.
   * @param value   Current value.
   * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
   * @return A new date field.
   */
  protected Field<?> buildDateField(String label, Date value, boolean enabled) {
    final Field<?> field;

    // Builds the field and sets its value.
    if (enabled) {
      final DateField dateField = createDateField(true);
      dateField.setValue(value);
      field = dateField;

    } else {
      final LabelField labelField = createLabelField();
      if (value != null) {
        labelField.setValue(DateUtils.DATE_SHORT.format(value));
      } else {
        labelField.setValue(EMPTY_VALUE);
      }
      field = labelField;
    }

    // Sets the field label.
    setLabel(label);
    field.setFieldLabel(getLabel());

    return field;
  }

  protected Component buildCountryField(String country, boolean enabled) {
    final Field<?> field = buildCountryField((CountryDTO) null, enabled);

    final int countryId = Integer.parseInt(country);

    dispatch.execute(new GetCountry(countryId), new CommandResultHandler<CountryDTO>() {

      @Override
      public void onCommandFailure(final Throwable caught) {
      }

      @Override
      public void onCommandSuccess(final CountryDTO result) {
        // BUGFIX #694: Disable events on first set.
        field.enableEvents(false);

        if (field instanceof ComboBox) {
          ((ComboBox<CountryDTO>) field).setValue(result);

        } else if (field instanceof LabelField) {
          ((LabelField) field).setValue(result.getName());
        }

        field.enableEvents(true);
      }

    });

    return field;
  }

  protected Field<?> buildCountryField(CountryDTO country, boolean enabled) {
    final Field<?> field;

    if (enabled) {
      final ComboBox<CountryDTO> comboBox = new ComboBox<CountryDTO>();
      comboBox.setEmptyText(I18N.CONSTANTS.flexibleElementDefaultSelectCountry());

      ensureCountryStore();

      comboBox.setStore(countriesStore);
      comboBox.setDisplayField(CountryDTO.NAME);
      comboBox.setValueField(CountryDTO.ID);
      comboBox.setTriggerAction(ComboBox.TriggerAction.ALL);
      comboBox.setEditable(true);
      comboBox.setAllowBlank(true);

      // Listens to the selection changes.
      comboBox.addSelectionChangedListener(new SelectionChangedListener<CountryDTO>() {

        @Override
        public void selectionChanged(SelectionChangedEvent<CountryDTO> se) {

          String value = null;
          final boolean isValueOn;

          // Gets the selected choice.
          final CountryDTO choice = se.getSelectedItem();

          // Checks if the choice isn't the default empty choice.
          isValueOn = choice != null && choice.getId() != null && choice.getId() != -1;

          if (choice != null) {
            value = String.valueOf(choice.getId());
          }

          if (value != null) {
            // Fires value change event.
            handlerManager.fireEvent(new ValueEvent(AbstractDefaultFlexibleElementDTO.this, value));
          }

          // Required element ?
          if (getValidates()) {
            handlerManager.fireEvent(new RequiredValueEvent(isValueOn));
          }
        }
      });

      if (country != null) {
        comboBox.setValue(country);
      }

      field = comboBox;

    } else /* not enabled */ {

      final LabelField labelField = createLabelField();

      if (country == null) {
        labelField.setValue(EMPTY_VALUE);
      } else {
        labelField.setValue(country.getName());
      }

      field = labelField;
    }

    // Sets the field label.
    setLabel(I18N.CONSTANTS.projectCountry());
    field.setFieldLabel(getLabel());

    return field;
  }

  /**
   * Creates the organization unit field.
   *
   * @param orgUnitId ID of the organization unit.
   * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
   * @return The organization unit field.
   */
  protected Field<?> buildOrgUnitField(String label, String orgUnitId, boolean enabled) {
    return buildOrgUnitField(label, Integer.parseInt(orgUnitId), enabled);
  }

  /**
   * Creates the organization unit field.
   *
   * @param label Label of the field
   * @param orgUnitId ID of the organization unit.
   * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
   * @return The organization unit field.
   */
  protected Field<?> buildOrgUnitField(String label, Integer orgUnitId, boolean enabled) {
    final Field<?> field;

    if (enabled) {

      final ComboBox<OrgUnitDTO> comboBox = new ComboBox<OrgUnitDTO>();

      ensureOrgUnitStore();

      comboBox.setStore(orgUnitsStore);
      comboBox.setDisplayField(OrgUnitDTO.COMPLETE_NAME);
      comboBox.setValueField(OrgUnitDTO.ID);
      comboBox.setTriggerAction(ComboBox.TriggerAction.ALL);
      comboBox.setEditable(true);
      comboBox.setAllowBlank(true);

      // BUGFIX #694 : SelectionChangedEvent listener is added AFTER
      // setting the initial value to avoid sending a
      // SelectionChangedEvent during view initialization.

      // Loading the current value from the cache.
      cache.getOrganizationCache().get(orgUnitId, new AsyncCallback<OrgUnitDTO>() {

        @Override
        public void onFailure(final Throwable caught) {
          // Not found.

          // Listens to the selection changes.
          addOrgUnitSelectionChangedListener(comboBox);
        }

        @Override
        public void onSuccess(final OrgUnitDTO result) {
          comboBox.setValue(result);

          // Listens to the selection changes.
          addOrgUnitSelectionChangedListener(comboBox);
        }

      });

      field = comboBox;

    } else {
      // Builds the field and sets its value.
      final LabelField labelField = createLabelField();

      cache.getOrganizationCache().get(orgUnitId, new AsyncCallback<OrgUnitDTO>() {

        @Override
        public void onSuccess(final OrgUnitDTO result) {
          // BUGFIX: Issue #718
          if(result != null) {
            labelField.setValue(result.getName() + " - " + result.getFullName());
          } else {
            labelField.setValue(EMPTY_VALUE);
          }
        }

        @Override
        public void onFailure(final Throwable caught) {
          labelField.setValue(EMPTY_VALUE);
        }
      });

      field = labelField;
    }

    // Sets the field label.
    setLabel(label);
    field.setFieldLabel(getLabel());

    return field;
  }

  /**
   * Creates the organization unit field.
   *
   * @param orgUnit Organization unit.
   * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
   * @return The organization unit field.
   */
  protected Field<?> buildOrgUnitField(String label, OrgUnitDTO orgUnit, boolean enabled) {
    final Field<?> field;

    if (enabled) {

      final ComboBox<OrgUnitDTO> comboBox = new ComboBox<OrgUnitDTO>();

      ensureOrgUnitStore();

      comboBox.setStore(orgUnitsStore);
      comboBox.setDisplayField(OrgUnitDTO.COMPLETE_NAME);
      comboBox.setValueField(OrgUnitDTO.ID);
      comboBox.setTriggerAction(ComboBox.TriggerAction.ALL);
      comboBox.setEditable(true);
      comboBox.setAllowBlank(true);

      // BUGFIX #694 : SelectionChangedEvent listener is added AFTER
      // setting the initial value to avoid sending a
      // SelectionChangedEvent during view initialization.
      comboBox.setValue(orgUnit);

      // Listens to the selection changes.
      addOrgUnitSelectionChangedListener(comboBox);

      field = comboBox;

    } else {
      // Builds the field and sets its value.
      final LabelField labelField = createLabelField();
      if(orgUnit != null) {
        labelField.setValue(orgUnit.getName() + " - " + orgUnit.getFullName());
      } else {
        labelField.setValue(EMPTY_VALUE);
      }

      field = labelField;
    }

    // Sets the field label.
    setLabel(label);
    field.setFieldLabel(getLabel());

    return field;
  }

  protected String formatCountry(String value) {
    if (cache != null) {
      try {
        final CountryDTO c = cache.getCountryCache().get(Integer.valueOf(value));
        if (c != null) {
          return c.getName();
        } else {
          return '#' + value;
        }
      } catch (NumberFormatException e) {
        return "";
      }
    } else {
      return '#' + value;
    }
  }

  protected String formatDate(String value) {
    try {
      final long time = Long.parseLong(value);
      final Date date = new Date(time);

      // Using a shared instance to allow parsing from client and server side.
      final com.google.gwt.i18n.shared.DateTimeFormat formatter = DateUtils.SHARED_DATE_SHORT;
      return formatter.format(date);

    } catch (NumberFormatException e) {
      return "";
    }
  }

  protected String formatText(String value) {
    return value.replace("\n", "<br>");
  }

  protected String formatOrgUnit(String value) {
    if (cache != null) {
      try {
        final OrgUnitDTO o = cache.getOrganizationCache().get(Integer.valueOf(value));
        if (o != null) {
          return o.getName() + " - " + o.getFullName();
        } else {
          return '#' + value;
        }
      } catch(NumberFormatException e) {
        return "";
      }
    } else {
      return '#' + value;
    }
  }

  /**
   * Creates and populates the shared country store if needed.
   */
  protected void ensureCountryStore() {
    if (countriesStore == null) {
      countriesStore = new ListStore<CountryDTO>();
    }

    // if country store is empty
    if (countriesStore.getCount() == 0) {

      if (cache != null) {
        cache.getCountryCache().get(new AsyncCallback<List<CountryDTO>>() {

          @Override
          public void onFailure(Throwable e) {
            Log.error("[getComponent] Error while getting countries list.", e);
          }

          @Override
          public void onSuccess(List<CountryDTO> result) {
            // Fills the store.
            countriesStore.add(result);
          }
        });

      } else /* cache is null */ {
        dispatch.execute(new GetCountries(CountryDTO.Mode.BASE), new CommandResultHandler<ListResult<CountryDTO>>() {

          @Override
          protected void onCommandSuccess(final ListResult<CountryDTO> result) {
            // Fills the store.
            countriesStore.add(result.getData());
          }

          @Override
          protected void onCommandFailure(final Throwable caught) {
            Log.error("[getComponent] Error while getting countries list.", caught);
          }
        });
      }
    }
  }


  /**
   * Creates and populates the shared org unit store if needed.
   */
  protected void ensureOrgUnitStore() {
    if (orgUnitsStore == null) {
      orgUnitsStore = new ListStore<OrgUnitDTO>();
    }

    if(orgUnitsStore.getCount() == 0) {
      cache.getOrganizationCache().get(new AsyncCallback<OrgUnitDTO>() {

        @Override
        public void onFailure(Throwable e) {
          Log.error("[getComponent] Error while getting users info.", e);
        }

        @Override
        public void onSuccess(OrgUnitDTO result) {
          // Fills the store.
          recursiveFillOrgUnitsList(result);
        }
      });
    }
  }

  /**
   * Fills recursively the org unit store from the given root org unit.
   *
   * @param root
   *          The root org unit.
   */
  private void recursiveFillOrgUnitsList(OrgUnitDTO root) {

    if (root.isCanContainProjects()) {
      orgUnitsStore.add(root);
    }

    for (final OrgUnitDTO child : root.getChildrenOrgUnits()) {
      recursiveFillOrgUnitsList(child);
    }
  }

  /**
   * Adds the selection changed listener to the given orgunit combobox.
   *
   * @param comboBox Combo box to configure.
   */
  protected abstract void addOrgUnitSelectionChangedListener(final ComboBox<OrgUnitDTO> comboBox);
}
