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
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.contact.ContactDetailsPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.contact.DedupeContactDialog;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.panel.Panels;

public class ContactDetailsView extends AbstractView implements ContactDetailsPresenter.View {
  private ContentPanel container;
  private ToolBar toolBar;
  private Button saveButton;
  private Button exportButton;

  @Override
  public void initialize() {
    container = Panels.content(null, false, Layouts.vBoxLayout(VBoxLayout.VBoxLayoutAlign.STRETCH, new Layouts.LayoutOptions(new Padding(10))), "x-border-layout-ct");
    container.setScrollMode(Style.Scroll.AUTOY);
    container.addStyleName("contact-details-container");
    add(container);

    saveButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
    exportButton = Forms.button(I18N.CONSTANTS.export(), IconImageBundle.ICONS.excel());

    toolBar = new ToolBar();
    toolBar.setAlignment(Style.HorizontalAlignment.LEFT);
    toolBar.setBorders(false);
    toolBar.add(saveButton);
    toolBar.add(exportButton);

    container.setTopComponent(toolBar);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void buildExportDialog(final ContactDetailsPresenter.ExportActionHandler handler) {

    final Window w = new Window();
    w.setPlain(true);
    w.setModal(true);
    w.setBlinkModal(true);
    w.setLayout(new FitLayout());
    w.setSize(400, 180);
    w.setHeadingHtml(I18N.CONSTANTS.exportData());

    final FormPanel panel = Forms.panel();

    final CheckBox synthesisBox = Forms.checkbox(I18N.CONSTANTS.caracteristics(), Boolean.TRUE);
    synthesisBox.setEnabled(false);
    final CheckBox allRelationsBox = Forms.checkbox(I18N.CONSTANTS.allRelations());
    final CheckBox frameworkRelationsBox = Forms.checkbox(I18N.CONSTANTS.frameworkRelations());
    final CheckBox relationsByElementBox = Forms.checkbox(I18N.CONSTANTS.relationsByElement());

    final CheckBoxGroup options =
        Forms.checkBoxGroup(I18N.CONSTANTS.exportOptions(), com.extjs.gxt.ui.client.Style.Orientation.VERTICAL, synthesisBox, allRelationsBox, frameworkRelationsBox, relationsByElementBox);

    panel.add(options);

    final Button export = Forms.button(I18N.CONSTANTS.export());
    panel.getButtonBar().add(export);
    export.addSelectionListener(new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(final ButtonEvent ce) {
        if (handler != null) {
          handler.onExportContact(synthesisBox.getValue(), allRelationsBox.getValue(), frameworkRelationsBox.getValue(), relationsByElementBox.getValue());
        }
        w.hide();
      }
    });

    w.add(panel);
    w.show();
  }

  @Override
  public LayoutContainer getDetailsContainer() {
    return container;
  }

  @Override
  public void fillContainer(Widget widget) {
    container.add(widget);
    container.layout();
  }

  @Override
  public Button getSaveButton() {
    return saveButton;
  }

  @Override
  public Button getExportButton() {
    return exportButton;
  }

  @Override
  public DedupeContactDialog generateDedupeDialog() {
    return new DedupeContactDialog(false);
  }
}
