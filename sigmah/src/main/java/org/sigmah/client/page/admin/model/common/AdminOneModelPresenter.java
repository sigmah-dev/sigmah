package org.sigmah.client.page.admin.model.common;

import java.util.HashMap;
import java.util.List;

import org.sigmah.client.EventBus;
import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.event.NavigationEvent;
import org.sigmah.client.event.NavigationEvent.NavigationError;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.AdminPresenter;
import org.sigmah.client.page.admin.AdminSubPresenter;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.client.page.admin.model.common.element.AdminFlexibleElementsPresenter;
import org.sigmah.client.page.admin.model.project.logframe.AdminLogFramePresenter;
import org.sigmah.client.page.admin.model.project.phase.AdminPhasesPresenter;
import org.sigmah.client.util.Notification;
import org.sigmah.client.util.state.IStateManager;
import org.sigmah.shared.command.CheckModelUsage;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetOrgUnitModel;
import org.sigmah.shared.command.GetProjectModel;
import org.sigmah.shared.command.GetProjectsByModel;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ModelCheckResult;
import org.sigmah.shared.command.result.ProjectListResult;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

public class AdminOneModelPresenter {

    private final static String[] MAIN_TABS = {
                                               I18N.CONSTANTS.adminProjectModelFields(),
                                               I18N.CONSTANTS.adminProjectModelPhases(),
                                               I18N.CONSTANTS.adminProjectModelLogFrame() };

    private final View view;
    private final Dispatcher dispatcher;
    private final EventBus eventBus;
    private AdminPageState currentState;
    private TabItem currentTabItem;
    private final AdminModelSubPresenter[] presenters;
    private ProjectModelDTO currentProjectModel;
    private OrgUnitModelDTO currentOrgUnitModel;
    private boolean dataChanged;

    @ImplementedBy(AdminOneModelView.class)
    public interface View {

        public Widget getMainPanel();

        public TabPanel getTabPanelParameters();

        public LayoutContainer getPanelSelectedTab();

        public void initModelView(Object model);

        public SimpleComboBox<String> getStatusList();

        public ContentPanel getTopPanel();

        public Boolean isProject();

        public Button getSaveButton();

        public TextField<String> getNameField();

        public ProjectModelType getCurrentModelType();

        public TextField<String> getTitleField();

        public CheckBox getHasBudgetCheckBox();

        public CheckBox getCanContainProjectsCheckBox();

        public RadioGroup getProjectTypeRadioGroup();

        public FormPanel getTopLeftFormPanel();

        public ProjectModelDTO getCurrentProjectModel();

        public OrgUnitModelDTO getCurrentOrgUnitModel();
    }

    @Inject
    public AdminOneModelPresenter(final EventBus eventBus, final Dispatcher dispatcher, final View view, final UserLocalCache cache, final Authentication authentication, IStateManager stateMgr) {
        this.dispatcher = dispatcher;
        this.view = view;
        this.presenters = new AdminModelSubPresenter[] {
                                                        new AdminFlexibleElementsPresenter(dispatcher),
                                                        new AdminPhasesPresenter(dispatcher),
                                                        new AdminLogFramePresenter(dispatcher) };
        this.eventBus = eventBus;

        dataChanged = false;

        addListeners();
    }

