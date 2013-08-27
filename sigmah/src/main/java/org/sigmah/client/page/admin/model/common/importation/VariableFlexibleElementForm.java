package org.sigmah.client.page.admin.model.common.importation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.common.element.ElementTypeEnum;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.domain.element.BudgetSubFieldType;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableBudgetSubFieldDTO;
import org.sigmah.shared.dto.importation.VariableDTO;
import org.sigmah.shared.dto.importation.VariableFlexibleElementDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;

public class VariableFlexibleElementForm extends FormPanel {

	private Dispatcher dispatcher;
	private ComboBox<VariableDTO> variablesCombo;
	private ComboBox<FlexibleElementDTO> flexibleElementsCombo;
	private ImportationSchemeModelDTO importationSchemeModel;
	private CheckBox isKeyCheckBox;
	private Text idKeyText;
	private Button submitButton;
	private FlexTable budgetSubFieldspanel;

	public VariableFlexibleElementForm(Dispatcher dispatcher, final MaskingAsyncMonitor maskingAsyncMonitor,
					final AsyncCallback<CreateResult> callback, ImportationSchemeModelDTO importationSchemeModel,
					final Boolean forKey) {
		this.dispatcher = dispatcher;
		this.importationSchemeModel = importationSchemeModel;
		setHeaderVisible(false);
		setWidth(400);
		
		budgetSubFieldspanel = new FlexTable();

		flexibleElementsCombo = new ComboBox<FlexibleElementDTO>();
		flexibleElementsCombo.setFieldLabel(I18N.CONSTANTS.adminFlexible());
		ListStore<FlexibleElementDTO> flexibleElementStore = new ListStore<FlexibleElementDTO>();

		// displaying the label of the defaultFlexibleElement
		List<FlexibleElementDTO> allElements = null;
		if (importationSchemeModel.getProjectModelDTO() != null) {
			allElements = importationSchemeModel.getProjectModelDTO().getAllElements();
		} else {
			allElements = importationSchemeModel.getOrgUnitModelDTO().getAllElements();
		}
		for (FlexibleElementDTO fle : allElements) {
			if (fle instanceof DefaultFlexibleElementDTO) {
				((DefaultFlexibleElementDTO) fle).setLabel(DefaultFlexibleElementType
								.getName(((DefaultFlexibleElementDTO) fle).getType()));
				if (fle instanceof BudgetElementDTO) {
					
					BudgetElementDTO budgetElementDTO = (BudgetElementDTO) fle;
					int y = 0;
					for (BudgetSubFieldDTO bsfDTO : budgetElementDTO.getBudgetSubFieldsDTO()) {
						ComboBox<VariableDTO> variablesCombo = new ComboBox<VariableDTO>();
						ListStore<VariableDTO> variableStore = new ListStore<VariableDTO>();
						variableStore.add(importationSchemeModel.getImportationSchemeDTO().getVariablesDTO());
						variablesCombo.setStore(variableStore);
						variablesCombo.setEditable(false);
						variablesCombo.setAllowBlank(false);
						variablesCombo.setDisplayField("name");
						
						String budgetSubFieldName = bsfDTO.getLabel();
						if (bsfDTO.getType() != null) {
							budgetSubFieldName = BudgetSubFieldType.getName(bsfDTO.getType());
						}
						Text budgetText = new Text(budgetSubFieldName);
						budgetSubFieldspanel.setWidget(y, 0, budgetText);
						budgetSubFieldspanel.setWidget(y, 1, variablesCombo);
						CheckBox checkBox = new CheckBox();
						checkBox.setData("budgetSubFieldId", bsfDTO.getId());
						budgetText.setVisible(true);
						budgetSubFieldspanel.setWidget(y, 2, checkBox);
						
						y++;
					}
					
				}
			}
			if(fle instanceof DefaultFlexibleElementDTO){
				DefaultFlexibleElementDTO defaultElement = (DefaultFlexibleElementDTO) fle;
				if(!(DefaultFlexibleElementType.COUNTRY.equals(defaultElement.getType())
								|| DefaultFlexibleElementType.ORG_UNIT.equals(defaultElement.getType())
								|| DefaultFlexibleElementType.MANAGER.equals(defaultElement.getType()))
								|| DefaultFlexibleElementType.OWNER.equals(defaultElement.getType())){
					flexibleElementStore.add(fle);
				}
			}
		}

		flexibleElementsCombo.setStore(flexibleElementStore);
		flexibleElementsCombo.setEditable(true);
		flexibleElementsCombo.setAllowBlank(false);
		flexibleElementsCombo.setDisplayField("label");

		variablesCombo = new ComboBox<VariableDTO>();
		variablesCombo.setFieldLabel(I18N.CONSTANTS.adminImportVariable());
		ListStore<VariableDTO> variableStore = new ListStore<VariableDTO>();

		variableStore.add(importationSchemeModel.getImportationSchemeDTO().getVariablesDTO());
		variablesCombo.setStore(variableStore);
		variablesCombo.setEditable(true);
		variablesCombo.setAllowBlank(false);
		variablesCombo.setDisplayField("name");

		for (VariableFlexibleElementDTO varfle : importationSchemeModel.getVariableFlexibleElementsDTO()) {
			variableStore.remove(varfle.getVariableDTO());
			flexibleElementStore.remove(varfle.getFlexibleElementDTO());
		}

		flexibleElementsCombo.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (flexibleElementsCombo.getValue() instanceof BudgetElementDTO) {
					budgetSubFieldspanel.setVisible(true);
					variablesCombo.hide();
					variablesCombo.setAllowBlank(true);
				} else {
					budgetSubFieldspanel.setVisible(false);
					variablesCombo.show();
					variablesCombo.setAllowBlank(false);
				}

			}

		});

		add(flexibleElementsCombo);

		add(variablesCombo);
		

		add(budgetSubFieldspanel);
		budgetSubFieldspanel.setVisible(false);
		
		isKeyCheckBox = new CheckBox();
		isKeyCheckBox.setBoxLabel(I18N.CONSTANTS.adminImportKeyIdentification());
		isKeyCheckBox.disable();
		add(isKeyCheckBox);
		isKeyCheckBox.hide();

		idKeyText = new Text(I18N.CONSTANTS.adminImportExplicationIdKey());
		idKeyText.hide();

		if (forKey) {
			isKeyCheckBox.setValue(true);
			isKeyCheckBox.show();
			flexibleElementsCombo.addListener(Events.OnChange, new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent be) {
					if (flexibleElementsCombo.getValue() != null &&
									(ElementTypeEnum.DEFAULT.equals(flexibleElementsCombo.getValue().getElementType())
									|| ElementTypeEnum.TEXT_AREA.equals(flexibleElementsCombo.getValue()
													.getElementType()))) {
						isKeyCheckBox.setValue(true);
					} else {
						isKeyCheckBox.disable();
						isKeyCheckBox.setValue(false);
						idKeyText.show();
					}
				}
			});
		}

		submitButton = new Button(I18N.CONSTANTS.save());
		submitButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				if (forKey) {
					if (isKeyCheckBox.getValue() == true && flexibleElementsCombo.getValue() != null) {
						createVariableFlexibleElement(maskingAsyncMonitor, callback, forKey);
					} else {
						MessageBox.alert(I18N.CONSTANTS.adminImportKeyIdentification(),
										I18N.CONSTANTS.adminImportKeyIdentificationMessage(), null);

					}
				} else {
					createVariableFlexibleElement(maskingAsyncMonitor, callback, forKey);
				}

			}
		});

		add(submitButton);

	}

	private void createVariableFlexibleElement(MaskingAsyncMonitor maskingAsyncMonitor,
					AsyncCallback<CreateResult> callback, Boolean forKey) {
		if (!this.isValid()) {
			MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
							I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminImportVariable()), null);
			return;
		}
		Map<String, Object> newVariableFlexibleElementProperties = new HashMap<String, Object>();
		newVariableFlexibleElementProperties.put(AdminUtil.ADMIN_IMPORTATION_SCHEME_MODEL, importationSchemeModel);
		newVariableFlexibleElementProperties.put(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT, flexibleElementsCombo.getValue());
		if(flexibleElementsCombo.getValue() instanceof BudgetElementDTO){
			List<VariableBudgetSubFieldDTO> listBudgetSubFields = new ArrayList<VariableBudgetSubFieldDTO>();
			for (int i = 0; i < budgetSubFieldspanel.getRowCount(); i++) {
				if (budgetSubFieldspanel.getWidget(i, 1) != null) {
					CheckBox budgetSubFieldCheckbox = (CheckBox) budgetSubFieldspanel.getWidget(i, 2);
					if(budgetSubFieldCheckbox.getValue()){
						Integer budgetSubFieldId = budgetSubFieldCheckbox.getData("budgetSubFieldId");
						if (budgetSubFieldId != null) {
							VariableBudgetSubFieldDTO vbsf = new VariableBudgetSubFieldDTO();
							BudgetSubFieldDTO bsf = new BudgetSubFieldDTO();
							bsf.setId(budgetSubFieldId);
							vbsf.setBudgetSubFieldDTO(bsf);
							ComboBox<VariableDTO> budgetSubFieldCombo = (ComboBox<VariableDTO>) budgetSubFieldspanel.getWidget(i, 1);
							vbsf.setVariableDTO(budgetSubFieldCombo.getValue());
							listBudgetSubFields.add(vbsf);
						}
					}
				}
			}
			newVariableFlexibleElementProperties.put(AdminUtil.PROP_VAR_FLE_BUDGETSUBFIELDS, listBudgetSubFields);
		}
		newVariableFlexibleElementProperties.put(AdminUtil.PROP_VAR_VARIABLE, variablesCombo.getValue());
		if (forKey) {
			newVariableFlexibleElementProperties.put(AdminUtil.PROP_VAR_FLE_ID_KEY, true);
		}
		CreateEntity cmd = new CreateEntity("ImportationSchemeModel", newVariableFlexibleElementProperties);
		dispatcher.execute(cmd, maskingAsyncMonitor, callback);
	}

}
