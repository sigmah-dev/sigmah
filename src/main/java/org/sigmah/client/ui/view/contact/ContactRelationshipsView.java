package org.sigmah.client.ui.view.contact;
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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.contact.ContactRelationshipsPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.command.result.ContactRelationship;
import org.sigmah.shared.dto.ContactDTO;

public class ContactRelationshipsView extends AbstractView implements ContactRelationshipsPresenter.View {
  private static final int RESIZE_DELAY = 200;
  private static final int BUTTONS_PANEL_HEIGHT = 50;
  private static final int PADDING = 10;

  private ContentPanel container;
  private ToolBar toolBar;
  private Button exportButton;
  private Grid<ContactRelationship> grid;
  private LayoutContainer buttonsContainer;
  private ToggleButton inboundToggleButton;
  private ToggleButton outboundToggleButton;

  @Override
  public void initialize() {
    container = Panels.content(null, false, Layouts.vBoxLayout(VBoxLayout.VBoxLayoutAlign.STRETCH, new Layouts.LayoutOptions(new Padding(10))), "x-border-layout-ct");
    container.setScrollMode(Style.Scroll.AUTOY);
    add(container);

    exportButton = Forms.button(I18N.CONSTANTS.export(), IconImageBundle.ICONS.excel());

    toolBar = new ToolBar();
    toolBar.setAlignment(Style.HorizontalAlignment.LEFT);
    toolBar.setBorders(false);
    toolBar.add(exportButton);

    container.setTopComponent(toolBar);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void buildExportDialog(final ContactRelationshipsPresenter.ExportActionHandler handler) {

    final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
    w.setPlain(true);
    w.setModal(true);
    w.setBlinkModal(true);
    w.setLayout(new FitLayout());
    w.setSize(400, 180);
    w.setHeadingHtml(I18N.CONSTANTS.exportData());

    final FormPanel panel = Forms.panel();

    final CheckBox allRelationsBox = Forms.checkbox(I18N.CONSTANTS.allRelations());
    final CheckBox frameworkRelationsBox = Forms.checkbox(I18N.CONSTANTS.frameworkRelations());
    final CheckBox relationsByElementBox = Forms.checkbox(I18N.CONSTANTS.relationsByElement());

    final CheckBoxGroup options =
        Forms.checkBoxGroup(I18N.CONSTANTS.exportOptions(), com.extjs.gxt.ui.client.Style.Orientation.VERTICAL, allRelationsBox, frameworkRelationsBox, relationsByElementBox);

    panel.add(options);

    final Button export = Forms.button(I18N.CONSTANTS.export());
    panel.getButtonBar().add(export);
    export.addSelectionListener(new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(final ButtonEvent ce) {
        if (handler != null) {
          handler.onExportContactRelationships(false, allRelationsBox.getValue(), frameworkRelationsBox.getValue(), relationsByElementBox.getValue());
        }
        w.hide();
      }
    });

    w.add(panel);
    w.show();
  }

  @Override
  public Button getExportButton() {
    return exportButton;
  }

  @Override
  public Grid<ContactRelationship> getRelationshipsGrid() {
    return grid;
  }

  @Override
  public ToggleButton outboundToggleButton() {
    return outboundToggleButton;
  }

  @Override
  public ToggleButton inboundToggleButton() {
    return inboundToggleButton;
  }

  @Override
  public void updateGridData(List<ContactRelationship> relationships) {
    grid.getStore().removeAll();
    grid.getStore().add(relationships);
    grid.recalculate();
    resizeGrid(grid, container, false);
  }

