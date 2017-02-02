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

import com.extjs.gxt.ui.client.widget.Layout;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FormData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sigmah.client.computation.ComputationTriggerManager;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchQueue;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.contact.ContactDetailsView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.contact.DedupeContactDialog;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.IterableGroupPanel;
import org.sigmah.client.ui.widget.form.IterableGroupPanel.IterableGroupItem;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.ImageProvider;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.command.CheckContactDuplication;
import org.sigmah.shared.command.DedupeContact;
import org.sigmah.shared.command.GetContact;
import org.sigmah.shared.command.GetContactDuplicatedProperties;
import org.sigmah.shared.command.GetCountry;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateContact;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.UpdateLayoutGroupIterations;
import org.sigmah.shared.command.UpdateLayoutGroupIterations.IterationChange;
import org.sigmah.shared.command.result.ContactDuplicatedProperty;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.element.DefaultContactFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementContainer;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.RequiredValueHandler;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.element.event.ValueHandler;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;
import org.sigmah.shared.dto.referential.ElementTypeEnum;

import com.allen_sauer.gwt.log.client.Log;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.util.ProfileUtils;

public class ContactDetailsPresenter extends AbstractPresenter<ContactDetailsPresenter.View> implements ContactPresenter.ContactSubPresenter<ContactDetailsPresenter.View>, IterableGroupPanel.Delegate {
  @ImplementedBy(ContactDetailsView.class)
  public interface View extends ViewInterface {
    LayoutContainer getDetailsContainer();

    Button getSaveButton();

    Button getDeleteButton();

    Button getExportButton();

    void buildExportDialog(ExportActionHandler handler);

    DedupeContactDialog generateDedupeDialog();

    void fillContainer(final Widget widget);
  }

  public static interface ExportActionHandler {
    void onExportContact(boolean characteristicsField, boolean allRelationsField, boolean frameworkRelationsField, boolean relationsByElementField);

  }

  private final ComputationTriggerManager computationTriggerManager;
  private final ImageProvider imageProvider;

  private List<ValueEvent> valueChanges = new ArrayList<ValueEvent>();

  private final Map<Integer, IterationChange> iterationChanges = new HashMap<Integer, IterationChange>();

  private final Map<Integer, IterableGroupItem> newIterationsTabItems = new HashMap<Integer, IterableGroupItem>();

  private FormPanel formPanel;

