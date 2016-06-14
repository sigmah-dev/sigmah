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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

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
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.element.DefaultContactFlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.servlet.ServletConstants;
import org.sigmah.shared.servlet.ServletRequestBuilder;

import com.allen_sauer.gwt.log.client.Log;

public class ContactPresenter extends AbstractPagePresenter<ContactPresenter.View> {
  @ImplementedBy(ContactView.class)
  public interface View extends ViewInterface {
    Component getMainComponent();

    void setAvatarUrl(String url);

    void setDefaultAvatar(ContactModelType type);

    void prepareContainers();

    void addLabel(String label);

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
    final ContactModelDTO contactModelDTO = contactDTO.getContactModel();

    view.prepareContainers();
    TreeSet<LayoutConstraintDTO> constraints = new TreeSet<LayoutConstraintDTO>(new Comparator<LayoutConstraintDTO>() {
      @Override
      public int compare(LayoutConstraintDTO constraint1, LayoutConstraintDTO constraint2) {
        if (constraint1.getSortOrder() == constraint2.getSortOrder()) {
          return constraint1.getFlexibleElementDTO().getId().compareTo(constraint2.getFlexibleElementDTO().getId());
        }
        return constraint1.getSortOrder() - constraint2.getSortOrder();
      }
    });

    for (LayoutGroupDTO layoutGroup : contactModelDTO.getCard().getLayout().getGroups()) {
      for (LayoutConstraintDTO constraint : layoutGroup.getConstraints()) {
        constraints.add(constraint);
      }
    }

    boolean hasPhoto = false;
    for (LayoutConstraintDTO constraint : constraints) {
      DefaultContactFlexibleElementDTO flexibleElementDTO = (DefaultContactFlexibleElementDTO) constraint.getFlexibleElementDTO();
      if (!flexibleElementDTO.getType().isVisibleForType(contactModelDTO.getType())) {
        continue;
      }
      switch (flexibleElementDTO.getType()) {
        case PHOTO:
          if (contactDTO.getPhoto() == null) {
            break;
          }

          hasPhoto = true;
          ServletRequestBuilder builder = new ServletRequestBuilder(injector, RequestBuilder.GET, ServletConstants.Servlet.FILE, ServletConstants.ServletMethod.DOWNLOAD_LOGO);
          builder.addParameter(RequestParameter.ID, contactDTO.getPhoto());
          builder.send(new ServletRequestBuilder.RequestCallbackAdapter() {
            @Override
            public void onResponseReceived(final Request request, final Response response) {
              if (response.getStatusCode() != Response.SC_OK) {
                view.setDefaultAvatar(contactModelDTO.getType());
                return;
              }

              view.setAvatarUrl(response.getText());
            }
          });
          break;
        case COUNTRY:
          if (contactDTO.getCountry() == null) {
            break;
          }
          view.addLabel(contactDTO.getCountry().getName());
          break;
        case CREATION_DATE:
          if (contactDTO.getDateCreated() == null) {
            break;
          }
          String date = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM).format(contactDTO.getDateCreated());
          view.addLabel(date);
          break;
        case DIRECT_MEMBERSHIP:
          if (contactDTO.getParent() == null) {
            break;
          }
          view.addLabel(contactDTO.getParent().getName());
          break;
        case EMAIL_ADDRESS:
          view.addLabel(contactDTO.getEmail());
          break;
        case FAMILY_NAME:
          view.addLabel(contactDTO.getName());
          break;
        case FIRST_NAME:
          view.addLabel(contactDTO.getFirstname());
          break;
        case LOGIN:
          view.addLabel(contactDTO.getLogin());
          break;
        case MAIN_ORG_UNIT:
          if (contactDTO.getMainOrgUnit() == null) {
            break;
          }
          view.addLabel(contactDTO.getMainOrgUnit().getName());
          break;
        case ORGANIZATION_NAME:
          view.addLabel(contactDTO.getName());
          break;
        case PHONE_NUMBER:
          view.addLabel(contactDTO.getPhoneNumber());
          break;
        case POSTAL_ADDRESS:
          view.addLabel(contactDTO.getPostalAddress());
          break;
        case SECONDARY_ORG_UNITS:
          for (OrgUnitDTO secondaryOrgUnit : contactDTO.getSecondaryOrgUnits()) {
            view.addLabel(secondaryOrgUnit.getName());
          }
          break;
        case TOP_MEMBERSHIP:
          if (contactDTO.getRoot() == null) {
            break;
          }
          view.addLabel(contactDTO.getRoot().getName());
          break;
        default:
          throw new IllegalStateException();
      }
    }

    if (!hasPhoto) {
      view.setDefaultAvatar(contactModelDTO.getType());
    }
  }
}
