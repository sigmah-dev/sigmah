package org.sigmah.client.ui.presenter.contact;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;

import java.util.Arrays;
import java.util.List;

import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.Presenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.contact.ContactView;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.shared.command.GetContact;
import org.sigmah.shared.dto.ContactDTO;

import com.allen_sauer.gwt.log.client.Log;

public class ContactPresenter extends AbstractPagePresenter<ContactPresenter.View> {
  @ImplementedBy(ContactView.class)
  public interface View extends ViewInterface {
    Component getMainComponent();

    void setHeaderText(String header);

    void addTab(final String tabTitle, final Widget tabView);
  }

  interface ContactSubPresenter<E extends ViewInterface> extends Presenter<E> {
    String getTabHeader();
  }

  private final List<? extends ContactSubPresenter> tabPresenters;

  private ContactDTO contactDTO;

  @Inject
  public ContactPresenter(View view, Injector injector, ContactDetailsPresenter contactDetailsPresenter, ContactRelationshipsPresenter contactRelationshipsPresenter, ContactHistoryPresenter contactHistoryPresenter) {
    super(view, injector);

    this.tabPresenters = Arrays.asList(contactDetailsPresenter, contactRelationshipsPresenter, contactHistoryPresenter);
  }

  @Override
  public void onBind() {
    for (ContactSubPresenter tabPresenter : tabPresenters) {
      tabPresenter.initialize();
      view.addTab(tabPresenter.getTabHeader(), tabPresenter.getView().asWidget());
    }
  }

  @Override
  public Page getPage() {
    return Page.CONTACT_DASHBOARD;
  }

  @Override
  public void onPageRequest(PageRequest request) {
    Integer contactId = request.getParameterInteger(RequestParameter.ID);
    loadContact(contactId, request);
  }

  private void loadContact(final Integer contactId, final PageRequest pageRequest) {
    if (contactDTO != null && contactDTO.getId().equals(contactId)) {
      // Already loaded
      onContactLoaded(pageRequest);
      return;
    }

    dispatch.execute(new GetContact(contactId, ContactDTO.Mode.ALL), new AsyncCallback<ContactDTO>() {
      @Override
      public void onFailure(Throwable caught) {
        Log.error("Error while requesting a contact.", caught);
      }

      @Override
      public void onSuccess(final ContactDTO contactDTO) {
        ContactPresenter.this.contactDTO = contactDTO;

        onContactLoaded(pageRequest);
      }
    }, new LoadingMask(view.getMainComponent()));
  }

  private void onContactLoaded(PageRequest request) {
    // Updates the tab title.
    eventBus.updateZoneRequest(Zone.MENU_BANNER.requestWith(RequestParameter.REQUEST, request).addData(RequestParameter.HEADER, contactDTO.getFullName()));
    view.setHeaderText(contactDTO.getFullName());

    // By calling following refresh methods without deferring, all card labels will be placed at the same position.
    // Its mainly due to the fact that GXT overuse absolute positioning and calculate once at runtime the position of
    // each element. As the view is not ready if the user is going back to the Contact tab, GXT cannot calculate the
    // right position.
    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
      @Override
      public void execute() {
        refreshCard();
      }
    });
  }

  private void refreshCard() {
    // TODO
  }
}