    private void addListeners() {

        // Status combox
        addStatusListListener();

        // Save button
        view.getSaveButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

            @Override
            public void handleEvent(ButtonEvent be) {
                updateModel();
                dataChanged = false;
            }
        });

        Listener<BaseEvent> dataChangedListener = new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                dataChanged = true;
            }
        };

        view.getCanContainProjectsCheckBox().addListener(Events.OnClick, dataChangedListener);
        view.getHasBudgetCheckBox().addListener(Events.OnClick, dataChangedListener);
        view.getTitleField().addListener(Events.Change, dataChangedListener);
        view.getNameField().addListener(Events.Change, dataChangedListener);
        // FIXME
        view.getProjectTypeRadioGroup().addListener(Events.Change, dataChangedListener);
    }

    private void addStatusListListener() {
        view.getStatusList().removeAllListeners();
        // StatusCombox
        view.getStatusList().addListener(Events.Select, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                if (view.getStatusList().getValue() != null) {
                    dataChanged = true;
                    if (view.isProject()) {// Project model

                        onProjectModelStatusChanged();
                    } else {// Orgunit model

                        onOrgUnitModelStatusChanged();
                    }

                }
            }

        });
    }

    public void setCurrentState(AdminPageState currentState) {
        this.currentState = currentState;
    }

    private void addAllTabs() {
        for (int i = 0; i < MAIN_TABS.length; i++) {
            final int index = i;
            String tabTitle = MAIN_TABS[i];

            final TabItem tabItem = new TabItem(tabTitle);
            tabItem.setLayout(new FitLayout());
            tabItem.setEnabled(true);
            tabItem.setAutoHeight(true);

            tabItem.addListener(Events.BeforeSelect, new Listener<BaseEvent>() {

                @Override
                public void handleEvent(BaseEvent be) {
                    final TabItem item = AdminOneModelPresenter.this.view.getTabPanelParameters().getItem(index);

                    if (!item.equals(currentTabItem)) {
                        NavigationEvent event =
                                new NavigationEvent(NavigationHandler.NavigationRequested, currentState.deriveTo(
                                    currentState.getCurrentSection(), currentState.getModel(), MAIN_TABS[index],
                                    currentState.isProject()), null);
                        eventBus.fireEvent(event);

                        if (event.getNavigationError() != NavigationError.NONE) {
                            be.setCancelled(true);
                        }
                    }
                }

            });

            this.view.getTabPanelParameters().add(tabItem);

        }
    }

    private void addSingleTab(Integer i) {
        final int index = i;
        String tabTitle = MAIN_TABS[i];

        final TabItem tabItem = new TabItem(tabTitle);
        tabItem.setLayout(new FitLayout());
        tabItem.setEnabled(true);
        tabItem.setAutoHeight(true);

        tabItem.addListener(Events.Select, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent be) {
                final TabItem item = AdminOneModelPresenter.this.view.getTabPanelParameters().getItem(index);

                if (!item.equals(currentTabItem)) {
                    eventBus.fireEvent(new NavigationEvent(NavigationHandler.NavigationRequested, currentState
                        .deriveTo(currentState.getCurrentSection(), currentState.getModel(), MAIN_TABS[index],
                            currentState.isProject()), null));
                }
            }

        });

        this.view.getTabPanelParameters().add(tabItem);
    }

    public boolean navigate(PageState place, final AdminPresenter.View view) {

        final AdminPageState adminPageState = (AdminPageState) place;
        currentState = adminPageState;
        if (currentState.isProject()) {
            addAllTabs();
            GetProjectModel command = new GetProjectModel(currentState.getModel());
            command.setId(currentState.getModel().intValue());
            dispatcher.execute(command, null, new AsyncCallback<ProjectModelDTO>() {

                @Override
                public void onFailure(Throwable throwable) {
                    // FIXME
                }

                @Override
                public void onSuccess(ProjectModelDTO model) {
                    AdminOneModelPresenter.this.setCurrentProjectModel(model);
                    if (model != null)
                        selectTab(currentState.getSubModel(), view, model, false, true);
                    else {
                        // FIXME
                    }
                }
            });
        } else {
            addSingleTab(0);
            GetOrgUnitModel command = new GetOrgUnitModel(currentState.getModel());
            command.setId(currentState.getModel().intValue());
            dispatcher.execute(command, null, new AsyncCallback<OrgUnitModelDTO>() {

                @Override
                public void onFailure(Throwable throwable) {
                    // FIXME
                }

                @Override
                public void onSuccess(OrgUnitModelDTO model) {
                    AdminOneModelPresenter.this.setCurrentOrgUnitModel(model);
                    if (model != null)
                        selectTab(currentState.getSubModel(), view, model, false, false);
                    else {
                        // FIXME
                    }
                }
            });
        }

        return true;
    }

    private void selectTab(String subModel, AdminPresenter.View adminView, Object model, boolean force,
            boolean isProject) {

        int index = arrayIndexOf(MAIN_TABS, subModel);
        if (index != -1) {

            final TabItem item = this.view.getTabPanelParameters().getItem(index);

            if (!item.equals(currentTabItem)) {
                currentTabItem = item;

                presenters[index].setCurrentState(currentState);
                presenters[index].setModel(model);

                this.view.getTabPanelParameters().setSelection(this.view.getTabPanelParameters().getItem(index));
                LayoutContainer l = this.view.getPanelSelectedTab();
                l.add(presenters[index].getView());
                l.setScrollMode(Style.Scroll.AUTO);
                this.view.getTabPanelParameters().getSelectedItem().add(l);
                view.initModelView(model);
                adminView.setMainPanel(view.getMainPanel());
                if (isProject)
                    this.setCurrentProjectModel((ProjectModelDTO) presenters[index].getModel());
                else
                    this.setCurrentOrgUnitModel((OrgUnitModelDTO) presenters[index].getModel());
                presenters[index].viewDidAppear();

            } else if (force) {
                presenters[index].setCurrentState(currentState);
                presenters[index].setModel(model);

                this.view.getTabPanelParameters().setSelection(this.view.getTabPanelParameters().getItem(index));
                LayoutContainer l = this.view.getPanelSelectedTab();
                l.add(presenters[index].getView());
                this.view.getTabPanelParameters().getSelectedItem().add(l);
                view.initModelView(model);
                adminView.setMainPanel(view.getMainPanel());
                if (isProject)
                    this.setCurrentProjectModel((ProjectModelDTO) presenters[index].getModel());
                else
                    this.setCurrentOrgUnitModel((OrgUnitModelDTO) presenters[index].getModel());
                presenters[index].viewDidAppear();
            }
        }
    }

    public int arrayIndexOf(Object[] array, Object o) {
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (o.equals(array[i])) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void setCurrentProjectModel(ProjectModelDTO currentProjectModel) {
        this.currentProjectModel = currentProjectModel;
    }

    public ProjectModelDTO getCurrentProjectModel() {
        return currentProjectModel;
    }

    public void setCurrentOrgUnitModel(OrgUnitModelDTO currentOrgUnitModel) {
        this.currentOrgUnitModel = currentOrgUnitModel;
    }

    public OrgUnitModelDTO getCurrentOrgUnitModel() {
        return currentOrgUnitModel;
    }

    /**
     * Check if the change action of model's status is valid.
     * 
     * @param currentStatus
     *            The current status of the model
     * @param targetStatus
     *            The status that users want to change to
     * @return {@link org.sigmah.client.page.admin.model.common.StatusChangeAction} object
     * @author HUZHE (zhe.hu32@gmail.com)
     */
    private StatusChangeAction isValidStatusChange(ProjectModelStatus currentStatus, ProjectModelStatus targetStatus) {
        StatusChangeAction statusChangeAction = new StatusChangeAction();

        // "Draft" model is only allowed to shift to "Ready" model
        if (currentStatus.equals(ProjectModelStatus.DRAFT) && !targetStatus.equals(ProjectModelStatus.READY)) {
            statusChangeAction.setValid(false);

            statusChangeAction.setFeedBackMessage(I18N.CONSTANTS.draftModelStatusChangeError());

        }
        // "Ready" model is only allowed to shift to "Draft" or "Unavailable"
        // model
        else if (currentStatus.equals(ProjectModelStatus.READY)
            && !(targetStatus.equals(ProjectModelStatus.DRAFT) || targetStatus.equals(ProjectModelStatus.UNAVAILABLE))) {
            statusChangeAction.setValid(false);

            statusChangeAction.setFeedBackMessage(I18N.CONSTANTS.readyModelStatusChangeError());
        }
        // "Used" model is only allowed to shift to "Unavailable" model
        else if (currentStatus.equals(ProjectModelStatus.USED) && !targetStatus.equals(ProjectModelStatus.UNAVAILABLE)) {
            statusChangeAction.setValid(false);

            statusChangeAction.setFeedBackMessage(I18N.CONSTANTS.usedModelStatusChangeError());
        }
        // Others cases are all allowed
        else {
            statusChangeAction.setValid(true);
        }

        return statusChangeAction;
    }

    /**
     * Check if the change action of model's status is valid when the current status is "unavailable".
     * 
     * @param targetStatus
     *            The status that users want to change to
     * @return {@link org.sigmah.client.page.admin.model.common.StatusChangeAction} object
     */
    @SuppressWarnings("unused")
    private void isValidUnavailableStatusChange(final ProjectModelStatus targetStatus) {

        // RPC call to check if current "unavailable" model is already used;

        final CheckModelUsage checkCommand = new CheckModelUsage();

        if (view.isProject()) {// Project model
            checkCommand.setModelType(CheckModelUsage.ModelType.ProjectModel);
            checkCommand.setProjectModelId(new Long(view.getCurrentProjectModel().getId()));
        } else {// OrgUnit model
            checkCommand.setModelType(CheckModelUsage.ModelType.OrgUnitModel);
            checkCommand.setOrgUnitModelId(new Integer(view.getCurrentOrgUnitModel().getId()));
        }

        // RPC begins
        dispatcher.execute(checkCommand, null, new AsyncCallback<ModelCheckResult>() {

            @Override
            public void onFailure(Throwable arg0) {

                // Row back value if RPC fails

                MessageBox.alert(I18N.CONSTANTS.adminModelCheckError(), I18N.CONSTANTS.adminModelCheckErrorDetails(),
                    null);

                if (checkCommand.getModelType().equals(CheckModelUsage.ModelType.ProjectModel)) {
                    view.getStatusList().setSimpleValue(
                        ProjectModelStatus.getName(view.getCurrentProjectModel().getStatus()));

                } else {
                    view.getStatusList().setSimpleValue(
                        ProjectModelStatus.getName(view.getCurrentOrgUnitModel().getStatus()));
                }

            }

            @Override
            public void onSuccess(ModelCheckResult result) {

                if (result == null || result.isUsed() == null) {// Row back
                                                                // value if RPC
                                                                // encounters
                                                                // errors

                    MessageBox.alert(I18N.CONSTANTS.adminModelCheckError(),
                        I18N.CONSTANTS.adminModelCheckErrorDetails(), null);

                    if (checkCommand.getModelType().equals(CheckModelUsage.ModelType.ProjectModel)) {
                        view.getStatusList().setSimpleValue(
                            ProjectModelStatus.getName(view.getCurrentProjectModel().getStatus()));

                    } else {
                        view.getStatusList().setSimpleValue(
                            ProjectModelStatus.getName(view.getCurrentOrgUnitModel().getStatus()));
                    }

                    return;
                } else {// RPC succeeds

                    // "Unavailable" model that is ever used is only allowed to
                    // shift back to "Used" model.
                    if (result.isUsed() && !targetStatus.equals(ProjectModelStatus.USED)) {

                        // Row back value

                        // MessageBox.alert(I18N.CONSTANTS.error(),
                        // I18N.CONSTANTS.unavailableUsedModelStatusChangeError(), null);

                        if (checkCommand.getModelType().equals(CheckModelUsage.ModelType.ProjectModel)) {
                            view.getStatusList().setSimpleValue(
                                ProjectModelStatus.getName(view.getCurrentProjectModel().getStatus()));

                        } else {
                            view.getStatusList().setSimpleValue(
                                ProjectModelStatus.getName(view.getCurrentOrgUnitModel().getStatus()));
                        }

                        return;

                    }
                    // "Unavailable" model that is never used is only allowed to
                    // shift back to "Ready" model.
                    else if (!result.isUsed() && !targetStatus.equals(ProjectModelStatus.READY)) {
                        // Row back value

                        // MessageBox.alert(I18N.CONSTANTS.error(),
                        // I18N.CONSTANTS.unavailableNotUsedModelStatusChangeError(), null);

                        if (checkCommand.getModelType().equals(CheckModelUsage.ModelType.ProjectModel)) {
                            view.getStatusList().setSimpleValue(
                                ProjectModelStatus.getName(view.getCurrentProjectModel().getStatus()));

                        } else {
                            view.getStatusList().setSimpleValue(
                                ProjectModelStatus.getName(view.getCurrentOrgUnitModel().getStatus()));
                        }

                        return;

                    }
                    // Other cases are all allowed.
                    else {
                        // Do nothing

                    }
                }

            }

        });

    }

    /**
     * Handler method when the status of orgunit model is changed
     * 
     * @author HUZHE (zhe.hu32@gmail.com)
     */
    private void onOrgUnitModelStatusChanged() {

        if (!view.getCurrentOrgUnitModel().getStatus().equals(ProjectModelStatus.UNAVAILABLE)) {
            StatusChangeAction statusChangeAction =
                    isValidStatusChange(view.getCurrentOrgUnitModel().getStatus(),
                        ProjectModelStatus.getStatus(view.getStatusList().getValue().getValue()));

            if (!statusChangeAction.isValid()) {// Unvalid status changing
                                                // action,rollback value
                MessageBox.alert(I18N.CONSTANTS.error(), statusChangeAction.getFeedBackMessage(), null);
                view.getStatusList().setSimpleValue(
                    ProjectModelStatus.getName(view.getCurrentOrgUnitModel().getStatus()));
                return;
            } else {// Valid status changing action

                // The "Draft" project model needs to be checked again
                if (view.getCurrentOrgUnitModel().getStatus().equals(ProjectModelStatus.DRAFT)) {
                    onDraftOrgUnitModelStatusChange(ProjectModelStatus.getStatus(view.getStatusList().getValue()
                        .getValue()));
                } else {
                    // Do nothing
                }
            }
        } else if (view.getCurrentOrgUnitModel().getStatus().equals(ProjectModelStatus.UNAVAILABLE)) {// "Unavailable"
                                                                                                      // model
                                                                                                      // needs
                                                                                                      // to
                                                                                                      // be
                                                                                                      // checked
                                                                                                      // in
                                                                                                      // a
                                                                                                      // different
                                                                                                      // way
            // isValidUnavailableStatusChange(ProjectModelStatus.getStatus(view.getStatusList().getValue().getValue()));
        }

    }

    /**
     * Method to execute when a "Draft" orgunit model'status is changing
     * 
     * @param status
     */
    private void onDraftOrgUnitModelStatusChange(ProjectModelStatus targetStatus) {

        if (!view.getCurrentOrgUnitModel().getStatus().equals(ProjectModelStatus.UNAVAILABLE)) {
            StatusChangeAction statusChangeAction =
                    isValidStatusChange(view.getCurrentOrgUnitModel().getStatus(),
                        ProjectModelStatus.getStatus(view.getStatusList().getValue().getValue()));

            if (!statusChangeAction.isValid()) {// Unvalid status changing
                                                // action,rollback value
                Log.debug("Invalid status changing !");
                MessageBox.alert(I18N.CONSTANTS.error(), statusChangeAction.getFeedBackMessage(), null);
                view.getStatusList().setSimpleValue(
                    ProjectModelStatus.getName(view.getCurrentOrgUnitModel().getStatus()));
                return;
            } else {// Valid status changing action

                Log.debug("valid status changing !");

                // The "Draft" project model needs to be checked again
                if (view.getCurrentProjectModel().getStatus().equals(ProjectModelStatus.DRAFT)) {
                    Log.debug("draft model status is changing !");

                    MessageBox.confirm(I18N.MESSAGES.adminModelStatusChangeBox(),
                        I18N.MESSAGES.adminModelDraftStatusChange(ProjectModelStatus.getName(targetStatus)),
                        new Listener<MessageBoxEvent>() {

                            @Override
                            public void handleEvent(MessageBoxEvent be) {

                                if (Dialog.NO.equals(be.getButtonClicked().getItemId())) {
                                    view.getStatusList().setSimpleValue(
                                        ProjectModelStatus.getName(view.getCurrentOrgUnitModel().getStatus()));
                                }
                            }

                        });
                } else {
                    Log.debug("non draft model status is changing !");
                    Log.debug("Current status: "
                        + ProjectModelStatus.getName(view.getCurrentOrgUnitModel().getStatus())
                        + " target status: "
                        + view.getStatusList().getValue().getValue());
                    // Do nothing
                }
            }
        } else if (view.getCurrentProjectModel().getStatus().equals(ProjectModelStatus.UNAVAILABLE)) {// "Unavailable"
                                                                                                      // model
                                                                                                      // needs
                                                                                                      // to
                                                                                                      // be
                                                                                                      // checked
                                                                                                      // in
                                                                                                      // a
                                                                                                      // different
                                                                                                      // way
            // isValidUnavailableStatusChange(ProjectModelStatus.getStatus(view.getStatusList().getValue().getValue()));
        }

    }

    /**
     * Handler method when the status of project model is changed
     * 
     * @author HUZHE (zhe.hu32@gmail.com)
     */
    private void onProjectModelStatusChanged() {

        if (!view.getCurrentProjectModel().getStatus().equals(ProjectModelStatus.UNAVAILABLE)) {
            StatusChangeAction statusChangeAction =
                    isValidStatusChange(view.getCurrentProjectModel().getStatus(),
                        ProjectModelStatus.getStatus(view.getStatusList().getValue().getValue()));

            if (!statusChangeAction.isValid()) {// Unvalid status changing
                                                // action,rollback value
                Log.debug("Invalid status changing !");
                MessageBox.alert(I18N.CONSTANTS.error(), statusChangeAction.getFeedBackMessage(), null);
                view.getStatusList().setSimpleValue(
                    ProjectModelStatus.getName(view.getCurrentProjectModel().getStatus()));
                return;
            } else {// Valid status changing action

                Log.debug("valid status changing !");

                // The "Draft" project model needs to be checked again
                if (view.getCurrentProjectModel().getStatus().equals(ProjectModelStatus.DRAFT)) {
                    Log.debug("draft model status is changing !");

                    onDraftProjectModelStatusChange(ProjectModelStatus.getStatus(view.getStatusList().getValue()
                        .getValue()));
                } else {
                    Log.debug("non draft model status is changing !");
                    Log.debug("Current status: "
                        + ProjectModelStatus.getName(view.getCurrentProjectModel().getStatus())
                        + " target status: "
                        + view.getStatusList().getValue().getValue());
                    // Do nothing
                }
            }
        } else if (view.getCurrentProjectModel().getStatus().equals(ProjectModelStatus.UNAVAILABLE)) {// "Unavailable"
                                                                                                      // model
                                                                                                      // needs
                                                                                                      // to
                                                                                                      // be
                                                                                                      // checked
                                                                                                      // in
                                                                                                      // a
                                                                                                      // different
                                                                                                      // way
            // isValidUnavailableStatusChange(ProjectModelStatus.getStatus(view.getStatusList().getValue().getValue()));
        }

    }

    /**
     * Method to execute when a "Draft" project model'status is changing
     * 
     * @param targetStatus
     *            The status that users want to change to
     */
    private void onDraftProjectModelStatusChange(final ProjectModelStatus targetStatus) {
        MessageBox.confirm(I18N.MESSAGES.adminModelStatusChangeBox(),
            I18N.MESSAGES.adminModelDraftStatusChange(ProjectModelStatus.getName(targetStatus)),
            new Listener<MessageBoxEvent>() {

                @Override
                public void handleEvent(MessageBoxEvent be) {
                    if (Dialog.NO.equals(be.getButtonClicked().getItemId())) {
                        view.getStatusList().setSimpleValue(
                            ProjectModelStatus.getName(view.getCurrentProjectModel().getStatus()));
                    } else {
                        GetProjectsByModel cmdGetProjectsByModel = new GetProjectsByModel();
                        cmdGetProjectsByModel.setProjectModelId(new Long((long) view.getCurrentProjectModel().getId()));

                        dispatcher.execute(cmdGetProjectsByModel, new MaskingAsyncMonitor(view.getTopPanel(),
                            I18N.CONSTANTS.loading()), new AsyncCallback<ProjectListResult>() {

                            @Override
                            public void onFailure(Throwable caught) {

                                // RPC failed
                                MessageBox.alert(I18N.CONSTANTS.error(), I18N.CONSTANTS.serverError(), null);
                                view.getStatusList().setSimpleValue(
                                    ProjectModelStatus.getName(view.getCurrentProjectModel().getStatus()));
                                return;
                            }

                            @Override
                            public void onSuccess(ProjectListResult result) {

                                List<ProjectDTOLight> testProjects = result.getListProjectsLightDTO();
                                if (result != null && testProjects != null && testProjects.size() > 0) {

                                    String testProjectNames = "";
                                    for (ProjectDTOLight p : testProjects) {
                                        testProjectNames += "[" + p.getName() + "] ";
                                    }

                                    String waringMessage =
                                            I18N.MESSAGES.DraftProjectModelChangeStatusDetails(testProjectNames,
                                                ProjectModelStatus.getName(targetStatus));

                                    Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {

                                        @Override
                                        public void handleEvent(MessageBoxEvent be) {

                                            if (be.getButtonClicked().getItemId().equals(Dialog.NO)) {
                                                view.getStatusList().setSimpleValue(
                                                    ProjectModelStatus.getName(view.getCurrentProjectModel()
                                                        .getStatus()));
                                            }

                                        }

                                    };

                                    MessageBox.confirm(I18N.CONSTANTS.projectChangeStatus(), waringMessage, l);

                                }

                            }

                        });

                    }

                }

            });
    }

    /**
     * Method to update the model
     */
    private void updateModel() {

        if (view.isProject()) {
            if (view.getNameField().getValue() == null
                || view.getStatusList().getValue() == null
                || view.getCurrentModelType() == null) {
                MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
                    I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminStandardModel()), null);
                return;
            }

            HashMap<String, Object> modelProperties = new HashMap<String, Object>();
            modelProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, view.getCurrentProjectModel());
            modelProperties.put(AdminUtil.PROP_PM_NAME, view.getNameField().getValue());

            if (view.getStatusList().getValue() != null) {
                String status = view.getStatusList().getValue().getValue();
                ProjectModelStatus statusEnum = ProjectModelStatus.getStatus(status);
                modelProperties.put(AdminUtil.PROP_PM_STATUS, statusEnum);
            }

            modelProperties.put(AdminUtil.PROP_PM_USE, view.getCurrentModelType());

            dispatcher.execute(new CreateEntity("ProjectModel", modelProperties), null,
                new AsyncCallback<CreateResult>() {

                    public void onFailure(Throwable caught) {
                        MessageBox.alert(
                            I18N.CONSTANTS.adminProjectModelUpdateBox(),
                            I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminProjectModelStandard()
                                + " '"
                                + view.getNameField().getValue()
                                + "'"), null);
                    }

                    @Override
                    public void onSuccess(CreateResult result) {
                        if (result != null && result.getEntity() != null) {

                            // Refresh the current projet model
                            currentProjectModel = (ProjectModelDTO) result.getEntity();

                            view.initModelView((ProjectModelDTO) result.getEntity());

                            // Refresh the listener
                            addStatusListListener();

                            // Window.alert("Zhe was here !");

                            // Window.Location.reload();

                            Notification.show(
                                I18N.CONSTANTS.adminProjectModelUpdateBox(),
                                I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminProjectModelStandard()
                                    + " '"
                                    + view.getNameField().getValue()
                                    + "'"));

                            // Refresh the page
                            int tabIndex = 0;
                            if (currentTabItem != null) {
                                for (int i = 0; i < MAIN_TABS.length; i++) {

                                    if (MAIN_TABS[i].equals(currentTabItem.getText())) {
                                        tabIndex = i;
                                        break;
                                    }
                                }
                            }
                            eventBus.fireEvent(new NavigationEvent(NavigationHandler.NavigationRequested, currentState
                                .deriveTo(currentState.getCurrentSection(), currentState.getModel(),
                                    MAIN_TABS[tabIndex], true), null));

                        } else {

                            MessageBox.alert(
                                I18N.CONSTANTS.adminProjectModelUpdateBox(),
                                I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminProjectModelStandard()
                                    + " '"
                                    + view.getNameField().getValue()
                                    + "'"), null);

                        }
                    }
                });
        } else {
            if (!view.getTopLeftFormPanel().isValid()) {
                MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
                    I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminStandardModel()), null);
                return;
            }

            final String nameValue = view.getNameField().getValue();
            final String title = view.getTitleField().getValue();
            final Boolean hasBudget = view.getHasBudgetCheckBox().getValue();
            final Boolean containsProjects = view.getCanContainProjectsCheckBox().getValue();

            HashMap<String, Object> modelProperties = new HashMap<String, Object>();
            modelProperties.put(AdminUtil.PROP_OM_NAME, view.getNameField().getValue());
            if (view.getStatusList().getValue() != null) {
                String status = view.getStatusList().getValue().getValue();
                ProjectModelStatus statusEnum = ProjectModelStatus.getStatus(status);
                modelProperties.put(AdminUtil.PROP_OM_STATUS, statusEnum);
            }
            modelProperties.put(AdminUtil.ADMIN_ORG_UNIT_MODEL, view.getCurrentOrgUnitModel());
            modelProperties.put(AdminUtil.PROP_OM_NAME, nameValue);
            modelProperties.put(AdminUtil.PROP_OM_TITLE, title);
            modelProperties.put(AdminUtil.PROP_OM_HAS_BUDGET, hasBudget);
            modelProperties.put(AdminUtil.PROP_OM_CONTAINS_PROJECTS, containsProjects);

            dispatcher.execute(new CreateEntity("OrgUnitModel", modelProperties), null,
                new AsyncCallback<CreateResult>() {

                    public void onFailure(Throwable caught) {
                        MessageBox.alert(
                            I18N.CONSTANTS.adminOrgUnitsModelCreationBox(),
                            I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminOrgUnitsModelStandard()
                                + " '"
                                + view.getNameField().getValue()
                                + "'"), null);
                    }

                    @Override
                    public void onSuccess(CreateResult result) {
                        if (result != null && result.getEntity() != null) {

                            // Refresh the current orguni model
                            currentOrgUnitModel = (OrgUnitModelDTO) result.getEntity();

                            view.initModelView((OrgUnitModelDTO) result.getEntity());

                            // Refresh the listener
                            addStatusListListener();

                            Notification.show(
                                I18N.CONSTANTS.adminOrgUnitsModelCreationBox(),
                                I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminOrgUnitsModelStandard()
                                    + " '"
                                    + view.getNameField().getValue()
                                    + "'"));

                            int tabIndex = 0;
                            if (currentTabItem != null) {
                                for (int i = 0; i < MAIN_TABS.length; i++) {

                                    if (MAIN_TABS[i].equals(currentTabItem.getText())) {
                                        tabIndex = i;
                                        break;
                                    }
                                }
                            }

                            // Refresh the page
                            eventBus.fireEvent(new NavigationEvent(NavigationHandler.NavigationRequested, currentState
                                .deriveTo(currentState.getCurrentSection(), currentState.getModel(),
                                    MAIN_TABS[tabIndex], false), null));

                        } else {
                            MessageBox.alert(
                                I18N.CONSTANTS.adminOrgUnitsModelCreationBox(),
                                I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminOrgUnitsModelStandard()
                                    + " '"
                                    + view.getNameField().getValue()
                                    + "'"), null);
                        }
                    }
                });
        }

    }

    public boolean hasValueChanged() {
        boolean subDataChanged = false;
        for (AdminSubPresenter presenter : presenters) {
            if (presenter.hasValueChanged()) {
                subDataChanged = true;
            }
        }
        return dataChanged || subDataChanged;
    }

    public void forgetAllChangedValues() {
        for (AdminSubPresenter presenter : presenters) {
            presenter.forgetAllChangedValues();
        }
        dataChanged = false;
    }

}
