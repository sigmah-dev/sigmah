package org.sigmah.client.ui.widget.contact;
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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WidgetListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.shared.command.result.ContactDuplicatedProperty;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.element.event.ValueHandler;

public class DedupeContactDialog extends Window {
  private final boolean createContact;

  private Grid<ContactDTO> possibleDuplicatesGrid;
  private Grid<ContactDuplicatedProperty> duplicatedPropertiesGrid;
  private Button firstStepMainButton;
  private Button secondStepMainButton;
  private LayoutContainer secondStepContainer;
  private CardLayout cardLayout;

  private List<ContactDuplicatedProperty> selectedProperties = new ArrayList<ContactDuplicatedProperty>();

  private ContactDTO selectedContact;
  private SecondStepHandler secondStepHandler;

  public DedupeContactDialog(boolean createContact) {
    super();

    this.createContact = createContact;

    initComponent();
  }

  private void initComponent() {
    this.setPlain(true);
    this.setModal(true);
    this.setBlinkModal(true);
    this.setLayout(new FitLayout());
    this.setSize(650, 300);
    this.setHeadingHtml(I18N.CONSTANTS.dedupeContactWindowTitle());

    possibleDuplicatesGrid = generatePossibleDuplicatesGrid();

    firstStepMainButton = generateFirstStepMainButton();
    LayoutContainer firstStepButtonsContainer = Layouts.border();
    firstStepButtonsContainer.add(firstStepMainButton, Layouts.borderLayoutData(Style.LayoutRegion.EAST));

    LayoutContainer firstStepContainer = Layouts.border();
    firstStepContainer.setScrollMode(Style.Scroll.AUTOY);
    firstStepContainer.add(generateMessageLabel(), Layouts.borderLayoutData(Style.LayoutRegion.NORTH, 50f,
        Layouts.Margin.HALF_TOP, Layouts.Margin.HALF_RIGHT, Layouts.Margin.HALF_BOTTOM, Layouts.Margin.HALF_LEFT));

    firstStepContainer.add(possibleDuplicatesGrid, Layouts.borderLayoutData(Style.LayoutRegion.CENTER,
        Layouts.Margin.HALF_TOP, Layouts.Margin.HALF_BOTTOM));

    firstStepContainer.add(firstStepButtonsContainer, Layouts.borderLayoutData(Style.LayoutRegion.SOUTH, 20f,
        Layouts.Margin.HALF_TOP, Layouts.Margin.HALF_RIGHT, Layouts.Margin.BOTTOM, Layouts.Margin.DOUBLE_LEFT));

    duplicatedPropertiesGrid = generateDuplicatedPropertiesGrid();

    secondStepMainButton = generateSecondStepMainButton();
    LayoutContainer secondStepButtonsContainer = Layouts.border();
    secondStepButtonsContainer.add(secondStepMainButton, Layouts.borderLayoutData(Style.LayoutRegion.EAST));

    secondStepContainer = Layouts.border();
    secondStepContainer.setScrollMode(Style.Scroll.AUTOY);

    secondStepContainer.add(duplicatedPropertiesGrid, Layouts.borderLayoutData(Style.LayoutRegion.CENTER,
        Layouts.Margin.HALF_TOP, Layouts.Margin.HALF_BOTTOM));

    secondStepContainer.add(secondStepButtonsContainer, Layouts.borderLayoutData(Style.LayoutRegion.SOUTH, 20f,
        Layouts.Margin.HALF_TOP, Layouts.Margin.HALF_RIGHT, Layouts.Margin.BOTTOM, Layouts.Margin.DOUBLE_LEFT));

    cardLayout = new CardLayout();
    LayoutContainer mainContainer = new LayoutContainer(cardLayout);
    mainContainer.add(firstStepContainer);
    mainContainer.add(secondStepContainer);
    cardLayout.setActiveItem(firstStepContainer);
    add(mainContainer);
  }

  private Label generateMessageLabel() {
    if (createContact) {
      return new Label(I18N.CONSTANTS.dedupeContactCreateMessage());
    }
    return new Label(I18N.CONSTANTS.dedupeContactUpdateMessage());
  }

  private Button generateFirstStepMainButton() {
    Button button;
    if (createContact) {
      button = new Button(I18N.CONSTANTS.dedupeContactCreateNewButton());
    } else {
      button = new Button(I18N.CONSTANTS.dedupeContactUpdateIndependently());
    }
    return button;
  }

