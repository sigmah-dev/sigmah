package org.sigmah.client.ui.view.admin.models.contact;
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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.contact.ContactModelsAdminPresenter;
import org.sigmah.client.ui.view.admin.models.base.AbstractModelsAdminView;
import org.sigmah.client.ui.widget.form.ContactModelTypeField;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

public class ContactModelsAdminView extends AbstractModelsAdminView<ContactModelDTO> implements ContactModelsAdminPresenter.View  {

  private TextField<String> nameField;
  private ComboBox<EnumModel<ProjectModelStatus>> statusField;
  private ContactModelTypeField modelTypeField;

  @Override
  public Field<String> getNameField() {
    return nameField;
  }

  @Override
  public Field<ContactModelType> getContactModelTypeField() {
    return modelTypeField;
  }

  @Override
  protected ColumnModel getColumnModel() {
    return new ContactModelsColumnsProvider() {
      @Override
      GridEventHandler<ContactModelDTO> getGridEventHandler() {
        return ContactModelsAdminView.super.getGridEventHandler();
      }
    }.getColumnModel();
  }

  @Override
  protected FormPanel buildHeaderForm() {
    nameField = Forms.text(I18N.CONSTANTS.adminContactModelName(), true);
    statusField = Forms.combobox(I18N.CONSTANTS.adminContactModelStatus(), true, EnumModel.VALUE_FIELD, EnumModel.DISPLAY_FIELD);
    modelTypeField = new ContactModelTypeField(I18N.CONSTANTS.adminContactModelType(), true, Style.Orientation.VERTICAL);

    final FormPanel headerForm = Forms.panel(140);
    headerForm.setAutoHeight(true);

    headerForm.add(nameField);
    headerForm.add(statusField);
    headerForm.add(getMaintenanceGroupField());
    headerForm.add(modelTypeField);
    return headerForm;
  }

  @Override
  protected String loadModelHeader(ContactModelDTO model) {
    nameField.setValue(model.getName());
    statusField.setValue(new EnumModel<ProjectModelStatus>(model.getStatus()));
    if (model.getType() == null) {
      modelTypeField.setValue(ContactModelType.INDIVIDUAL);
    } else {
      modelTypeField.setValue(model.getType());
    }
    getMaintenanceGroupField().setVisible(model.getStatus() == ProjectModelStatus.USED || model.isUnderMaintenance());
    getUnderMaintenanceField().setValue(model.getDateMaintenance() != null);
    getMaintenanceDateField().setValue(model.getDateMaintenance());
    getMaintenanceTimeField().setValue(model.getDateMaintenance() != null ? getMaintenanceTimeField().findModel(model.getDateMaintenance()) : null);
    statusField.setEnabled(model.getDateMaintenance() == null);

    return model.getName();
  }

  @Override
  public ComboBox<EnumModel<ProjectModelStatus>> getHeaderStatusField() {
    return statusField;
  }
}
