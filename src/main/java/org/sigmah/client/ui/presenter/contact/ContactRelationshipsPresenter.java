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

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.grid.Grid;

import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.contact.ContactRelationshipsView;
import org.sigmah.shared.command.GetContactRelationships;
import org.sigmah.shared.command.result.ContactRelationship;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ContactDTO;

public class ContactRelationshipsPresenter extends AbstractPresenter<ContactRelationshipsPresenter.View> implements ContactPresenter.ContactSubPresenter<ContactRelationshipsPresenter.View> {
  @ImplementedBy(ContactRelationshipsView.class)
  public interface View extends ViewInterface {
    void reloadView(ContactDTO contactDTO, AnchorHandler anchorHandler);

    Grid<ContactRelationship> getRelationshipsGrid();

    ToggleButton outboundToggleButton();

    ToggleButton inboundToggleButton();

    void updateGridData(List<ContactRelationship> relationships);
  }

  public interface AnchorHandler {
    void handleClick(ContactRelationship.Type type, Integer id);
  }

  @Inject
  public ContactRelationshipsPresenter(View view, Injector injector) {
    super(view, injector);
  }

  @Override
  public String getTabHeader() {
    return I18N.CONSTANTS.contactRelationshipsHeader();
  }

  @Override
  public void refresh(final ContactDTO contactDTO) {
    view.reloadView(contactDTO, new AnchorHandler() {
      @Override
      public void handleClick(ContactRelationship.Type type, Integer id) {
        Page page;
        switch (type) {
          case PROJECT:
            page = Page.PROJECT_DASHBOARD;
            break;
          case ORGUNIT:
            page = Page.ORGUNIT_DASHBOARD;
            break;
          case CONTACT:
            page = Page.CONTACT_DASHBOARD;
            break;
          default:
            throw new IllegalStateException();
        }
        eventBus.navigateRequest(page.requestWith(RequestParameter.ID, id));
      }
    });

    reloadData(contactDTO.getId(), null);

    view.inboundToggleButton().toggle(false);
    view.outboundToggleButton().toggle(false);
    view.inboundToggleButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        view.outboundToggleButton().toggle(false);
        reloadData(contactDTO.getId(), view.inboundToggleButton().isPressed() ? ContactRelationship.Direction.INBOUND : null);
      }
    });
    view.outboundToggleButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        view.inboundToggleButton().toggle(false);
        reloadData(contactDTO.getId(), view.outboundToggleButton().isPressed() ? ContactRelationship.Direction.OUTBOUND : null);
      }
    });
  }

  private void reloadData(Integer contactId, ContactRelationship.Direction direction) {
    dispatch.execute(new GetContactRelationships(contactId, direction), new CommandResultHandler<ListResult<ContactRelationship>>() {
      @Override
      protected void onCommandSuccess(ListResult<ContactRelationship> result) {
        view.updateGridData(result.getList());
      }
    }, new LoadingMask(view.getRelationshipsGrid()));
  }
}
