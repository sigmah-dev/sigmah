package org.sigmah.client.page.admin.management;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.management.AdminManagementPresenter.AdminManagementSubPresenter;
import org.sigmah.shared.dto.OrgUnitDTOLight;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * Presenter of the backup page
 * 
 * @author Aurélien Ponçon
 *
 */
public class AdminBackupManagementPresenter implements AdminManagementSubPresenter {

    @ImplementedBy(AdminBackupManagementView.class)
    public interface View {

        ContentPanel getContentPanel();

        RadioButton getAllVersionsRadioButton();
        
        RadioButton getLastVersionRadioButton();

        ListBox getOrgUnitListBox();

        Button getBackupButton();
    }

    private final View view;

    private List<OrgUnitDTOLight> units;
    
    
    
    @Inject
    public AdminBackupManagementPresenter(final UserLocalCache cache, final View view, final Dispatcher dispatcher) {
        this.view = view;        
        units = new ArrayList<OrgUnitDTOLight>();
        
        cache.getOrganizationCache().get(new AsyncCallback<OrgUnitDTOLight>() {

            @Override
            public void onFailure(Throwable e) {
                // nothing
            }

            @Override
            public void onSuccess(OrgUnitDTOLight result) {

                if (result != null) {
                    view.getOrgUnitListBox().clear();
                    units.clear();

                    units.add(result);
                    crawlOrgUnits(result, units);

                    for(OrgUnitDTOLight unit : units) {
                        view.getOrgUnitListBox().addItem(unit.getFullName());
                    }
                }
            }
        });
        
        view.getLastVersionRadioButton().setValue(true);
        
        view.getBackupButton().addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent arg0) {
                StringBuilder stringBuilder = new StringBuilder(GWT.getModuleBaseURL() + "backup");
                stringBuilder.append("?");
                stringBuilder.append("downloadVersions").append("=").append(view.getAllVersionsRadioButton().getValue());
                stringBuilder.append("&");
                stringBuilder.append("orgUnit").append("=").append(units.get(view.getOrgUnitListBox().getSelectedIndex()).getId());
                
                Window.open(stringBuilder.toString(), "_parent", "location=no");
                
            }
        });
    }
    
    
    
    @Override
    public boolean hasValueChanged() {
        return false;
    }

    @Override
    public void forgetAllChangedValues() {
        
    }

    @Override
    public void setCurrentState() {
        
    }

    @Override
    public ContentPanel getContentPanel() {
        return view.getContentPanel();
    }

    @Override
    public String getName() {
        return I18N.CONSTANTS.backupManagementTitle();
    }
    
    private void crawlOrgUnits(OrgUnitDTOLight root, List<OrgUnitDTOLight> result) {
        if (!root.getChildrenDTO().isEmpty()) {
            for (OrgUnitDTOLight child : root.getChildrenDTO()) {
                result.add(child);
                crawlOrgUnits(child, result);
            }
        }
    }
}