  @Override
  public void reloadView(ContactDTO contactDTO, ContactRelationshipsPresenter.AnchorHandler anchorHandler) {
    container.remove(buttonsContainer);
    container.remove(grid);
    container.layout();

    outboundToggleButton = new ToggleButton(I18N.CONSTANTS.contactRelationshipOwnedByToggleButtonLabel());
    inboundToggleButton = new ToggleButton(I18N.CONSTANTS.contactRelationshipOwnerOfToggleButtonLabel());
    buttonsContainer = Layouts.hBox(HBoxLayout.HBoxLayoutAlign.MIDDLE);
    buttonsContainer.add(outboundToggleButton);
    buttonsContainer.add(inboundToggleButton);
    buttonsContainer.setHeight(BUTTONS_PANEL_HEIGHT);

    grid = new Grid<ContactRelationship>(new ListStore<ContactRelationship>(), generateColumnModel(contactDTO, anchorHandler));
    grid.getView().setForceFit(true);
    grid.setAutoHeight(true);
    grid.addListener(Events.ViewReady, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        resizeGrid(grid, container, true);
      }
    });
    Window.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        resizeGrid(grid, container, false);
      }
    });

    container.add(buttonsContainer);
    container.add(grid);
    container.layout();
  }

  private ColumnModel generateColumnModel(final ContactDTO contactDTO, final ContactRelationshipsPresenter.AnchorHandler anchorHandler) {
    List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
    columnConfigs.add(new ColumnConfig(ContactRelationship.FIELD_NAME, I18N.CONSTANTS.contactRelationshipElementLabel(), 200));
    columnConfigs.add(new ColumnConfig(ContactRelationship.GROUP_NAME, I18N.CONSTANTS.contactRelationshipGroupTitle(), 200));
    columnConfigs.add(new ColumnConfig(ContactRelationship.FORMATTED_TYPE, I18N.CONSTANTS.contactRelationshipType(), 100));

    ColumnConfig nameColumnConfig = new ColumnConfig(ContactRelationship.FIELD_NAME, I18N.CONSTANTS.contactRelationshipName(), 250);
    nameColumnConfig.setRenderer(new GridCellRenderer<ContactRelationship>() {
      @Override
      public Object render(final ContactRelationship model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ContactRelationship> store, Grid<ContactRelationship> grid) {
        Anchor anchor = new Anchor(model.getName());
        anchor.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            anchorHandler.handleClick(model.getType(), model.getRelationshipId());
          }
        });
        return anchor;
      }
    });
    columnConfigs.add(nameColumnConfig);

    ColumnConfig memberColumnConfig = new ColumnConfig(ContactRelationship.FIELD_NAME, I18N.CONSTANTS.contactRelationshipMember(), 120);
    memberColumnConfig.setRenderer(new GridCellRenderer<ContactRelationship>() {
      @Override
      public Object render(ContactRelationship model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ContactRelationship> store, Grid<ContactRelationship> grid) {
        if (model.getDirection() == null) {
          return "";
        }

        switch (model.getDirection()) {
          case INBOUND:
            return I18N.MESSAGES.contactRelationshipsInbound(contactDTO.getFullName());
          case OUTBOUND:
            return I18N.MESSAGES.contactRelationshipsOutbound(contactDTO.getFullName());
          default:
            return "";
        }
      }
    });
    columnConfigs.add(memberColumnConfig);

    return new ColumnModel(columnConfigs);
  }

  private void resizeGrid(final Grid grid, final LayoutContainer container, final boolean canBeDelayed) {
    new Timer() {
      @Override
      public void run() {
        if (!grid.isVisible()) {
          if (canBeDelayed) {
            this.schedule(RESIZE_DELAY);
          }
          return;
        }

        grid.setAutoHeight(false);

        int height;
        int maxHeight = container.getHeight() - PADDING * 2;

        int unwrappedGridHeight = grid.getView().getScroller().firstChild().getHeight() + grid.getView().getHeader().getHeight();
        if (unwrappedGridHeight < maxHeight) {
          height = unwrappedGridHeight;
        } else {
          height = maxHeight;
        }

        if (grid.getHeight() == height) {
          return;
        }

        grid.setHeight(height);
        grid.getView().layout();
      }
    }.schedule(RESIZE_DELAY);
  }
}
