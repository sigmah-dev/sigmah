package org.sigmah.client.ui.presenter.admin;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.view.admin.CategoriesAdminView;
import org.sigmah.client.ui.widget.ColorField;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.DeletionError;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.DeleteCategories;
import org.sigmah.shared.command.GetCategories;
import org.sigmah.shared.command.GetProjectModels;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.referential.CategoryIcon;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.command.DisableCategoryElements;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.ui.notif.ConfirmCallback;

/**
 * Admin categories Presenter which manages {@link CategoriesAdminView}.
 *
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class CategoriesAdminPresenter extends AbstractAdminPresenter<CategoriesAdminPresenter.View> {

    /**
     * Description of the view managed by this presenter.
     */
    @ImplementedBy(CategoriesAdminView.class)
    public static interface View extends AbstractAdminPresenter.View {

        ListStore<CategoryTypeDTO> getCategoriesStore();

        ListStore<CategoryElementDTO> getCategoryElementsStore();

        Grid<CategoryElementDTO> getCategoryElementsGrid();

        Grid<CategoryTypeDTO> getCategoriesGrid();

        SimpleComboBox<String> getCategoryIcon();

        TextField<String> getCategoryName();

        Button getAddCategoryElementButton();

        Button getDeleteCategoryElementButton();

        Button getDisableCategoryElementButton();

        Button getEnableCategoryElementButton();

        Button getDeleteCategoryTypeButton();

        LoadingMask getGategoriesTypeLoadingMonitor();

        LoadingMask getGategoriesElementsLoadingMonitor();

        Button getAddCategoryTypeButton();

        void setCategoryPresenterHandler(CategoryPresenterHandler handler);

        TextField<String> getName();

        ColorField getColorField();

        Boolean getisdisable();

        Button getImportCategoryTypeButton();

    }

    public static interface CategoryPresenterHandler {

        void onClickHandler(CategoryTypeDTO categoryTypeDTO);

        void onSelectHandler(CategoryTypeDTO categoryTypeDTO);

    }

    private CategoryTypeDTO currentCategoryType;

    /**
     * Presenters's initialization.
     *
     * @param view Presenter's view interface.
     * @param injector Injected client injector.
     */
    @Inject
    protected CategoriesAdminPresenter(View view, Injector injector) {
        super(view, injector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page getPage() {
        return Page.ADMIN_CATEGORIES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBind() {

        /**
         * *************** Category Panel *************************
         */
		// ADD CATEGORY
        createCategoryButtonListener();

		// Delete Category
        deleteCategoryButtonListener();

		// Import Category
        importCategoryButtonListener();

        /**
         * ****************** Category Element Panel *****************
         */
		// ADD ELEMENT CATEGORY
        createCategoryElementListener();

		// DELETE ELEMENT CATEGORY
        deleteCategoryElementListener();

		//Disable category elements
        disableCategoryElementListener();
		//Enable category elements
        enableCategoryElementListener();

        /**
         * ************* Presenter Handler *******************
         */
        view.setCategoryPresenterHandler(new CategoryPresenterHandler() {

            /**
             * show Category Elements
             */
            @Override
            public void onSelectHandler(CategoryTypeDTO categoryTypeDTO) {
                currentCategoryType = categoryTypeDTO;

                view.getCategoryElementsGrid().show();
                view.getCategoryElementsStore().removeAll();
                for (CategoryElementDTO categoryElementDTO : categoryTypeDTO.getCategoryElementsDTO()) {
                    view.getCategoryElementsStore().add(categoryElementDTO);
                }
                view.getCategoryElementsStore().commitChanges();
                view.getAddCategoryElementButton().enable();

            }

            /**
             * Export Category
             */
            @Override
            public void onClickHandler(CategoryTypeDTO categoryTypeDTO) {

                final ServletUrlBuilder urlBuilder
                        = new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), Servlet.EXPORT, ServletMethod.EXPORT_MODEL_CATEGORY);

                urlBuilder.addParameter(RequestParameter.ID, categoryTypeDTO.getId());

                ClientUtils.launchDownload(urlBuilder.toString());

            }
        });

		// Handler
        registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

            @Override
            public void onUpdate(final UpdateEvent event) {

                if (event.concern(UpdateEvent.CATEGORY_MODEL_IMPORT)) {

                    refreshCategoryTypePanel();
                }
            }
        }));
    }

    private void importCategoryButtonListener() {

        view.getImportCategoryTypeButton().addListener(Events.Select, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(ButtonEvent be) {
                eventBus.navigateRequest(Page.IMPORT_MODEL.requestWith(RequestParameter.TYPE, AdminUtil.ADMIN_CATEGORY_MODEL));
            }
        });

    }

    private void deleteCategoryElementListener() {

        view.getDeleteCategoryElementButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(ButtonEvent be) {
                onDeleteCategoryElement(view.getCategoryElementsGrid().getSelectionModel().getSelectedItems());
            }

        });
    }

    private void disableCategoryElementListener() {
        view.getDisableCategoryElementButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(final ButtonEvent event) {
                onDisableCategoryElement(view.getCategoryElementsGrid().getSelectionModel().getSelectedItems());
            }
        });
    }

    private void enableCategoryElementListener() {
        view.getEnableCategoryElementButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(final ButtonEvent event) {
                onEnableCategoryElement(view.getCategoryElementsGrid().getSelectionModel().getSelectedItems());
            }
        });
    }

    /**
     * Method to delete a element under a category Firstly,get all project
     * models for verifying if the selected category elements are being used by
     * one or more project models. If being used, a category element should not
     * be deleted.
     *
     * @param selectedItems The selected categories
     */
    private void onDeleteCategoryElement(final List<CategoryElementDTO> selectedItems) {

        // Check if there is at least one item selected
        if (ClientUtils.isEmpty(selectedItems)) {
            N10N.warn(I18N.CONSTANTS.error(), I18N.CONSTANTS.selectCategoryElementToDelete());
            return;
        }

        dispatch.execute(new GetProjectModels(ProjectModelDTO.Mode.ALL, ProjectModelStatus.values()), new CommandResultHandler<ListResult<ProjectModelDTO>>() {

            @Override
            public void onCommandFailure(final Throwable arg0) {

                N10N.error(I18N.CONSTANTS.error(), I18N.CONSTANTS.deleteCategoryGetProjectModelsError());

            }

            @Override
            public void onCommandSuccess(final ListResult<ProjectModelDTO> result) {

                deleteCategoryElementVerify(selectedItems, result.getList());

            }
        }, view.getGategoriesElementsLoadingMonitor());
    }

    private void onDisableCategoryElement(final List<CategoryElementDTO> selection) {

        final StringBuilder fields = new StringBuilder();

        for (CategoryElementDTO s : selection) {

            if (fields.length() > 0) {
                fields.append(", ");
            }
            fields.append(s.getLabel());
        }

        dispatch.execute(new DisableCategoryElements(selection, true), new AsyncCallback<VoidResult>() {
            @Override
            public void onFailure(Throwable caught) {
                N10N.error(I18N.CONSTANTS.error(), I18N.MESSAGES.flexibleElementDisableError(fields.toString()));
            }

            @Override
            public void onSuccess(VoidResult result) {
                // update view   
                for (CategoryElementDTO element : selection) {

                    //
                    element.setisDisabled(true);
                    //view.getCategoryElementsStore().remove(element);
                    view.getCategoryElementsStore().update(element);
                }

                // Feedback 
                N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(),
                        I18N.CONSTANTS.adminDisableCategoryElementsConfirm());
            }
        });

    }

    private void onEnableCategoryElement(final List<CategoryElementDTO> selection) {

        final StringBuilder fields = new StringBuilder();
        final List<String> elementNames = new ArrayList<String>();

        for (final CategoryElementDTO element : selection) {
            elementNames.add(element.getLabel());
        }

        for (CategoryElementDTO s : selection) {

            if (fields.length() > 0) {
                fields.append(", ");
            }
            fields.append(s.getLabel());
        }

        N10N.confirmation(I18N.CONSTANTS.enable(), I18N.CONSTANTS.adminCategoryElementEnableConfirm(), elementNames, new ConfirmCallback() {

            @Override
            public void onAction() {

                dispatch.execute(new DisableCategoryElements(selection, false), new AsyncCallback<VoidResult>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        N10N.error(I18N.CONSTANTS.error(), I18N.MESSAGES.flexibleElementDisableError(fields.toString()));
                    }

                    @Override
                    public void onSuccess(VoidResult result) {
                        // update view   
                        for (CategoryElementDTO element : selection) {
                            element.setisDisabled(false);
                            view.getCategoryElementsStore().update(element);
                        }

                        // Feedback 
                        N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(),
                                I18N.CONSTANTS.adminEnableCategoryElementsConfirm());
                    }
                });
            }
        });

    }

    private void createCategoryElementListener() {

        view.getAddCategoryElementButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(ButtonEvent be) {
                if (view.getName().getValue() != null
                        && !view.getName().getValue().isEmpty()
                        && view.getCategoryElementsStore().findModel("label", view.getName().getValue()) == null) {

                    final List<String> elementNames = new ArrayList<String>();

                    elementNames.add(view.getName().getValue());

                    N10N.confirmation(I18N.CONSTANTS.categoryElements(), I18N.CONSTANTS.adminCategoryElementEnableConfirm(), elementNames, new ConfirmCallback() {

                        @Override
                        public void onAction() {

                            Map<String, Object> newCategoryElementProperties = new HashMap<String, Object>();
                            newCategoryElementProperties.put(AdminUtil.PROP_CATEGORY_ELEMENT_NAME, view.getName().getValue());
                            newCategoryElementProperties.put(AdminUtil.PROP_CATEGORY_ELEMENT_COLOR, view.getColorField().getValue());
                            newCategoryElementProperties.put(AdminUtil.PROP_CATEGORY_ELEMENT_ISDISABLED, false);
                            newCategoryElementProperties.put(AdminUtil.PROP_CATEGORY_TYPE, currentCategoryType);

                            dispatch.execute(new CreateEntity("CategoryElement", newCategoryElementProperties), new CommandResultHandler<CreateResult>() {

                                @Override
                                public void onCommandFailure(Throwable caught) {
                                    N10N.warn(I18N.CONSTANTS.adminCategoryTypeCreationBox(),
                                            I18N.MESSAGES.adminStandardCreationFailureF(I18N.CONSTANTS.adminCategoryTypeStandard() + " '" + currentCategoryType.getLabel() + "'"));
                                }

                                @Override
                                public void onCommandSuccess(CreateResult result) {
                                    if (result != null && result.getEntity() != null) {
                                        view.getCategoryElementsStore().add((CategoryElementDTO) result.getEntity());
                                        view.getCategoryElementsStore().commitChanges();
                                        List<CategoryElementDTO> elements = null;
                                        if (currentCategoryType.getCategoryElementsDTO() == null) {
                                            elements = new ArrayList<CategoryElementDTO>();
                                        } else {
                                            elements = currentCategoryType.getCategoryElementsDTO();
                                        }
                                        elements.add((CategoryElementDTO) result.getEntity());
                                        currentCategoryType.setCategoryElementsDTO(elements);
                                        view.getCategoriesStore().update(currentCategoryType);
                                        view.getCategoriesStore().commitChanges();
                                        view.getName().clear();
                                        N10N.infoNotif(I18N.CONSTANTS.adminCategoryTypeCreationBox(),
                                                I18N.MESSAGES.adminStandardUpdateSuccessF(I18N.CONSTANTS.adminCategoryTypeStandard() + " '" + currentCategoryType.getLabel() + "'"));
                                    } else {
                                        N10N.warn(I18N.CONSTANTS.adminCategoryTypeCreationBox(),
                                                I18N.MESSAGES.adminStandardCreationNullF(I18N.CONSTANTS.adminCategoryTypeStandard() + " '" + currentCategoryType.getLabel() + "'"));
                                    }
                                }
                            });
                        }
                    });
                } else {
                    N10N.warn("", I18N.CONSTANTS.adminStandardInvalidValues());
                }
            }
        });
    }

    /**
     * add category button listener
     */
    private void createCategoryButtonListener() {

        view.getAddCategoryTypeButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {

                if (view.getCategoryName().getValue() != null
                        && view.getCategoryIcon().getValue() != null
                        && view.getCategoriesStore().findModel("label", view.getCategoryName().getValue()) == null) {

                    Map<String, Object> newCategoryTypeProperties = new HashMap<String, Object>();

                    newCategoryTypeProperties.put(AdminUtil.PROP_CATEGORY_TYPE_ICON, CategoryIcon.getIcon(view.getCategoryIcon().getValue().getValue()));
                    newCategoryTypeProperties.put(AdminUtil.PROP_CATEGORY_TYPE_NAME, view.getCategoryName().getValue());

                    dispatch.execute(new CreateEntity("CategoryType", newCategoryTypeProperties), new CommandResultHandler<CreateResult>() {

                        @Override
                        public void onCommandFailure(Throwable caught) {
                            N10N.warn(I18N.CONSTANTS.adminCategoryTypeCreationBox(),
                                    I18N.MESSAGES.adminStandardCreationFailureF(I18N.CONSTANTS.adminCategoryTypeStandard() + " '" + view.getCategoryName().getValue() + "'"));
                        }

                        @Override
                        public void onCommandSuccess(CreateResult result) {
                            if (result != null && result.getEntity() != null) {

                                view.getCategoriesStore().add((CategoryTypeDTO) result.getEntity());
                                view.getCategoriesStore().commitChanges();

                                N10N.infoNotif(I18N.CONSTANTS.adminCategoryTypeCreationBox(),
                                        I18N.MESSAGES.adminStandardUpdateSuccessF(I18N.CONSTANTS.adminCategoryTypeStandard() + " '" + view.getCategoryName().getValue() + "'"));

                                List<CategoryTypeDTO> selectedCategory = new ArrayList<CategoryTypeDTO>();
                                selectedCategory.add((CategoryTypeDTO) result.getEntity()); // Focus and scroll to the new created
                                // category
                                int rowIndex = view.getCategoriesStore().indexOf((CategoryTypeDTO) result.getEntity());
                                Element addedRow = view.getCategoriesGrid().getView().getRow(rowIndex);
                                view.getCategoriesGrid().getSelectionModel().setSelection(selectedCategory);
                                addedRow.setScrollTop(addedRow.getScrollTop());
                                addedRow.scrollIntoView();

                                view.getCategoryName().clear();
                                view.getCategoryIcon().clearSelections();

                            } else {
                                N10N.infoNotif(I18N.CONSTANTS.adminCategoryTypeCreationBox(),
                                        I18N.MESSAGES.adminStandardCreationNullF(I18N.CONSTANTS.adminCategoryTypeStandard() + " '" + view.getCategoryName().getValue() + "'"));
                            }
                        }
                    });
                } else {
                    N10N.warn("", I18N.CONSTANTS.adminStandardInvalidValues());
                }
            }
        ;
    }

    );
	}

	/**
	 * Delete Category Button Listener
	 */
	public void deleteCategoryButtonListener() {

        view.getDeleteCategoryTypeButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(ButtonEvent be) {
                onDeleteCategory(view.getCategoriesGrid().getSelectionModel().getSelectedItems());
            }

        });

    }

    /**
     * Method to delete a category. Firstly,get all project models for verifying
     * if there is one or more in the selected categories who are being used by
     * one or more project models. If being used, a category should not be
     * deleted.
     *
     * @param selectedItems The selected categories
     */
    protected void onDeleteCategory(final List<CategoryTypeDTO> selectedItems) {

        // Check if there is at least one item selected
        if (selectedItems == null || selectedItems.size() == 0) {
            N10N.warn(I18N.CONSTANTS.error(), I18N.CONSTANTS.selectCategoryToDelete());
            return;
        }

        GetProjectModels cmdGetProjectModels = new GetProjectModels(ProjectModelDTO.Mode.ALL, ProjectModelStatus.values());

        dispatch.execute(cmdGetProjectModels, new CommandResultHandler<ListResult<ProjectModelDTO>>() {

            @Override
            public void onCommandFailure(Throwable arg0) {

                N10N.warn(I18N.CONSTANTS.error(), I18N.CONSTANTS.deleteCategoryGetProjectModelsError());

            }

            @Override
            public void onCommandSuccess(ListResult<ProjectModelDTO> result) {

                deleteCategoryVerify(selectedItems, result.getList());

            }
        }, view.getGategoriesTypeLoadingMonitor());

    }

    /**
     * Method to verify the deletion action. If the verification passes, try to
     * delete a category, or show a alert window.
     *
     * @param selectedItems The selected categories
     * @param allProjectModelsList The list of all project models
     */
    private void deleteCategoryVerify(final List<CategoryTypeDTO> selectedItems, List<ProjectModelDTO> allProjectModelsList) {

        // A List to store DeletionError object
        List<DeletionError> deletionErrorList = new ArrayList<DeletionError>();

        // Check
        for (ProjectModelDTO projectModelDTO : allProjectModelsList) {
            List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();
            allElements = projectModelDTO.getAllElements();

            for (FlexibleElementDTO e : allElements) {
                if (e.getElementType() == ElementTypeEnum.QUESTION) {
                    QuestionElementDTO questionElement = (QuestionElementDTO) e;

                    if (questionElement.getCategoryType() != null && selectedItems.contains(questionElement.getCategoryType())) {
                        deletionErrorList.add(new DeletionError(questionElement.getCategoryType().getLabel(), projectModelDTO.getName(), questionElement.getLabel()));
                    }

                }
            }

        }

        // If the category is used by project models,show an alert window
        if (deletionErrorList.size() > 0) {

            // Create a dialog window to show error message
            final Dialog errorDialog = new Dialog();
            errorDialog.setHeadingHtml(I18N.CONSTANTS.deletionError());
            errorDialog.setButtons(Dialog.CANCEL);
            errorDialog.setScrollMode(Scroll.AUTO);
            errorDialog.setHideOnButtonClick(true);
            errorDialog.setModal(true);
            errorDialog.setWidth(500);
            errorDialog.setHeight(250);

            String errorText = "";
            for (DeletionError error : deletionErrorList) {
                errorText = errorText + I18N.MESSAGES.categoryBeingUsed(error.getCategoryTypeName(), error.getProjectModelName(), error.getFieldName()) + "<br />";

            }
            errorDialog.addText(errorText);
            errorDialog.show();

        } // Else, try to delete
        else {

            List<Integer> ids = new ArrayList<Integer>();
            String names = "";
            for (CategoryTypeDTO s : selectedItems) {
                ids.add(s.getId());
                names = s.getLabel() + ", " + names;
            }

            final String toDelete = names;
            final DeleteCategories deactivate = new DeleteCategories(selectedItems, null);
            dispatch.execute(deactivate, new CommandResultHandler<VoidResult>() {

                @Override
                public void onCommandFailure(Throwable caught) {
                    N10N.warn(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(toDelete));
                }

                @Override
                public void onCommandSuccess(VoidResult result) {
                    for (CategoryTypeDTO model : selectedItems) {
                        view.getCategoriesStore().remove(model);
                        view.getCategoryElementsStore().removeAll();
                        view.getCategoriesStore().commitChanges();
                    }
                    view.getCategoriesStore().commitChanges();
                    view.getAddCategoryElementButton().disable();
                    N10N.infoNotif(I18N.CONSTANTS.adminCategoryTypeCreationBox(), I18N.MESSAGES.adminStandardUpdateSuccessF(I18N.CONSTANTS.adminCategoryTypeStandard()));
                }
            });

        }

    }

    /**
     * Method to verify the deletion action. If the verification passes, try to
     * delete a category element, or show a alert window.
     *
     * @param selectedItems The selected category elements
     * @param allProjectModelsList The list of all project models
     */
    protected void deleteCategoryElementVerify(final List<CategoryElementDTO> selectedItems, List<ProjectModelDTO> allProjectModelsList) {

        // Get the parent CategoryTypeDTO object, they have the same parent CategoryTypeDTO object
        CategoryTypeDTO parentCategoryTypeDTO = selectedItems.get(0).getParentCategoryDTO();

        // A List to store DeletionError object
        List<DeletionError> deletionErrorList = new ArrayList<DeletionError>();

        // Check
        for (ProjectModelDTO projectModelDTO : allProjectModelsList) {
            List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();
            allElements = projectModelDTO.getAllElements();

            for (FlexibleElementDTO e : allElements) {
                if (e.getElementType() == ElementTypeEnum.QUESTION) {
                    QuestionElementDTO questionElement = (QuestionElementDTO) e;

                    if (questionElement.getCategoryType() != null && parentCategoryTypeDTO.getId().equals(questionElement.getCategoryType().getId())) {
                        // Add a deletion error object
                        deletionErrorList.add(new DeletionError(parentCategoryTypeDTO.getLabel(), projectModelDTO.getName(), questionElement.getLabel()));
                    }

                }
            }

        }

        // If the category is used by project models,show an alert window
        if (deletionErrorList.size() > 0) {
            // Create a dialog window to show error message
            final Dialog errorDialog = new Dialog();
            errorDialog.setHeadingHtml(I18N.CONSTANTS.deletionError());
            errorDialog.setButtons(Dialog.CANCEL);
            errorDialog.setScrollMode(Scroll.AUTO);
            errorDialog.setHideOnButtonClick(true);
            errorDialog.setModal(true);
            errorDialog.setHeight(250);
            errorDialog.setWidth(500);

            String errorText = "";
            for (DeletionError error : deletionErrorList) {
                errorText = errorText + I18N.MESSAGES.categoryBeingUsed(error.getCategoryTypeName(), error.getProjectModelName(), error.getFieldName()) + "<br />";

            }
            errorDialog.addText(errorText);
            errorDialog.show();
            return;
        } // Else, try to delete
        else {
            List<Integer> ids = new ArrayList<Integer>();
            String names = "";
            for (CategoryElementDTO s : selectedItems) {
                ids.add(s.getId());
                names = s.getLabel() + ", " + names;
            }

            final String toDelete = names;
            final DeleteCategories deactivate = new DeleteCategories(null, selectedItems);
            dispatch.execute(deactivate, new CommandResultHandler<VoidResult>() {

                @Override
                public void onCommandFailure(Throwable caught) {
                    N10N.warn(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(toDelete), null);
                }

                @Override
                public void onCommandSuccess(VoidResult result) {

                    List<CategoryElementDTO> elements = currentCategoryType.getCategoryElementsDTO();
                    for (CategoryElementDTO model : selectedItems) {
                        view.getCategoryElementsStore().remove(model);
                        elements.remove(model);

                    }
                    view.getCategoryElementsStore().commitChanges();

                    currentCategoryType.setCategoryElementsDTO(elements);
                    view.getCategoriesStore().update(currentCategoryType);
                    view.getCategoriesStore().commitChanges();
                }
            });
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPageRequest(final PageRequest request) {

        // clear view
        view.getCategoryName().clear();
        view.getCategoryIcon().clearSelections();
        view.getName().clear();

        // load category
        refreshCategoryTypePanel();

    }

    /**
     * Load category
     */
    public void refreshCategoryTypePanel() {

        dispatch.execute(new GetCategories(), new CommandResultHandler<ListResult<CategoryTypeDTO>>() {

            @Override
            public void onCommandFailure(Throwable arg0) {
                N10N.warn(I18N.CONSTANTS.adminboard(), I18N.CONSTANTS.adminProblemLoading());
            }

            @Override
            public void onCommandSuccess(ListResult<CategoryTypeDTO> result) {

                if (result.getList() != null && !result.getList().isEmpty()) {
                    view.getCategoriesStore().removeAll();
                    view.getCategoriesStore().add(result.getList());
                    view.getCategoriesStore().commitChanges();
                }

            }
        }, view.getGategoriesTypeLoadingMonitor());
    }

}
