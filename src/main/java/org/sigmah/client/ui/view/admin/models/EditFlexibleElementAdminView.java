package org.sigmah.client.ui.view.admin.models;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.EditFlexibleElementAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.button.ClickableLabel;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.TextButtonField;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.dto.referential.TextAreaType;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import org.sigmah.client.ui.widget.form.ClearableField;

/**
 * {@link EditFlexibleElementAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class EditFlexibleElementAdminView extends AbstractPopupView<PopupWidget> implements EditFlexibleElementAdminPresenter.View {

	// CSS style names.
	private static final String STYLE_FORM_HEADER_LABEL = "form-header-label";

	// --
	// Common properties form components.
	// --

	private FormPanel commonForm;
	private HtmlEditor nameField;
	private LabelField nameReadOnlyField;
	private ComboBox<EnumModel<ElementTypeEnum>> typeField;
	private ComboBox<BaseModelData> containerField;
	private ComboBox<LayoutGroupDTO> layoutGroupField;
	private NumberField orderField;
	private CheckBox mandatoryField;
	private ComboBox<PrivacyGroupDTO> privacyGroupField;
	private CheckBox amendableField;
	private CheckBox exportableField;

	// --
	// Specific properties form components.
	// --

	private FormPanel specificForm;
	private CheckBox bannerField;
	private SimpleComboBox<Integer> bannerPositionField;

	private Set<Field<?>> textAreaFields;
	private ComboBox<EnumModel<TextAreaType>> textAreaTypeField;
	private NumberField lengthField;
	private CheckBox decimalField;
	private NumberField minLimitField;
	private NumberField maxLimitField;
	private DateField minDateField;
	private DateField maxDateField;

	private ComboBox<ReportModelDTO> reportModelField;

	private CheckBox multipleChoicesField;
	private CheckBox qualityLinkField;
	private ComboBox<CategoryTypeDTO> categoryTypeField;
	private TextButtonField customChoiceAddField;
	private FlowPanel customChoicesPanel;
	private AdapterField customChoicesField;

	// TODO budget specific fields.

	private FlexTable budgetFields;
	private FlexTable ratioFlexTable;
	private ComboBox<BudgetSubFieldDTO> upBudgetSubFieldCombo;
	private ComboBox<BudgetSubFieldDTO> downBudgetSubFieldCombo;
	private ListStore<BudgetSubFieldDTO> upBudgetSubFieldStore;
	private ListStore<BudgetSubFieldDTO> downBudgetSubFieldStore;
	private Anchor anchorAddSubField;

	// --
	// Other components.
	// --

	private Button saveButton;

	/**
	 * Popup's initialization.
	 */
	public EditFlexibleElementAdminView() {
		super(new PopupWidget(true), 900);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// --
		// Common properties form components.
		// --

		nameField = new HtmlEditor();
		nameField.hide();
		nameField.setEnableAlignments(false);
		nameField.setEnableLinks(false);
		nameField.setEnableFont(false);
		nameField.setEnableLists(false);
		nameField.setEnableColors(false);
		nameField.setSourceEditMode(false);
		nameField.setHeight(75);
		nameField.setBorders(true);
		nameField.setFieldLabel(I18N.CONSTANTS.adminFlexibleName());

		nameReadOnlyField = Forms.label(I18N.CONSTANTS.adminFlexibleName());

		typeField = Forms.combobox(I18N.CONSTANTS.adminFlexibleType(), true, EnumModel.VALUE_FIELD, EnumModel.DISPLAY_FIELD);
		typeField.setEmptyText(I18N.CONSTANTS.adminFlexibleType());

		// The value property should match OrgUnitDetails, ProjectDetails and PhaseModel 'name' property.
		containerField = Forms.combobox(I18N.CONSTANTS.adminFlexibleContainer(), true, EntityDTO.ID, PhaseModelDTO.NAME);
		containerField.setEmptyText(I18N.CONSTANTS.adminFlexibleContainerChoice());
		containerField.setFireChangeEventOnSetValue(true);

		layoutGroupField = Forms.combobox(I18N.CONSTANTS.adminFlexibleGroup(), true, LayoutGroupDTO.ID, LayoutGroupDTO.TITLE);
		orderField = Forms.number(I18N.CONSTANTS.adminFlexibleOrder(), true);
		mandatoryField = Forms.checkbox("", null, I18N.CONSTANTS.adminFlexibleCompulsory(), false);
		privacyGroupField = Forms.combobox(I18N.CONSTANTS.adminPrivacyGroups(), false, PrivacyGroupDTO.ID, PrivacyGroupDTO.TITLE);
		privacyGroupField.setEmptyText(I18N.CONSTANTS.adminPrivacyGroupChoice());
		amendableField = Forms.checkbox("", null, I18N.CONSTANTS.partOfProjectCore(), false);
		exportableField = Forms.checkbox("", null, I18N.CONSTANTS.adminFlexibleExportable(), false);

		// Form initialization.
		commonForm = Forms.panel(130);
		commonForm.add(nameField);
		commonForm.add(nameReadOnlyField);
		commonForm.add(typeField);
		commonForm.add(containerField);
		commonForm.add(layoutGroupField);
		commonForm.add(orderField);
		commonForm.add(mandatoryField);
		commonForm.add(new ClearableField<PrivacyGroupDTO>(privacyGroupField));
		commonForm.add(amendableField);
		commonForm.add(exportableField);

		// --
		// Specific properties form components.
		// --

		textAreaFields = new HashSet<Field<?>>();

		bannerField = Forms.checkbox("", null, I18N.CONSTANTS.Admin_BANNER(), false);
		bannerField.setFireChangeEventOnSetValue(true);

		bannerPositionField = Forms.simpleCombobox(I18N.CONSTANTS.adminFlexibleBannerPosition(), false);
		bannerPositionField.disable();

		textAreaTypeField = Forms.combobox(I18N.CONSTANTS.adminFlexibleTextType(), true, EnumModel.VALUE_FIELD, EnumModel.DISPLAY_FIELD);

		lengthField = Forms.number(I18N.CONSTANTS.adminFlexibleLength(), false);
		decimalField = Forms.checkbox("", null, I18N.CONSTANTS.adminFlexibleDecimal(), false);
		minLimitField = Forms.number(I18N.CONSTANTS.adminFlexibleMinLimit(), false);
		maxLimitField = Forms.number(I18N.CONSTANTS.adminFlexibleMaxLimit(), false);
		minDateField = Forms.date(I18N.CONSTANTS.adminFlexible_form_minDate(), false);
		maxDateField = Forms.date(I18N.CONSTANTS.adminFlexible_form_maxDate(), false);

		textAreaFields.add(lengthField);
		textAreaFields.add(decimalField);
		textAreaFields.add(minLimitField);
		textAreaFields.add(maxLimitField);
		textAreaFields.add(minDateField);
		textAreaFields.add(maxDateField);

		reportModelField = Forms.combobox(I18N.CONSTANTS.adminReportName(), true, ReportModelDTO.ID, ReportModelDTO.NAME);

		multipleChoicesField = Forms.checkbox("", null, I18N.CONSTANTS.adminFlexibleMultipleQ(), false);
		qualityLinkField = Forms.checkbox("", null, I18N.CONSTANTS.adminFlexibleLinkedToQuality(), false);
		categoryTypeField = Forms.combobox(I18N.CONSTANTS.adminFlexibleLinkedCategory(), false, CategoryTypeDTO.ID, CategoryTypeDTO.LABEL);
		categoryTypeField.setFireChangeEventOnSetValue(true);
		customChoiceAddField = new TextButtonField(I18N.CONSTANTS.adminFlexibleQChoices());
		customChoicesPanel = new FlowPanel();
		customChoicesField = Forms.adapter(null, customChoicesPanel);

		// --
		// specific properties for budget field
		// --

		upBudgetSubFieldStore = new ListStore<BudgetSubFieldDTO>();
		downBudgetSubFieldStore = new ListStore<BudgetSubFieldDTO>();

		budgetFields = new FlexTable();
		budgetFields.setVisible(false);
		budgetFields.setCellSpacing(5);
		budgetFields.setCellPadding(5);
		budgetFields.addStyleName("budget-sub-fields-table");

		Text subFieldTitle = new Text(I18N.CONSTANTS.adminBudgetSubField());
		subFieldTitle.addStyleName("budget-sub-fields-title");
		budgetFields.setWidget(0, 0, subFieldTitle);
		budgetFields.getFlexCellFormatter().setColSpan(0, 0, 3);

		anchorAddSubField = new Anchor();
		anchorAddSubField.setHTML(IconImageBundle.ICONS.add().getHTML() + I18N.CONSTANTS.adminAddBudgetSubField());
		anchorAddSubField.setVisible(false);

		ratioFlexTable = new FlexTable();

		upBudgetSubFieldCombo = new ComboBox<BudgetSubFieldDTO>();
		upBudgetSubFieldCombo.setDisplayField("label");

		downBudgetSubFieldCombo = new ComboBox<BudgetSubFieldDTO>();
		downBudgetSubFieldCombo.setDisplayField("label");

		upBudgetSubFieldStore = new ListStore<BudgetSubFieldDTO>();
		downBudgetSubFieldStore = new ListStore<BudgetSubFieldDTO>();

		upBudgetSubFieldCombo.setStore(upBudgetSubFieldStore);
		downBudgetSubFieldCombo.setStore(downBudgetSubFieldStore);

		upBudgetSubFieldCombo.setTriggerAction(TriggerAction.ALL);
		upBudgetSubFieldCombo.setEditable(false);

		downBudgetSubFieldCombo.setTriggerAction(TriggerAction.ALL);
		downBudgetSubFieldCombo.setEditable(false);

		ratioFlexTable.setWidget(0, 0, new Text(I18N.CONSTANTS.adminBudgetRatio() + ":"));
		ratioFlexTable.setWidget(0, 1, upBudgetSubFieldCombo);
		ratioFlexTable.setWidget(0, 2, new Text("/"));
		ratioFlexTable.setWidget(0, 3, downBudgetSubFieldCombo);
		ratioFlexTable.setVisible(false);

		// Form initialization.
		specificForm = Forms.panel(150);
		specificForm.add(bannerField);
		specificForm.add(bannerPositionField);
		specificForm.add(textAreaTypeField);
		specificForm.add(lengthField);
		specificForm.add(decimalField);
		specificForm.add(minLimitField);
		specificForm.add(maxLimitField);
		specificForm.add(minDateField);
		specificForm.add(maxDateField);
		specificForm.add(reportModelField);
		specificForm.add(multipleChoicesField);
		specificForm.add(qualityLinkField);
		specificForm.add(categoryTypeField);
		specificForm.add(customChoiceAddField);
		specificForm.add(customChoicesField);

		specificForm.add(budgetFields);
		specificForm.add(anchorAddSubField);
		specificForm.add(ratioFlexTable);
		// --
		// Other components.
		// --

		saveButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());

		// To apply the form button wrapping styles.
		final FormPanel buttonFormPanel = Forms.panel();
		buttonFormPanel.addButton(saveButton);

		// --
		// Global popup initialization.
		// --

		final Grid mainContainer = new Grid(3, 2);
		mainContainer.setWidth("100%");
		mainContainer.getElement().getStyle().setTableLayout(TableLayout.FIXED);
		mainContainer.getColumnFormatter().getElement(0).getStyle().setProperty("width", "50%");
		mainContainer.getColumnFormatter().getElement(1).getStyle().setProperty("width", "50%");
		mainContainer.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);

		mainContainer.setWidget(0, 0, buildFormHeaderLabel(I18N.CONSTANTS.adminFlexible_form_header_common()));
		mainContainer.setWidget(0, 1, buildFormHeaderLabel(I18N.CONSTANTS.adminFlexible_form_header_specific()));
		mainContainer.setWidget(1, 0, commonForm);
		mainContainer.setWidget(1, 1, specificForm);
		mainContainer.setWidget(2, 1, buttonFormPanel);

		initPopup(mainContainer);
	}

	@Override
	public void onViewRevealed() {
		specificForm.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getSaveButton() {
		return saveButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getCommonForm() {
		return commonForm;
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
	public Field<Object> getNameReadOnlyField() {
		return nameReadOnlyField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<EnumModel<ElementTypeEnum>> getTypeField() {
		return typeField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<BaseModelData> getContainerField() {
		return containerField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<LayoutGroupDTO> getLayoutGroupField() {
		return layoutGroupField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Number> getOrderField() {
		return orderField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getMandatoryField() {
		return mandatoryField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<PrivacyGroupDTO> getPrivacyGroupField() {
		return privacyGroupField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getAmendableField() {
		return amendableField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getExportableField() {
		return exportableField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getSpecificForm() {
		return specificForm;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getBannerField() {
		return bannerField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleComboBox<Integer> getBannerPositionField() {
		return bannerPositionField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<EnumModel<TextAreaType>> getTextAreaTypeField() {
		return textAreaTypeField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Number> getLengthField() {
		return lengthField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getDecimalField() {
		return decimalField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Number> getMinLimitField() {
		return minLimitField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Number> getMaxLimitField() {
		return maxLimitField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Date> getMinDateField() {
		return minDateField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Date> getMaxDateField() {
		return maxDateField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<ReportModelDTO> getReportModelField() {
		return reportModelField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getMultipleChoicesField() {
		return multipleChoicesField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getQualityLinkField() {
		return qualityLinkField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<CategoryTypeDTO> getCategoryTypeField() {
		return categoryTypeField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getCustomChoiceField() {
		return customChoiceAddField.getTextField(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getCustomChoiceAddButton() {
		return customChoiceAddField.getButton();
	}

	@Override
	public FlexTable getBudgetFields() {
		return budgetFields;
	}

	@Override
	public FlexTable getRatioFlexTable() {
		return ratioFlexTable;
	}

	@Override
	public ComboBox<BudgetSubFieldDTO> getUpBudgetSubFieldCombo() {
		return upBudgetSubFieldCombo;
	}

	@Override
	public ComboBox<BudgetSubFieldDTO> getDownBudgetSubFieldCombo() {
		return downBudgetSubFieldCombo;
	}

	@Override
	public ListStore<BudgetSubFieldDTO> getUpBudgetSubFieldStore() {
		return upBudgetSubFieldStore;
	}

	@Override
	public ListStore<BudgetSubFieldDTO> getDownBudgetSubFieldStore() {
		return downBudgetSubFieldStore;
	}

	@Override
	public Anchor getAnchorAddSubField() {
		return anchorAddSubField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		commonForm.clearAll();
		specificForm.clearAll();
		customChoicesPanel.clear();
	}

	@Override
	public void clearBudgetFields() {

		int i = this.getBudgetFields().getRowCount();
		i--;
		while (i >= 1) {
			this.getBudgetFields().removeRow(i);
			i--;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCustomChoice(final String value, final ClickHandler deleteHandler) {

		final ClickableLabel customChoiceLabel = new ClickableLabel(value);

		customChoiceLabel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				if (deleteHandler != null) {
					deleteHandler.onClick(event);
				}
				customChoiceLabel.removeFromParent();
			}
		});

		customChoicesPanel.add(customChoiceLabel);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addUndeletableCustomChoice(final String value, boolean checked, final Listener<FieldEvent> disableHandler) {
		
		final ClickableLabel customChoiceLabel = new ClickableLabel(value);
		final Grid grid = (Grid) customChoiceLabel.getWidget();
		
		final CheckBox checkBox = new CheckBox();
		checkBox.setValue(checked);
		grid.setWidget(0, 0, checkBox);

		if(disableHandler != null) {
			checkBox.addListener(Events.Change, disableHandler);
		}

		customChoicesPanel.add(customChoiceLabel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCustomChoiceAddFieldEnabled(boolean enabled) {
		getCustomChoiceField().clear();
		customChoiceAddField.setEnabled(enabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSpecificFieldsVisibility(final ElementTypeEnum elementType, final DefaultFlexibleElementType defaultFlexibleElementType) {

		hideFields(specificForm.getFields());

		if (elementType == null) {
			return;
		}

		switch (elementType) {

			case DEFAULT:

				bannerField.show();
				bannerPositionField.show();

				switch (defaultFlexibleElementType) {

					case BUDGET:

						budgetFields.setVisible(true);
						anchorAddSubField.setVisible(true);
						ratioFlexTable.setVisible(true);

						break;

					default:
						break;
				}
				break;

			case FILES_LIST:
				maxLimitField.show();
				break;

			case QUESTION:
				multipleChoicesField.show();
				categoryTypeField.show();
				customChoiceAddField.show();
				customChoicesField.show();
				break;

			case REPORT:
			case REPORT_LIST:
				reportModelField.show();
				reportModelField.setAllowBlank(false);
				break;

			case TEXT_AREA:
				textAreaTypeField.show();
				textAreaTypeField.setAllowBlank(false);
				break;

			default:
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTextAreaSpecificFieldsVisibility(final TextAreaType textAreaType) {

		hideFields(textAreaFields);

		if (textAreaType == null) {
			return;
		}

		switch (textAreaType) {

			case PARAGRAPH:
			case TEXT:
				lengthField.show();
				break;

			case NUMBER:
				decimalField.show();
				minLimitField.show();
				maxLimitField.show();
				break;

			case DATE:
				minDateField.show();
				maxDateField.show();
				break;

			default:
				break;
		}
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Builds a new form header label widget.
	 * 
	 * @param label
	 *          The form header label.
	 * @return A new form header label widget.
	 */
	private static final Widget buildFormHeaderLabel(final String label) {
		final Label headerLabel = new Label(label);
		headerLabel.setStyleName(STYLE_FORM_HEADER_LABEL);
		return headerLabel;
	}

	/**
	 * Hides properly the given {@code fields}.<br>
	 * The fields are also cleared and are no longer mandatory.
	 * 
	 * @param fields
	 *          The fields to hide.
	 */
	private static void hideFields(final Collection<Field<?>> fields) {

		if (ClientUtils.isEmpty(fields)) {
			return;
		}

		for (final Field<?> field : fields) {
			field.hide();
			field.clear();
			field.clearInvalid();
			if (field instanceof TextField) {
				((TextField<?>) field).setAllowBlank(true); // No longer mandatory.
			}
		}
	}

	public static String getStyleFormHeaderLabel() {
		return STYLE_FORM_HEADER_LABEL;
	}

}
