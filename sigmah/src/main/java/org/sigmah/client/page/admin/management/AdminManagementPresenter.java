package org.sigmah.client.page.admin.management;

import org.sigmah.client.EventBus;
import org.sigmah.client.SigmahInjector;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.AdminSubPresenter;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.inject.Inject;

/**
 * Presenter for the management view in the Admin view
 * 
 * @author Aurélien Ponçon
 */
public class AdminManagementPresenter implements AdminSubPresenter {

    public static abstract class View extends ContentPanel {        
        abstract void addPanelToMainContainer(ContentPanel contentPanel);

        abstract void addPanelToSecondaryContainer(ContentPanel contentPanel);
    }

    public interface AdminManagementSubPresenter {

        boolean hasValueChanged();

        void forgetAllChangedValues();

        void setCurrentState();

        ContentPanel getContentPanel();

        String getName();
    }

    private View view;
    private AdminPageState currentState;
    private AdminManagementSubPresenter[] subPresenters;

    @Inject
    public AdminManagementPresenter(final Dispatcher dispatcher, View view, final EventBus eventBus, final SigmahInjector injector) {
        this.view = view;

        subPresenters = new AdminManagementSubPresenter[] {
                                                           injector.getAdminCoreManagementPresenter(),
                                                           injector.getAdminBackupManagementPresenter(),
                                                           injector.getAdminExportManagementPresenter()};        
        
        subPresenters[0].getContentPanel().setWidth("55%");
        subPresenters[1].getContentPanel().setWidth("45%");
        subPresenters[2].getContentPanel().setWidth("100%");

        subPresenters[0].getContentPanel().setHeight(400);
        subPresenters[1].getContentPanel().setHeight(400);
        subPresenters[2].getContentPanel().setHeight(5000);
        view.addPanelToMainContainer(subPresenters[0].getContentPanel());
        view.addPanelToMainContainer(subPresenters[1].getContentPanel());
        view.addPanelToSecondaryContainer(subPresenters[2].getContentPanel());
        
        
    }

    @Override
    public Component getView() {
        return view;
    }

    @Override
    public void discardView() {
        this.view = null;

    }

    @Override
    public void viewDidAppear() {

    }

    @Override
    public boolean hasValueChanged() {
        for (AdminManagementSubPresenter subPresenter : subPresenters) {
            if (subPresenter.hasValueChanged()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void forgetAllChangedValues() {
        for (AdminManagementSubPresenter subPresenter : subPresenters) {
            subPresenter.forgetAllChangedValues();
        }
    }

    @Override
    public void setCurrentState(AdminPageState currentState) {
        this.currentState = currentState;
    }
}
