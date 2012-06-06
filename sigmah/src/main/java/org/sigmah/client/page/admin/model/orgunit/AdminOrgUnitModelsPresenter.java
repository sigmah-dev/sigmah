package org.sigmah.client.page.admin.model.orgunit;

import org.sigmah.client.EventBus;
import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.AdminSubPresenter;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.shared.command.GetOrgUnitModels;
import org.sigmah.shared.command.result.OrgUnitModelListResult;
import org.sigmah.shared.dto.OrgUnitModelDTO;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

public class AdminOrgUnitModelsPresenter implements AdminSubPresenter {

    private final View view;
    private static boolean alert = false;
    private final Dispatcher dispatcher;
    private AdminPageState currentState;
    private int modelId = -1;

    @ImplementedBy(AdminOrgUnitModelsView.class)
    public static abstract class View extends ContentPanel {

        public abstract AdminModelsStore getAdminModelsStore();

        public abstract MaskingAsyncMonitor getOrgUnitModelsLoadingMonitor();

        public abstract Component getMainPanel(int id);

        public abstract void setCurrentState(AdminPageState currentState);

        public abstract Grid<OrgUnitModelDTO> getOrgUnitModelGrid();
    }

    public static class AdminModelsStore extends ListStore<OrgUnitModelDTO> {
    }

    @Inject
    public AdminOrgUnitModelsPresenter(Dispatcher dispatcher, UserLocalCache cache, final Authentication authentication, EventBus eventBus, final AdminPageState currentState) {
        this.currentState = currentState;
        // this.cache = cache;
        this.dispatcher = dispatcher;
        // this.authentication = authentication;
        this.view = new AdminOrgUnitModelsView(dispatcher, cache, eventBus);
    }

    public static void refreshOrgUnitModelsPanel(Dispatcher dispatcher, final View view) {
        dispatcher.execute(new GetOrgUnitModels(), view.getOrgUnitModelsLoadingMonitor(),
            new AsyncCallback<OrgUnitModelListResult>() {

                @Override
                public void onFailure(Throwable arg0) {
                    AdminUtil.alertPbmData(alert);
                }

                @Override
                public void onSuccess(OrgUnitModelListResult result) {
                    if (result.getList() != null && !result.getList().isEmpty()) {
                        view.getAdminModelsStore().removeAll();
                        view.getAdminModelsStore().add(result.getList());
                        view.getAdminModelsStore().commitChanges();
                    }

                }
            });
    }

    @Override
    public Component getView() {
        refreshOrgUnitModelsPanel(dispatcher, view);
        if (currentState != null) {
            final Integer mod = currentState.getModel();
            if (mod != null) {
                modelId = mod;
            }
        }

        return view.getMainPanel(modelId);
    }

    @Override
    public void setCurrentState(AdminPageState currentState) {
        this.currentState = currentState;
        view.setCurrentState(currentState);
    }

    @Override
    public void discardView() {
    }

    @Override
    public void viewDidAppear() {
    }

    @Override
    public boolean hasValueChanged() {
        return false;
    }

    @Override
    public void forgetAllChangedValues() {
    }

}
