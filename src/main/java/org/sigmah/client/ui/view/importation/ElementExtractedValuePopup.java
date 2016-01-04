package org.sigmah.client.ui.view.importation;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.HasGrid;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.ElementExtractedValue;
import org.sigmah.shared.dto.ImportDetails;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.QuestionChoiceElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.element.TripletsListElementDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ElementExtractedValueStatus;
import org.sigmah.shared.util.ValueResultUtils;

/**
 * Displays the changes that will be applied on a project/orgunit after an 
 * import.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class ElementExtractedValuePopup extends PopupWidget implements HasGrid<ElementExtractedValue> {

	private static final String FIELD_LABEL = "fieldLabel";
	private static final String OLD_VALUE = "oldValue";
	private static final String NEW_VALUE = "newValue";
	
	private Grid<ElementExtractedValue> grid;
	private Button confirmButton;
	
    private ImportDetails parentModel;
	private EntityDTO<?> entity;
	
	public ElementExtractedValuePopup() {
		super(true, Layouts.fitLayout());
		setWidth("900px");
		setHeight("500px");
	}
	
	public void initialize() {
		// Building the grid.
		final CheckBoxSelectionModel<ElementExtractedValue> selectionModel = new CheckBoxSelectionModel<ElementExtractedValue>();
		final ColumnModel columnModel = createColumnModel(selectionModel);
		grid = createGrid(columnModel, selectionModel);
		
		grid.addPlugin(selectionModel);
		
		// Creating the OK button.
		confirmButton = Forms.button(I18N.CONSTANTS.ok());
		
		// Preparing the popup.
		setContent(grid);
		addButton(confirmButton);
	}
	
	@Override
	public Grid<ElementExtractedValue> getGrid() {
		return grid;
	}

	@Override
	public ListStore<ElementExtractedValue> getStore() {
		return grid.getStore();
	}
	
	public GridSelectionModel<ElementExtractedValue> getSelectionModel() {
		return grid.getSelectionModel();
	}

	public Button getConfirmButton() {
		return confirmButton;
	}

	@Override
	public void setGridEventHandler(GridEventHandler<ElementExtractedValue> handler) {
	}

    public ImportDetails getParentModel() {
        return parentModel;
    }

    public void setParentModel(ImportDetails parentModel) {
        this.parentModel = parentModel;
    }

	/**
	 * Retrieves the current entity.
	 * 
	 * @return 
	 */
	public EntityDTO<?> getEntity() {
		return entity;
	}
	
	/**
	 * Changes the current entity displayed in this popup and updates the title.
	 * 
	 * @param entity Entity to use.
	 */
	public void setEntity(EntityDTO<?> entity) {
		this.entity = entity;
		updateTitle();
	}
	
	/**
	 * Retrieves the current selection.
	 * 
	 * @return List of {@link ElementExtractedValue} selected by the user.
	 */
	public List<ElementExtractedValue> getSelection() {
		return grid.getSelectionModel().getSelectedItems();
	}
	
	// --
	// Utility methods.
	// --
	
	/**
	 * Modify this popup header with information from the current entity.
	 */
	private void updateTitle() {
		final String information;
		
		if(entity instanceof OrgUnitDTO) {
			final OrgUnitDTO orgUnit = (OrgUnitDTO)entity;
			information = orgUnit.getFullName() + " (" + orgUnit.getName() + ')';
			
		} else if(entity instanceof ProjectDTO) {
			final ProjectDTO project = (ProjectDTO)entity;
			information = project.getFullName() + " (" + project.getName() + ')';
			
		} else {
			throw new IllegalArgumentException("Type not supported: " + entity);
		}
		
		setTitle(I18N.MESSAGES.importConfirmationDetailsHeading(information));
	}
	
	/**
	 * Creates the grid of this popup.
	 * 
	 * @param columnModel
	 * @param selectionModel
	 * @return 
	 */
	private Grid<ElementExtractedValue> createGrid(final ColumnModel columnModel, final GridSelectionModel<ElementExtractedValue> selectionModel) {
		final Grid<ElementExtractedValue> grid = new Grid<ElementExtractedValue>(new ListStore<ElementExtractedValue>(), columnModel);
		
		grid.setSelectionModel(selectionModel);
		grid.getView().setForceFit(true);
		
		return grid;
	}
	
	/**
	 * Creates the column model.
	 * 
	 * @param selectionModel
	 * @return 
	 */
	private ColumnModel createColumnModel(CheckBoxSelectionModel<ElementExtractedValue> selectionModel) {
		// Checkbox column (to select changes to apply).
		final ColumnConfig checkboxColumnConfig = selectionModel.getColumn();
		
		// Field label column.
		final ColumnConfig fieldLabelColumnConfig = new ColumnConfig(FIELD_LABEL, I18N.CONSTANTS.adminFlexible(), 100);
		fieldLabelColumnConfig.setRenderer(new GridCellRenderer<ElementExtractedValue>() {

			@Override
			public Object render(final ElementExtractedValue model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ElementExtractedValue> store, Grid<ElementExtractedValue> grid) {
				return formatLabel(model);
			}
		});
		
		// Old value column.
		final ColumnConfig oldValueColumnConfig = new ColumnConfig(OLD_VALUE, I18N.CONSTANTS.adminImportOldValue(), 70);
		oldValueColumnConfig.setRenderer(new GridCellRenderer<ElementExtractedValue>() {

			@Override
			public Object render(final ElementExtractedValue model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ElementExtractedValue> store, Grid<ElementExtractedValue> grid) {
				if (model.getStatus() == ElementExtractedValueStatus.VALID_VALUE) {
					return formatValue(model.getElement(), model.getOldValue(), model.getNewBudgetValues(), model.getOldBudgetValues());
				}
				return null;
			}
		});
		
		// New value column.
		final ColumnConfig newValueColumnConfig = new ColumnConfig(NEW_VALUE, I18N.CONSTANTS.adminImportNewValue(), 70);
		newValueColumnConfig.setRenderer(new GridCellRenderer<ElementExtractedValue>() {

			@Override
			public Object render(final ElementExtractedValue model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ElementExtractedValue> store, Grid<ElementExtractedValue> grid) {
				if (model.getStatus() == ElementExtractedValueStatus.VALID_VALUE) {
					return formatValue(model.getElement(), model.getNewValue(), model.getNewBudgetValues(), model.getNewBudgetValues());
				} else {
					return ElementExtractedValueStatus.getMessage(model.getStatus());
				}
			}
		});
		
		// Creating the column model.
		final ArrayList<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
		columnConfigs.add(checkboxColumnConfig);
		columnConfigs.add(fieldLabelColumnConfig);
		columnConfigs.add(oldValueColumnConfig);
		columnConfigs.add(newValueColumnConfig);
		
		return new ColumnModel(columnConfigs);
	}
	
	/**
	 * Return a formatted version of the label of the given element.
	 * 
	 * @param model ExtractedValue containing a FlexibleElementDTO.
	 * @return Its label, formatted.
	 */
	private String formatLabel(ElementExtractedValue model) {
		final String elementLabel;
				
		if (model.getElement() instanceof BudgetElementDTO) {
			final BudgetElementDTO budgetElement = (BudgetElementDTO) model.getElement();
			final StringBuilder fieldNameBuilder = new StringBuilder();

			for (BudgetSubFieldDTO subField : budgetElement.getBudgetSubFields()) {
				if (model.getNewBudgetValues().containsKey(subField.getId())) {
					if (subField.getType() != null) {
						fieldNameBuilder.append(BudgetSubFieldType.getName(subField.getType()));
					} else {
						fieldNameBuilder.append(subField.getLabel());
					}
					fieldNameBuilder.append("; ");
				}
			}
			
			if(fieldNameBuilder.length() == 0) {
				fieldNameBuilder.append(DefaultFlexibleElementType.getName(DefaultFlexibleElementType.BUDGET));
			}
			
			elementLabel = fieldNameBuilder.toString();
			
		} else {
			elementLabel = model.getElement().getFormattedLabel();
		}

		return elementLabel;
	}
	
	/**
	 * Return a formatted version of the value of the given element.
	 * 
	 * @param element Element containing the value.
	 * @param value Simple value.
	 * @param budgetHaystack Map of budget values where to search.
	 * @param budgetValues Map of buget values to use.
	 * @return Its value, formatted.
	 */
	private <V extends Serializable> String formatValue(FlexibleElementDTO element, Serializable value, Map<Integer, Serializable> budgetHaystack, Map<Integer, V> budgetValues) {
		final String elementValue;
		
		if(value instanceof String) {
			return element.toHTML((String)value);
		}
		
		// TODO: Modify the code to return values as String (instead of Serializable) and allow the use of element.toHTML(...).
				
		if (element instanceof BudgetElementDTO && !budgetValues.isEmpty()) {
			// --
			// Budget element.
			// --
			final BudgetElementDTO budgetElement = (BudgetElementDTO) element;

			final StringBuilder valueBuilder = new StringBuilder();
			for (BudgetSubFieldDTO subField : budgetElement.getBudgetSubFields()) {
				if (budgetHaystack.containsKey(subField.getId())) {
					final V subFieldValue = budgetValues.get(subField.getId());

					if (subFieldValue != null) {
						valueBuilder.append(subFieldValue);
					} else {
						valueBuilder.append("0.0");
					}
					valueBuilder.append("; ");
				}
			}
			elementValue = valueBuilder.toString();
				
		} else if(value != null) {
			if(element instanceof QuestionElementDTO && isQuestionElementValueValid((QuestionElementDTO)element, value)) {
				// --
				// Question element.
				// --
				final QuestionElementDTO questionElement = (QuestionElementDTO) element;
				
				final List<Integer> choices;
				if(questionElement.getMultiple() != null && questionElement.getMultiple()) {
					choices = ValueResultUtils.splitValuesAsInteger(value.toString());
				} else {
					choices = Collections.singletonList((Integer)value);
				}

				final StringBuilder valueBuilder = new StringBuilder();
				if (choices != null && !choices.isEmpty()) {
					for (QuestionChoiceElementDTO questionChoice : questionElement.getChoices()) {
						if(choices.contains(questionChoice.getId())){
							if(questionChoice.getCategoryElement() != null) {
								valueBuilder.append(questionChoice.getCategoryElement().getLabel());
							} else {
								valueBuilder.append(questionChoice.getLabel());
							}
							valueBuilder.append("; ");
						}
					}
				}
				elementValue = valueBuilder.toString();
				
			} else if(element instanceof TripletsListElementDTO && value instanceof String[]) {
				// --				
				// Triplet list value.
				// --
				final String[] tripletValues = (String[]) value;
				
				elementValue = new StringBuilder()
					.append(I18N.CONSTANTS.flexibleElementTripletsListCode()).append(" : ")
					.append(tripletValues[0]).append(" - ")
					.append(I18N.CONSTANTS.flexibleElementTripletsListName()).append(" : ")
					.append(tripletValues[1]).append(" - ")
					.append(I18N.CONSTANTS.flexibleElementTripletsListPeriod()).append(" : ")
					.append(tripletValues[2])
					.toString();
				
			} else if(element instanceof TextAreaElementDTO
				&& Character.valueOf('D').equals(((TextAreaElementDTO)element).getType())
				&& value instanceof Date) {
				// --
				// Date type text area element.
				// --
				final Date dateValue = (Date) value;
				elementValue = dateValue.toString();
				
			} else {
				// --
				// Basic value.
				// --
				elementValue = value.toString();
			}
			
		} else {
			// --
			// No value.
			// --
			elementValue = null;
		}
		
		return elementValue;
	}
	
	/**
	 * Verify the type of the value for the given <code>questionElement</code>.
	 * 
	 * @param questionElement
	 * @param value
	 * @return 
	 */
	private boolean isQuestionElementValueValid(QuestionElementDTO questionElement, Serializable value) {
		return (questionElement.getMultiple() != null && questionElement.getMultiple() && value instanceof String)
			|| ((questionElement.getMultiple() == null || !questionElement.getMultiple()) && value instanceof Integer);
	}
}
