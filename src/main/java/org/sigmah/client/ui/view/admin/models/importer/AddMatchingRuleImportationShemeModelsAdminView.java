package org.sigmah.client.ui.view.admin.models.importer;

import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.importer.AddMatchingRuleImportationShemeModelsAdminPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.MessageElementDTO;
import org.sigmah.shared.dto.importation.VariableDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
@Singleton
public class AddMatchingRuleImportationShemeModelsAdminView extends AbstractPopupView<PopupWidget> implements
																																																	AddMatchingRuleImportationShemeModelsAdminPresenter.View {

	private ComboBox<VariableDTO> variablesCombo;
	private ComboBox<FlexibleElementDTO> flexibleElementsCombo;
	private CheckBox isKeyCheckBox;
	private Text idKeyText;
	private Button submitButton;
	private FlexTable budgetSubFlexTable;
	private ListStore<FlexibleElementDTO> flexibleElementStore;

	private ContentPanel mainPanel;

	@Inject
	protected AddMatchingRuleImportationShemeModelsAdminView() {
		super(new PopupWidget(true), 500);
	}

	@Override
	public void initialize() {

		mainPanel = Panels.content(null);
		mainPanel.setHeaderVisible(false);

		budgetSubFlexTable = new FlexTable();

		flexibleElementsCombo = new ComboBox<FlexibleElementDTO>();
		flexibleElementsCombo.setFieldLabel(I18N.CONSTANTS.adminFlexible());
		flexibleElementStore = new ListStore<FlexibleElementDTO>();

		// displaying the label of the defaultFlexibleElement

		List<FlexibleElementDTO> allElements = null;

		// if (importationSchemeModel.getProjectModelDTO() != null) {
		// allElements = importationSchemeModel.getProjectModelDTO().getAllElements();
		// } else {
		// allElements = importationSchemeModel.getOrgUnitModelDTO().getAllElements();
		// }

		for (FlexibleElementDTO fle : allElements) {

			if (fle instanceof DefaultFlexibleElementDTO) {

				DefaultFlexibleElementDTO dfle = (DefaultFlexibleElementDTO) fle;
				dfle.setLabel(dfle.getFormattedLabel());

				if (fle instanceof BudgetElementDTO) {

					BudgetElementDTO budgetElementDTO = (BudgetElementDTO) fle;
					int y = 0;

					for (BudgetSubFieldDTO bsfDTO : budgetElementDTO.getBudgetSubFields()) {

						ComboBox<VariableDTO> variablesCombo = new ComboBox<VariableDTO>();
						ListStore<VariableDTO> variableStore = new ListStore<VariableDTO>();
						// variableStore.add(importationSchemeModel.getImportationSchemeDTO().getVariablesDTO());
						variablesCombo.setStore(variableStore);
						variablesCombo.setEditable(true);
						variablesCombo.setAllowBlank(false);
						variablesCombo.setDisplayField("name");

						String budgetSubFieldName = bsfDTO.getLabel();

						if (bsfDTO.getType() != null) {
							budgetSubFieldName = BudgetSubFieldType.getName(bsfDTO.getType());
						}

						Text budgetText = new Text(budgetSubFieldName);
						budgetSubFlexTable.setWidget(y, 0, budgetText);
						budgetSubFlexTable.setWidget(y, 1, variablesCombo);
						CheckBox checkBox = new CheckBox();
						checkBox.setData("budgetSubFieldId", bsfDTO.getId());
						budgetText.setVisible(true);
						budgetSubFlexTable.setWidget(y, 2, checkBox);

						y++;
					}
					budgetSubFlexTable.setCellPadding(2);
				}
			}
			if (fle instanceof DefaultFlexibleElementDTO) {
				DefaultFlexibleElementDTO defaultElement = (DefaultFlexibleElementDTO) fle;
				if (!(DefaultFlexibleElementType.COUNTRY.equals(defaultElement.getType()) || DefaultFlexibleElementType.ORG_UNIT.equals(defaultElement.getType()) || DefaultFlexibleElementType.MANAGER
					.equals(defaultElement.getType())) || DefaultFlexibleElementType.OWNER.equals(defaultElement.getType())) {
					flexibleElementStore.add(fle);
				}
			} else if (!(fle instanceof MessageElementDTO)) {
				flexibleElementStore.add(fle);
			}
		}

		flexibleElementsCombo.setStore(flexibleElementStore);
		flexibleElementsCombo.setEditable(true);
		flexibleElementsCombo.setAllowBlank(false);
		flexibleElementsCombo.setDisplayField("label");

		variablesCombo = new ComboBox<VariableDTO>();
		variablesCombo.setFieldLabel(I18N.CONSTANTS.adminImportVariable());
		ListStore<VariableDTO> variableStore = new ListStore<VariableDTO>();

		// variableStore.add(importationSchemeModel.getImportationSchemeDTO().getVariablesDTO());

		variablesCombo.setStore(variableStore);
		variablesCombo.setEditable(true);
		variablesCombo.setAllowBlank(false);
		variablesCombo.setDisplayField("name");

		/*
		 * for (VariableFlexibleElementDTO varfle : importationSchemeModel.getVariableFlexibleElementsDTO()) {
		 * flexibleElementStore.remove(varfle.getFlexibleElementDTO()); }
		 */

		flexibleElementsCombo.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (flexibleElementsCombo.getValue() instanceof BudgetElementDTO) {
					budgetSubFlexTable.setVisible(true);
					variablesCombo.hide();
					variablesCombo.setAllowBlank(true);
				} else {
					budgetSubFlexTable.setVisible(false);
					variablesCombo.show();
					variablesCombo.setAllowBlank(false);
				}

			}

		});

		mainPanel.add(flexibleElementsCombo);
		mainPanel.add(variablesCombo);
		mainPanel.add(budgetSubFlexTable);

		budgetSubFlexTable.setVisible(false);

		isKeyCheckBox = new CheckBox();
		isKeyCheckBox.setBoxLabel(I18N.CONSTANTS.adminImportKeyIdentification());
		isKeyCheckBox.disable();

		mainPanel.add(isKeyCheckBox);

		isKeyCheckBox.hide();

		idKeyText = new Text(I18N.CONSTANTS.adminImportExplicationIdKey());
		idKeyText.hide();

		/*
		 * if (forKey) { isKeyCheckBox.setValue(true); isKeyCheckBox.show();
		 * flexibleElementsCombo.addListener(Events.OnChange, new Listener<BaseEvent>() {
		 * @Override public void handleEvent(BaseEvent be) { if (flexibleElementsCombo.getValue() != null &&
		 * (ElementTypeEnum.DEFAULT.equals(flexibleElementsCombo.getValue().getElementType()) ||
		 * ElementTypeEnum.TEXT_AREA.equals(flexibleElementsCombo .getValue().getElementType()))) {
		 * isKeyCheckBox.setValue(true); } else { isKeyCheckBox.disable(); isKeyCheckBox.setValue(false); idKeyText.show();
		 * } } }); }
		 */

		submitButton = new Button(I18N.CONSTANTS.save());

		/*
		 * submitButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
		 * @Override public void handleEvent(ButtonEvent be) { if (forKey) { if (isKeyCheckBox.getValue() == true &&
		 * flexibleElementsCombo.getValue() != null) { createVariableFlexibleElement(maskingAsyncMonitor, callback, forKey);
		 * } else { MessageBox.alert(I18N.CONSTANTS.adminImportKeyIdentification(),
		 * I18N.CONSTANTS.adminImportKeyIdentificationMessage(), null); } } else {
		 * createVariableFlexibleElement(maskingAsyncMonitor, callback, forKey); } } });
		 */

		mainPanel.add(submitButton);

		initPopup(mainPanel);

	}

	@Override
	public ComboBox<VariableDTO> getVariablesCombo() {
		return variablesCombo;
	}

	@Override
	public ComboBox<FlexibleElementDTO> getFlexibleElementsCombo() {
		return flexibleElementsCombo;
	}

	@Override
	public CheckBox getIsKeyCheckBox() {
		return isKeyCheckBox;
	}

	@Override
	public Text getIdKeyText() {
		return idKeyText;
	}

	@Override
	public Button getSubmitButton() {
		return submitButton;
	}

	@Override
	public FlexTable getBudgetSubFlexTable() {
		return budgetSubFlexTable;
	}

}
