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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;

import org.sigmah.client.ui.presenter.contact.ContactPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.referential.ContactModelType;

public class ContactView extends AbstractView implements ContactPresenter.View {
  private static final int AVATAR_WIDTH = 128;
  private static final int AVATAR_HEIGHT = 128;
  private static final int CARD_WIDTH = 600;
  private static final int LABEL_HEIGHT = 30;
  private static final int PADDING = 15;

  private final String AVATAR_STYLE_NAME = "contact-card-avatar";
  private final String AVATAR_DEFAULT_INDIVIDUAL_STYLE_NAME = "contact-card-avatar-individual";
  private final String AVATAR_DEFAULT_ORGANIZATION_STYLE_NAME = "contact-card-avatar-organization";
  private final String LABEL_STYLE_NAME = "contact-card-label";
  public final String NAME_STYLE_NAME = "contact-card-name";
  public final String ORGANIZATION_STYLE_NAME = "contact-card-organization";

  private ContentPanel contentPanel;
  private HTML avatar;
  private LayoutContainer topDetailsContainer;
  private LayoutContainer bottomDetailsContainer;
  private TabPanel tabPanel;

  @Override
  public Component getMainComponent() {
    return contentPanel;
  }

  @Override
  public void initialize() {
    Widget contactCardPanel = createContactCardPanel();

    tabPanel = Panels.tab();
    tabPanel.setBorderStyle(true);
    tabPanel.setBodyBorder(true);
    tabPanel.setBorders(true);
    tabPanel.setPlain(true);
    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
      @Override
      public void execute() {
        tabPanel.setWidth(contentPanel.getWidth() - CARD_WIDTH);
      }
    });

    LayoutContainer layoutContainer = Layouts.hBox(HBoxLayout.HBoxLayoutAlign.STRETCH);
    layoutContainer.add(contactCardPanel);
    layoutContainer.add(tabPanel);

    contentPanel = Panels.content(""); // Temporary title.
    contentPanel.setHeaderVisible(true);
    contentPanel.add(layoutContainer);

    add(contentPanel);
  }

  private Widget createContactCardPanel() {
    avatar = new HTML();
    avatar.setWidth(AVATAR_WIDTH + "px");
    avatar.setHeight(AVATAR_HEIGHT + "px");
    avatar.setStyleName(AVATAR_STYLE_NAME);

    topDetailsContainer = Layouts.vBox(VBoxLayout.VBoxLayoutAlign.LEFT);
    topDetailsContainer.setHeight(AVATAR_HEIGHT);
    topDetailsContainer.setWidth(CARD_WIDTH - AVATAR_WIDTH);

    LayoutContainer topContainer = Layouts.hBox(HBoxLayout.HBoxLayoutAlign.MIDDLE);
    topContainer.setWidth(CARD_WIDTH);
    topContainer.setHeight(AVATAR_HEIGHT + PADDING * 2);
    topContainer.add(avatar);
    topContainer.add(topDetailsContainer);

    bottomDetailsContainer = Layouts.vBox(VBoxLayout.VBoxLayoutAlign.LEFT, new Layouts.LayoutOptions(new Padding(0, 0, 0, 20)));
    bottomDetailsContainer.setWidth(CARD_WIDTH - AVATAR_WIDTH);

    LayoutContainer cardContainer = Layouts.vBox(VBoxLayout.VBoxLayoutAlign.STRETCH);
    cardContainer.setWidth(CARD_WIDTH);
    cardContainer.add(topContainer);
    cardContainer.add(bottomDetailsContainer);
    return cardContainer;
  }

  @Override
  public void setAvatarUrl(String url) {
    avatar.getElement().getStyle().setBackgroundImage("url(\"" + url + "\")");
  }

  @Override
  public void setDefaultAvatar(ContactModelType type) {
    avatar.getElement().getStyle().clearBackgroundImage();
    switch (type) {
      case INDIVIDUAL:
        avatar.addStyleName(AVATAR_DEFAULT_INDIVIDUAL_STYLE_NAME);
        break;
      case ORGANIZATION:
        avatar.addStyleName(AVATAR_DEFAULT_ORGANIZATION_STYLE_NAME);
        break;
      default:
        throw new IllegalStateException("Unknown ContactModelType : " + type);
    }
  }

  @Override
  public void prepareContainers() {
    topDetailsContainer.removeAll();
    bottomDetailsContainer.removeAll();
    avatar.removeStyleName(AVATAR_DEFAULT_INDIVIDUAL_STYLE_NAME);
    avatar.removeStyleName(AVATAR_DEFAULT_ORGANIZATION_STYLE_NAME);
    avatar.getElement().getStyle().clearBackgroundImage();
  }

  @Override
  public void addLabel(String label) {
    addLabel(label, LABEL_STYLE_NAME);
  }

  @Override
  public void addNameLabel(String name) {
    addLabel(name, NAME_STYLE_NAME);
  }

  @Override
  public void addOrganizationLabel(String organization) {
    addLabel(organization, ORGANIZATION_STYLE_NAME);
  }

  @Override
  public void addLabel(String label, String style) {
    if (ClientUtils.isBlank(label)) {
      return;
    }

    HTML html = new HTML(label);
    html.setHeight(LABEL_HEIGHT + "px");
    html.setStyleName(style);
    html.setWordWrap(false);
    if ((topDetailsContainer.getItemCount() + 1 ) * LABEL_HEIGHT > AVATAR_HEIGHT) {
      bottomDetailsContainer.add(html);
      bottomDetailsContainer.setHeight(bottomDetailsContainer.getItemCount() * LABEL_HEIGHT);
      bottomDetailsContainer.layout();
      return;
    }
    topDetailsContainer.add(html);
    topDetailsContainer.layout();
  }

  @Override
  public void setHeaderText(String header) {
    contentPanel.setHeadingText(header);
  }

  @Override
  public void addTab(String tabTitle, final Widget tabView) {
    final TabItem tabItem = new TabItem(tabTitle);
    tabItem.addListener(Events.Select, new Listener<ComponentEvent>() {
      @Override
      public void handleEvent(ComponentEvent be) {
        fixTabViewHeight(tabView, tabItem);
      }
    });
    tabItem.add(tabView);

    Window.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        fixTabViewHeight(tabView, tabItem);
      }
    });
    fixTabViewHeight(tabView, tabItem);

    tabPanel.add(tabItem);
  }

  private void fixTabViewHeight(final Widget tabView, final TabItem tabItem) {
    Timer timer = new Timer() {
      @Override
      public void run() {
        if (tabView.isVisible() && tabItem == tabPanel.getSelectedItem()) {
          tabView.setHeight(tabItem.getHeight() + "px");
        }
      }
    };
    timer.schedule(200);
  }
}
