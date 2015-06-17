package org.sigmah.shared.dto.element;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.project.category.CategoryIconProvider;
import org.sigmah.client.ui.widget.FlexibleGrid;
import org.sigmah.client.ui.widget.HistoryTokenText;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;
import org.sigmah.shared.dto.quality.QualityCriterionDTO;
import org.sigmah.shared.util.ValueResultUtils;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import java.util.Collections;
import java.util.HashSet;
import org.sigmah.shared.dto.referential.ValueEventChangeType;

/**
 * QuestionElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 */
public class QuestionElementDTO extends FlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "element.QuestionElement";

	// DTO attributes keys.
	public static final String CHOICES = "choices";
	public static final String MULTIPLE = "multiple";
	public static final String CATEGORY_TYPE = "categoryType";
	public static final String QUALITY_CRITERION = "qualityCriterion";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	// Question choices list
	public List<QuestionChoiceElementDTO> getChoices() {
		return get(CHOICES);
	}

	public void setChoices(List<QuestionChoiceElementDTO> choices) {
		set(CHOICES, choices);
	}

	// Question multiple mode
	public Boolean getMultiple() {
		return get(MULTIPLE);
	}

	public void setMultiple(Boolean multiple) {
		set(MULTIPLE, multiple);
	}

	// Question category type
	public CategoryTypeDTO getCategoryType() {
		return get(CATEGORY_TYPE);
	}

	public void setCategoryType(CategoryTypeDTO categoryType) {
		set(CATEGORY_TYPE, categoryType);
	}

	// Question quality criterion
	public QualityCriterionDTO getQualityCriterion() {
		return get(QUALITY_CRITERION);
	}

	public void setQualityCriterion(QualityCriterionDTO qualityCriterion) {
		set(QUALITY_CRITERION, qualityCriterion);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {
		final boolean canEdit = enabled && userCanPerformChangeType(ValueEventChangeType.EDIT);
		
		// Question's component.
		final Component component;

		// Creates choices store.
		final ListStore<QuestionChoiceElementDTO> store = new ListStore<QuestionChoiceElementDTO>();
		store.add(getChoices());

		// Creates the listener of selection changes.
		final ComboBoxSelectionListener listener = new ComboBoxSelectionListener();

		// Selection.
		List<QuestionChoiceElementDTO> selectedChoices = Collections.emptyList();
		
		// Single selection case.
		if (!Boolean.TRUE.equals(getMultiple())) {

			final ComboBox<QuestionChoiceElementDTO> comboBox = new ComboBox<QuestionChoiceElementDTO>();
			comboBox.setEmptyText(I18N.CONSTANTS.flexibleElementQuestionEmptyChoice());

			comboBox.setStore(store);
			comboBox.setFieldLabel(getLabel());
			comboBox.setDisplayField(LABEL);
			comboBox.setValueField(ID);
			comboBox.setLabelSeparator("");
			comboBox.setTriggerAction(TriggerAction.ALL);
			comboBox.setEditable(false);
			comboBox.setAllowBlank(true);

			if (getCategoryType() != null) {

				for (final QuestionChoiceElementDTO choiceDTO : store.getModels()) {
					if (choiceDTO.getCategoryElement() != null) {
						choiceDTO.getCategoryElement().setIconHtml(CategoryIconProvider.getIconHtml(choiceDTO.getCategoryElement(), false));
					}
				}

				comboBox.setTemplate(CategoryIconProvider.getComboboxIconTemplate());
			}

			if (valueResult != null && valueResult.isValueDefined()) {

				final String idChoice = valueResult.getValueObject();

				for (QuestionChoiceElementDTO choiceDTO : getChoices()) {
					if (idChoice.equals(String.valueOf(choiceDTO.getId()))) {
						comboBox.setValue(choiceDTO);
						selectedChoices = Collections.singletonList(choiceDTO);
						break;
					}
				}
			}

			// Listens to the selection changes.
			comboBox.addSelectionChangedListener(listener);

			comboBox.setEnabled(enabled);

			component = comboBox;
		}
		// Multiple selection case.
		else {

			// Selection model.
			final CheckBoxSelectionModel<QuestionChoiceElementDTO> selectionModel = new CheckBoxSelectionModel<QuestionChoiceElementDTO>();
			selectionModel.setSelectionMode(SelectionMode.MULTI);
			selectionModel.addListener(Events.SelectionChange, listener);

			// Defines grid column model.
			final ColumnConfig labelColumn = new ColumnConfig();
			labelColumn.setId(LABEL);
			labelColumn.setHeaderText(I18N.CONSTANTS.flexibleElementQuestionMutiple());
			labelColumn.setWidth(500);
			if (getCategoryType() != null) {
				labelColumn.setRenderer(new GridCellRenderer<QuestionChoiceElementDTO>() {

					@Override
					public Object render(QuestionChoiceElementDTO model, String property, ColumnData config, int rowIndex, int colIndex,
							ListStore<QuestionChoiceElementDTO> store, Grid<QuestionChoiceElementDTO> grid) {

						final com.google.gwt.user.client.ui.Grid panel = new com.google.gwt.user.client.ui.Grid(1, 2);
						panel.setCellPadding(0);
						panel.setCellSpacing(0);

						panel.setWidget(0, 0, CategoryIconProvider.getIcon(model.getCategoryElement()));
						panel.setText(0, 1, (String) model.get(property));

						final Element element = panel.getCellFormatter().getElement(0, 1);
						element.getStyle().setPaddingTop(1, Unit.PX);
						element.getStyle().setPaddingLeft(5, Unit.PX);

						return panel;
					}
				});
			}

			// Visible columns.
			final ColumnConfig[] columnConfigs = canEdit ?
				new ColumnConfig[] {selectionModel.getColumn(), labelColumn} :
				new ColumnConfig[] {labelColumn};
				
			// Grid used as a list box.
			final FlexibleGrid<QuestionChoiceElementDTO> multipleQuestion = new FlexibleGrid<QuestionChoiceElementDTO>(store, selectionModel, columnConfigs);
			multipleQuestion.setAutoExpandColumn(LABEL);
			multipleQuestion.setVisibleElementsCount(5);

			final ContentPanel contentPanel = new ContentPanel();
			contentPanel.setHeaderVisible(true);
			contentPanel.setBorders(true);
			contentPanel.setHeadingText(getLabel());
			contentPanel.setTopComponent(null);
			contentPanel.add(multipleQuestion);

			// Selects the already selected choices.
			if (valueResult != null && valueResult.isValueDefined()) {

				final HashSet<Integer> selectedChoicesId = new HashSet<Integer>(ValueResultUtils.splitValuesAsInteger(valueResult.getValueObject()));
				selectedChoices = new ArrayList<QuestionChoiceElementDTO>();

				for (final QuestionChoiceElementDTO choiceDTO : getChoices()) {
					if (selectedChoicesId.contains(choiceDTO.getId())) {
						selectedChoices.add(choiceDTO);
					}
				}

				selectionModel.select(selectedChoices, false);
			}

			component = contentPanel;
		}

		// If the component is a category and/or a quality criterion.
		if (getCategoryType() != null) {

			if (getQualityCriterion() != null) {
				component.setToolTip(I18N.MESSAGES.flexibleElementQuestionCategory(getCategoryType().getLabel())
					+ "<br/>"
					+ I18N.MESSAGES.flexibleElementQuestionQuality(getQualityCriterion().getInfo()));
			} else {
				component.setToolTip(I18N.MESSAGES.flexibleElementQuestionCategory(getCategoryType().getLabel()));
			}

		} else if (getQualityCriterion() != null) {
			component.setToolTip(I18N.MESSAGES.flexibleElementQuestionQuality(getQualityCriterion().getInfo()));
		}
		
		// Remove disabled options (not selected)
        for (QuestionChoiceElementDTO choice : getChoices()) {
        	if (choice.isDisabled() && !selectedChoices.contains(choice)) {
        		store.remove(choice);
        	}
        }

		return component;
	}

	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {

		if (result == null || !result.isValueDefined()) {
			return false;
		}

		// Single selection case.
		if (!Boolean.TRUE.equals(getMultiple())) {
			try {
				final String value = result.getValueObject();
				return value != null && !"".equals(value);
			} catch (ClassCastException e) {
				return false;
			}
		}
		// Multiple selection case.
		else {
			final List<Integer> selectedChoicesId = ValueResultUtils.splitValuesAsInteger(result.getValueObject());
			return selectedChoicesId != null && selectedChoicesId.size() > 0;
		}
	}

	/**
	 * Basic selection changes listener implementation to fire value changes events of the current flexible element.
	 * 
	 * @author tmi
	 */
	private class ComboBoxSelectionListener extends SelectionChangedListener<QuestionChoiceElementDTO> {

		@Override
		public void selectionChanged(SelectionChangedEvent<QuestionChoiceElementDTO> se) {

			String value = null;
			final boolean isValueOn;

			// Single selection case.
			if (!Boolean.TRUE.equals(getMultiple())) {

				// Gets the selected choice.
				final QuestionChoiceElementDTO choice = se.getSelectedItem();

				// Checks if the choice isn't the default empty choice.
				isValueOn = choice != null && choice.getId() != null && choice.getId() != -1;

				if (choice != null) {
					value = String.valueOf(choice.getId());
				}
			}
			// Multiple selection case.
			else {

				// Gets the selected choices.
				final List<QuestionChoiceElementDTO> choices = se.getSelection();

				isValueOn = choices != null && choices.size() != 0;

				if (choices != null) {
					value = ValueResultUtils.mergeValues(choices);
				}
			}

			if (value != null) {
				// Fires value change event.
				handlerManager.fireEvent(new ValueEvent(QuestionElementDTO.this, value));
			}

			// Required element ?
			if (getValidates()) {
				handlerManager.fireEvent(new RequiredValueEvent(isValueOn));
			}
		}

	}
	
	private String toLabel(String value) {
		final QuestionChoiceElementDTO singleChoice = pickChoice(Integer.valueOf(value));

		if (singleChoice != null) {
			return singleChoice.getLabel();
		} else {
			return "";
		}
	}
	
	private List<String> toLabels(String values) {
		final List<Integer> selectedChoicesId = ValueResultUtils.splitValuesAsInteger(values);

		final ArrayList<String> labels = new ArrayList<String>();
		for (final Integer id : selectedChoicesId) {
			final QuestionChoiceElementDTO choice = pickChoice(id.intValue());
			if (choice != null) {
				labels.add(choice.getLabel());
			}
		}
		
		return labels;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object renderHistoryToken(HistoryTokenListDTO token) {

		// Single selection case.
		if (!Boolean.TRUE.equals(getMultiple())) {
			return new HistoryTokenText(toLabel(token.getTokens().get(0).getValue()));
		}
		// Multiple selection case.
		else {
			return new HistoryTokenText(toLabels(token.getTokens().get(0).getValue()));
		}
	}

	@Override
	public String toHTML(String value) {
		if(value == null || value.length() == 0) {
			return "";
		}
		
		final StringBuilder htmlBuilder = new StringBuilder();
		
		// Single selection case.
		if (!Boolean.TRUE.equals(getMultiple())) {
			htmlBuilder.append(toLabel(value));
		}
		// Multiple selection case.
		else {
			for(final String entry : toLabels(value)) {
				htmlBuilder.append(" -").append(entry).append("<br>");
			}
		}
		
		return htmlBuilder.toString();
	}

	/**
	 * Select a choice among the list of the choices.
	 * 
	 * @param id
	 *          The wanted choice's id.
	 * @return The choice if it exists, <code>null</code> otherwise.
	 */
	private QuestionChoiceElementDTO pickChoice(int id) {

		if (getChoices() != null) {
			for (final QuestionChoiceElementDTO choice : getChoices()) {
				if (choice.getId().equals(id)) {
					return choice;
				}
			}
		}

		return null;
	}
}
