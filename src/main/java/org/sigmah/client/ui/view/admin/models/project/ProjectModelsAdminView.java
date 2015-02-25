package org.sigmah.client.ui.view.admin.models.project;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.project.ProjectModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.project.ProjectModelsAdminPresenter.ProjectTypeProvider;
import org.sigmah.client.ui.view.admin.models.base.AbstractModelsAdminView;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.ProjectModelTypeField;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ProjectModelType;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.gwt.user.client.ui.Grid;
import com.google.inject.Singleton;
import java.util.Date;

/**
 * {@link ProjectModelsAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class ProjectModelsAdminView extends AbstractModelsAdminView<ProjectModelDTO> implements ProjectModelsAdminPresenter.View {

	private TextField<String> nameField;
	private ComboBox<EnumModel<ProjectModelStatus>> statusField;
	private ProjectModelTypeField modelTypeField;
	private ProjectTypeProvider projectTypeProvider;
	private AdapterField maintenanceGroupField;
	private CheckBox underMaintenanceField;
	private DateField maintenanceDateField;
	private TimeField maintenanceTimeField;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ColumnModel getColumnModel() {
		return new ProjectModelsColumnsProvider() {

			@Override
			GridEventHandler<ProjectModelDTO> getGridEventHandler() {
				return ProjectModelsAdminView.super.getGridEventHandler();
			}

			@Override
			ProjectTypeProvider getProjectTypeProvider() {
				return projectTypeProvider;
			}

		}.getColumnModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectTypeProvider(final ProjectTypeProvider provider) {
		this.projectTypeProvider = provider;
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
		statusField = Forms.combobox(I18N.CONSTANTS.adminProjectModelsStatus(), true, EnumModel.VALUE_FIELD, EnumModel.DISPLAY_FIELD);
		modelTypeField = new ProjectModelTypeField(I18N.CONSTANTS.adminProjectModelType(), true, Orientation.VERTICAL);
		
		underMaintenanceField = Forms.checkbox(I18N.CONSTANTS.UNDER_MAINTENANCE());
		maintenanceDateField = Forms.date(null, true);
		maintenanceTimeField = Forms.time(null, true);
		
		final Grid grid = new Grid(1, 5);
		grid.setWidget(0, 0, underMaintenanceField);
		grid.setWidget(0, 2, maintenanceDateField);
		grid.setWidget(0, 4, maintenanceTimeField);
		
		maintenanceGroupField = new AdapterField(grid);
		maintenanceGroupField.setFieldLabel("Under maintenance");
		
		final FormPanel headerForm = Forms.panel(140);

		headerForm.add(nameField);
		headerForm.add(statusField);
		headerForm.add(maintenanceGroupField);
		headerForm.add(modelTypeField);

		return headerForm;
	}

	@Override
	public float getDetailsHeaderFormHeight() {
		return 230f;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String loadModelHeader(final ProjectModelDTO model) {
		nameField.setValue(model.getName());
		statusField.setValue(new EnumModel<ProjectModelStatus>(model.getStatus()));
		modelTypeField.setValue(projectTypeProvider.getProjectModelType(model));
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
	public Field<ProjectModelType> getProjectModelTypeField() {
		return modelTypeField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Grid getMaintenanceGrid() {
		return (Grid) maintenanceGroupField.getWidget();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getUnderMaintenanceField() {
		return underMaintenanceField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Date> getMaintenanceDateField() {
		return maintenanceDateField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Time> getMaintenanceTimeField() {
		return maintenanceTimeField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<?> getMaintenanceGroupField() {
		return maintenanceGroupField;
	}
}
