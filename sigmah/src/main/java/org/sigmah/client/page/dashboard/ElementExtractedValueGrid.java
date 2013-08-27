package org.sigmah.client.page.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.domain.ElementExtractedValue;
import org.sigmah.shared.domain.element.BudgetSubFieldType;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.OrgUnitDTOLight;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class ElementExtractedValueGrid extends ContentPanel {
	private Grid<ElementExtractedValue> grid;
	private ListStore<ElementExtractedValue> flexibleElementExtractedValuesStore;
	private Button confirmButton;

	public ElementExtractedValueGrid(Dispatcher dispatcher, List<ElementExtractedValue> flexibleElementExtractedValues,
	                EntityDTO container) {

		grid = builElementExtractedValue();
		grid.getSelectionModel().selectAll();
		grid.disableEvents(true);
		setPixelSize(500, 200);
		setLayout(new FitLayout());

		flexibleElementExtractedValuesStore.add(flexibleElementExtractedValues);
		flexibleElementExtractedValuesStore.commitChanges();

		add(grid);

		confirmButton = new Button(I18N.CONSTANTS.save());

		ToolBar toolbar = new ToolBar();
		toolbar.setAlignment(HorizontalAlignment.CENTER);
		toolbar.add(confirmButton);

		setBottomComponent(toolbar);

		setHeaderVisible(true);

		String entityInformation = "";
		if (container instanceof OrgUnitDTOLight) {
			OrgUnitDTOLight orgUnit = (OrgUnitDTOLight) container;
			entityInformation = orgUnit.getFullName() + "(" + orgUnit.getName() + ")";
		} else {
			ProjectDTO project = (ProjectDTO) container;
			entityInformation = project.getFullName() + "(" + project.getName() + ")";
		}

		setHeading(I18N.MESSAGES.importConfirmationDetailsHeading(entityInformation));

		layout();
	}

	private Grid<ElementExtractedValue> builElementExtractedValue() {
		flexibleElementExtractedValuesStore = new ListStore<ElementExtractedValue>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();

		CheckBoxSelectionModel<ElementExtractedValue> checkBoxColumn = new CheckBoxSelectionModel<ElementExtractedValue>();

		configs.add(checkBoxColumn.getColumn());

		column = new ColumnConfig("fieldLabel", I18N.CONSTANTS.adminFlexible(), 100);
		column.setRenderer(new GridCellRenderer<ElementExtractedValue>() {

			@Override
			public Object render(final ElementExtractedValue model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ElementExtractedValue> store, Grid<ElementExtractedValue> grid) {
				String elementLabel = "default";
				if (model.getElement() instanceof BudgetElementDTO) {
					BudgetElementDTO budgetElement = (BudgetElementDTO) model.getElement();
					String budgetSubFieldNames = "";
					for (BudgetSubFieldDTO budgetSubField : budgetElement.getBudgetSubFieldsDTO()) {
						if (model.getNewBudgetValues().containsKey(budgetSubField.getId())) {
							if (budgetSubField.getType() != null) {
								budgetSubFieldNames += BudgetSubFieldType.getName(budgetSubField.getType());
							} else {
								budgetSubFieldNames += budgetSubField.getLabel() + "; ";

							}
						}
					}
					elementLabel = budgetSubFieldNames;
				} else if (model.getElement() instanceof DefaultFlexibleElementDTO) {
					DefaultFlexibleElementDTO defaultElement = (DefaultFlexibleElementDTO) model.getElement();
					elementLabel = DefaultFlexibleElementType.getName(defaultElement.getType());

				} else {
					elementLabel = model.getElement().getLabel();
				}
				return elementLabel;
			}

		});
		configs.add(column);

		column = new ColumnConfig("oldValue", I18N.CONSTANTS.adminImportOldValue(), 70);
		column.setRenderer(new GridCellRenderer<ElementExtractedValue>() {

			@Override
			public Object render(final ElementExtractedValue model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ElementExtractedValue> store, Grid<ElementExtractedValue> grid) {
				String elementValue = "";
				if (model.getOldValue() != null || model.getOldBudgetValues() != null) {
					if (model.getElement() instanceof BudgetElementDTO) {
						BudgetElementDTO budgetElement = (BudgetElementDTO) model.getElement();
						String budgetSubFieldValues = "";
						for (BudgetSubFieldDTO budgetSubField : budgetElement.getBudgetSubFieldsDTO()) {
							if (model.getNewBudgetValues().containsKey(budgetSubField.getId())) {
								if (model.getOldBudgetValues().containsKey(budgetSubField.getId())) {
									budgetSubFieldValues += model.getOldBudgetValues().get(budgetSubField.getId());
								} else {
									budgetSubFieldValues += "0.0; ";

								}
							}
						}
						elementValue = budgetSubFieldValues;
					} else {
						elementValue = model.getOldValue().toString();
					}
				}
				return elementValue;
			}

		});
		configs.add(column);

		column = new ColumnConfig("newValue", I18N.CONSTANTS.adminImportNewValue(), 70);
		column.setRenderer(new GridCellRenderer<ElementExtractedValue>() {

			@Override
			public Object render(final ElementExtractedValue model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ElementExtractedValue> store, Grid<ElementExtractedValue> grid) {
				String elementValue = "";
				if (model.getNewValue() != null || model.getNewBudgetValues() != null) {
					if (model.getElement() instanceof BudgetElementDTO) {
						BudgetElementDTO budgetElement = (BudgetElementDTO) model.getElement();
						String budgetSubFieldValues = "";
						for (BudgetSubFieldDTO budgetSubField : budgetElement.getBudgetSubFieldsDTO()) {
							if (model.getNewBudgetValues().containsKey(budgetSubField.getId())) {
								budgetSubFieldValues += model.getNewBudgetValues().get(budgetSubField.getId());
							}
						}
						elementValue = budgetSubFieldValues;
					} else {
						elementValue = model.getNewValue().toString();
					}
				}
				return elementValue;
			}

		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<ElementExtractedValue> elementExtractedValueGrid = new Grid<ElementExtractedValue>(
		                flexibleElementExtractedValuesStore, cm);
		elementExtractedValueGrid.setSelectionModel(checkBoxColumn);
		elementExtractedValueGrid.setBorders(true);
		elementExtractedValueGrid.setBorders(true);
		elementExtractedValueGrid.setAutoHeight(true);
		elementExtractedValueGrid.setAutoWidth(false);
		elementExtractedValueGrid.getView().setForceFit(true);
		return elementExtractedValueGrid;
	}

	/**
	 * @return the confirmButton
	 */
	public Button getConfirmButton() {
		return confirmButton;
	}

	/**
	 * @param confirmButton
	 *            the confirmButton to set
	 */
	public void setConfirmButton(Button confirmButton) {
		this.confirmButton = confirmButton;
	}

	public List<ElementExtractedValue> getGridSelection() {
		return grid.getSelectionModel().getSelectedItems();
	}

}
