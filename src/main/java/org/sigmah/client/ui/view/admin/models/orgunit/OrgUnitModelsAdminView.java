package org.sigmah.client.ui.view.admin.models.orgunit;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.orgunit.OrgUnitModelsAdminPresenter;
import org.sigmah.client.ui.view.admin.models.base.AbstractModelsAdminView;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.inject.Singleton;

/**
 * {@link OrgUnitModelsAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class OrgUnitModelsAdminView extends AbstractModelsAdminView<OrgUnitModelDTO> implements OrgUnitModelsAdminPresenter.View {

	private TextField<String> nameField;
	private TextField<String> typeField;
	private ComboBox<EnumModel<ProjectModelStatus>> statusField;
	private CheckBox hasBudgetField;
	private CheckBox canContainProjectsField;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ColumnModel getColumnModel() {
		return new OrgUnitModelsColumnsProvider() {

			@Override
			GridEventHandler<OrgUnitModelDTO> getGridEventHandler() {
				return OrgUnitModelsAdminView.super.getGridEventHandler();
			}

		}.getColumnModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<EnumModel<ProjectModelStatus>> getHeaderStatusField() {
		return statusField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FormPanel buildHeaderForm() {

		nameField = Forms.text(I18N.CONSTANTS.adminProjectModelsName(), true);
		typeField = Forms.text(I18N.CONSTANTS.adminOrgUnitsModelTitle(), true);
		statusField = Forms.combobox(I18N.CONSTANTS.adminProjectModelsStatus(), true, EnumModel.VALUE_FIELD, EnumModel.DISPLAY_FIELD);
		hasBudgetField = Forms.checkbox(I18N.CONSTANTS.adminOrgUnitsModelHasBudget(), null, I18N.CONSTANTS.adminOrgUnitsModelHasBudget(), false);
		canContainProjectsField =
				Forms.checkbox(I18N.CONSTANTS.adminOrgUnitsModelContainProjects(), null, I18N.CONSTANTS.adminOrgUnitsModelContainProjects(), false);

		final FormPanel headerForm = Forms.panel(170);

		headerForm.add(nameField);
		headerForm.add(typeField);
		headerForm.add(statusField);
		headerForm.add(hasBudgetField);
		headerForm.add(canContainProjectsField);

		return headerForm;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String loadModelHeader(final OrgUnitModelDTO model) {
		nameField.setValue(model.getName());
		typeField.setValue(model.getTitle());
		statusField.setValue(new EnumModel<ProjectModelStatus>(model.getStatus()));
		hasBudgetField.setValue(model.getHasBudget());
		canContainProjectsField.setValue(model.getCanContainProjects());
		return model.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getNameField() {
		return nameField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getTypeField() {
		return typeField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getHasBudgetField() {
		return hasBudgetField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getCanContainProjectsField() {
		return canContainProjectsField;
	}

}
