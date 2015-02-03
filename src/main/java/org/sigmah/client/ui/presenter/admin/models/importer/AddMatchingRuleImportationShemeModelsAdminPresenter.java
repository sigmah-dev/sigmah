package org.sigmah.client.ui.presenter.admin.models.importer;

import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.admin.models.importer.AddMatchingRuleImportationShemeModelsAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.MessageElementDTO;
import org.sigmah.shared.dto.importation.VariableBudgetSubFieldDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;

/**
 * Popup to add or edit a matching rule on a link between an importation scheme
 * and a project model/org unit model.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class AddMatchingRuleImportationShemeModelsAdminPresenter extends AbstractPagePresenter<AddMatchingRuleImportationShemeModelsAdminPresenter.View> {

	private EntityDTO<Integer> currentModel;
	private ImportationSchemeModelDTO currentImportationScheme;

	/**
	 * Description of the view managed by this presenter.
	 */

	@ImplementedBy(AddMatchingRuleImportationShemeModelsAdminView.class)
	public static interface View extends ViewPopupInterface {

		ComboBox<VariableDTO> getVariablesCombo();

		ComboBox<FlexibleElementDTO> getFlexibleElementsCombo();

		CheckBox getIsKeyCheckBox();

		Text getIdKeyText();

		Button getSubmitButton();

		FlexTable getBudgetSubFlexTable();

		void clearForm();

		ListStore<FlexibleElementDTO> getFlexibleElementStore();

		ListStore<VariableDTO> getVariableStore();

		ContentPanel getMainPanel();

		boolean isValid();
	}

	@Inject
	protected AddMatchingRuleImportationShemeModelsAdminPresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public Page getPage() {

		return Page.ADMIN_ADD_IMPORTATION_SCHEME_MODEL_MATCHING_RULE;

	}

	@Override
	public void onBind() {

		// On Select Combo Champs (case budget)

		view.getFlexibleElementsCombo().addListener(Events.Select, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent event) {
				final boolean selectionIsBudget = view.getFlexibleElementsCombo().getValue() instanceof BudgetElementDTO;

				view.getBudgetSubFlexTable().setVisible(selectionIsBudget);
				view.getVariablesCombo().setVisible(!selectionIsBudget);
				view.getVariablesCombo().setAllowBlank(selectionIsBudget);
			}

		});

		// Save Matching rule

		view.getSubmitButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if(!view.isValid()) {
					return;
				}
				
				final boolean forKey = view.getIsKeyCheckBox().getValue();
				
				if(forKey && view.getFlexibleElementsCombo().getValue() == null) {
					N10N.error(I18N.CONSTANTS.adminImportKeyIdentification(),
						I18N.CONSTANTS.adminImportKeyIdentificationMessage());
				
				} else {
					createVariableFlexibleElement(forKey);
				}
			}

		});

	}
	
	@Override
	public void onPageRequest(PageRequest request) {

		currentModel = request.getData(RequestParameter.MODEL);
		currentImportationScheme = request.getData(RequestParameter.IMPORTATION_SCHEME_MODEL);
		final Boolean forKey = request.getData(RequestParameter.FOR_KEY);

		view.clearForm();

		initForm();

		setKeyVisible(forKey != null && forKey);

		setPageTitle(I18N.CONSTANTS.adminAddKeyVariableFlexibleElementHeading());

	}

	/**
	 * Load importation Scheme Model Variable And model fields
	 */
	private void initForm() {

		// INIT CHAMPS

		final List<FlexibleElementDTO> allElements;
		if (currentImportationScheme.getProjectModelDTO() != null) {
			allElements = currentImportationScheme.getProjectModelDTO().getAllElements();
			
		} else if(currentImportationScheme.getOrgUnitModelDTO() != null) {
			allElements = currentImportationScheme.getOrgUnitModelDTO().getAllElements();
			
		} else {
			throw new IllegalArgumentException("Current importation scheme is not linked to a project model nor to an org unit model.");
		}
		
		view.getFlexibleElementStore().removeAll();
		
		for(final FlexibleElementDTO flexibleElement : allElements) {
			if(flexibleElement instanceof DefaultFlexibleElementDTO) {
				final DefaultFlexibleElementDTO defaultFlexibleElement = (DefaultFlexibleElementDTO)flexibleElement;
				defaultFlexibleElement.setLabel(defaultFlexibleElement.getFormattedLabel());
				view.getFlexibleElementStore().add(defaultFlexibleElement);

				if(flexibleElement instanceof BudgetElementDTO) {
					initializeBudgetFlexTable((BudgetElementDTO) flexibleElement, currentImportationScheme.getImportationSchemeDTO().getVariables());
				}
				
			} else if(!(flexibleElement instanceof MessageElementDTO)) {
				view.getFlexibleElementStore().add(flexibleElement);
			} 
		}
		
		view.getFlexibleElementStore().commitChanges();

		// Variable

		view.getVariableStore().add(currentImportationScheme.getImportationSchemeDTO().getVariables());
		view.getVariableStore().commitChanges();
	}
	
	/**
	 * Fill the budget editor with the fields defined in the given budget element.
	 * 
	 * @param budgetElement Budget element to read.
	 * @param variables List of variables in the current importation scheme.
	 */
	private void initializeBudgetFlexTable(BudgetElementDTO budgetElement, List<VariableDTO> variables) {
		// Clear the table.
		view.getBudgetSubFlexTable().clear();
		view.getBudgetSubFlexTable().removeAllRows();
		
		// Add a new row for each budget sub field.
		final List<BudgetSubFieldDTO> subFields = budgetElement.getBudgetSubFields();
		final int size = subFields.size();
		
		for(int y = 0; y < size; y++) {
			final BudgetSubFieldDTO subField = subFields.get(y);
			
			// Label.
			final String label = subField.getType() != null ?
				BudgetSubFieldType.getName(subField.getType()) :
				subField.getLabel();
			
			// Variable store.
			final ListStore<VariableDTO> variableStore = new ListStore<VariableDTO>();
			variableStore.add(variables);
			
			// Variable combo box.
			final ComboBox<VariableDTO> variablesComboBox = new ComboBox<VariableDTO>();
			variablesComboBox.setStore(variableStore);
			variablesComboBox.setDisplayField("name");
			variablesComboBox.setAllowBlank(false);
			
			// Check box.
			final CheckBox checkBox = new CheckBox();
			checkBox.setData("budgetSubFieldId", subField.getId());
			
			// Creating the row.
			view.getBudgetSubFlexTable().setText(y, 0, label);
			view.getBudgetSubFlexTable().setWidget(y, 1, variablesComboBox);
			view.getBudgetSubFlexTable().setWidget(y, 2, checkBox);
		}
	}
	
	private void setKeyVisible(boolean visible) {
		view.getIdKeyText().setVisible(visible);
		view.getIsKeyCheckBox().setValue(visible);
		view.getIsKeyCheckBox().setVisible(visible);
	}
	
	private void createVariableFlexibleElement(boolean forKey) {
		final HashMap<String, Object> properties = new HashMap<String, Object>();
		
		final FlexibleElementDTO flexibleElement = view.getFlexibleElementsCombo().getValue();
		
		properties.put(AdminUtil.ADMIN_IMPORTATION_SCHEME_MODEL, currentImportationScheme);
		properties.put(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT, flexibleElement);
		
		if(flexibleElement instanceof BudgetElementDTO) {
			final ArrayList<VariableBudgetSubFieldDTO> subFields = new ArrayList<VariableBudgetSubFieldDTO>();
			
			final int size = view.getBudgetSubFlexTable().getRowCount();
			for(int y = 0; y < size; y++) {
				final CheckBox budgetSubFieldCheckbox = (CheckBox) view.getBudgetSubFlexTable().getWidget(y, 2);
				if(budgetSubFieldCheckbox.getValue()) {
					final ComboBox<VariableDTO> budgetSubFieldComboBox = (ComboBox<VariableDTO>) view.getBudgetSubFlexTable().getWidget(y, 1);
					final Integer budgetSubFieldId = budgetSubFieldCheckbox.getData("budgetSubFieldId");
					
					if(budgetSubFieldComboBox.getValue() != null && budgetSubFieldId != null) {
						final VariableBudgetSubFieldDTO variableBudgetSubField = new VariableBudgetSubFieldDTO();
						variableBudgetSubField.setBudgetSubFieldDTO(new BudgetSubFieldDTO(budgetSubFieldId));
						variableBudgetSubField.setVariableDTO(budgetSubFieldComboBox.getValue());
						
						subFields.add(variableBudgetSubField);
					}
				}
			}
			
			properties.put(AdminUtil.PROP_VAR_FLE_BUDGETSUBFIELDS, subFields);
		}
		
		properties.put(AdminUtil.PROP_VAR_VARIABLE, view.getVariablesCombo().getValue());
		properties.put(AdminUtil.PROP_VAR_FLE_ID_KEY, forKey);
		
		dispatch.execute(new CreateEntity(ImportationSchemeModelDTO.ENTITY_NAME, properties), new CommandResultHandler<CreateResult>() {

			@Override
			protected void onCommandSuccess(CreateResult result) {
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.IMPORTATION_MATCHING_RULE_UPDATE, result.getEntity()));
				hideView();
			}
		}, view.getSubmitButton());
	}
}
