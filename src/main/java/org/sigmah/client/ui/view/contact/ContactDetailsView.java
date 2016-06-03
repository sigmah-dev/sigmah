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
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.panel.Panels;

public class ContactDetailsView extends AbstractView implements ContactDetailsPresenter.View {
  private ContentPanel container;
  private ToolBar toolBar;
  private Button saveButton;

  @Override
  public void initialize() {
    container = Panels.content(null, false, Layouts.vBoxLayout(VBoxLayout.VBoxLayoutAlign.STRETCH, new Layouts.LayoutOptions(new Padding(10))), "x-border-layout-ct");
    container.setScrollMode(Style.Scroll.AUTOY);
    container.addStyleName("contact-details-container");
    add(container);

    saveButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());

    toolBar = new ToolBar();
    toolBar.setAlignment(Style.HorizontalAlignment.LEFT);
    toolBar.setBorders(false);
    toolBar.add(saveButton);

    container.setTopComponent(toolBar);
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
  public DedupeContactDialog generateDedupeDialog() {
    return new DedupeContactDialog(false);
  }
}
