package org.sigmah.client.page.admin.model.common.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.ui.ClickableLabel;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetCategories;
import org.sigmah.shared.command.GetPrivacyGroups;
import org.sigmah.shared.command.GetReportModels;
import org.sigmah.shared.command.result.CategoriesListResult;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.PrivacyGroupsListResult;
import org.sigmah.shared.command.result.ReportModelsListResult;
import org.sigmah.shared.command.result.UpdateModelResult;
import org.sigmah.shared.domain.element.BudgetSubFieldType;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.dto.OrgUnitDetailsDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectBannerDTO;
import org.sigmah.shared.dto.ProjectDetailsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
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
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;

/**
 * Create flexible element form.
 * 
 * @author nrebiai
 * 
 */
public class ElementForm extends ContentPanel {

	private final Dispatcher dispatcher;
	private ProjectModelDTO projectModelToUpdate;
	private OrgUnitModelDTO orgUnitModelToUpdate;
	private FlexibleElementDTO flexibleElementToUpdate;
	private final boolean isProject;
	private final HashMap<String, Object> oldFieldProperties = new HashMap<String, Object>();

	// Common attributes
	// private final TextArea nameField;
	private final FormPanel commonPanel;
	private final HtmlEditor htmlArea;
	private final SimpleComboBox<String> typeList;
	private final ComboBox<BaseModelData> containerList;
	private final ComboBox<LayoutGroupDTO> groupList;
	private final NumberField orderField;
	private final CheckBox isBanner;
	private final SimpleComboBox<Integer> posBanner;
	private final CheckBox validates;
	private final ComboBox<PrivacyGroupDTO> privacyGroupsListCombo;
	private final CheckBox isAmendable;
	private final CheckBox isExportable = new CheckBox();

	// Specific attributes
	private final FormPanel specificsPanel;
	private final SimpleComboBox<String> textAreaTypeList;
	private final NumberField maxLimitField;
	private final NumberField minLimitField;
	private final NumberField lengthField;
	private final CheckBox isDecimal;
	private final ComboBox<ReportModelDTO> reportModelList;
	private final ListStore<ReportModelDTO> reportModelsStore;

	private final Grid qChoicesAddGrid = new Grid(1, 3);
	private final CheckBox isMultipleQ;
	private final CheckBox isLinkedToQuality;
	private final ComboBox<CategoryTypeDTO> linkedCategory;
	private final TextField<String> questionChoice;
	private final Button addChoiceButton = new Button(I18N.CONSTANTS.addItem());
	private final Map<Integer, ClickableLabel> selectableChoices = new HashMap<Integer, ClickableLabel>();
	private final List<String> selectedChoicesLabels = new ArrayList<String>();
	final CategoryTypeDTO defaultNoCategory = new CategoryTypeDTO();

	private final FlexTable budgetFields;
	private final FlexTable ratioFlexTable;
	private final ComboBox<BudgetSubFieldDTO> upBudgetSubFieldCombo;
	private final ComboBox<BudgetSubFieldDTO> downBudgetSubFieldCombo;
	private final ListStore<BudgetSubFieldDTO> upBudgetSubFieldStore;
	private final ListStore<BudgetSubFieldDTO> downBudgetSubFieldStore;
	private final Anchor anchorAddSubField;

	private int num = 0;

	private final static int LABEL_WIDTH = 90;
	private final static int MAX_TENTATIVES_FOR_CHOICES = 100;

