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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FormData;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.computation.ComputationTriggerManager;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchQueue;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.contact.ContactDetailsView;
import org.sigmah.client.ui.view.contact.ContactView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.element.DefaultContactFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.element.event.ValueHandler;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.servlet.ServletConstants;
import org.sigmah.shared.servlet.ServletRequestBuilder;

import com.allen_sauer.gwt.log.client.Log;

public class ContactDetailsPresenter extends AbstractPresenter<ContactDetailsPresenter.View> implements ContactPresenter.ContactSubPresenter<ContactDetailsPresenter.View> {
  @ImplementedBy(ContactDetailsView.class)
  public interface View extends ViewInterface {
    LayoutContainer getDetailsContainer();

    Button getSaveButton();

    void fillContainer(final Widget widget);
  }

  private final ComputationTriggerManager computationTriggerManager;

  private List<ValueEvent> valueChanges = new ArrayList<ValueEvent>();

  @Inject
  public ContactDetailsPresenter(View view, Injector injector, ComputationTriggerManager computationTriggerManager) {
    super(view, injector);

    this.computationTriggerManager = computationTriggerManager;
  }

  @Override
  public String getTabHeader() {
    return I18N.CONSTANTS.contactDetailsHeader();
  }

  @Override
  public void refresh(final ContactDTO contactDTO) {
    computationTriggerManager.prepareForContact(contactDTO);
    valueChanges.clear();

    // Clear panel.
    view.getDetailsContainer().removeAll();

    // Layout.
    final LayoutDTO layout = contactDTO.getContactModel().getDetails().getLayout();

    // Counts elements.
    int count = 0;
    for (final LayoutGroupDTO groupDTO : layout.getGroups()) {
      count += groupDTO.getConstraints().size();
    }

    if (count == 0) {
      // Default details page.
      view.fillContainer(new Label(I18N.CONSTANTS.contactDetailsNoDetails()));
      return;
    }

    final FormPanel formPanel = Forms.panel();
    final Grid gridLayout = new Grid(layout.getRowsCount(), layout.getColumnsCount());
    gridLayout.setCellPadding(0);
    gridLayout.setCellSpacing(0);

    final DispatchQueue queue = new DispatchQueue(dispatch, true);

    for (final LayoutGroupDTO groupLayout : layout.getGroups()) {

      // Creates the fieldset and positions it.
      final FieldSet fieldSet = (FieldSet) groupLayout.getWidget();
      gridLayout.setWidget(groupLayout.getRow(), groupLayout.getColumn(), fieldSet);

      // For each constraint in the current layout group.
      if (ClientUtils.isEmpty(groupLayout.getConstraints())) {
        continue;
      }

      for (final LayoutConstraintDTO constraintDTO : groupLayout.getConstraints()) {

        // Gets the element managed by this constraint.
        final FlexibleElementDTO elementDTO = constraintDTO.getFlexibleElementDTO();

        if (elementDTO.isDisabled()) {
          continue;
        }

        // Check if the element is a default element and is visible or not for the contactModel type
        if (elementDTO.getElementType() == ElementTypeEnum.DEFAULT_CONTACT &&
            !((DefaultContactFlexibleElementDTO) elementDTO).getType().isVisibleForType(contactDTO.getContactModel().getType())) {
          continue;
        }

        // Remote call to ask for this element value.
        queue.add(new GetValue(contactDTO.getId(), elementDTO.getId(), elementDTO.getEntityName()), new CommandResultHandler<ValueResult>() {

          @Override
          public void onCommandFailure(final Throwable throwable) {
            if (Log.isErrorEnabled()) {
              Log.error("Error, element value not loaded.", throwable);
            }
            throw new RuntimeException(throwable);
          }

          @Override
          public void onCommandSuccess(final ValueResult valueResult) {

            if (Log.isDebugEnabled()) {
              Log.debug("Element value(s) object : " + valueResult);
            }

            // --
            // -- ELEMENT COMPONENT
            // --

            // Configures the flexible element for the current application state before generating its component.
            elementDTO.setService(dispatch);
            elementDTO.setAuthenticationProvider(injector.getAuthenticationProvider());
            elementDTO.setEventBus(eventBus);
            elementDTO.setCache(injector.getClientCache());
            elementDTO.setCurrentContainerDTO(contactDTO);
            elementDTO.setTransfertManager(injector.getTransfertManager());
            elementDTO.assignValue(valueResult);
            if (elementDTO instanceof DefaultContactFlexibleElementDTO) {
              ((DefaultContactFlexibleElementDTO) elementDTO).setFormPanel(formPanel);
              ((DefaultContactFlexibleElementDTO) elementDTO).setImageProvider(new DefaultContactFlexibleElementDTO.ImageProvider() {
                @Override
                public void provideImageUrl(String imageId, final AsyncCallback<String> callback) {
                  ServletRequestBuilder builder = new ServletRequestBuilder(injector, RequestBuilder.GET, ServletConstants.Servlet.FILE, ServletConstants.ServletMethod.DOWNLOAD_LOGO);
                  builder.addParameter(RequestParameter.ID, imageId);
                  builder.send(new ServletRequestBuilder.RequestCallbackAdapter() {
                    @Override
                    public void onResponseReceived(final Request request, final Response response) {
                      if (response.getStatusCode() != Response.SC_OK) {
                        callback.onFailure(new RequestException(response.getStatusText()));
                        return;
                      }
                      callback.onSuccess(response.getText());
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {
                      super.onError(request, exception);

                      callback.onFailure(exception);
                    }
                  });
                }
              });
            }

            // Generates element component (with the value).
            elementDTO.init();
            final Component elementComponent = elementDTO.getElementComponent(valueResult);

            // Component width.
            final FormData formData;
            if (elementDTO.getPreferredWidth() == 0) {
              formData = new FormData("100%");
            } else {
              formData = new FormData(elementDTO.getPreferredWidth(), -1);
            }

            if (elementComponent != null) {
              fieldSet.add(elementComponent, formData);
            }
            fieldSet.layout();

            // --
            // -- ELEMENT HANDLERS
            // --

            // Adds a value change handler if this element is a dependency of a ComputationElementDTO.
            computationTriggerManager.listenToValueChangesOfElement(elementDTO, elementComponent, valueChanges);

            // Adds a value change handler to this element.
            elementDTO.addValueHandler(new ValueHandler() {

              @Override
              public void onValueChange(ValueEvent event) {

                // Stores the change to be saved later.
                valueChanges.add(event);

                // Enables the save action.
                view.getSaveButton().enable();
              }

            });

            view.getDetailsContainer().layout();
          }
        }, new LoadingMask(view.getDetailsContainer()));
      }
    }

    view.getSaveButton().removeAllListeners();
    view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent event) {
        view.getSaveButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

          @Override
          public void handleEvent(ButtonEvent be) {
            // TODO
          }
        });
      }
    });

    queue.start();

    formPanel.add(gridLayout);
    view.fillContainer(formPanel);
  }
}
