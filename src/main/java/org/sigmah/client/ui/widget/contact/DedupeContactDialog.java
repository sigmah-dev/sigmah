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

import com.google.gwt.user.client.ui.Label;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import java.util.Arrays;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.shared.dto.ContactDTO;

public class DedupeContactDialog extends Window {
  private final boolean createContact;

  private Grid<ContactDTO> possibleDuplicatesGrid;
  private Button firstStepMainButton;

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

    // TODO: Create second step container

    CardLayout cardLayout = new CardLayout();
    LayoutContainer mainContainer = new LayoutContainer(cardLayout);
    mainContainer.add(firstStepContainer);
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
    if (createContact) {
      return new Button(I18N.CONSTANTS.dedupeContactCreateNewButton());
    }
    return new Button(I18N.CONSTANTS.dedupeContactUpdateIndependently());
  }

  private Grid<ContactDTO> generatePossibleDuplicatesGrid() {
    ColumnConfig nameColumn = new ColumnConfig(ContactDTO.FULLNAME, I18N.CONSTANTS.fullName(), 250);
    ColumnConfig emailColumn = new ColumnConfig(ContactDTO.EMAIL, I18N.CONSTANTS.email(), 250);
    ColumnConfig actionsColumn = new ColumnConfig();
    actionsColumn.setWidth(100);
    actionsColumn.setRenderer(new GridCellRenderer() {
      @Override
      public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
        Button button = Forms.button(I18N.CONSTANTS.dedupeContactUpdateButton());
        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
          @Override
          public void componentSelected(ButtonEvent ce) {
            // TODO
          }
        });

        return button;
      }
    });

    ColumnModel columnModel = new ColumnModel(Arrays.asList(nameColumn, emailColumn, actionsColumn));
    return new Grid<ContactDTO>(new ListStore<ContactDTO>(), columnModel);
  }

  public Grid<ContactDTO> getPossibleDuplicatesGrid() {
    return possibleDuplicatesGrid;
  }

  public Button getFirstStepMainButton() {
    return firstStepMainButton;
  }
}
