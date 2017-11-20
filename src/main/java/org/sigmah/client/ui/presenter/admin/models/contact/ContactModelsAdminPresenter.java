package org.sigmah.client.ui.presenter.admin.models.contact;
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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.extjs.gxt.ui.client.widget.form.Field;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.admin.models.FlexibleElementsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.base.AbstractModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.importer.ImportationSchemeModelsAdminPresenter;
import org.sigmah.client.ui.view.admin.models.contact.ContactModelsAdminView;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetContactModel;
import org.sigmah.shared.command.GetContactModelCopy;
import org.sigmah.shared.command.GetContactModels;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

public class ContactModelsAdminPresenter extends AbstractModelsAdminPresenter<ContactModelDTO, ContactModelsAdminPresenter.View> {
  @ImplementedBy(ContactModelsAdminView.class)
  public interface View extends AbstractModelsAdminPresenter.View<ContactModelDTO> {

    Field<String> getNameField();

    Field<ContactModelType> getContactModelTypeField();

  }

  @Inject
  protected ContactModelsAdminPresenter(ContactModelsAdminPresenter.View view, Injector injector, Provider<FlexibleElementsAdminPresenter<ContactModelDTO>> flexibleElementsProvider, final Provider<ImportationSchemeModelsAdminPresenter<ContactModelDTO>> importationSchemeModelsAdminPresenterProvider) {
    super(view, injector, flexibleElementsProvider.get(), importationSchemeModelsAdminPresenterProvider.get());
  }

  @Override
  public Page getPage() {
    return Page.ADMIN_CONTACT_MODELS;
  }

  @Override
  protected String getUpdateEventKey() {
    return UpdateEvent.CONTACT_MODEL_ADD;
  }

  @Override
  protected Page getAddModelPage() {
    return Page.ADMIN_ADD_CONTACT_MODEL;
  }

  @Override
  protected String getModelToImport() {
    return AdminUtil.ADMIN_CONTACT_MODEL;
  }

  @Override
  protected Command<ListResult<ContactModelDTO>> getLoadModelsCommand() {
    return new GetContactModels();
  }

  @Override
  protected Command<ContactModelDTO> getLoadModelCommand(final Integer modelId) {
    return new GetContactModel(modelId);
  }

  @Override
  protected void onStatusChangeEvent(final Integer modelId, final ProjectModelStatus currentStatus, final ProjectModelStatus targetStatus,
                                     final AsyncCallback<Void> callback) {

    if (currentStatus == ProjectModelStatus.DRAFT) {

      N10N.confirmation(I18N.MESSAGES.adminModelStatusChangeBox(), I18N.MESSAGES.adminModelDraftStatusChange(ProjectModelStatus.getName(targetStatus)),
          new ConfirmCallback() {

            /**
             * On OK.
             */
            @Override
            public void onAction() {
              // TODO: check if there is a contact implementing the updated model
            }

          }, new ConfirmCallback() {

            /**
             * On CANCEL.
             */
            @Override
            public void onAction() {
              callback.onFailure(null);
            }
          });

    } else if (currentStatus == ProjectModelStatus.UNAVAILABLE) {
      // "UNAVAILABLE" model needs to be checked in a different way.
    }
  }

  @Override
  protected void onSaveAction(final ContactModelDTO currentModel, final AsyncCallback<ContactModelDTO> callback) {
    final String nameValue = view.getNameField().getValue();
    final ContactModelType type = view.getContactModelTypeField().getValue();
    final EnumModel<ProjectModelStatus> statusModel = view.getHeaderStatusField().getValue();
    final ProjectModelStatus status = statusModel != null ? statusModel.getEnum() : null;

    final Map<String, Object> modelProperties = new HashMap<String, Object>();
    modelProperties.put(AdminUtil.PROP_CM_NAME, nameValue);
    modelProperties.put(AdminUtil.PROP_CM_STATUS, status);
    modelProperties.put(AdminUtil.ADMIN_CONTACT_MODEL, currentModel);
    modelProperties.put(AdminUtil.PROP_CM_TYPE, type);
    modelProperties.put(AdminUtil.PROP_CM_MAINTENANCE_DATE, getMaintenanceDate());

    dispatch.execute(new CreateEntity(ContactModelDTO.ENTITY_NAME, modelProperties), new CommandResultHandler<CreateResult>() {

      @Override
      public void onCommandFailure(final Throwable caught) {
        N10N.error(I18N.CONSTANTS.adminContactModelCreationBox(),
            I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminContactModelStandard() + " '" + nameValue + "'"));
        callback.onFailure(caught);
      }

      @Override
      public void onCommandSuccess(final CreateResult result) {

        if (result == null || result.getEntity() == null) {
          N10N.warn(I18N.CONSTANTS.adminContactModelCreationBox(),
              I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminContactModelStandard() + " '" + nameValue + "'"));
          return;
        }

        final ContactModelDTO contactModelDTO = (ContactModelDTO) result.getEntity();

        callback.onSuccess(contactModelDTO);

        N10N.infoNotif(I18N.CONSTANTS.adminContactModelCreationBox(),
            I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminContactModelStandard() + " '" + nameValue + "'"));
      }
    }, view.getHeaderSaveButton(), new LoadingMask(view.getHeaderForm()));
  }

  @Override
  protected void onDuplicateAction(final ContactModelDTO model) {
    dispatch.execute(new GetContactModelCopy(model.getId(), I18N.MESSAGES.copyOf(model.getName())),
        new CommandResultHandler<ContactModelDTO>() {

          @Override
          public void onCommandFailure(final Throwable caught) {
            N10N.error(I18N.CONSTANTS.adminContactModelCopy(), I18N.CONSTANTS.adminContactModelCopyError());
          }

          @Override
          public void onCommandSuccess(final ContactModelDTO result) {
            if (result == null) {
              return;
            }

            view.getStore().add(result);
            view.getStore().commitChanges();

            // Selects the model in the grid.
            view.getGrid().getSelectionModel().select(result, false);

            // Shows notification.
            N10N.infoNotif(I18N.CONSTANTS.adminContactModelCopy(), I18N.CONSTANTS.adminContactModelCopyDetail());
          }
        }, view.getGridDuplicateButton(), view.getGridMask());
  }
}