  private Button generateSecondStepMainButton() {
    Button button = new Button(I18N.CONSTANTS.dedupeContactUpdateButton());
    button.addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        secondStepHandler.handleDedupeContact(selectedContact.getId(), selectedProperties);
      }
    });
    return button;
  }

  private Grid<ContactDTO> generatePossibleDuplicatesGrid() {
    ColumnConfig nameColumn = new ColumnConfig(ContactDTO.FULLNAME, I18N.CONSTANTS.fullName(), 250);
    ColumnConfig emailColumn = new ColumnConfig(ContactDTO.EMAIL, I18N.CONSTANTS.email(), 250);
    ColumnConfig actionsColumn = new ColumnConfig();
    actionsColumn.setWidth(100);
    actionsColumn.setRenderer(new GridCellRenderer<ContactDTO>() {
      @Override
      public Object render(final ContactDTO contact, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
        Button button = Forms.button(I18N.CONSTANTS.dedupeContactUpdateButton());
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
          @Override
          public void componentSelected(ButtonEvent ce) {
            cardLayout.setActiveItem(secondStepContainer);
            secondStepHandler.initialize(contact.getId(), duplicatedPropertiesGrid.getStore());
            selectedContact = contact;
          }
        });

        return button;
      }
    });

    ColumnModel columnModel = new ColumnModel(Arrays.asList(nameColumn, emailColumn, actionsColumn));
    return new Grid<ContactDTO>(new ListStore<ContactDTO>(), columnModel);
  }

  private Grid<ContactDuplicatedProperty> generateDuplicatedPropertiesGrid() {
    ColumnConfig selectColumn = new ColumnConfig();
    selectColumn.setWidth(20);
    selectColumn.setRenderer(new GridCellRenderer<ContactDuplicatedProperty>() {
      @Override
      public Object render(final ContactDuplicatedProperty contactDuplicatedProperty, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
        final CheckBox checkbox = Forms.checkbox(null);
        checkbox.addHandler(new ValueChangeHandler<Boolean>() {
          @Override
          public void onValueChange(ValueChangeEvent<Boolean> event) {
            if (event.getValue()) {
              selectedProperties.add(contactDuplicatedProperty);
            } else {
              selectedProperties.remove(contactDuplicatedProperty);
            }
          }
        }, ValueChangeEvent.getType());
        checkbox.addListener(Events.OnClick, new Listener<FieldEvent>() {

          @Override
          public void handleEvent(FieldEvent event) {
            if (checkbox.getValue()) {
              selectedProperties.add(contactDuplicatedProperty);
            } else {
              selectedProperties.remove(contactDuplicatedProperty);
            }
          }
        });
        return checkbox;
      }
    });

    ColumnConfig propertyNameColumn = new ColumnConfig(ContactDuplicatedProperty.PROPERTY_LABEL, I18N.CONSTANTS.dedupeContactPropertyNameHeader(), 150);

    ColumnConfig oldValueColumn = new ColumnConfig();
    oldValueColumn.setHeaderText(I18N.CONSTANTS.dedupeContactOldValue());
    oldValueColumn.setWidth(200);
    oldValueColumn.setRenderer(new GridCellRenderer<ContactDuplicatedProperty>() {
      @Override
      public Object render(ContactDuplicatedProperty contactDuplicatedProperty, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
        return renderProperty(contactDuplicatedProperty.getValueType(), contactDuplicatedProperty.getFormattedOldValue(), contactDuplicatedProperty.getSerializedOldValue());
      }
    });

    ColumnConfig newValueColumn = new ColumnConfig();
    newValueColumn.setHeaderText(I18N.CONSTANTS.dedupeContactNewValue());
    newValueColumn.setWidth(200);
    newValueColumn.setRenderer(new GridCellRenderer<ContactDuplicatedProperty>() {
      @Override
      public Object render(ContactDuplicatedProperty contactDuplicatedProperty, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
        return renderProperty(contactDuplicatedProperty.getValueType(), contactDuplicatedProperty.getFormattedNewValue(), contactDuplicatedProperty.getSerializedNewValue());
      }
    });

    ColumnModel columnModel = new ColumnModel(Arrays.asList(selectColumn, propertyNameColumn, oldValueColumn, newValueColumn));
    return new Grid<ContactDuplicatedProperty>(new ListStore<ContactDuplicatedProperty>(), columnModel);
  }

  public Grid<ContactDTO> getPossibleDuplicatesGrid() {
    return possibleDuplicatesGrid;
  }

  public Grid<ContactDuplicatedProperty> getDuplicatedPropertiesGrid() {
    return duplicatedPropertiesGrid;
  }

  public Button getFirstStepMainButton() {
    return firstStepMainButton;
  }

  public Button getSecondStepMainButton() {
    return secondStepMainButton;
  }

  public void setSecondStepHandler(SecondStepHandler secondStepHandler) {
    this.secondStepHandler = secondStepHandler;
  }

  private Object renderProperty(ContactDuplicatedProperty.ValueType valueType, String formattedValue, String serializedValue) {
    if (serializedValue == null) {
      return "";
    }

    switch (valueType) {
      case STRING:
        return formattedValue;
      case IMAGE:
        Image image = new Image();
        secondStepHandler.downloadImage(serializedValue, image);
        image.getElement().getStyle().setHeight(50, com.google.gwt.dom.client.Style.Unit.PX);
        return image;
      default:
        throw new IllegalStateException("Unknown ValueType : " + valueType);
    }
  }

  public interface SecondStepHandler {
    void initialize(Integer contactId, ListStore<ContactDuplicatedProperty> propertiesStore);

    void downloadImage(String id, Image image);

    void handleDedupeContact(Integer targetedContactId, List<ContactDuplicatedProperty> selectedProperties);
  }
}
