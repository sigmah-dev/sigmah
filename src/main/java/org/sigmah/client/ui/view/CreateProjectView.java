package org.sigmah.client.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.CreateProjectPresenter;
import org.sigmah.client.ui.presenter.CreateProjectPresenter.DeleteTestProjectAction;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.sigmah.shared.dto.util.EntityConstants;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;

/**
 * Create project view.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class CreateProjectView extends AbstractPopupView<PopupWidget> implements CreateProjectPresenter.View {

	// CSS.
	private static final String CSS_PROJECT_TYPE = "create-project-type";
	private static final String CSS_PROJECT_TYPE_IMAGE = "create-project-type-image";
	private static final String CSS_PROJECT_TYPE_LABEL = "create-project-type-label";

	private FormPanel formPanel;
	private TextField<String> nameField;
	private TextField<String> fullNameField;
	private NumberField budgetField;
	private ComboBox<OrgUnitDTO> orgUnitsField;
	private ComboBox<ProjectModelDTO> modelsField;
	private SimplePanel modelTypeImage;
	private Label modelTypeLabel;
	private LabelField percentageField;
	private LabelField baseProjectBudgetField;
	private NumberField amountField;
	private DeleteTestProjectAction deleteTestProjectAction;
	private Grid<ProjectDTO> testProjectsGrid;
	private Button createButton;

	/**
	 * Builds the view.
	 */
	public CreateProjectView() {
		super(new PopupWidget(true));
		popup.setWidth("590px");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// --
		// -- Builds the form fields.
		// --

		// Project.
		nameField = Forms.text(I18N.CONSTANTS.projectName(), true, EntityConstants.NAME_MAX_LENGTH);
		fullNameField = Forms.text(I18N.CONSTANTS.projectFullName(), true, EntityConstants.USER_DATABASE_FULL_NAME_MAX_LENGTH);
		budgetField =
				Forms.number(I18N.CONSTANTS.projectPlannedBudget() + " (" + I18N.CONSTANTS.currencyEuro() + ')', true, false, true,
					NumberFormat.getCurrencyFormat("EUR"));
		orgUnitsField =
				Forms.combobox(I18N.CONSTANTS.orgunit(), true, OrgUnitDTO.ID, OrgUnitDTO.COMPLETE_NAME, I18N.CONSTANTS.orgunitEmptyChoice(),
					new ListStore<OrgUnitDTO>());
		modelsField =
				Forms.combobox(I18N.CONSTANTS.projectModel(), true, ProjectModelDTO.ID, ProjectModelDTO.NAME, I18N.CONSTANTS.projectModelEmptyChoice(),
					new ListStore<ProjectModelDTO>());

		// Project model type.
		modelTypeImage = new SimplePanel();
		modelTypeImage.addStyleName(CSS_PROJECT_TYPE_IMAGE);
		modelTypeLabel = new Label();
		modelTypeLabel.addStyleName(CSS_PROJECT_TYPE_LABEL);
		final HorizontalPanel modelTypePanel = new HorizontalPanel();
		modelTypePanel.addStyleName(CSS_PROJECT_TYPE);
		modelTypePanel.add(modelTypeImage);
		modelTypePanel.add(modelTypeLabel);

		// Founding.
		baseProjectBudgetField = Forms.label("");
		amountField = Forms.number(null, false, false, false, NumberFormat.getCurrencyFormat("EUR"));
		percentageField = Forms.label(I18N.CONSTANTS.createProjectPercentage());

		// Tests.
		testProjectsGrid = new Grid<ProjectDTO>(new ListStore<ProjectDTO>(), buildTestProjectsColumnModel());
		testProjectsGrid.setAutoExpandColumn(ProjectDTO.FULL_NAME);
		testProjectsGrid.setHeight(200);
		testProjectsGrid.getView().setForceFit(true);
		testProjectsGrid.getStore().setMonitorChanges(true);

		// Buttons.
		createButton = Forms.button(I18N.CONSTANTS.createProjectCreateButton());

		// Builds the form.
		formPanel = Forms.panel(170);

		formPanel.add(nameField);
		formPanel.add(fullNameField);
		formPanel.add(budgetField);
		formPanel.add(orgUnitsField);
		formPanel.add(modelsField);
		formPanel.add(modelTypePanel);
		formPanel.add(baseProjectBudgetField);
		formPanel.add(amountField);
		formPanel.add(percentageField);
		formPanel.add(testProjectsGrid);
		formPanel.addButton(createButton);

		initPopup(formPanel);

	}

	/**
	 * Build the columns for the test project grid.
	 * 
	 * @return the columns for the test project grid.
	 */
	private ColumnModel buildTestProjectsColumnModel() {

		final List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		ColumnConfig column;

		// Name.
		column = new ColumnConfig(ProjectDTO.NAME, I18N.CONSTANTS.projectName(), 50);
		column.setDataIndex(ProjectDTO.NAME);
		column.setWidth(135);
		column.setAlignment(HorizontalAlignment.RIGHT);
		columns.add(column);

		// Fullname.
		column = new ColumnConfig(ProjectDTO.FULL_NAME, I18N.CONSTANTS.projectFullName(), 100);
		column.setDataIndex(ProjectDTO.FULL_NAME);
		column.setWidth(320);
		columns.add(column);

		// Delete button.
		column = new ColumnConfig();
		column.setWidth(30);
		column.setId(ProjectDTO.ID);
		column.setDataIndex(ProjectDTO.ID);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ProjectDTO> store,
					Grid<ProjectDTO> grid) {

				final Button deleteBouton = Forms.button("", IconImageBundle.ICONS.delete());
				deleteBouton.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(final ButtonEvent ce) {
						deleteTestProjectAction.deleteTestProject(model, deleteBouton);
					}

				});
				deleteBouton.setWidth(24);
				deleteBouton.setData("testProjectId", model.getId());

				return deleteBouton;

			}
		});
		columns.add(column);

		return new ColumnModel(columns);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getFormPanel() {
		return formPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextField<String> getNameField() {
		return nameField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextField<String> getFullNameField() {
		return fullNameField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NumberField getBudgetField() {
		return budgetField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<OrgUnitDTO> getOrgUnitsField() {
		return orgUnitsField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<ProjectModelDTO> getModelsField() {
		return modelsField;
	}

	public LabelField getBaseProjectBudgetField() {
		return baseProjectBudgetField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NumberField getAmountField() {
		return amountField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LabelField getPercentageField() {
		return percentageField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getCreateButton() {
		return createButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectModelType(ProjectModelType type) {

		modelTypeImage.clear();
		modelTypeLabel.setText(null);

		if (type != null) {
			modelTypeImage.add(FundingIconProvider.getProjectTypeIcon(type, IconSize.MEDIUM).createImage());
			modelTypeLabel.setText(ProjectModelType.getName(type));
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDeleteTestProjectAction(DeleteTestProjectAction deleteTestProjectAction) {
		this.deleteTestProjectAction = deleteTestProjectAction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Grid<ProjectDTO> getTestProjectsField() {
		return testProjectsGrid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTitle(String title) {
		setPopupTitle(title);
	}

}
