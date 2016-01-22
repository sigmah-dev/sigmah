package org.sigmah.client.ui.presenter.admin.models;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasForm;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.admin.models.EditFlexibleElementAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetCategories;
import org.sigmah.shared.command.GetPrivacyGroups;
import org.sigmah.shared.command.GetReportModels;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.OrgUnitBannerDTO;
import org.sigmah.shared.dto.ProjectBannerDTO;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.QuestionChoiceElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.dto.referential.TextAreaType;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.computation.Computation;
import org.sigmah.shared.computation.Computations;
import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 * Presenter in charge of creating/editing a flexible element.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class EditFlexibleElementAdminPresenter extends AbstractPagePresenter<EditFlexibleElementAdminPresenter.View> implements HasForm {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(EditFlexibleElementAdminView.class)
	public static interface View extends ViewPopupInterface {

		// --
		// Common form components.
		// --

		FormPanel getCommonForm();

		Field<String> getNameField();

		Field<Object> getNameReadOnlyField();

		ComboBox<EnumModel<ElementTypeEnum>> getTypeField();

		ComboBox<BaseModelData> getContainerField();

		ComboBox<LayoutGroupDTO> getLayoutGroupField();

		Field<Number> getOrderField();

		Field<Boolean> getMandatoryField();

		ComboBox<PrivacyGroupDTO> getPrivacyGroupField();

		Field<Boolean> getAmendableField();

		Field<Boolean> getExportableField();

		// --
		// Specific form components.
		// --

		FormPanel getSpecificForm();

		Field<Boolean> getBannerField();

		SimpleComboBox<Integer> getBannerPositionField();

		ComboBox<EnumModel<TextAreaType>> getTextAreaTypeField();

		Field<String> getCodeField();
		
		Field<Number> getLengthField();

		Field<Boolean> getDecimalField();

		Field<Number> getMinLimitField();

		Field<Number> getMaxLimitField();

		Field<Date> getMinDateField();

		Field<Date> getMaxDateField();

		ComboBox<ReportModelDTO> getReportModelField();

		Field<Boolean> getMultipleChoicesField();

		Field<Boolean> getQualityLinkField();

		ComboBox<CategoryTypeDTO> getCategoryTypeField();

		Field<String> getCustomChoiceField();

		Button getCustomChoiceAddButton();

		FlexTable getBudgetFields();

		FlexTable getRatioFlexTable();

		ComboBox<BudgetSubFieldDTO> getUpBudgetSubFieldCombo();

		ComboBox<BudgetSubFieldDTO> getDownBudgetSubFieldCombo();

		ListStore<BudgetSubFieldDTO> getUpBudgetSubFieldStore();

		ListStore<BudgetSubFieldDTO> getDownBudgetSubFieldStore();

		Anchor getAnchorAddSubField();

		void clearBudgetFields();
		
		Field<String> getComputationRuleField();

		// --
		// Methods.
		// --

		Button getSaveButton();

		/**
		 * Clears the forms.
		 */
		void clear();

		/**
		 * Initializes the given {@code elementType} specific form fields.
		 * 
		 * @param elementType
		 *          The flexible element type, may be {@code null}.
		 * @param defaultFlexibleElementType
		 *          The <b>default</b> flexible element type, may be {@code null}.
		 */
		void setSpecificFieldsVisibility(ElementTypeEnum elementType, DefaultFlexibleElementType defaultFlexibleElementType);

		/**
		 * Initializes the given {@code textAreaType} specific form fields.
		 * 
		 * @param textAreaType
		 *          The text area type, may be {@code null}.
		 */
		void setTextAreaSpecificFieldsVisibility(TextAreaType textAreaType);

		/**
		 * Adds the given {@code customChoice} as a new custom choice label.
		 * 
		 * @param customChoice
		 *          The custom choice label.
		 * @param deleteHandler
		 *          The delete handler triggered if the label is deleted.
		 */
		void addCustomChoice(String customChoice, ClickHandler deleteHandler);
		
		/**
		 * Adds the given {@code customChoice} as an existing custom that can't be deleted.
		 * 
		 * @param customChoice
		 *          The custom choice label.
		 * @param checked
		 *			Initial value of the checkbox.
		 * @param disableHandler
		 *          The disable handler triggered if the associated checkbox is selected.
		 */
		void addUndeletableCustomChoice(String customChoice, boolean checked, Listener<FieldEvent> disableHandler);

		/**
		 * Sets the custom choices add fields enabled state.
		 * 
		 * @param enabled
		 *          {@code true} to enable the fields, {@code false} to disable them.
		 */
		void setCustomChoiceAddFieldEnabled(boolean enabled);

	}

	/**
	 * Banner positions.
	 */
	private static final Integer[] BANNER_POSITIONS = { 1, 2, 3, 4, 5, 6 };

	/**
	 * Default category type.
	 */
	private static final CategoryTypeDTO DEFAULT_CATEGORY_TYPE = new CategoryTypeDTO(I18N.CONSTANTS.adminFlexibleNoLinkedCategory());

	/**
	 * HTML new line tag.
	 */
	private static final String HTML_TAG_NEW_LINE = "<br>";

	/**
	 * The current model. Should never be {@code null}.
	 */
	private IsModel currentModel;

	/**
	 * The edited {@link FlexibleElementDTO}, or {@code null} in case of creation.
	 */
	private FlexibleElementDTO flexibleElement;

	/**
	 * Old flexible element properties map.
	 */
	private Map<String, Object> oldFieldProperties;;

	/**
	 * Custom choices labels.
	 */
	private Set<String> customChoices;
	
	/**
	 * Disabled custom choices labels.
	 */
	private Set<String> disabledCustomChoices;

	/**
	 * Presenter's initialization.
	 * 
	 * @param view
	 *          The view managed by the presenter.
	 * @param injector
	 *          The application injector.
	 */
	@Inject
	protected EditFlexibleElementAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_EDIT_FLEXIBLE_ELEMENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		customChoices = new HashSet<String>();
		disabledCustomChoices = new HashSet<String>();

		// --
		// Type field select handler.
		// --

		view.getTypeField().addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {

				final ElementTypeEnum type = EnumModel.getEnum(view.getTypeField().getValue());
				loadFlexibleElementSpecificFields(flexibleElement, type);

				if (type == ElementTypeEnum.FILES_LIST || type == ElementTypeEnum.REPORT || type == ElementTypeEnum.REPORT_LIST) {
					view.getExportableField().hide();
					view.getExportableField().clear();
				} else {
					view.getExportableField().show();
					view.getExportableField().setValue(flexibleElement != null ? flexibleElement.getExportable() : null);
				}
			}
		});

		// --
		// TextArea type field select handler.
		// --

		view.getTextAreaTypeField().addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				final TextAreaType textAreaType = EnumModel.getEnum(view.getTextAreaTypeField().getValue());
				loadFlexibleElementTextAreaFields(flexibleElement, textAreaType);
			}
		});

		// --
		// Container field change handler.
		// --

		view.getContainerField().addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {

				final BaseModelData hasLayout = view.getContainerField().getValue();
				final LayoutDTO selectedContainer = EditLayoutGroupAdminPresenter.getLayout(hasLayout);

				if (hasLayout instanceof ProjectBannerDTO || hasLayout instanceof OrgUnitBannerDTO) {
					view.getBannerField().setValue(true); // Updates the bannerPosition field.
				}

				view.getLayoutGroupField().getStore().removeAll();
				view.getLayoutGroupField().disable();

				if (selectedContainer != null) {
					view.getLayoutGroupField().getStore().add(selectedContainer.getGroups());
					view.getLayoutGroupField().getStore().commitChanges();
					view.getLayoutGroupField().setValue(view.getLayoutGroupField().getStore().getAt(0));
					view.getLayoutGroupField().enable();
				}
			}
		});

		// --
		// Banner field change handler.
		// --

		view.getBannerField().addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				final boolean checked = ClientUtils.isTrue(view.getBannerField().getValue());
				view.getBannerPositionField().setValue(null);
				view.getBannerPositionField().setEnabled(checked);
				view.getBannerPositionField().setAllowBlank(!checked);
			}
		});

		// --
		// Custom choice add button handler.
		// --

		view.getCustomChoiceAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onAddCustomChoice(view.getCustomChoiceField().getValue());
			}
		});

		// --
		// Category type.
		// --

		view.getCategoryTypeField().addListener(Events.Select, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(final FieldEvent event) {
				final CategoryTypeDTO value = view.getCategoryTypeField().getValue();
				view.setCustomChoiceAddFieldEnabled(value == null || DEFAULT_CATEGORY_TYPE.equals(value));
			}
		});

		// --
		// add sub budget field
		// --
		view.getAnchorAddSubField().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (flexibleElement instanceof BudgetElementDTO) {

					BudgetElementDTO budgetElementDTO = (BudgetElementDTO) flexibleElement;
					BudgetSubFieldDTO budgetSubFieldDTO = new BudgetSubFieldDTO();
					budgetElementDTO.getBudgetSubFields().add(budgetSubFieldDTO);
					eventBus.navigateRequest(Page.ADMIN_EDIT_FLEXIBLE_ELEMENT_ADD_BUDGETSUBFIELD.request().addData(RequestParameter.DTO, budgetSubFieldDTO));

				}

			}
		});

		// --
		// Button save handler.
		// --

		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onSaveAction();
			}
		});

		// --
		// Handlers
		// --

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.EDIT_FLEXIBLEELEMNT_EDIT_BUDGETSUBFIELD)) {
					loadFlexibleElement(flexibleElement);
				}
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		setPageTitle(I18N.CONSTANTS.adminFlexible());
		oldFieldProperties = new HashMap<String, Object>();

		// Reads parameters/data from request.
		currentModel = request.getData(RequestParameter.MODEL);
		flexibleElement = request.getData(RequestParameter.DTO);

		if (currentModel == null) {
			hideView();
			throw new IllegalArgumentException("Invalid required model.");
		}

		// Forms reset.
		view.clear();

		// Loads static values.
		loadStaticValues();

		// Loads containers.
		loadContainers(currentModel);
		
		// Change the form according to the maintenance state.
		loadUnderMaintenanceState(currentModel);

		// Loads the privacy groups.
		loadPrivacyGroups(flexibleElement);

		// Loads reports models.
		loadReportModels(flexibleElement);

		// Loads category types.
		loadCategoryTypes(flexibleElement);

		// Loads the other fields of the flexible element.
		loadFlexibleElement(flexibleElement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel[] getForms() {
		return new FormPanel[] { view.getCommonForm(), view.getSpecificForm() };
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Returns the given {@code flexibleElement} corresponding flexible element type.<br>
	 * If the given {@code flexibleElement} is <b>not</b> a flexible element (or is {@code null}), the method returns
	 * {@code null}.
	 * 
	 * @param flexibleElement
	 *          The flexible element.
	 * @return The given {@code flexibleElement} corresponding flexible element type, or {@code null}.
	 */
	private static ElementTypeEnum getElementType(final FlexibleElementDTO flexibleElement) {
		if (!(flexibleElement instanceof FlexibleElementDTO)) {
			return null;
		}
		return flexibleElement.getElementType();
	}

	/**
	 * Returns the given {@code flexibleElement} corresponding default flexible element type.<br>
	 * If the given {@code flexibleElement} is <b>not</b> a <em>default</em> flexible element (or is {@code null}), the
	 * method returns {@code null}.
	 * 
	 * @param flexibleElement
	 *          The flexible element.
	 * @return The given {@code flexibleElement} corresponding default flexible element type, or {@code null}.
	 */
	private static DefaultFlexibleElementType getDefaultElementType(final FlexibleElementDTO flexibleElement) {
		if (!(flexibleElement instanceof DefaultFlexibleElementDTO)) {
			return null;
		}
		return ((DefaultFlexibleElementDTO) flexibleElement).getType();
	}

	/**
	 * Returns <code>true</code> if the user is updating an existing flexible
	 * element and its creation date is prior to the maintenance start date.
	 * 
	 * @return <code>true</code> if updating an existing flexible element and 
	 */
	private boolean isUpdateAndUnderMaintenance() {
		return flexibleElement != null && currentModel.isUnderMaintenance() && 
			(flexibleElement.getCreationDate() == null || flexibleElement.getCreationDate().before(currentModel.getDateMaintenance()));
	}
	
	/**
	 * <p>
	 * Loads the given {@code flexibleElement} <b>common fields</b> and sets the corresponding form fields values.
	 * </p>
	 * <p>
	 * Executes {@link #loadFlexibleElementSpecificFields(FlexibleElementDTO, ElementTypeEnum)} method.
	 * </p>
	 * 
	 * @param flexibleElement
	 *          The edited flexible element, may be {@code null}.
	 */
	private void loadFlexibleElement(final FlexibleElementDTO flexibleElement) {

		final boolean defaultFlexibleElement = getElementType(flexibleElement) == ElementTypeEnum.DEFAULT;

		view.getNameField().setVisible(!defaultFlexibleElement);
		view.getNameReadOnlyField().setVisible(defaultFlexibleElement);
		view.getTypeField().setEnabled(flexibleElement == null);
		view.getExportableField().setValue(true);

		loadFlexibleElementSpecificFields(flexibleElement, getElementType(flexibleElement));

		if (flexibleElement != null) {

			// --
			// Common properties.
			// --

			view.getNameField().setValue(flexibleElement.getFormattedLabel());
			view.getNameReadOnlyField().setValue(DefaultFlexibleElementType.getName(getDefaultElementType(flexibleElement)));
			view.getTypeField().setValue(new EnumModel<ElementTypeEnum>(getElementType(flexibleElement)));
			view.getCodeField().setValue(flexibleElement.getCode());

			// Banner constraint.
			final LayoutConstraintDTO bannerConstraint = flexibleElement.getBannerConstraint();
			view.getBannerField().setValue(bannerConstraint != null); // Updates the bannerPosition field.
			view.getBannerPositionField().setSimpleValue(bannerConstraint != null ? bannerConstraint.getSortOrder() : null);

			// Layout constraint.
			final LayoutConstraintDTO constraint = flexibleElement.getConstraint();
			view.getContainerField().setValue(flexibleElement.getContainerModel());
			view.getLayoutGroupField().setValue(constraint != null ? constraint.getParentLayoutGroup() : null);
			view.getOrderField().setValue(constraint != null ? constraint.getSortOrder() : null);

			view.getMandatoryField().setValue(flexibleElement.getValidates());

			view.getAmendableField().setValue(flexibleElement.getAmendable());

			view.getExportableField().setValue(flexibleElement.getExportable());

			// --
			// Current ('OLD') properties map initialization.
			// Relies on the form fields values to avoid retrieving the data once again.
			//
			// This map initialization should be executed ONLY ONCE during presenter load process.
			// --

			// Common properties.
			oldFieldProperties.put(AdminUtil.PROP_FX_NAME, view.getNameField().getValue());
			oldFieldProperties.put(AdminUtil.PROP_FX_TYPE, EnumModel.getEnum(view.getTypeField().getValue()));
			oldFieldProperties.put(AdminUtil.PROP_FX_IN_BANNER, view.getBannerField().getValue());
			oldFieldProperties.put(AdminUtil.PROP_FX_POS_IN_BANNER, ClientUtils.getSimpleValue(view.getBannerPositionField()));
			oldFieldProperties.put(AdminUtil.PROP_FX_GROUP, view.getLayoutGroupField().getValue());
			oldFieldProperties.put(AdminUtil.PROP_FX_ORDER_IN_GROUP, ClientUtils.getInteger(view.getOrderField().getValue()));
			oldFieldProperties.put(AdminUtil.PROP_FX_LC, constraint);
			// BUGFIX #719: sending the current banner constraint to avoid a null pointer exception.
			oldFieldProperties.put(AdminUtil.PROP_FX_LC_BANNER, flexibleElement.getBannerConstraint());
			oldFieldProperties.put(AdminUtil.PROP_FX_IS_COMPULSARY, view.getMandatoryField().getValue());
			oldFieldProperties.put(AdminUtil.PROP_FX_AMENDABLE, view.getAmendableField().getValue());
			oldFieldProperties.put(AdminUtil.PROP_FX_EXPORTABLE, view.getExportableField().getValue());

			// Specific properties.
			oldFieldProperties.put(AdminUtil.PROP_FX_TEXT_TYPE, TextAreaType.getCode(EnumModel.getEnum(view.getTextAreaTypeField().getValue())));
			oldFieldProperties.put(AdminUtil.PROP_FX_MIN_LIMIT, view.getMinLimitField().getValue());
			oldFieldProperties.put(AdminUtil.PROP_FX_MAX_LIMIT, view.getMaxLimitField().getValue());
			oldFieldProperties.put(AdminUtil.PROP_FX_LENGTH, ClientUtils.getInteger(view.getLengthField().getValue()));
			oldFieldProperties.put(AdminUtil.PROP_FX_DECIMAL, view.getDecimalField().getValue());
			oldFieldProperties.put(AdminUtil.PROP_FX_Q_QUALITY, view.getQualityLinkField().getValue());
			oldFieldProperties.put(AdminUtil.PROP_FX_Q_MULTIPLE, view.getMultipleChoicesField().getValue());
		}
	}

	/**
	 * <p>
	 * Loads the given {@code flexibleElement} <b>specific fields</b> and sets the corresponding form fields values.
	 * </p>
	 * <p>
	 * Executes {@link #loadFlexibleElementTextAreaFields(FlexibleElementDTO, TextAreaType)} method if the
	 * {@code flexibleElement} is an instance of {@link TextAreaElementDTO}.
	 * </p>
	 * 
	 * @param flexibleElement
	 *          The edited flexible element, may be {@code null}.
	 * @param type
	 *          The element type, may be {@code null}.
	 */
	private void loadFlexibleElementSpecificFields(final FlexibleElementDTO flexibleElement, final ElementTypeEnum type) {

		// clear specific element for bubget
		view.getBudgetFields().setVisible(false);
		view.getAnchorAddSubField().setVisible(false);
		view.getRatioFlexTable().setVisible(false);

		view.clearBudgetFields();
		view.getUpBudgetSubFieldCombo().clearSelections();
		view.getDownBudgetSubFieldCombo().clearSelections();
		view.getUpBudgetSubFieldStore().removeAll();
		view.getUpBudgetSubFieldStore().commitChanges();
		view.getDownBudgetSubFieldStore().removeAll();
		view.getDownBudgetSubFieldStore().commitChanges();

		// Specific fields visibility.
		view.setSpecificFieldsVisibility(type, getDefaultElementType(flexibleElement));

		view.getBannerPositionField().disable();
		view.getBannerPositionField().setAllowBlank(true);

        customChoices.clear();
        disabledCustomChoices.clear();

		if (flexibleElement instanceof FilesListElementDTO) {

			view.getMaxLimitField().setValue(((FilesListElementDTO) flexibleElement).getLimit());

		} else if (flexibleElement instanceof TextAreaElementDTO || type == ElementTypeEnum.TEXT_AREA) {

			final TextAreaType textAreaType;
			if (flexibleElement instanceof TextAreaElementDTO) {
				textAreaType = TextAreaType.fromCode(((TextAreaElementDTO) flexibleElement).getType());
			} else {
				textAreaType = null;
			}

			loadFlexibleElementTextAreaFields(flexibleElement, textAreaType);

		} else if (flexibleElement instanceof QuestionElementDTO) {

			final QuestionElementDTO questionElement = (QuestionElementDTO) flexibleElement;

			view.setCustomChoiceAddFieldEnabled(true);
			view.getQualityLinkField().setValue(questionElement.getQualityCriterion() != null);
			view.getMultipleChoicesField().setValue(questionElement.getMultiple());
			
			view.getMultipleChoicesField().setEnabled(!isUpdateAndUnderMaintenance());

			if (ClientUtils.isNotEmpty(questionElement.getChoices())) {
				for (final QuestionChoiceElementDTO choice : questionElement.getChoices()) {
					if(!isUpdateAndUnderMaintenance()) {
						onAddCustomChoice(choice.getLabel());
					} else {
						onAddUndeletableCustomChoice(choice.getLabel(), !choice.isDisabled());
					}
				}
			}
		} else if (flexibleElement instanceof BudgetElementDTO) {

			final BudgetElementDTO budgetElement = (BudgetElementDTO) flexibleElement;

			// Showing or hiding the fields depending on the maintenance state of the current model.
			view.getBudgetFields().setVisible(!isUpdateAndUnderMaintenance());
			view.getUpBudgetSubFieldCombo().setVisible(!isUpdateAndUnderMaintenance());
			view.getDownBudgetSubFieldCombo().setVisible(!isUpdateAndUnderMaintenance());
			
			int row = 1;

			for (final BudgetSubFieldDTO budgetField : budgetElement.getBudgetSubFields()) {

				final Text budgetText = new Text();
				budgetText.setData("budgetField", budgetField);
				budgetText.addStyleName("budget-sub-fields-text");

				if (budgetField.getType() != null) {
					budgetField.setLabel(BudgetSubFieldType.getName(budgetField.getType()));
				}

				budgetText.setText(budgetField.getLabel());

				view.getBudgetFields().setWidget(row, 0, budgetText);
				view.getBudgetFields().setWidget(row, 1, new TextField<String>());

				if (budgetField.getType() == null) {

					Anchor anchorEditSubField = new Anchor(IconImageBundle.ICONS.editPage().getSafeHtml());
					Anchor anchorDeleteSubField = new Anchor(IconImageBundle.ICONS.delete().getSafeHtml());
					view.getBudgetFields().setWidget(row, 2, anchorEditSubField);
					view.getBudgetFields().setWidget(row, 3, anchorDeleteSubField);

					anchorEditSubField.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							eventBus.navigateRequest(Page.ADMIN_EDIT_FLEXIBLE_ELEMENT_ADD_BUDGETSUBFIELD.request().addData(RequestParameter.DTO, budgetField));
						}
					});

					anchorDeleteSubField.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							budgetElement.getBudgetSubFields().remove(budgetField);
							loadFlexibleElement(flexibleElement);
						}
					});

				}

				budgetText.show();

				if (budgetField.getId() != null) {

					view.getUpBudgetSubFieldStore().add(budgetField);
					view.getDownBudgetSubFieldStore().add(budgetField);

				}

				row++;

			}

			view.getUpBudgetSubFieldStore().commitChanges();
			view.getDownBudgetSubFieldStore().commitChanges();

			for (BudgetSubFieldDTO budgetField : budgetElement.getBudgetSubFields()) {
				// BUGFIX #706
				if(budgetField.getId() != null) {
					if (budgetField.getId().equals(budgetElement.getRatioDividend().getId())) {
						view.getUpBudgetSubFieldCombo().setValue(budgetField);
					}

					if (budgetField.getId().equals(budgetElement.getRatioDivisor().getId())) {
						view.getDownBudgetSubFieldCombo().setValue(budgetField);
					}
				}
			}

			view.getSpecificForm().recalculate();
			
		} else if (flexibleElement instanceof ComputationElementDTO) {
			
			final ComputationElementDTO computationElement = (ComputationElementDTO) flexibleElement;
			final Computation computation = Computations.parse(computationElement.getRule(), currentModel.getAllElements());
			
			view.getComputationRuleField().setValue(computation.toHumanReadableString());
		}
	}

	/**
	 * Loads the given {@code flexibleElement} <b>text area fields</b> and sets the corresponding form fields values.
	 * 
	 * @param flexibleElement
	 *          The edited flexible element of type {@link ElementTypeEnum#TEXT_AREA}.
	 * @param textAreaType
	 *          The text area type, may be {@code null}.
	 */
	private void loadFlexibleElementTextAreaFields(final FlexibleElementDTO flexibleElement, final TextAreaType textAreaType) {

		view.setTextAreaSpecificFieldsVisibility(textAreaType);

		view.getTextAreaTypeField().setEnabled(!isUpdateAndUnderMaintenance());
		view.getCodeField().setEnabled(!isUpdateAndUnderMaintenance());
		view.getMinDateField().setEnabled(!isUpdateAndUnderMaintenance());
		view.getMaxDateField().setEnabled(!isUpdateAndUnderMaintenance());
		view.getMinLimitField().setEnabled(!isUpdateAndUnderMaintenance());
		view.getMaxLimitField().setEnabled(!isUpdateAndUnderMaintenance());
		
		// Fires change event.
		view.getTextAreaTypeField().setValue(textAreaType != null ? new EnumModel<TextAreaType>(textAreaType) : null);

		if (flexibleElement instanceof TextAreaElementDTO) {

			final TextAreaElementDTO textAreaElement = (TextAreaElementDTO) flexibleElement;
			final Long minValue = textAreaElement.getMinValue();
			final Long maxValue = textAreaElement.getMaxValue();

			view.getMinLimitField().setValue(minValue);
			view.getMaxLimitField().setValue(maxValue);
			view.getLengthField().setValue(textAreaElement.getLength());
			view.getDecimalField().setValue(textAreaElement.getIsDecimal());
			view.getMinDateField().setValue(minValue != null ? new Date(minValue) : null);
			view.getMaxDateField().setValue(maxValue != null ? new Date(maxValue) : null);
		}
	}

	/**
	 * Loads the static field values.
	 */
	private void loadStaticValues() {

		// Loads element types.
		view.getTypeField().getStore().removeAll();

		for (final ElementTypeEnum type : ElementTypeEnum.values()) {
			if (type != ElementTypeEnum.DEFAULT && type != ElementTypeEnum.INDICATORS) {
				view.getTypeField().getStore().add(new EnumModel<ElementTypeEnum>(type));
			}
		}

		// Loads banner positions.
		view.getBannerPositionField().removeAll();

		for (final Integer position : BANNER_POSITIONS) {
			view.getBannerPositionField().add(position);
		}

		// Loads textArea types.
		view.getTextAreaTypeField().getStore().removeAll();

		for (final TextAreaType textAreaType : TextAreaType.values()) {
			view.getTextAreaTypeField().getStore().add(new EnumModel<TextAreaType>(textAreaType));
		}
	}

	/**
	 * Loads the containers and populates the corresponding form field.
	 * 
	 * @param currentModel
	 *          The current model.
	 */
	private void loadContainers(final IsModel currentModel) {

		view.getContainerField().getStore().removeAll();

		if (ClientUtils.isNotEmpty(currentModel.getHasLayoutElements())) {
			for (final AbstractModelDataEntityDTO<?> hasLayout : currentModel.getHasLayoutElements()) {
				view.getContainerField().getStore().add(hasLayout);
			}
		}

		view.getContainerField().getStore().commitChanges();
	}
	
	/**
	 * Change the state of the fields if the current model is under maintenance.
	 * 
	 * @param currentModel 
	 *			The current model.
	 */
	private void loadUnderMaintenanceState(final IsModel currentModel) {
		view.getAmendableField().setVisible(!currentModel.isUnderMaintenance());
	}

	/**
	 * Loads the privacy groups and populates the corresponding form field.<br>
	 * If an edited flexible element is provided, the form field is set on the corresponding value.
	 * 
	 * @param flexibleElement
	 *          The edited flexible element, or {@code null} in case of creation.
	 */
	private void loadPrivacyGroups(final FlexibleElementDTO flexibleElement) {

		view.getPrivacyGroupField().getStore().removeAll();

		dispatch.execute(new GetPrivacyGroups(), new CommandResultHandler<ListResult<PrivacyGroupDTO>>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				if (Log.isErrorEnabled()) {
					Log.error("Error while loading the privacy groups.", caught);
				}
				N10N.error(I18N.CONSTANTS.adminChoiceProblem());
			}

			@Override
			public void onCommandSuccess(final ListResult<PrivacyGroupDTO> result) {

				if (result == null || result.isEmpty()) {
					return;
				}

				view.getPrivacyGroupField().getStore().add(result.getList());
				view.getPrivacyGroupField().getStore().commitChanges();

				if (flexibleElement != null) {
					final PrivacyGroupDTO privacyGroup = flexibleElement.getPrivacyGroup();
					view.getPrivacyGroupField().setValue(privacyGroup);
					oldFieldProperties.put(AdminUtil.PROP_FX_PRIVACY_GROUP, privacyGroup);
				}
				view.getPrivacyGroupField().clearInvalid();
			}
		});
	}

	/**
	 * Loads the reports models and populates the corresponding form field.<br>
	 * If an edited flexible element is provided, the form field is set on the corresponding value.
	 * 
	 * @param flexibleElement
	 *          The edited flexible element, or {@code null} in case of creation.
	 */
	private void loadReportModels(final FlexibleElementDTO flexibleElement) {

		view.getReportModelField().getStore().removeAll();

		dispatch.execute(new GetReportModels(), new CommandResultHandler<ListResult<ReportModelDTO>>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				if (Log.isErrorEnabled()) {
					Log.error("Error while loading the report models.", caught);
				}
				N10N.error(I18N.CONSTANTS.adminChoiceProblem());
			}

			@Override
			public void onCommandSuccess(final ListResult<ReportModelDTO> result) {

				if (result == null || result.isEmpty()) {
					return;
				}

				// Retrieves report model id (in case of flexible element edition).
				final Integer reportModelId;
				if (flexibleElement instanceof ReportElementDTO || flexibleElement instanceof ReportListElementDTO) {
					reportModelId = flexibleElement.get(ReportElementDTO.MODEL_ID);
				} else {
					reportModelId = null;
				}

				ReportModelDTO selectedValue = null;

				// Populates the field store and detects selected value (if any).
				for (final ReportModelDTO reportModel : result.getList()) {
					view.getReportModelField().getStore().add(reportModel);
					if (reportModelId != null && reportModel.getId().equals(reportModelId)) {
						selectedValue = reportModel;
					}
				}

				view.getReportModelField().getStore().commitChanges();
				view.getReportModelField().setValue(selectedValue);
				view.getReportModelField().clearInvalid();

				oldFieldProperties.put(AdminUtil.PROP_FX_REPORT_MODEL, selectedValue);
			}
		});
	}

	/**
	 * Loads the category types and populates the corresponding form field.<br>
	 * If an edited flexible element is provided, the form field is set on the corresponding value.
	 * 
	 * @param flexibleElement
	 *          The edited flexible element, or {@code null} in case of creation.
	 */
	private void loadCategoryTypes(final FlexibleElementDTO flexibleElement) {

		view.getCategoryTypeField().getStore().removeAll();

		dispatch.execute(new GetCategories(), new CommandResultHandler<ListResult<CategoryTypeDTO>>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				if (Log.isErrorEnabled()) {
					Log.error("Error while loading the category types.", caught);
				}
				N10N.error(I18N.CONSTANTS.adminChoiceProblem());
			}

			@Override
			public void onCommandSuccess(final ListResult<CategoryTypeDTO> result) {

				if (result == null || result.isEmpty()) {
					return;
				}

				view.getCategoryTypeField().getStore().add(DEFAULT_CATEGORY_TYPE);
				view.getCategoryTypeField().getStore().add(result.getList());
				view.getCategoryTypeField().getStore().commitChanges();

				if (flexibleElement instanceof QuestionElementDTO) {
					view.getCategoryTypeField().setValue(((QuestionElementDTO) flexibleElement).getCategoryType());
				}

				if (view.getCategoryTypeField().getValue() == null) {
					// If no value selected, default one is automatically selected.
					view.getCategoryTypeField().setValue(DEFAULT_CATEGORY_TYPE);
				}

				view.getCategoryTypeField().clearInvalid();
				oldFieldProperties.put(AdminUtil.PROP_FX_Q_CATEGORY, view.getCategoryTypeField().getValue());
			}
		});
	}

	/**
	 * Callback executed on custom choice add action.<br>
	 * Does nothing if one of the arguments is {@code null} or if the {@code customChoice} is already present.
	 * 
	 * @param customChoice
	 *          The custom choice value.
	 */
	private void onAddCustomChoice(final String customChoice) {

		view.getCustomChoiceField().clear();

		if (ClientUtils.isBlank(customChoice) || customChoices.contains(customChoice)) {
			return;
		}

		view.addCustomChoice(customChoice, new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				customChoices.remove(customChoice);
				view.getCategoryTypeField().setEnabled(ClientUtils.isEmpty(customChoices));
			}
		});

		customChoices.add(customChoice);
		view.getCategoryTypeField().setEnabled(false);
	}

	/**
	 * Callback executed on custom choice add action.<br>
	 * Does nothing if one of the arguments is {@code null} or if the {@code customChoice} is already present.
	 * 
	 * @param customChoice
	 *          The custom choice value.
	 */
	private void onAddUndeletableCustomChoice(final String customChoice, boolean checked) {

		view.getCustomChoiceField().clear();

		if (ClientUtils.isBlank(customChoice) || customChoices.contains(customChoice)) {
			return;
		}
		
		if(!checked) {
			disabledCustomChoices.add(customChoice);
		}

		view.addUndeletableCustomChoice(customChoice, checked, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				final Boolean value = (Boolean) be.getValue();
				if(value != null && value) {
					disabledCustomChoices.remove(customChoice);
				} else {
					disabledCustomChoices.add(customChoice);
				}
			}
		});

		customChoices.add(customChoice);
		view.getCategoryTypeField().setEnabled(false);
	}

	/**
	 * Callback executed on save button action.
	 */
	private void onSaveAction() {

		final String htmlName = ClientUtils.removeLastSuffix(view.getNameField().getValue(), HTML_TAG_NEW_LINE);

		// --
		// Forms validation.
		// --

		if (!FormPanel.valid(view.getCommonForm(), view.getSpecificForm())) {
			// Form(s) validation failed.
			return;
		}

		if (htmlName != null && htmlName.isEmpty()) {
			// Invalid HTML name.
			N10N.warn(I18N.CONSTANTS.form_validation_ko());
			return;
		}

		// --
		// Common properties.
		// --

		final ElementTypeEnum type = EnumModel.getEnum(view.getTypeField().getValue());
		final LayoutGroupDTO group = view.getLayoutGroupField().getValue();
		final Integer order = ClientUtils.getInteger(view.getOrderField().getValue().intValue());
		final Boolean mandatory = view.getMandatoryField().getValue();
		final PrivacyGroupDTO privacyGroup = view.getPrivacyGroupField().getValue();
		final Boolean amendable = view.getAmendableField().getValue();
		final Boolean exportable = view.getExportableField().getValue();
		final String code = view.getCodeField().getValue();

		// --
		// Specific properties.
		// --

		final Boolean banner = view.getBannerField().getValue();
		final Integer bannerPosition = ClientUtils.getSimpleValue(view.getBannerPositionField());
		final TextAreaType textAreaType = EnumModel.getEnum(view.getTextAreaTypeField().getValue());

		final Integer length = ClientUtils.getInteger(view.getLengthField().getValue());
		final Boolean decimal = view.getDecimalField().getValue();
		final Integer maxLimit = ClientUtils.getInteger(view.getMaxLimitField().getValue());
		final Integer minLimit = ClientUtils.getInteger(view.getMinLimitField().getValue());

		final Long minLimitDate = ClientUtils.getTimestamp(view.getMinDateField().getValue());
		final Long maxLimitDate = ClientUtils.getTimestamp(view.getMaxDateField().getValue());

		final ReportModelDTO reportModel = view.getReportModelField().getValue();

		final Boolean multiple = view.getMultipleChoicesField().getValue();
		final CategoryTypeDTO category = view.getCategoryTypeField().getValue();
		
		final String computationRule = Computations.formatRule(view.getComputationRuleField().getValue(), currentModel.getAllElements());

		// --
		// Initializing 'NEW' properties map.
		// --

		final Map<String, Object> newFieldProperties = new HashMap<String, Object>();

		newFieldProperties.put(AdminUtil.PROP_FX_NAME, htmlName);
		newFieldProperties.put(AdminUtil.PROP_FX_CODE, code);
		newFieldProperties.put(AdminUtil.PROP_FX_TYPE, (flexibleElement instanceof BudgetElementDTO) ? ElementTypeEnum.DEFAULT : type);
		newFieldProperties.put(AdminUtil.PROP_FX_GROUP, group);
		newFieldProperties.put(AdminUtil.PROP_FX_ORDER_IN_GROUP, order);
		newFieldProperties.put(AdminUtil.PROP_FX_IN_BANNER, banner);
		newFieldProperties.put(AdminUtil.PROP_FX_POS_IN_BANNER, bannerPosition); // Layout id for banner.
		newFieldProperties.put(AdminUtil.PROP_FX_IS_COMPULSARY, mandatory);
		newFieldProperties.put(AdminUtil.PROP_FX_PRIVACY_GROUP, privacyGroup);
		newFieldProperties.put(AdminUtil.PROP_FX_AMENDABLE, amendable);
		newFieldProperties.put(AdminUtil.PROP_FX_EXPORTABLE, exportable);
		newFieldProperties.put(AdminUtil.PROP_FX_TEXT_TYPE, textAreaType != null ? textAreaType.getCode() : null);
		newFieldProperties.put(AdminUtil.PROP_FX_LENGTH, length);
		newFieldProperties.put(AdminUtil.PROP_FX_MAX_LIMIT, maxLimit);
		newFieldProperties.put(AdminUtil.PROP_FX_MIN_LIMIT, minLimit);
		
		if (textAreaType == TextAreaType.DATE) {
			newFieldProperties.put(AdminUtil.PROP_FX_MAX_LIMIT, maxLimitDate);
			newFieldProperties.put(AdminUtil.PROP_FX_MIN_LIMIT, minLimitDate);
		}

		if (type == ElementTypeEnum.TEXT_AREA && decimal != null) {
			newFieldProperties.put(AdminUtil.PROP_FX_DECIMAL, decimal);
		}

		newFieldProperties.put(AdminUtil.PROP_FX_REPORT_MODEL, reportModel);

		newFieldProperties.put(AdminUtil.PROP_FX_Q_MULTIPLE, multiple);

		if (category != null && !DEFAULT_CATEGORY_TYPE.equals(category)) {
			newFieldProperties.put(AdminUtil.PROP_FX_Q_CATEGORY, category);
		}

		if (ClientUtils.isNotEmpty(customChoices)) {
			newFieldProperties.put(AdminUtil.PROP_FX_Q_CHOICES, new ArrayList<String>(customChoices));
		}

		if (ClientUtils.isNotEmpty(disabledCustomChoices)) {
			newFieldProperties.put(AdminUtil.PROP_FX_Q_CHOICES_DISABLED, disabledCustomChoices);
		}

		List<BudgetSubFieldDTO> budgetSubFieldsToUpdate = new ArrayList<BudgetSubFieldDTO>();
		if (view.getBudgetFields().getRowCount() > 0) {
			for (int i = 1; i < view.getBudgetFields().getRowCount(); i++) {

				if (view.getBudgetFields().getWidget(i, 0) != null) {
					Object budgetFieldData = ((Text) view.getBudgetFields().getWidget(i, 0)).getData("budgetField");
					if (budgetFieldData != null) {
						((BudgetSubFieldDTO) budgetFieldData).setFieldOrder(i);
						budgetSubFieldsToUpdate.add((BudgetSubFieldDTO) budgetFieldData);
					}
				}
			}
			newFieldProperties.put(AdminUtil.PROP_FX_B_BUDGETSUBFIELDS, budgetSubFieldsToUpdate);
			newFieldProperties.put(AdminUtil.PROP_FX_B_BUDGET_RATIO_DIVIDEND, view.getUpBudgetSubFieldCombo().getValue());
			newFieldProperties.put(AdminUtil.PROP_FX_B_BUDGET_RATIO_DIVISOR, view.getDownBudgetSubFieldCombo().getValue());
		}
		
		newFieldProperties.put(AdminUtil.PROP_FX_COMPUTATION_RULE, computationRule);

		// --
		// Logging old/new properties & filtering actual modifications.
		// --

		final StringBuilder message = new StringBuilder();
		message.append("New : (");
		for (Map.Entry<String, Object> newP : newFieldProperties.entrySet()) {
			message.append(newP.getKey()).append('=').append(newP.getValue()).append(", ");
		}

		if (Log.isDebugEnabled()) {
			Log.debug(message.append(')').toString());
		}

		// Only keep actual changes.
		if (flexibleElement != null) {
			message.setLength(0);
			message.append("Old : (");

			for (final Entry<String, Object> old : oldFieldProperties.entrySet()) {
				message.append(old.getKey()).append('=').append(old.getValue()).append(", ");

				if ((old.getValue() != null && old.getValue().equals(newFieldProperties.get(old.getKey())))
					|| (old.getValue() == null && newFieldProperties.get(old.getKey()) == null)) {
					newFieldProperties.remove(old.getKey());
				}
			}

			if (Log.isDebugEnabled()) {
				Log.debug(message.append(')').toString());
			}
		}

		message.setLength(0);
		message.append("Register : (");
		for (final Entry<String, Object> newP : newFieldProperties.entrySet()) {
			message.append(newP.getKey()).append('=').append(newP.getValue()).append(", ");
		}

		if (Log.isDebugEnabled()) {
			Log.debug(message.append(')').toString());
		}

		// --
		// Last properties map adjustments.
		// --

		if (newFieldProperties.get(AdminUtil.PROP_FX_ORDER_IN_GROUP) != null) {
			// If order has changed force putting group.
			newFieldProperties.put(AdminUtil.PROP_FX_GROUP, group);
		}

		newFieldProperties.put(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT, (flexibleElement != null) ? flexibleElement : new TextAreaElementDTO());
		newFieldProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, currentModel);
		newFieldProperties.put(AdminUtil.ADMIN_ORG_UNIT_MODEL, currentModel);
		newFieldProperties.put(AdminUtil.PROP_FX_OLD_FIELDS, oldFieldProperties);

		// --
		// Executing creation/update command.
		// --

		dispatch.execute(new CreateEntity(currentModel.getEntityName(), newFieldProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminFlexibleCreationBox(),
					I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminStandardFlexibleName() + " '" + htmlName + "'"));
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				if (result == null) {
					N10N.warn(I18N.CONSTANTS.adminFlexibleCreationBox(),
						I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminStandardFlexibleName() + " '" + htmlName + "'"));
					return;
				}

				final boolean update = flexibleElement != null;
				final IsModel updatedModel = (IsModel) result.getEntity();
				FlexibleElementDTO updatedOrCreatedFlexibleElement = null;

				if (update) {

					// --
					// UPDATE CASE - Retrieving 'updated' flexible element id.
					// --

					for (final FlexibleElementDTO updatedFlexibleElement : updatedModel.getAllElements()) {
						if (updatedFlexibleElement.getId().equals(flexibleElement.getId())) {
							updatedOrCreatedFlexibleElement = updatedFlexibleElement;
						}
					}

					N10N.infoNotif(I18N.CONSTANTS.adminFlexibleCreationBox(),
						I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminStandardFlexibleName() + " '" + htmlName + "'"));

				} else {

					// --
					// CREATION CASE - Retrieving 'created' flexible element id.
					// --

					final List<Integer> oldFlexibleIds = new ArrayList<Integer>();

					// Collects 'OLD' flexible elements ids.
					for (final FlexibleElementDTO oldFlexibleElement : currentModel.getAllElements()) {
						oldFlexibleIds.add(oldFlexibleElement.getId());
					}

					// Compares 'NEW' flexible elements ids with 'OLD' ones.
					for (final FlexibleElementDTO newFlexibleElement : updatedModel.getAllElements()) {
						if (!oldFlexibleIds.contains(newFlexibleElement.getId())) {
							updatedOrCreatedFlexibleElement = newFlexibleElement;
						}
					}

					N10N.infoNotif(I18N.CONSTANTS.adminFlexibleCreationBox(),
						I18N.MESSAGES.adminStandardCreationSuccess(I18N.CONSTANTS.adminStandardFlexibleName() + " '" + htmlName + "'"));
				}

				// Sends an update event to notify registered components.
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.FLEXIBLE_ELEMENT_UPDATE, updatedModel, update, updatedOrCreatedFlexibleElement));

				hideView();
			}
		}, view.getSaveButton());
	}

}
