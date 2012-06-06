package org.sigmah.client.page.admin.model.common.element;

import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.client.page.admin.model.common.ModelView;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;

public class AdminFlexibleElementsPresenter implements AdminModelSubPresenter {

    private final View view;
    private ProjectModelDTO projectModel;
    private OrgUnitModelDTO orgUnitModel;

    public static abstract class View extends ModelView {

        public abstract Component getMainPanel();

        public abstract ListStore<FlexibleElementDTO> getFieldsStore();

        public abstract void showNewFlexibleElementForm(FlexibleElementDTO element, boolean isUpdate);

        public abstract void showNewGroupForm(final FlexibleElementDTO model, final boolean isUpdate);

        public abstract List<FlexibleElementDTO> getDeleteSelection();

        public abstract void confirmDeleteSelected(ConfirmCallback confirmCallback);
    }

    public AdminFlexibleElementsPresenter(Dispatcher dispatcher) {
        this.view = new AdminFlexibleElementsView(dispatcher);
    }

    @Override
    public Component getView() {

        assert (projectModel != null || orgUnitModel != null);
        if (projectModel != null) {
            view.setProjectModel(projectModel);
            view.setOrgUnitModel(null);
            view.getFieldsStore().add(projectModel.getAllElements());
            view.getFieldsStore().commitChanges();
            projectModel = view.getModel();
        } else if (orgUnitModel != null) {
            view.setOrgUnitModel(orgUnitModel);
            view.setProjectModel(null);
            view.getFieldsStore().add(orgUnitModel.getAllElements());
            view.getFieldsStore().commitChanges();
            orgUnitModel = view.getModel();
        }

        return view.getMainPanel();
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

    @Override
    public void setCurrentState(AdminPageState currentState) {
    }

    @Override
    public void setModel(Object model) {
        if (model instanceof ProjectModelDTO) {
            this.projectModel = (ProjectModelDTO) model;
            this.orgUnitModel = null;
        } else if (model instanceof OrgUnitModelDTO) {
            this.orgUnitModel = (OrgUnitModelDTO) model;
            this.projectModel = null;
        }
    }

    @Override
    public Object getModel() {
        if (projectModel != null)
            return projectModel;
        else
            return orgUnitModel;
    }
}
