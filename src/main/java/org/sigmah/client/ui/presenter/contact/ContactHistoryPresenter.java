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

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.Grid;

import java.util.List;

import org.sigmah.client.ClientFactory;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.contact.ContactHistoryView;
import org.sigmah.client.util.ImageProvider;
import org.sigmah.shared.command.GetContactHistory;
import org.sigmah.shared.command.result.ContactHistory;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ContactDTO;

public class ContactHistoryPresenter extends AbstractPresenter<ContactHistoryPresenter.View> implements ContactPresenter.ContactSubPresenter<ContactHistoryPresenter.View> {
  @ImplementedBy(ContactHistoryView.class)
  public interface View extends ViewInterface {
    void updateGridData(List<ContactHistory> contactHistories);

    void setImageProvider(ImageProvider imageProvider);
  }

  private final ImageProvider imageProvider;

  public ContactHistoryPresenter(View view, ClientFactory factory) {
    super(view, factory);

    this.imageProvider = factory.getImageProvider();
  }

  @Override
  public String getTabHeader() {
    return I18N.CONSTANTS.contactHistoryHeader();
  }

  @Override
  public void onBind() {


    // Contact delete event handler.
    registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

      @Override
      public void onUpdate(final UpdateEvent event) {

        if (event.concern(UpdateEvent.CONTACT_UPDATE)) {
          final ContactDTO contact = event.getParam(0);
          refresh(contact);
        }
      }
    }));
  }

  @Override
  public void refresh(ContactDTO contactDTO) {
    view.setImageProvider(imageProvider);
    dispatch.execute(new GetContactHistory(contactDTO.getId()), new CommandResultHandler<ListResult<ContactHistory>>() {
      @Override
      protected void onCommandSuccess(ListResult<ContactHistory> result) {
        view.updateGridData(result.getList());
      }
    });
  }

  public boolean hasValueChanged() {
    return false;
  }
}