	public ElementForm(final Dispatcher dispatcher, final AsyncCallback<UpdateModelResult> callback,
	                final FlexibleElementDTO flexibleElement, ProjectModelDTO projectModelToUpdate,
	                OrgUnitModelDTO orgUnitModelToUpdate) {

		final VBoxLayout mainPanelLayout = new VBoxLayout();
		mainPanelLayout.setVBoxLayoutAlign(VBoxLayout.VBoxLayoutAlign.STRETCH);
		setLayout(mainPanelLayout);
		setHeaderVisible(false);

		if (projectModelToUpdate != null) {
			isProject = true;
		} else {
			isProject = false;
		}
		this.dispatcher = dispatcher;
		this.projectModelToUpdate = projectModelToUpdate;
		this.orgUnitModelToUpdate = orgUnitModelToUpdate;
		this.flexibleElementToUpdate = flexibleElement;
		UIConstants constants = GWT.create(UIConstants.class);

		// **********************************************Specific attributes
		// component *******************************************/
		specificsPanel = new FormPanel();
		specificsPanel.setHeaderVisible(false);
		specificsPanel.setBorders(true);
		specificsPanel.setWidth(340);
		specificsPanel.hide();

		budgetFields = new FlexTable();
		budgetFields.setVisible(false);
		anchorAddSubField = new Anchor();
		anchorAddSubField.setHTML(IconImageBundle.ICONS.add().getHTML() + I18N.CONSTANTS.adminAddBudgetSubField());
		anchorAddSubField.addStyleName("credits-partner-url");
		anchorAddSubField.setEnabled(true);

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

		// Report & Report List special
		reportModelList = new ComboBox<ReportModelDTO>();
		reportModelList.setEditable(false);
		reportModelList.hide();
		reportModelList.setAllowBlank(false);
		reportModelList.setFieldLabel(I18N.CONSTANTS.adminReportName());
		reportModelList.setDisplayField("name");
		reportModelList.setValueField("id");
		reportModelList.setTriggerAction(TriggerAction.ALL);
		reportModelsStore = new ListStore<ReportModelDTO>();
		dispatcher.execute(new GetReportModels(), null, new AsyncCallback<ReportModelsListResult>() {

			@Override
			public void onFailure(Throwable arg0) {
				reportModelList.setEmptyText(I18N.CONSTANTS.adminChoiceProblem());// FIXME
			}

			@Override
			public void onSuccess(ReportModelsListResult result) {
				reportModelList.setEmptyText(I18N.CONSTANTS.adminReportName());// FIXME
				reportModelsStore.removeAll();
				if (result != null) {
					reportModelsStore.add(result.getList());
					reportModelsStore.commitChanges();
				}

				if (flexibleElement != null) {
					if (flexibleElement instanceof ReportElementDTO) {
						if (((ReportElementDTO) flexibleElement).getModelId() != null) {
							for (ReportModelDTO reportModel : reportModelsStore.getModels()) {
								if (reportModel.getId() == ((ReportElementDTO) flexibleElement).getModelId()) {
									reportModelList.setValue(reportModel);
								}
							}
						}
					}
				}

				if (flexibleElement != null) {
					if (flexibleElement instanceof ReportListElementDTO) {
						if (((ReportListElementDTO) flexibleElement).getModelId() != null) {
							for (ReportModelDTO reportModel : reportModelsStore.getModels()) {
								if (reportModel.getId() == ((ReportListElementDTO) flexibleElement).getModelId()) {
									reportModelList.setValue(reportModel);
								}
							}
						}
					}
				}
			}
		});

		reportModelList.setStore(reportModelsStore);

		// Files list and text area special
		maxLimitField = new NumberField();
		maxLimitField.setFieldLabel(I18N.CONSTANTS.adminFlexibleMaxLimit());
		maxLimitField.hide();

		// Text area special
		textAreaTypeList = new SimpleComboBox<String>();
		textAreaTypeList.setTriggerAction(TriggerAction.ALL);
		textAreaTypeList.setEditable(false);
		textAreaTypeList.hide();
		textAreaTypeList.setFieldLabel(I18N.CONSTANTS.adminFlexibleTextType());
		List<String> textTypes = new ArrayList<String>();
		textTypes.add(I18N.CONSTANTS.adminFlexibleTextTypeP());
		textTypes.add(I18N.CONSTANTS.adminFlexibleTextTypeT());
		textTypes.add(I18N.CONSTANTS.adminFlexibleTextTypeN());
		textTypes.add(I18N.CONSTANTS.adminFlexibleTextTypeD());
		textAreaTypeList.add(textTypes);

		minLimitField = new NumberField();
		minLimitField.setFieldLabel(I18N.CONSTANTS.adminFlexibleMinLimit());
		minLimitField.hide();

		lengthField = new NumberField();
		lengthField.setFieldLabel(I18N.CONSTANTS.adminFlexibleLength());
		lengthField.hide();

		isDecimal = new CheckBox();
		isDecimal.hide();
		// isDecimal.setBoxLabel(I18N.CONSTANTS.adminFlexibleDecimal());
		isDecimal.setBoxLabel(" ");
		isDecimal.setFieldLabel(I18N.CONSTANTS.adminFlexibleDecimal());
		isDecimal.setLabelSeparator("");

		// Question special

		isMultipleQ = new CheckBox();
		isMultipleQ.hide();
		// isMultipleQ.setBoxLabel(I18N.CONSTANTS.adminFlexibleMultipleQ());
		isMultipleQ.setBoxLabel(" ");
		isMultipleQ.setFieldLabel(I18N.CONSTANTS.adminFlexibleMultipleQ());
		isMultipleQ.setLabelSeparator("");

		isLinkedToQuality = new CheckBox();
		isLinkedToQuality.hide();
		// isLinkedToQuality.setBoxLabel(I18N.CONSTANTS.adminFlexibleLinkedToQuality());
		isLinkedToQuality.setBoxLabel(" ");
		isLinkedToQuality.setFieldLabel(I18N.CONSTANTS.adminFlexibleLinkedToQuality());
		isLinkedToQuality.setLabelSeparator("");

		defaultNoCategory.setLabel(I18N.CONSTANTS.adminFlexibleNoLinkedCategory());
		defaultNoCategory.setId(-1);
		linkedCategory = new ComboBox<CategoryTypeDTO>();
		linkedCategory.setEditable(false);
		linkedCategory.hide();

		linkedCategory.setDisplayField(I18N.CONSTANTS.adminFlexibleNoLinkedCategory());
		linkedCategory.setFieldLabel(I18N.CONSTANTS.adminFlexibleLinkedCategory());
		linkedCategory.setDisplayField("label");
		linkedCategory.setValueField("id");
		linkedCategory.setTriggerAction(TriggerAction.ALL);
		final ListStore<CategoryTypeDTO> linkedCategoriesStore = new ListStore<CategoryTypeDTO>();
		linkedCategoriesStore.add(defaultNoCategory);
		dispatcher.execute(new GetCategories(), null, new AsyncCallback<CategoriesListResult>() {

			@Override
			public void onFailure(Throwable arg0) {
				linkedCategory.setEmptyText(I18N.CONSTANTS.adminChoiceProblem());
			}

			@Override
			public void onSuccess(CategoriesListResult result) {
				linkedCategory.setEmptyText(I18N.CONSTANTS.adminUserCreationProfileChoice());
				linkedCategoriesStore.removeAll();
				linkedCategoriesStore.add(defaultNoCategory);
				if (result != null) {
					linkedCategoriesStore.add(result.getList());
					linkedCategoriesStore.commitChanges();
				}
			}
		});

		linkedCategory.setStore(linkedCategoriesStore);

		for (int i = 0; i < MAX_TENTATIVES_FOR_CHOICES; i++) {
			final ClickableLabel label = new ClickableLabel();
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					label.hide();
					selectedChoicesLabels.remove(label.getText());
					if (selectedChoicesLabels.size() == 0) {
						linkedCategory.setEnabled(true);
					}
				}

			});
			label.hide();
			selectableChoices.put(i, label);
		}

		questionChoice = new TextField<String>();
		questionChoice.hide();

		qChoicesAddGrid.setVisible(false);
		qChoicesAddGrid.getCellFormatter().setWidth(0, 0, (LABEL_WIDTH) + "px");
		qChoicesAddGrid.setCellPadding(0);
		qChoicesAddGrid.setCellSpacing(0);
		qChoicesAddGrid.setWidget(0, 0, new LabelField(I18N.CONSTANTS.adminFlexibleQChoices() + ":"));
		qChoicesAddGrid.setWidget(0, 1, questionChoice);
		questionChoice.setHideLabel(false);

		addChoiceButton.hide();
		addChoiceButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				if (questionChoice.getValue() != null) {
					if (!selectedChoicesLabels.contains(questionChoice.getValue())) {
						if (num < MAX_TENTATIVES_FOR_CHOICES) {
							linkedCategory.setEnabled(false);
							selectableChoices.get(num).setText(questionChoice.getValue());
							selectableChoices.get(num).show();
							num++;
							selectedChoicesLabels.add(questionChoice.getValue());
						} else {
							MessageBox.alert(I18N.CONSTANTS.adminMaxAttempts(),
							                I18N.CONSTANTS.adminMaxAttemptsQChoices(), null);
							ElementForm.this.removeFromParent();
						}
					}
				}
			}
		});

		qChoicesAddGrid.setWidget(0, 2, addChoiceButton);

		// If user choose to link the question to a category, he can't add
		// personal choices
		linkedCategory.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (linkedCategory.getValue() == null || defaultNoCategory.equals(linkedCategory.getValue())) {
					questionChoice.setEnabled(true);
					addChoiceButton.setEnabled(true);
				} else {
					questionChoice.setEnabled(false);
					addChoiceButton.setEnabled(false);
					for (ClickableLabel selected : selectableChoices.values()) {
						selected.setEnabled(false);
					}
				}
			}
		});

		// Only for default elements
		posBanner = new SimpleComboBox<Integer>();
		posBanner.setTriggerAction(TriggerAction.ALL);
		posBanner.setEditable(false);
		posBanner.setFieldLabel(constants.adminFlexibleBannerPosition());
		posBanner.removeAll();
		for (int i = 1; i < 7; i++) {
			posBanner.add(i);
		}
		posBanner.setAllowBlank(true);
		posBanner.setEnabled(false);
		posBanner.hide();

		isBanner = new CheckBox();
		isBanner.setFieldLabel(constants.Admin_BANNER());
		isBanner.setBoxLabel(" ");// constants.Admin_BANNER());
		isBanner.setValue(false);
		isBanner.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (isBanner.getValue()) {
					posBanner.removeAll();
					for (int i = 1; i < 7; i++) {
						posBanner.add(i);
					}
					posBanner.setEnabled(true);
					posBanner.setAllowBlank(false);
				} else {
					posBanner.removeAll();
					posBanner.setValue(null);
					posBanner.setAllowBlank(true);
					posBanner.setEnabled(false);
				}

			}

		});
		isBanner.hide();

		// initSpecifics(flexibleElement);
		specificsPanel.add(isBanner);
		specificsPanel.add(posBanner);
		specificsPanel.add(reportModelList);
		specificsPanel.add(textAreaTypeList);
		specificsPanel.add(lengthField);
		specificsPanel.add(isDecimal);
		specificsPanel.add(minLimitField);
		specificsPanel.add(maxLimitField);
		specificsPanel.add(isMultipleQ);
		specificsPanel.add(isLinkedToQuality);
		specificsPanel.add(linkedCategory);
		specificsPanel.add(qChoicesAddGrid);
		specificsPanel.add(budgetFields);
		specificsPanel.add(anchorAddSubField);
		specificsPanel.add(ratioFlexTable);
		for (ClickableLabel selected : selectableChoices.values()) {
			selected.hide();
			specificsPanel.add(selected);
		}

		// ********************************************** Common attributes
		// component *******************************************/
		commonPanel = new FormPanel();
		commonPanel.setHeaderVisible(false);

		htmlArea = new HtmlEditor();
		htmlArea.hide();
		htmlArea.setEnableAlignments(false);
		htmlArea.setEnableLinks(false);
		htmlArea.setEnableFont(false);
		htmlArea.setEnableLists(false);
		htmlArea.setEnableColors(false);
		htmlArea.setSourceEditMode(false);
		htmlArea.setHeight(75);
		htmlArea.setFieldLabel(constants.adminFlexibleName());
		if (flexibleElement != null && flexibleElement.getLabel() != null) {
			htmlArea.setValue(flexibleElement.getLabel());
			oldFieldProperties.put(AdminUtil.PROP_FX_NAME, flexibleElement.getLabel());
		}

		LabelField label = new LabelField();
		label.hide();
		label.setFieldLabel(constants.adminFlexibleName());

		if (flexibleElement != null && flexibleElement.getElementType() != null) {
			if (ElementTypeEnum.DEFAULT.equals(flexibleElement.getElementType())) {
				label.setText(DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO) flexibleElement)
				                .getType()));
				label.show();
				oldFieldProperties.put(AdminUtil.PROP_FX_NAME, DefaultFlexibleElementType
				                .getName(((DefaultFlexibleElementDTO) flexibleElement).getType()));
				htmlArea.setValue(DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO) flexibleElement)
				                .getType()));
				if (DefaultFlexibleElementType.BUDGET.equals(((DefaultFlexibleElementDTO) flexibleElement).getType())) {
					final BudgetElementDTO budgetElement = (BudgetElementDTO) flexibleElement;
					upBudgetSubFieldCombo.addListener(Events.OnChange, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {
							updateRatioCombos(upBudgetSubFieldCombo);

						}

					});

					downBudgetSubFieldCombo.addListener(Events.OnChange, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {
							updateRatioCombos(downBudgetSubFieldCombo);

						}

					});
					updateElementFormForBudgetElement(budgetElement);

					final Window window = new Window();
					window.setSize(300, 200);
					window.setPlain(true);
					window.setModal(true);
					window.setBlinkModal(true);
					window.setLayout(new FitLayout());

					anchorAddSubField.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							final BudgetSubFieldForm budgetSubFieldForm = new BudgetSubFieldForm(dispatcher, null,
							                budgetElement, null);
							budgetSubFieldForm.getSaveButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

								@Override
								public void handleEvent(BaseEvent be) {
									if (budgetSubFieldForm.isValid()) {
										if (budgetSubFieldForm.getNameField().getValue() != null) {
											final BudgetSubFieldDTO newBudgetSubField = new BudgetSubFieldDTO();
											newBudgetSubField.setLabel(budgetSubFieldForm.getNameField().getValue());
											final Text budgetFieldName = new Text(budgetSubFieldForm.getNameField()
											                .getValue());
											budgetFieldName.setData("budgetField", newBudgetSubField);
											budgetFieldName.addStyleName("budget-sub-fields-text");
											upBudgetSubFieldCombo.getStore().add(newBudgetSubField);
											downBudgetSubFieldCombo.getStore().add(newBudgetSubField);
											int row = budgetFields.getRowCount();
											budgetFields.setWidget(row, 0, budgetFieldName);

											Anchor anchorEditSubField = new Anchor(IconImageBundle.ICONS.editPage()
											                .getSafeHtml());
											anchorEditSubField.addClickHandler(new ClickHandler() {

												@Override
												public void onClick(ClickEvent arg0) {
													final Window window = new Window();
													window.setSize(400, 200);
													window.setPlain(true);
													window.setModal(true);
													window.setBlinkModal(true);
													window.setLayout(new FitLayout());
													final BudgetSubFieldForm budgetSubFieldForm = new BudgetSubFieldForm(
													                dispatcher, null, null, null);
													budgetSubFieldForm.getNameField().setValue(
													                newBudgetSubField.getLabel());
													budgetSubFieldForm.getSaveButton().addListener(Events.OnClick,
													                new Listener<BaseEvent>() {

														                @Override
														                public void handleEvent(BaseEvent be) {
															                if (budgetSubFieldForm.isValid()) {
																                if (budgetSubFieldForm.getNameField()
																                                .getValue() != null) {
																	                newBudgetSubField
																	                                .setLabel(budgetSubFieldForm
																	                                                .getNameField()
																	                                                .getValue());
																	                budgetFieldName.setText(budgetSubFieldForm
																	                                .getNameField()
																	                                .getValue());
																	                ((BudgetSubFieldDTO) budgetFieldName
																	                                .getData("budgetField"))
																	                                .setLabel(budgetSubFieldForm
																	                                                .getNameField()
																	                                                .getValue());
																	                window.hide();
																                }
															                } else {
																                MessageBox.alert(
																                                I18N.CONSTANTS.createFormIncomplete(),
																                                I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS
																                                                .adminBudgetSubField()),
																                                null);
															                }
														                }
													                });

													window.add(budgetSubFieldForm);
													window.show();

												}
											});
											Anchor anchorDeleteSubField = new Anchor(IconImageBundle.ICONS.delete()
											                .getSafeHtml());
											anchorDeleteSubField.addClickHandler(new ClickHandler() {

												@Override
												public void onClick(ClickEvent arg0) {
													budgetFields.removeRow(budgetFields.getRowCount());
													upBudgetSubFieldStore.remove(newBudgetSubField);
													upBudgetSubFieldStore.commitChanges();
													downBudgetSubFieldStore.remove(newBudgetSubField);
													downBudgetSubFieldStore.commitChanges();
												}
											});
											budgetFields.setWidget(row, 1, new TextField<String>());
											budgetFields.setWidget(row, 2, anchorEditSubField);
											budgetFields.setWidget(row, 3, anchorDeleteSubField);
											budgetFieldName.setVisible(true);
											window.hide();

										}
									} else {
										MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(), I18N.MESSAGES
										                .createFormIncompleteDetails(I18N.CONSTANTS
										                                .adminBudgetSubField()), null);
									}
								}

							});
							window.add(budgetSubFieldForm);
							window.show();
						}
					});

				}

			} else {
				htmlArea.show();
			}
		} else {
			htmlArea.show();
		}

		commonPanel.add(label);
		commonPanel.add(htmlArea);

		typeList = new SimpleComboBox<String>();
		typeList.setEditable(false);
		typeList.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				hideAllSpecificAttributes();
				showSpecificAttributes(typeList.getSimpleValue(), flexibleElementToUpdate, true);
			}

		});

		typeList.setFieldLabel(constants.adminFlexibleType());
		typeList.setAllowBlank(false);
		typeList.setTriggerAction(TriggerAction.ALL);
		List<String> values = new ArrayList<String>();
		for (ElementTypeEnum e : ElementTypeEnum.values()) {
			// Hide the types: "DEFAULT" except for the BUDGET and
			// "INDICATOR LIST"
			if (!ElementTypeEnum.DEFAULT.equals(e) && !ElementTypeEnum.INDICATORS.equals(e))
				values.add(ElementTypeEnum.getName(e));
		}
		typeList.add(values);
		if (flexibleElement != null && flexibleElement.getElementType() != null) {

			String typeOfElement = ElementTypeEnum.getName(flexibleElement.getElementType());
			typeList.setSimpleValue(typeOfElement);
			showSpecificAttributes(typeOfElement, flexibleElement, false);
			oldFieldProperties.put(AdminUtil.PROP_FX_TYPE, flexibleElement.getElementType());
			if (ElementTypeEnum.DEFAULT.equals(flexibleElement.getElementType())) {
				typeList.setEnabled(false);
			}
		} else
			typeList.setEmptyText(I18N.CONSTANTS.adminFlexibleType());

		commonPanel.add(typeList);

		final ListStore<BaseModelData> containersStore = new ListStore<BaseModelData>();
		containerList = new ComboBox<BaseModelData>();
		containerList.setEditable(false);
		containerList.setDisplayField("name");
		containerList.setValueField("id");
		containerList.setFieldLabel(I18N.CONSTANTS.adminFlexibleContainer());
		containerList.setAllowBlank(false);
		containerList.setTriggerAction(TriggerAction.ALL);
		if (flexibleElement == null)
			containerList.setEmptyText(I18N.CONSTANTS.adminFlexibleContainerChoice());

		final ListStore<LayoutGroupDTO> groupsStore = new ListStore<LayoutGroupDTO>();
		groupList = new ComboBox<LayoutGroupDTO>();
		groupList.setEditable(false);
		groupList.setFieldLabel(constants.adminFlexibleGroup());
		groupList.setDisplayField("title");
		groupList.setValueField("id");
		groupList.setTriggerAction(TriggerAction.ALL);

		// groupList depends on container layout
		containerList.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				LayoutDTO container = null;
				if (containerList.getValue() != null) {
					if (containerList.getValue() instanceof ProjectBannerDTO) {
						container = ((ProjectBannerDTO) containerList.getValue()).getLayoutDTO();
						// when selecting banner, automatically check inBanner
						isBanner.setValue(true);
						posBanner.setAllowBlank(false);
						posBanner.setEnabled(true);
					} else if (containerList.getValue() instanceof ProjectDetailsDTO) {
						container = ((ProjectDetailsDTO) containerList.getValue()).getLayoutDTO();
					} else if (containerList.getValue() instanceof PhaseModelDTO) {
						container = ((PhaseModelDTO) containerList.getValue()).getLayoutDTO();
					} else if (containerList.getValue() instanceof OrgUnitDetailsDTO) {
						container = ((OrgUnitDetailsDTO) containerList.getValue()).getLayout();
					}
				}
				if (container != null) {
					groupsStore.removeAll();
					for (LayoutGroupDTO lg : container.getLayoutGroupsDTO()) {
						groupsStore.add(lg);
					}
					groupList.setValue(groupsStore.getAt(0));
				}
			}

		});

		orderField = new NumberField();
		orderField.setAllowBlank(false);
		orderField.setFieldLabel(constants.adminFlexibleOrder());
		orderField.clear();

		// scanning all layouts
		oldFieldProperties.put(AdminUtil.PROP_FX_IN_BANNER, false);
		oldFieldProperties.put(AdminUtil.PROP_FX_POS_IN_BANNER, -1);

		if (isProject) {
			if (flexibleElement != null) {
				BaseModelData container = flexibleElement.getContainerModel();
				containerList.setValue(flexibleElement.getContainerModel());
				groupList.setValue(flexibleElement.getConstraint().getParentLayoutGroupDTO());
				orderField.setValue(flexibleElement.getConstraint().getSortOrder());
				groupsStore.removeAll();
				if (container instanceof PhaseModelDTO) {
					for (LayoutGroupDTO groupChoice : ((PhaseModelDTO) container).getLayoutDTO().getLayoutGroupsDTO()) {
						groupsStore.add(groupChoice);
					}
				} else {
					for (LayoutGroupDTO groupChoice : ((ProjectDetailsDTO) container).getLayoutDTO()
					                .getLayoutGroupsDTO()) {
						groupsStore.add(groupChoice);
					}
				}
				if (flexibleElement.getBannerConstraint() != null) {
					posBanner.setSimpleValue(flexibleElement.getBannerConstraint().getSortOrder());
					isBanner.setValue(true);

					oldFieldProperties.put(AdminUtil.PROP_FX_IN_BANNER, true);
					oldFieldProperties.put(AdminUtil.PROP_FX_POS_IN_BANNER, flexibleElement.getBannerConstraint()
					                .getSortOrder());
					oldFieldProperties.put(AdminUtil.PROP_FX_LC_BANNER, flexibleElement.getBannerConstraint());
				}

				oldFieldProperties.put(AdminUtil.PROP_FX_ORDER_IN_GROUP, new Integer(flexibleElement.getConstraint()
				                .getSortOrder()));
				oldFieldProperties.put(AdminUtil.PROP_FX_GROUP, flexibleElement.getConstraint()
				                .getParentLayoutGroupDTO());
				oldFieldProperties.put(AdminUtil.PROP_FX_LC, flexibleElement.getConstraint());

			}
			containersStore.add(projectModelToUpdate.getProjectDetailsDTO());
			for (PhaseModelDTO p : projectModelToUpdate.getPhaseModelsDTO()) {
				containersStore.add(p);
			}
		} else {
			if (flexibleElement != null) {
				BaseModelData container = flexibleElement.getContainerModel();
				containerList.setValue(flexibleElement.getContainerModel());
				groupList.setValue(flexibleElement.getConstraint().getParentLayoutGroupDTO());
				orderField.setValue(flexibleElement.getConstraint().getSortOrder());
				groupsStore.removeAll();
				for (LayoutGroupDTO groupChoice : ((OrgUnitDetailsDTO) container).getLayout().getLayoutGroupsDTO()) {
					groupsStore.add(groupChoice);
				}
				// addNewGroupsToStore(((OrgUnitDetailsDTO)container).getLayout(),
				// groupsStore, addedGroups);
				if (flexibleElement.getBannerConstraint() != null) {
					posBanner.setSimpleValue(flexibleElement.getBannerConstraint().getSortOrder());
					isBanner.setValue(true);

					oldFieldProperties.put(AdminUtil.PROP_FX_IN_BANNER, true);
					oldFieldProperties.put(AdminUtil.PROP_FX_POS_IN_BANNER, flexibleElement.getBannerConstraint()
					                .getSortOrder());
					oldFieldProperties.put(AdminUtil.PROP_FX_LC_BANNER, flexibleElement.getBannerConstraint());
				}

				oldFieldProperties.put(AdminUtil.PROP_FX_ORDER_IN_GROUP, new Integer(flexibleElement.getConstraint()
				                .getSortOrder()));
				oldFieldProperties.put(AdminUtil.PROP_FX_GROUP, flexibleElement.getConstraint()
				                .getParentLayoutGroupDTO());
				oldFieldProperties.put(AdminUtil.PROP_FX_LC, flexibleElement.getConstraint());

			}
			containersStore.add(orgUnitModelToUpdate.getDetails());
		}

		containerList.setStore(containersStore);
		commonPanel.add(containerList);
		groupList.setStore(groupsStore);
		commonPanel.add(groupList);
		commonPanel.add(orderField);

		validates = new CheckBox();
		validates.setFieldLabel(constants.adminFlexibleCompulsory());
		// validates.setBoxLabelX(constants.adminFlexibleCompulsory());
		validates.setBoxLabel(" ");
		validates.setValue(false);
		if (flexibleElement != null) {
			validates.setValue(flexibleElement.getValidates());
			oldFieldProperties.put(AdminUtil.PROP_FX_IS_COMPULSARY, flexibleElement.getValidates());
		}

		commonPanel.add(validates);

		privacyGroupsListCombo = new ComboBox<PrivacyGroupDTO>();
		privacyGroupsListCombo.setEditable(false);
		privacyGroupsListCombo.setFieldLabel(constants.adminPrivacyGroups());
		privacyGroupsListCombo.setDisplayField("title");
		privacyGroupsListCombo.setValueField("id");
		privacyGroupsListCombo.setTriggerAction(TriggerAction.ALL);
		final ListStore<PrivacyGroupDTO> privacyGroupsStore = new ListStore<PrivacyGroupDTO>();
		dispatcher.execute(new GetPrivacyGroups(), null, new AsyncCallback<PrivacyGroupsListResult>() {

			@Override
			public void onFailure(Throwable arg0) {
				privacyGroupsListCombo.setEmptyText(I18N.CONSTANTS.adminChoiceProblem());
			}

			@Override
			public void onSuccess(PrivacyGroupsListResult result) {
				privacyGroupsListCombo.setEmptyText(I18N.CONSTANTS.adminPrivacyGroupChoice());
				privacyGroupsStore.removeAll();
				if (result != null) {
					privacyGroupsStore.add(result.getList());
					privacyGroupsStore.commitChanges();
				}
			}
		});

		privacyGroupsListCombo.setStore(privacyGroupsStore);
		if (flexibleElement != null && flexibleElement.getPrivacyGroup() != null) {
			privacyGroupsListCombo.setValue(flexibleElement.getPrivacyGroup());
			oldFieldProperties.put(AdminUtil.PROP_FX_PRIVACY_GROUP, flexibleElement.getPrivacyGroup());
		}

		commonPanel.add(privacyGroupsListCombo);

		isAmendable = new CheckBox();
		// isAmendable.setBoxLabel(constants.adminFlexibleAmendable());
		isAmendable.setBoxLabel(" ");
		isAmendable.setFieldLabel(constants.adminFlexibleAmendable());
		isAmendable.setValue(false);
		if (flexibleElement != null) {
			isAmendable.setValue(flexibleElement.getAmendable());
			oldFieldProperties.put(AdminUtil.PROP_FX_AMENDABLE, flexibleElement.getAmendable());
		}

		commonPanel.add(isAmendable);

		// exported in a synthesis sheet
		isExportable.setBoxLabel(" ");
		isExportable.setFieldLabel(constants.adminFlexibleExportable());
		isExportable.setValue(true);
		if (flexibleElement != null) {
			isExportable.setValue(flexibleElement.getExportable());
			oldFieldProperties.put(AdminUtil.PROP_FX_EXPORTABLE, flexibleElement.getExportable());
		}

		commonPanel.add(isExportable);

		// Create button.
		final Button createButton = new Button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		createButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				createFlexibleElement(callback);
			}
		});

		final ContentPanel form = new ContentPanel();
		form.setHeight(470);
		form.setHeaderVisible(false);
		form.setLayout(new BorderLayout());
		final BorderLayoutData leftLayoutData = new BorderLayoutData(LayoutRegion.WEST);
		leftLayoutData.setSize(320);
		form.add(commonPanel, leftLayoutData);

		final BorderLayoutData rightLayoutData = new BorderLayoutData(LayoutRegion.CENTER);
		rightLayoutData.setMargins(new Margins(0, 0, 2, 4));
		rightLayoutData.setSize(200);
		form.add(specificsPanel, rightLayoutData);

		final VBoxLayoutData topVBoxLayoutData = new VBoxLayoutData();
		topVBoxLayoutData.setFlex(1.0);
		add(form, topVBoxLayoutData);

		ToolBar toolbar = new ToolBar();
		toolbar.setAlignment(HorizontalAlignment.CENTER);
		toolbar.add(createButton);

		setBottomComponent(toolbar);

		layout();
	}

	private void hideAllSpecificAttributes() {
		linkedCategory.hide();
		isLinkedToQuality.hide();
		isMultipleQ.hide();
		questionChoice.hide();
		addChoiceButton.hide();
		for (ClickableLabel selected : selectableChoices.values()) {
			selected.hide();
		}
		qChoicesAddGrid.setVisible(false);
		textAreaTypeList.hide();
		maxLimitField.hide();
		minLimitField.hide();
		lengthField.hide();
		isDecimal.hide();
		reportModelList.hide();
		isBanner.hide();
		posBanner.hide();
		budgetFields.setVisible(false);
		ratioFlexTable.setVisible(false);
		anchorAddSubField.setVisible(false);
		specificsPanel.hide();
	}

	private void showSpecificAttributes(String type, FlexibleElementDTO flexibleElement, boolean onSelectAction) {
		initSpecifics(flexibleElement);
		if (ElementTypeEnum.getName(ElementTypeEnum.DEFAULT).equals(type)) {
			isBanner.show();
			posBanner.show();
			specificsPanel.show();
			if (DefaultFlexibleElementType.BUDGET.equals(((DefaultFlexibleElementDTO) flexibleElement).getType())) {
				budgetFields.setVisible(true);
				ratioFlexTable.setVisible(true);
			}
		} else if (ElementTypeEnum.getName(ElementTypeEnum.CHECKBOX).equals(type)) {
			// no additional fields
		} else if (ElementTypeEnum.getName(ElementTypeEnum.FILES_LIST).equals(type)) {
			if (onSelectAction) {
				maxLimitField.clear();
			}
			maxLimitField.show();
			specificsPanel.show();
			isExportable.hide();
		} else if (ElementTypeEnum.getName(ElementTypeEnum.INDICATORS).equals(type)) {
			// no additional fields
			isExportable.hide();
		} else if (ElementTypeEnum.getName(ElementTypeEnum.MESSAGE).equals(type)) {
			// no additional fields
		} else if (ElementTypeEnum.getName(ElementTypeEnum.QUESTION).equals(type)) {
			linkedCategory.show();
			// FIXME isLinkedToQuality.show();
			isMultipleQ.show();
			questionChoice.show();
			addChoiceButton.show();
			for (ClickableLabel selected : selectableChoices.values()) {
				if (selected.getText() != null) {
					selected.show();
				}
			}
			qChoicesAddGrid.setVisible(true);
			specificsPanel.show();
		} else if (ElementTypeEnum.getName(ElementTypeEnum.REPORT).equals(type)
		                || ElementTypeEnum.getName(ElementTypeEnum.REPORT_LIST).equals(type)) {
			reportModelList.show();
			specificsPanel.show();
			isExportable.hide();
		} else if (ElementTypeEnum.getName(ElementTypeEnum.TEXT_AREA).equals(type)) {
			if (onSelectAction) {
				maxLimitField.clear();
			}
			textAreaTypeList.show();
			lengthField.show();
			isDecimal.show();
			minLimitField.show();
			maxLimitField.show();
			specificsPanel.show();
		}
	}

	private void createFlexibleElement(final AsyncCallback<UpdateModelResult> callback) {

		// common attributes
		String send = "";
		if (htmlArea.getValue() != null && !htmlArea.getValue().isEmpty()) {
			String a = htmlArea.getValue();

			if (a.endsWith("<br>")) {
				// Log.debug("The name ends with br: "+a);
				send = a.substring(0, a.length() - 4);
			} else {
				// Log.debug("The name ends without br: "+a);
				send = a;
			}

		}

		// If the type chosen is a report or a report list and the report model
		// is not given
		if (!commonPanel.isValid()
		                || htmlArea.getValue() != null
		                && htmlArea.getValue().isEmpty()
		                || "".equals(send)
		                || ((ElementTypeEnum.getName(ElementTypeEnum.REPORT).equals(typeList.getSimpleValue()) || ElementTypeEnum
		                                .getName(ElementTypeEnum.REPORT_LIST).equals(typeList.getSimpleValue())) && !reportModelList
		                                .isValid())) {
			MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
			                I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminStandardFlexibleName()), null);
			return;
		}

		final String name = send;

		// Log.debug("Sending final " + name);

		String type = null;
		if (typeList.getSimpleValue() != null) {
			type = typeList.getSimpleValue();
		}

		LayoutGroupDTO group = groupList.getValue();
		Integer order = null;
		if (orderField.getValue() != null)
			order = new Integer(orderField.getValue().intValue());

		Boolean isCompulsory = validates.getValue();
		PrivacyGroupDTO pg = privacyGroupsListCombo.getValue();
		Boolean amend = isAmendable.getValue();
		Boolean export = isExportable.getValue();

		// specific attributes
		Boolean inBanner = isBanner.getValue();
		Integer posB = null;
		if (posBanner.getValue() != null)
			posB = posBanner.getValue().getValue();
		String textType = null;
		if (textAreaTypeList.getValue() != null)
			textType = textAreaTypeList.getSimpleValue();

		Character textTypeC = null;

		if (I18N.CONSTANTS.adminFlexibleTextTypeP().equals(textType)) {
			textTypeC = 'P';
		} else if (I18N.CONSTANTS.adminFlexibleTextTypeT().equals(textType)) {
			textTypeC = 'T';
		} else if (I18N.CONSTANTS.adminFlexibleTextTypeN().equals(textType)) {
			textTypeC = 'N';
		} else if (I18N.CONSTANTS.adminFlexibleTextTypeD().equals(textType)) {
			textTypeC = 'D';
		}

		Integer maxLimit = null;
		if (maxLimitField.getValue() != null)
			maxLimit = new Integer(maxLimitField.getValue().intValue());
		Integer minLimit = null;
		if (minLimitField.getValue() != null)
			minLimit = new Integer(minLimitField.getValue().intValue());
		Integer length = null;
		if (lengthField.getValue() != null)
			length = new Integer(lengthField.getValue().intValue());
		Boolean decimal = isDecimal.getValue();

		ReportModelDTO reportModel = reportModelList.getValue();

		Boolean multipleQ = isMultipleQ.getValue();
		CategoryTypeDTO category = linkedCategory.getValue();

		Map<String, Object> newFieldProperties = new HashMap<String, Object>();

		newFieldProperties.put(AdminUtil.PROP_FX_NAME, name);
		if (flexibleElementToUpdate != null && flexibleElementToUpdate instanceof BudgetElementDTO) {
			newFieldProperties.put(AdminUtil.PROP_FX_TYPE, ElementTypeEnum.DEFAULT);
		} else {
			newFieldProperties.put(AdminUtil.PROP_FX_TYPE, ElementTypeEnum.getType(type));
		}
		newFieldProperties.put(AdminUtil.PROP_FX_GROUP, group);
		newFieldProperties.put(AdminUtil.PROP_FX_ORDER_IN_GROUP, order);
		newFieldProperties.put(AdminUtil.PROP_FX_IN_BANNER, inBanner);
		newFieldProperties.put(AdminUtil.PROP_FX_POS_IN_BANNER, posB);// layout
		                                                              // id
		                                                              // for
		                                                              // banner
		newFieldProperties.put(AdminUtil.PROP_FX_IS_COMPULSARY, isCompulsory);
		newFieldProperties.put(AdminUtil.PROP_FX_PRIVACY_GROUP, pg);
		newFieldProperties.put(AdminUtil.PROP_FX_AMENDABLE, amend);
		newFieldProperties.put(AdminUtil.PROP_FX_EXPORTABLE, export);

		if (textTypeC != null)
			newFieldProperties.put(AdminUtil.PROP_FX_TEXT_TYPE, textTypeC);
		if (length != null)
			newFieldProperties.put(AdminUtil.PROP_FX_LENGTH, length);
		if (maxLimit != null)
			newFieldProperties.put(AdminUtil.PROP_FX_MAX_LIMIT, maxLimit);
		if (minLimit != null)
			newFieldProperties.put(AdminUtil.PROP_FX_MIN_LIMIT, minLimit);
		if (decimal != null && ElementTypeEnum.TEXT_AREA.equals(ElementTypeEnum.getType(type)))
			newFieldProperties.put(AdminUtil.PROP_FX_DECIMAL, decimal);
		// Report Element
		if (reportModel != null)
			newFieldProperties.put(AdminUtil.PROP_FX_REPORT_MODEL, reportModel);
		// Question Element
		if (multipleQ != null)
			newFieldProperties.put(AdminUtil.PROP_FX_Q_MULTIPLE, multipleQ);
		if (category != null && category != defaultNoCategory)
			newFieldProperties.put(AdminUtil.PROP_FX_Q_CATEGORY, category);
		if (selectedChoicesLabels != null)
			newFieldProperties.put(AdminUtil.PROP_FX_Q_CHOICES, selectedChoicesLabels);

		List<BudgetSubFieldDTO> budgetSubFieldsToUpdate = new ArrayList<BudgetSubFieldDTO>();
		if (budgetFields.getRowCount() > 0) {
			for (int i = 1; i < budgetFields.getRowCount(); i++) {
				if (budgetFields.getWidget(i, 0) != null) {
					Object budgetFieldData = ((Text) budgetFields.getWidget(i, 0)).getData("budgetField");
					if (budgetFieldData != null) {
						((BudgetSubFieldDTO) budgetFieldData).setFieldOrder(i);
						budgetSubFieldsToUpdate.add((BudgetSubFieldDTO) budgetFieldData);
					}
				}
			}
			newFieldProperties.put(AdminUtil.PROP_FX_B_BUDGETSUBFIELDS, budgetSubFieldsToUpdate);
			newFieldProperties.put(AdminUtil.PROP_FX_B_BUDGET_RATIO_DIVIDEND, upBudgetSubFieldCombo.getValue());
			newFieldProperties.put(AdminUtil.PROP_FX_B_BUDGET_RATIO_DIVISOR, downBudgetSubFieldCombo.getValue());
		}

		for (String selLab : selectedChoicesLabels) {
			Log.debug("Sel : " + selLab);
		}

		String message = "New : (";
		for (Map.Entry<String, Object> newP : newFieldProperties.entrySet()) {
			message += newP.getKey() + "=" + newP.getValue() + ", ";
		}

		Log.debug(message + ")");

		// Only keep actual changes
		if (flexibleElementToUpdate != null) {
			message = "Old : (";
			for (Map.Entry<String, Object> old : oldFieldProperties.entrySet()) {
				message += old.getKey() + "=" + old.getValue() + ", ";
				if ((old.getValue() != null && old.getValue().equals(newFieldProperties.get(old.getKey())))
				                || (old.getValue() == null && newFieldProperties.get(old.getKey()) == null)) {
					newFieldProperties.remove(old.getKey());
				}
			}
			Log.debug(message + ")");
		}

		message = "Register : (";
		for (Map.Entry<String, Object> newP : newFieldProperties.entrySet()) {
			if (newP.getValue() != null)
				message += newP.getKey() + "=" + newP.getValue() + ", ";
		}

		Log.debug(message + ")");

		if (newFieldProperties.get(AdminUtil.PROP_FX_ORDER_IN_GROUP) != null) {// if
			                                                                   // order
			                                                                   // has
			                                                                   // changed
			                                                                   // force
			                                                                   // putting
			                                                                   // group
			newFieldProperties.put(AdminUtil.PROP_FX_GROUP, group);
		}

		if (flexibleElementToUpdate != null) {
			newFieldProperties.put(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT, flexibleElementToUpdate);
		} else {
			TextAreaElementDTO t = new TextAreaElementDTO();
			newFieldProperties.put(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT, t);
		}
		newFieldProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, projectModelToUpdate);
		newFieldProperties.put(AdminUtil.ADMIN_ORG_UNIT_MODEL, orgUnitModelToUpdate);
		newFieldProperties.put(AdminUtil.PROP_FX_OLD_FIELDS, oldFieldProperties);

		if (isProject) {
			dispatcher.execute(new CreateEntity("ProjectModel", newFieldProperties), null,
			                new AsyncCallback<CreateResult>() {
				                public void onFailure(Throwable caught) {
					                MessageBox.alert(
					                                I18N.CONSTANTS.adminFlexibleCreationBox(),
					                                I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS
					                                                .adminStandardFlexibleName() + " '" + name + "'"),
					                                null);
					                callback.onFailure(caught);
				                }

				                @Override
				                public void onSuccess(CreateResult result) {
					                if (result != null) {
						                ProjectModelDTO pModelUpdated = (ProjectModelDTO) result.getEntity();
						                UpdateModelResult completeResult = new UpdateModelResult(pModelUpdated.getId());
						                completeResult.setEntity(pModelUpdated);

						                if (flexibleElementToUpdate != null) {// Update
							                for (FlexibleElementDTO f : pModelUpdated.getAllElements()) {
								                if (f.getId() == flexibleElementToUpdate.getId()) {
									                completeResult.setAnnexEntity(f);
								                }
							                }
							                Notification.show(I18N.CONSTANTS.adminFlexibleCreationBox(), I18N.MESSAGES
							                                .adminStandardUpdateSuccess(I18N.CONSTANTS
							                                                .adminStandardFlexibleName()
							                                                + " '"
							                                                + name
							                                                + "'"));
						                } else {// Creation
							                List<Integer> flexibleIds = new ArrayList<Integer>();
							                // Get old ids
							                for (FlexibleElementDTO f : projectModelToUpdate.getAllElements()) {
								                flexibleIds.add(f.getId());
							                }
							                // Get new ids
							                for (FlexibleElementDTO f : pModelUpdated.getAllElements()) {
								                if (!flexibleIds.contains(f.getId())) {
									                completeResult.setAnnexEntity(f);
								                }
							                }

							                Notification.show(I18N.CONSTANTS.adminFlexibleCreationBox(), I18N.MESSAGES
							                                .adminStandardCreationSuccess(I18N.CONSTANTS
							                                                .adminStandardFlexibleName()
							                                                + " '"
							                                                + name
							                                                + "'"));
						                }
						                callback.onSuccess(completeResult);

					                } else {
						                Throwable t = new Throwable("ElementForm : creation result is null");
						                MessageBox.alert(I18N.CONSTANTS.adminFlexibleCreationBox(), I18N.MESSAGES
						                                .adminStandardCreationNull(I18N.CONSTANTS
						                                                .adminStandardFlexibleName()
						                                                + " '"
						                                                + name
						                                                + "'"), null);
						                callback.onFailure(t);
					                }
				                }
			                });
		} else {
			dispatcher.execute(new CreateEntity("OrgUnitModel", newFieldProperties), null,
			                new AsyncCallback<CreateResult>() {
				                public void onFailure(Throwable caught) {
					                MessageBox.alert(
					                                I18N.CONSTANTS.adminFlexibleCreationBox(),
					                                I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS
					                                                .adminStandardFlexibleName() + " '" + name + "'"),
					                                null);
					                callback.onFailure(caught);
				                }

				                @Override
				                public void onSuccess(CreateResult result) {
					                if (result != null) {
						                OrgUnitModelDTO oModelUpdated = (OrgUnitModelDTO) result.getEntity();
						                UpdateModelResult completeResult = new UpdateModelResult(oModelUpdated.getId());
						                completeResult.setEntity(oModelUpdated);

						                if (flexibleElementToUpdate != null) {// Update
							                for (FlexibleElementDTO f : oModelUpdated.getAllElements()) {
								                if (f.getId() == flexibleElementToUpdate.getId()) {
									                Log.debug("@EF --> FlexibleElement Updated : " + f.getId());
									                completeResult.setAnnexEntity(f);
								                }
							                }
							                Notification.show(I18N.CONSTANTS.adminFlexibleCreationBox(), I18N.MESSAGES
							                                .adminStandardUpdateSuccess(I18N.CONSTANTS
							                                                .adminStandardFlexibleName()
							                                                + " '"
							                                                + name
							                                                + "'"));
						                } else {// Creation

							                List<Integer> flexibleIds = new ArrayList<Integer>();
							                // Get old ids
							                for (FlexibleElementDTO f : orgUnitModelToUpdate.getAllElements()) {
								                flexibleIds.add(f.getId());
							                }
							                // Get new ids
							                for (FlexibleElementDTO f : oModelUpdated.getAllElements()) {
								                if (!flexibleIds.contains(f.getId())) {
									                completeResult.setAnnexEntity(f);
								                }
							                }

							                Notification.show(I18N.CONSTANTS.adminFlexibleCreationBox(), I18N.MESSAGES
							                                .adminStandardCreationSuccess(I18N.CONSTANTS
							                                                .adminStandardFlexibleName()
							                                                + " '"
							                                                + name
							                                                + "'"));
						                }
						                callback.onSuccess(completeResult);

					                } else {
						                Throwable t = new Throwable("ElementForm : creation result is null");
						                MessageBox.alert(I18N.CONSTANTS.adminFlexibleCreationBox(), I18N.MESSAGES
						                                .adminStandardCreationNull(I18N.CONSTANTS
						                                                .adminStandardFlexibleName()
						                                                + " '"
						                                                + name
						                                                + "'"), null);
						                callback.onFailure(t);
					                }
				                }
			                });
		}

	}

	private void initSpecifics(FlexibleElementDTO flexibleElement) {

		isDecimal.setValue(false);
		isMultipleQ.setValue(false);
		isExportable.show();

		linkedCategory.setValue(defaultNoCategory);
		isLinkedToQuality.setValue(false);
		// Question Multiple/Choices implications
		linkedCategory.setEnabled(true);
		questionChoice.setEnabled(true);
		addChoiceButton.setEnabled(true);
		for (ClickableLabel selected : selectableChoices.values()) {
			selected.setEnabled(true);
		}

		if (flexibleElement != null) {
			if (flexibleElement instanceof ReportElementDTO) {
				if (((ReportElementDTO) flexibleElement).getModelId() != null) {
					Log.debug("Init specifics for Report " + ((ReportElementDTO) flexibleElement).getModelId()
					                + " store " + reportModelsStore.getModels().size());
					for (ReportModelDTO reportModel : reportModelsStore.getModels()) {
						if (reportModel.getId() == ((ReportElementDTO) flexibleElement).getModelId()) {
							reportModelList.setValue(reportModel);
						}
					}
				}
			} else if (flexibleElement instanceof FilesListElementDTO) {
				if (((FilesListElementDTO) flexibleElement).getLimit() != null) {
					maxLimitField.setValue(((FilesListElementDTO) flexibleElement).getLimit());
					oldFieldProperties.put(AdminUtil.PROP_FX_MAX_LIMIT,
					                ((FilesListElementDTO) flexibleElement).getLimit());
				}

			} else if (flexibleElement instanceof TextAreaElementDTO) {

				if (((TextAreaElementDTO) flexibleElement).getMaxValue() != null) {
					maxLimitField.setValue(((TextAreaElementDTO) flexibleElement).getMaxValue());
					oldFieldProperties.put(AdminUtil.PROP_FX_MAX_LIMIT,
					                ((TextAreaElementDTO) flexibleElement).getMaxValue());
				}

				TextAreaElementDTO textElement = (TextAreaElementDTO) flexibleElement;
				if ("P".equals(textElement.getType().toString())) {
					oldFieldProperties.put(AdminUtil.PROP_FX_TEXT_TYPE, 'P');
					textAreaTypeList.setSimpleValue(I18N.CONSTANTS.adminFlexibleTextTypeP());
				} else if ("T".equals(textElement.getType().toString())) {
					oldFieldProperties.put(AdminUtil.PROP_FX_TEXT_TYPE, 'T');
					textAreaTypeList.setSimpleValue(I18N.CONSTANTS.adminFlexibleTextTypeT());
				} else if ("N".equals(textElement.getType().toString())) {
					oldFieldProperties.put(AdminUtil.PROP_FX_TEXT_TYPE, 'N');
					textAreaTypeList.setSimpleValue(I18N.CONSTANTS.adminFlexibleTextTypeN());
				} else if ("D".equals(textElement.getType().toString())) {
					oldFieldProperties.put(AdminUtil.PROP_FX_TEXT_TYPE, 'D');
					textAreaTypeList.setSimpleValue(I18N.CONSTANTS.adminFlexibleTextTypeD());
				}

				if (((TextAreaElementDTO) flexibleElement).getMinValue() != null) {
					minLimitField.setValue(((TextAreaElementDTO) flexibleElement).getMinValue());
					oldFieldProperties.put(AdminUtil.PROP_FX_MIN_LIMIT,
					                ((TextAreaElementDTO) flexibleElement).getMinValue());
				}

				if (((TextAreaElementDTO) flexibleElement).getLength() != null) {
					lengthField.setValue(((TextAreaElementDTO) flexibleElement).getLength());
					oldFieldProperties
					                .put(AdminUtil.PROP_FX_LENGTH, ((TextAreaElementDTO) flexibleElement).getLength());
				}

				if (((TextAreaElementDTO) flexibleElement).getIsDecimal() != null) {
					isDecimal.setValue(((TextAreaElementDTO) flexibleElement).getIsDecimal());
					oldFieldProperties.put(AdminUtil.PROP_FX_DECIMAL,
					                ((TextAreaElementDTO) flexibleElement).getIsDecimal());
				}
			} else if (flexibleElement instanceof QuestionElementDTO) {
				if (((QuestionElementDTO) flexibleElement).getQualityCriterionDTO() != null) {
					isLinkedToQuality.setValue(true);
					oldFieldProperties.put(AdminUtil.PROP_FX_Q_QUALITY, true);
				}

				if (((QuestionElementDTO) flexibleElement).getIsMultiple() != null) {
					isMultipleQ.setValue(((QuestionElementDTO) flexibleElement).getIsMultiple());
					oldFieldProperties.put(AdminUtil.PROP_FX_Q_MULTIPLE,
					                ((QuestionElementDTO) flexibleElement).getIsMultiple());
				}

				if (((QuestionElementDTO) flexibleElement).getCategoryTypeDTO() != null) {
					linkedCategory.setEnabled(true);
					questionChoice.setEnabled(false);
					addChoiceButton.setEnabled(false);
					for (ClickableLabel selected : selectableChoices.values()) {
						selected.setEnabled(false);
					}
					linkedCategory.setValue(((QuestionElementDTO) flexibleElement).getCategoryTypeDTO());
					oldFieldProperties.put(AdminUtil.PROP_FX_Q_CATEGORY,
					                ((QuestionElementDTO) flexibleElement).getCategoryTypeDTO());
				} else {
					linkedCategory.setValue(defaultNoCategory);
					linkedCategory.setEnabled(false);
					questionChoice.setEnabled(true);
					addChoiceButton.setEnabled(true);
					for (ClickableLabel selected : selectableChoices.values()) {
						selected.setEnabled(true);
					}

					List<QuestionChoiceElementDTO> usedChoices = ((QuestionElementDTO) flexibleElement).getChoicesDTO();
					for (QuestionChoiceElementDTO choice : usedChoices) {
						selectedChoicesLabels.add(choice.getLabel());

						if (num < MAX_TENTATIVES_FOR_CHOICES) {
							selectableChoices.get(num).setText(choice.getLabel());
							selectableChoices.get(num).show();
							num++;
						} else {
							MessageBox.alert(I18N.CONSTANTS.adminMaxAttempts(),
							                I18N.CONSTANTS.adminMaxAttemptsQChoices(), null);
							ElementForm.this.removeFromParent();
						}
					}
				}
			}
		}
	}

	private void addNewGroupsToStore(LayoutDTO container, ListStore<LayoutGroupDTO> groupsStore,
	                List<LayoutGroupDTO> addedGroups) {
		for (LayoutGroupDTO lg : addedGroups) {
			if (lg.getParentLayoutDTO().getId() == container.getId()) {
				groupsStore.add(lg);
			}
		}
	}

	public void updateElementFormForBudgetElement(BudgetElementDTO budgetElement) {
		upBudgetSubFieldStore.removeAll();
		downBudgetSubFieldStore.removeAll();
		budgetFields.removeAllRows();
		int i = 1;
		budgetFields.setCellSpacing(5);
		budgetFields.setCellPadding(5);
		budgetFields.addStyleName("budget-sub-fields-table");

		Text subFieldTitle = new Text(I18N.CONSTANTS.adminBudgetSubField());
		subFieldTitle.addStyleName("budget-sub-fields-title");
		budgetFields.setWidget(0, 0, subFieldTitle);
		budgetFields.getFlexCellFormatter().setColSpan(0, 0, 3);

		for (final BudgetSubFieldDTO budgetField : budgetElement.getBudgetSubFieldsDTO()) {
			final Integer index = i;
			final Text budgetText = new Text();
			budgetText.setData("budgetField", budgetField);
			budgetText.addStyleName("budget-sub-fields-text");
			if (budgetField.getType() != null) {
				// Sets the label for the combobox purpose
				budgetField.setLabel(BudgetSubFieldType.getName(budgetField.getType()));
			}
			budgetText.setText(budgetField.getLabel());

			budgetFields.setWidget(i, 0, budgetText);
			budgetFields.setWidget(i, 1, new TextField<String>());
			if (budgetField.getType() == null) {
				Anchor anchorEditSubField = new Anchor(IconImageBundle.ICONS.editPage().getSafeHtml());
				anchorEditSubField.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent arg0) {
						final Window window = new Window();
						window.setSize(400, 200);
						window.setPlain(true);
						window.setModal(true);
						window.setBlinkModal(true);
						window.setLayout(new FitLayout());
						final BudgetSubFieldForm budgetSubFieldForm = new BudgetSubFieldForm(dispatcher, null, null,
						                null);
						budgetSubFieldForm.getNameField().setValue(budgetField.getLabel());
						budgetSubFieldForm.getSaveButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

							@Override
							public void handleEvent(BaseEvent be) {
								if (budgetSubFieldForm.isValid()) {
									if (budgetSubFieldForm.getNameField().getValue() != null) {
										budgetField.setLabel(budgetSubFieldForm.getNameField().getValue());
										budgetText.setText(budgetSubFieldForm.getNameField().getValue());
										((BudgetSubFieldDTO) budgetText.getData("budgetField"))
										                .setLabel(budgetSubFieldForm.getNameField().getValue());
										window.hide();
									}
								} else {
									MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(), I18N.MESSAGES
									                .createFormIncompleteDetails(I18N.CONSTANTS.adminBudgetSubField()),
									                null);
								}
							}
						});

						window.add(budgetSubFieldForm);
						window.show();

					}
				});
				Anchor anchorDeleteSubField = new Anchor(IconImageBundle.ICONS.delete().getSafeHtml());
				anchorDeleteSubField.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent arg0) {
						budgetFields.removeRow(index);
						upBudgetSubFieldStore.remove(budgetField);
						upBudgetSubFieldStore.commitChanges();
						downBudgetSubFieldStore.remove(budgetField);
						downBudgetSubFieldStore.commitChanges();
					}
				});
				budgetFields.setWidget(i, 2, anchorEditSubField);
				budgetFields.setWidget(i, 3, anchorDeleteSubField);
			}

			budgetText.show();
			if (budgetField.getId() != budgetElement.getRatioDividend().getId()) {
				upBudgetSubFieldStore.add(budgetField);
			}
			if (budgetField.getId() != budgetElement.getRatioDivisor().getId()) {
				downBudgetSubFieldStore.add(budgetField);
			}
			i++;
		}
		upBudgetSubFieldCombo.setValue(budgetElement.getRatioDividend());

		upBudgetSubFieldCombo.setEditable(true);
		upBudgetSubFieldCombo.setAllowBlank(false);
		downBudgetSubFieldCombo.setValue(budgetElement.getRatioDivisor());

		downBudgetSubFieldCombo.setEditable(true);
		downBudgetSubFieldCombo.setAllowBlank(false);

		budgetFields.setVisible(true);
		ratioFlexTable.setVisible(true);
		anchorAddSubField.setVisible(true);
	}

	protected void updateRatioCombos(ComboBox<BudgetSubFieldDTO> budgetSubFieldsComboChanged) {
		if (budgetSubFieldsComboChanged == upBudgetSubFieldCombo) {
			if (downBudgetSubFieldCombo.getStore().contains(upBudgetSubFieldCombo.getValue())) {
				BudgetSubFieldDTO oldValue = downBudgetSubFieldCombo.getValue();
				downBudgetSubFieldStore.removeAll();
				if (budgetFields.getRowCount() > 0) {
					for (int i = 0; i < budgetFields.getRowCount(); i++) {
						if (budgetFields.getWidget(i, 0) != null) {
							Object budgetFieldData = ((Text) budgetFields.getWidget(i, 0)).getData("budgetField");
							BudgetSubFieldDTO b = (BudgetSubFieldDTO) budgetFieldData;
							if (b != budgetSubFieldsComboChanged.getValue()) {
								downBudgetSubFieldStore.add(b);

							}
						}
					}

				}
				if (downBudgetSubFieldStore.contains(oldValue)) {
					downBudgetSubFieldCombo.setValue(oldValue);
				}
				downBudgetSubFieldStore.commitChanges();
			}
		} else {
			if (upBudgetSubFieldCombo.getStore().contains(budgetSubFieldsComboChanged.getValue())) {
				BudgetSubFieldDTO oldValue = upBudgetSubFieldCombo.getValue();
				upBudgetSubFieldStore.removeAll();
				;
				if (budgetFields.getRowCount() > 0) {
					for (int i = 0; i < budgetFields.getRowCount(); i++) {
						if (budgetFields.getWidget(i, 0) != null) {
							Object budgetFieldData = ((Text) budgetFields.getWidget(i, 0)).getData("budgetField");
							BudgetSubFieldDTO b = (BudgetSubFieldDTO) budgetFieldData;
							if (b != budgetSubFieldsComboChanged.getValue()) {
								upBudgetSubFieldStore.add(b);

							}
						}
					}

				}
				if (upBudgetSubFieldStore.contains(oldValue)) {
					upBudgetSubFieldCombo.setValue(oldValue);
				}
				upBudgetSubFieldStore.commitChanges();

			}
		}

	}
}