  @Inject
  public ContactDetailsPresenter(View view, Injector injector, ComputationTriggerManager computationTriggerManager, ImageProvider imageProvider) {
    super(view, injector);

    this.computationTriggerManager = computationTriggerManager;
    this.imageProvider = imageProvider;
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

    formPanel = Forms.panel();
    final Grid gridLayout = new Grid(layout.getRowsCount(), layout.getColumnsCount());
    gridLayout.setCellPadding(0);
    gridLayout.setCellSpacing(0);

    // unique id
    formPanel.add(new Label(I18N.CONSTANTS.contactUniqueId() + contactDTO.getId()));

    final DispatchQueue queue = new DispatchQueue(dispatch, true);

    for (final LayoutGroupDTO groupLayout : layout.getGroups()) {

      // simple group
      if(!groupLayout.getHasIterations()) {

        FieldSet fieldSet = createGroupLayoutFieldSet(contactDTO, groupLayout, queue, null, null, null);
        fieldSet.setHeadingHtml(groupLayout.getTitle());
        fieldSet.setCollapsible(true);
        fieldSet.setBorders(true);
        gridLayout.setWidget(groupLayout.getRow(), groupLayout.getColumn(), fieldSet);
        continue;
      }

      final FieldSet fieldSet = (FieldSet) groupLayout.getWidget();
      gridLayout.setWidget(groupLayout.getRow(), groupLayout.getColumn(), fieldSet);

      // iterative group
      final IterableGroupPanel tabPanel = Forms.iterableGroupPanel(dispatch, groupLayout, contactDTO, ProfileUtils.isGranted(auth(), GlobalPermissionEnum.CREATE_ITERATIONS));
      tabPanel.setDelegate(this);
      fieldSet.add(tabPanel);

      tabPanel.setAutoHeight(true);
      tabPanel.setAutoWidth(true);
      tabPanel.setTabScroll(true);
      tabPanel.addStyleName("white-tab-body");
      tabPanel.setBorders(true);
      tabPanel.setBodyBorder(false);

      GetLayoutGroupIterations getIterations = new GetLayoutGroupIterations(groupLayout.getId(), contactDTO.getId(), -1);

      queue.add(getIterations, new CommandResultHandler<ListResult<LayoutGroupIterationDTO>>() {

        @Override
        public void onCommandFailure(final Throwable throwable) {
          if (Log.isErrorEnabled()) {
            Log.error("Error, layout group iterations not loaded.", throwable);
          }
          throw new RuntimeException(throwable);
        }

        @Override
        protected void onCommandSuccess(ListResult<LayoutGroupIterationDTO> result) {
          DispatchQueue iterationsQueue = new DispatchQueue(dispatch, true);

          for(final LayoutGroupIterationDTO iteration : result.getList()) {

            final IterableGroupItem tab = new IterableGroupItem(tabPanel, iteration.getId(), iteration.getName());
            tabPanel.addIterationTab(tab);

            Layout tabLayout = Layouts.fitLayout();

            tab.setLayout(tabLayout);

            FieldSet tabSet = createGroupLayoutFieldSet(contactDTO, groupLayout, iterationsQueue, iteration == null ? null : iteration.getId(), tabPanel, tab);

            tab.add(tabSet);
          }

          iterationsQueue.start();

          if(tabPanel.getItemCount() > 0) {
            tabPanel.setSelection(tabPanel.getItem(0));
          }

        }
      }, new LoadingMask(view.getDetailsContainer()));

      fieldSet.layout();
    }

    view.getSaveButton().removeAllListeners();
    view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(final ButtonEvent buttonEvent) {
        view.getSaveButton().disable();

        dispatch.execute(buildCheckContactDuplicationCommand(contactDTO), new CommandResultHandler<ListResult<ContactDTO>>() {
          @Override
          protected void onCommandSuccess(ListResult<ContactDTO> result) {
            if (result == null || result.isEmpty()) {
              updateContact(contactDTO, new CommandResultHandler<ContactDTO>() {
                @Override
                protected void onCommandSuccess(ContactDTO updatedContactDTO) {
                  view.getSaveButton().enable();
                }
              }, view.getDetailsContainer());

              return;
            }

            final DedupeContactDialog dedupeContactDialog = view.generateDedupeDialog();
            dedupeContactDialog.getPossibleDuplicatesGrid().getStore().add(result.getList());
            dedupeContactDialog.getFirstStepMainButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
              @Override
              public void componentSelected(ButtonEvent ce) {
                updateContact(contactDTO, new CommandResultHandler<ContactDTO>() {
                  @Override
                  protected void onCommandSuccess(ContactDTO result) {
                    dedupeContactDialog.hide();
                  }
                }, view.getDetailsContainer());
              }
            });
            dedupeContactDialog.setSecondStepHandler(new DedupeContactDialog.SecondStepHandler() {
              @Override
              public void initialize(final Integer contactId, final ListStore<ContactDuplicatedProperty> propertiesStore) {
                updateContact(contactDTO, new CommandResultHandler<ContactDTO>() {
                  @Override
                  protected void onCommandSuccess(ContactDTO updatedContactDTO) {
                    dispatch.execute(new GetContactDuplicatedProperties(contactId, updatedContactDTO.getId(), null), new CommandResultHandler<ListResult<ContactDuplicatedProperty>>() {
                      @Override
                      protected void onCommandSuccess(ListResult<ContactDuplicatedProperty> result) {
                        propertiesStore.add(result.getList());
                      }
                    }, new LoadingMask(dedupeContactDialog));
                  }
                }, dedupeContactDialog);
              }

              @Override
              public void downloadImage(String id, final Image image) {
                imageProvider.provideDataUrl(id, new SuccessCallback<String>() {
                  @Override
                  public void onSuccess(String dataUrl) {
                    image.setUrl(dataUrl);
                  }
                });
              }

              @Override
              public void handleDedupeContact(final Integer targetedContactId, List<ContactDuplicatedProperty> selectedProperties) {
                dispatch.execute(new DedupeContact(selectedProperties, contactDTO.getId(), targetedContactId), new CommandResultHandler<ContactDTO>() {
                  @Override
                  protected void onCommandSuccess(ContactDTO targetedContactDTO) {
                    dedupeContactDialog.hide();
                    final PageRequest currentRequest = injector.getPageManager().getCurrentPageRequest(false);
                    eventBus.navigateRequest(Page.CONTACT_DASHBOARD.requestWith(RequestParameter.ID, targetedContactId));
                    eventBus.fireEvent(new UpdateEvent(UpdateEvent.CONTACT_DELETE, currentRequest));
                  }
                });
              }

              @Override
              public void handleCancel() {
                dedupeContactDialog.hide();
              }
            });
            dedupeContactDialog.addWindowListener(new WindowListener() {
              @Override
              public void windowHide(WindowEvent windowEvent) {
                super.windowHide(windowEvent);

                if (windowEvent.getType() == Events.Hide) {
                  view.getSaveButton().enable();
                }
              }
            });
            dedupeContactDialog.show();
          }
        });
      }
    });

    view.getDeleteButton().removeAllListeners();
    view.getDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(final ButtonEvent ce) {
        onDeleteContact(contactDTO);
      }
    });
    view.getDeleteButton().setEnabled(canDeleteContact());
    view.getDeleteButton().setVisible(canDeleteContact());


    // --
    // Contact export button handler.
    // --
    view.getExportButton().removeAllListeners();
    view.getExportButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

      @Override
      public void componentSelected(final ButtonEvent ce) {
        onExportContact(contactDTO);
      }
    });

    queue.start();

    formPanel.add(gridLayout);
    view.fillContainer(formPanel);
  }

  private CheckContactDuplication buildCheckContactDuplicationCommand(ContactDTO contactDTO) {
    CheckContactDuplication checkDuplicationCommand;

    String currentEmail = getCurrentSingleValue(DefaultContactFlexibleElementType.EMAIL_ADDRESS, contactDTO.getEmail());
    switch (contactDTO.getContactModel().getType()) {
      case INDIVIDUAL:
        String currentFamilyName = getCurrentSingleValue(DefaultContactFlexibleElementType.FAMILY_NAME, contactDTO.getFamilyName());
        String currentFirstName = getCurrentSingleValue(DefaultContactFlexibleElementType.FIRST_NAME, contactDTO.getFirstname());
        checkDuplicationCommand = new CheckContactDuplication(contactDTO.getId(), currentEmail, currentFamilyName, currentFirstName);
        break;
      case ORGANIZATION:
        String currentOrganizationName = getCurrentSingleValue(DefaultContactFlexibleElementType.ORGANIZATION_NAME, contactDTO.getOrganizationName());
        checkDuplicationCommand = new CheckContactDuplication(contactDTO.getId(), currentEmail, currentOrganizationName, null);
        break;
      default:
        throw new IllegalStateException("Unknown ContactModelType : " + contactDTO.getContactModel().getType());
    }
    return checkDuplicationCommand;
  }

  @Override
  public IterationChange getIterationChange(int iterationId) {
    return iterationChanges.get(iterationId);
  }

  @Override
  public void setIterationChange(IterationChange iterationChange) {
    iterationChanges.put(iterationChange.getIterationId(), iterationChange);

    view.getSaveButton().enable();
  }

  @Override
  public void addIterationTabItem(int iterationId, IterableGroupItem tab) {
    newIterationsTabItems.put(iterationId, tab);
  }

  @Override
  public FieldSet createGroupLayoutFieldSet(FlexibleElementContainer container, LayoutGroupDTO groupLayout, DispatchQueue queue, final Integer iterationId, final IterableGroupPanel tabPanel, final IterableGroupItem tabItem) {
    final ContactDTO contact = (ContactDTO)container;

    // Creates the fieldset and positions it.
    final FieldSet fieldSet = (FieldSet) groupLayout.getWidget();

    // For each constraint in the current layout group.
    if (ClientUtils.isEmpty(groupLayout.getConstraints())) {
      return fieldSet;
    }

    for (final LayoutConstraintDTO constraintDTO : groupLayout.getConstraints()) {

      // Gets the element managed by this constraint.
      final FlexibleElementDTO elementDTO = constraintDTO.getFlexibleElementDTO();

      // --
      // -- DISABLED ELEMENTS
      // --

      if(elementDTO.isDisabled()) {
        continue;
      }

      // Check if the element is a default element and is visible or not for the contactModel type
      if (elementDTO.getElementType() == ElementTypeEnum.DEFAULT_CONTACT &&
          !((DefaultContactFlexibleElementDTO) elementDTO).getType().isVisibleForType(contact.getContactModel().getType())) {
        continue;
      }

      // --
      // -- ELEMENT VALUE
      // --

      // Remote call to ask for this element value.
      queue.add(new GetValue(contact.getId(), elementDTO.getId(), elementDTO.getEntityName(), null, iterationId), new CommandResultHandler<ValueResult>() {

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
          elementDTO.setCurrentContainerDTO(contact);
          elementDTO.setTransfertManager(injector.getTransfertManager());
          elementDTO.assignValue(valueResult);
          elementDTO.setImageProvider(imageProvider);
          if (elementDTO instanceof DefaultContactFlexibleElementDTO) {
            ((DefaultContactFlexibleElementDTO) elementDTO).setFormPanel(formPanel);
          }
          elementDTO.setTabPanel(tabPanel);

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
            public void onValueChange(final ValueEvent event) {

              if(tabPanel != null) {
                event.setIterationId(tabPanel.getCurrentIterationId());
              }

              // TODO: Find linked computation fields if any and recompute the value.

              // Stores the change to be saved later.
              valueChanges.add(event);

              // Enables the save action.
              view.getSaveButton().enable();
            }
          });

          if(elementDTO.getValidates() && tabItem != null) {
            tabItem.setElementValidity(elementDTO, elementDTO.isCorrectRequiredValue(valueResult));
            tabItem.refreshTitle();
            elementDTO.addRequiredValueHandler(new RequiredValueHandlerImpl(elementDTO));
          }
        }
      }, new LoadingMask(view.getDetailsContainer()));
    }

    fieldSet.setCollapsible(false);
    fieldSet.setAutoHeight(true);
    fieldSet.setBorders(false);
    fieldSet.setHeadingHtml("");

    return fieldSet;
  }

  /**
   * Internal class handling the value changes of the required elements.
   */
  private class RequiredValueHandlerImpl implements RequiredValueHandler {

    private final FlexibleElementDTO elementDTO;

    public RequiredValueHandlerImpl(FlexibleElementDTO elementDTO) {
      this.elementDTO = elementDTO;
    }

    @Override
    public void onRequiredValueChange(RequiredValueEvent event) {

      // Refresh the panel's header
      elementDTO.getTabPanel().setElementValidity(elementDTO, event.isValueOn());
      elementDTO.getTabPanel().validateElements();
    }
  }

  private void updateContact(final ContactDTO contactDTO, final AsyncCallback<ContactDTO> callback, final Component target) {

    // Checks if there are any changes regarding layout group iterations
    dispatch.execute(new UpdateLayoutGroupIterations(new ArrayList<IterationChange>(iterationChanges.values()), contactDTO.getId()), new CommandResultHandler<ListResult<IterationChange>>() {

      @Override
      public void onCommandFailure(final Throwable caught) {
        N10N.error(I18N.CONSTANTS.save(), I18N.CONSTANTS.saveError());
      }

      @Override
      protected void onCommandSuccess(ListResult<IterationChange> result) {

        for (IterationChange iterationChange : result.getList()) {
          if (iterationChange.isDeleted()) {
            // remove corresponding valueEvents

            Iterator<ValueEvent> valuesIterator = valueChanges.iterator();
            while (valuesIterator.hasNext()) {
              ValueEvent valueEvent = valuesIterator.next();

              if (valueEvent.getIterationId() == iterationChange.getIterationId()) {
                valuesIterator.remove();
              }
            }
          } else if (iterationChange.isCreated()) {
            // change ids in valueEvents
            int oldId = iterationChange.getIterationId();
            int newId = iterationChange.getNewIterationId();

            // updating tabitem id
            newIterationsTabItems.get(oldId).setIterationId(newId);

            for (ValueEvent valueEvent : valueChanges) {
              if (valueEvent.getIterationId() == oldId) {
                valueEvent.setIterationId(newId);
              }
            }
          }
        }

        iterationChanges.clear();
        newIterationsTabItems.clear();

        dispatch.execute(new UpdateContact(contactDTO.getId(), valueChanges), new CommandResultHandler<VoidResult>() {

          @Override
          public void onCommandFailure(final Throwable caught) {
            N10N.error(I18N.CONSTANTS.save(), I18N.CONSTANTS.saveError());
          }

          @Override
          protected void onCommandSuccess(final VoidResult result) {

            N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.saveConfirm());
            eventBus.fireEvent(new UpdateEvent(UpdateEvent.CONTACT_UPDATE, contactDTO));

            // Checks if there is any update needed to the local project instance.
            for (final ValueEvent event : valueChanges) {
              if (event.getSource() instanceof DefaultContactFlexibleElementDTO) {
                updateCurrentContact(contactDTO, (DefaultContactFlexibleElementDTO) event.getSource(), event.getSingleValue());
              }
            }

            valueChanges.clear();

            if (callback != null) {
              callback.onSuccess(contactDTO);
            }
            refresh(contactDTO);
          }
        }, view.getSaveButton(), new LoadingMask(view.getDetailsContainer()), new LoadingMask(target));
      }
    }, view.getSaveButton(), new LoadingMask(view.getDetailsContainer()), new LoadingMask(target));
  }

  private void updateCurrentContact(final ContactDTO contactDTO, DefaultContactFlexibleElementDTO element, String value) {
    if (!element.getType().isUpdatable()) {
      return;
    }

    switch (element.getType()) {
      case COUNTRY:
        dispatch.execute(new GetCountry(Integer.parseInt(value)), new AsyncCallback<CountryDTO>() {
          @Override
          public void onFailure(Throwable caught) {
            Log.error("Error while updating default contact information.", caught);
          }

          @Override
          public void onSuccess(CountryDTO country) {
            contactDTO.setCountry(country);
          }
        });
        break;
      case DIRECT_MEMBERSHIP:
        dispatch.execute(new GetContact(Integer.parseInt(value), ContactDTO.Mode.MAIN_INFORMATION), new AsyncCallback<ContactDTO>() {
          @Override
          public void onFailure(Throwable caught) {
            Log.error("Error while updating default contact information.", caught);
          }

          @Override
          public void onSuccess(ContactDTO parent) {
            if (parent == null) {
              contactDTO.setParent(null);
              contactDTO.setRoot(null);
            }

            contactDTO.setParent(parent);
            if (parent.getRoot() != null) {
              contactDTO.setRoot(parent.getRoot());
            } else {
              contactDTO.setRoot(parent);
            }
          }
        });
        break;
      case EMAIL_ADDRESS:
        contactDTO.setEmail(value);
        break;
      case FIRST_NAME:
        contactDTO.setFirstname(value);
        break;
      case FAMILY_NAME:
        // fall through
      case ORGANIZATION_NAME:
        contactDTO.setName(value);
        break;
      case PHONE_NUMBER:
        contactDTO.setPhoneNumber(value);
        break;
      case PHOTO:
        contactDTO.setPhoto(value);
        break;
      case POSTAL_ADDRESS:
        contactDTO.setPostalAddress(value);
        break;

      // Ignored element types
      // Should always be unmodifiable
      case LOGIN:
      case MAIN_ORG_UNIT:
      case SECONDARY_ORG_UNITS:
      case TOP_MEMBERSHIP:
      case CREATION_DATE:
        break;
      default:
        throw new IllegalStateException("Unknown DefaultContactFlexibleElementType : " + element.getType());
    }
  }

  private String getCurrentSingleValue(DefaultContactFlexibleElementType type, String oldValue) {
    String singleValue = oldValue;

    for (ValueEvent valueChange : valueChanges) {
      if (!(valueChange.getSourceElement() instanceof DefaultContactFlexibleElementDTO)) {
        continue;
      }
      if (((DefaultContactFlexibleElementDTO) valueChange.getSourceElement()).getType() != type) {
        continue;
      }
      singleValue = valueChange.getSingleValue();
    }

    return singleValue;
  }

  /**
   * Method executed on delete contact action.
   *
   * @param contact
   *          The contact to delete.
   */
  private void onDeleteContact(final ContactDTO contact) {

    if (contact == null || !canDeleteContact()) {
      return;
    }
    N10N.confirmation(I18N.CONSTANTS.confirmDeleteContactMessageBoxTitle(), I18N.CONSTANTS.confirmDeleteContactMessageBoxContent(), new ConfirmCallback() {

      /**
       * OK action.
       */
      @Override
      public void onAction() {

        final Map<String, Object> changes = new HashMap<String, Object>();
        changes.put("dateDeleted", new Date());

        dispatch.execute(new UpdateEntity(contact, changes), new CommandResultHandler<VoidResult>() {

          @Override
          public void onCommandSuccess(final VoidResult result) {

            final PageRequest currentRequest = injector.getPageManager().getCurrentPageRequest(false);
            eventBus.fireEvent(new UpdateEvent(UpdateEvent.CONTACT_DELETE, currentRequest));

            N10N.infoNotif(I18N.CONSTANTS.deleteContactNotificationTitle(), I18N.CONSTANTS.deleteContactNotificationContent());
          }
        }, view.getDeleteButton());

      }
    });
  }

  /**
   * Returns if the current authenticated user is authorized to delete a contact.
   *
   * @return {@code true} if the current authenticated user is authorized to delete a contact.
   */
  private boolean canDeleteContact() {
    return ProfileUtils.isGranted(auth(), GlobalPermissionEnum.DELETE_VISIBLE_CONTACTS);
  }

  /**
   * Method executed on export contact action.
   *
   * @param contact
   *          The contact to export.
   */
  private void onExportContact(final ContactDTO contact) {

    view.buildExportDialog(new ExportActionHandler() {

      @Override
      public void onExportContact(final boolean characteristicsField, final boolean allRelationsField, final boolean frameworkRelationsField, final boolean relationsByElementField) {

        final ServletUrlBuilder urlBuilder =
            new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), Servlet.EXPORT, ServletMethod.EXPORT_CONTACT);

        urlBuilder.addParameter(RequestParameter.ID, contact.getId());
        urlBuilder.addParameter(RequestParameter.WITH_CHARACTERISTICS, characteristicsField);
        urlBuilder.addParameter(RequestParameter.WITH_ALL_RELATIONS, allRelationsField);
        urlBuilder.addParameter(RequestParameter.WITH_FRAMEWORK_RELATIONS, frameworkRelationsField);
        urlBuilder.addParameter(RequestParameter.WITH_RELATIONS_BY_ELEMENT, relationsByElementField);

        final FormElement form = FormElement.as(DOM.createForm());
        form.setAction(urlBuilder.toString());
        form.setTarget("_downloadFrame");
        form.setMethod(Method.POST.name());

        RootPanel.getBodyElement().appendChild(form);

        form.submit();
        form.removeFromParent();
      }
    });
  }
}
