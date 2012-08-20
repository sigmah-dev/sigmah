package org.sigmah.client.page.admin.management;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.management.AdminManagementPresenter.AdminManagementSubPresenter;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.UpdateOrganization;
import org.sigmah.shared.command.result.OrganizationResult;
import org.sigmah.shared.dto.OrganizationDTO;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * Presenter for the core management, the place where users can modify some informations like organization name, etc.
 * 
 * @author Aurélien Ponçon
 */
public class AdminCoreManagementPresenter implements AdminManagementSubPresenter {

    @ImplementedBy(AdminCoreManagementView.class)
    public interface View {

        ContentPanel getContentPanel();

        TextBox getOrganizationNameTextBox();

        FileUpload getLogoFileUpload();

        Image getLogoImage();

        Button getSaveButton();

        FormPanel getFileFormPanel();
    }

    private final View view;

    private boolean fieldValueChanged;

    private OrganizationDTO organization;

    @Inject
    public AdminCoreManagementPresenter(final UserLocalCache cache, final View view, final Dispatcher dispatcher) {
        this.view = view;
        fieldValueChanged = false;

        ChangeHandler changeHandler = new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                fieldValueChanged = true;
            }
        };

        view.getOrganizationNameTextBox().addChangeHandler(changeHandler);
        view.getLogoFileUpload().addChangeHandler(changeHandler);

        organization = cache.getOrganizationCache().getOrganization();
        view.getOrganizationNameTextBox().setText(organization.getName());

        view.getLogoImage().setUrl(
            GWT.getModuleBaseURL()
                + "image-provider?"
                + FileUploadUtils.IMAGE_URL
                + "="
                + organization.getLogo()
                + "&tmstmp="
                + System.currentTimeMillis());

        view.getFileFormPanel().setAction(
            GWT.getModuleBaseURL() + "image-provider?organization=" + organization.getId());

        view.getFileFormPanel().addSubmitCompleteHandler(new SubmitCompleteHandler() {

            @Override
            public void onSubmitComplete(SubmitCompleteEvent event) {
                view.getLogoImage().setUrl(
                    GWT.getModuleBaseURL()
                        + "image-provider?"
                        + FileUploadUtils.IMAGE_URL
                        + "="
                        + organization.getLogo()
                        + "&tmstmp="
                        + System.currentTimeMillis());
                Notification.show(I18N.CONSTANTS.organizationManagementLogoNotificationTitle(),
                    I18N.CONSTANTS.organizationManagementLogoNotificationMessage());
            }
        });

        view.getSaveButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (view.getOrganizationNameTextBox().getText().isEmpty()) {
                    Window.alert(I18N.CONSTANTS.organizationManagementBlankNameNotificationError());
                } else {
                    if (!view.getLogoFileUpload().getFilename().isEmpty()) {
                        view.getFileFormPanel().submit();
                    }

                    dispatcher.execute(
                        new UpdateOrganization(organization, view.getOrganizationNameTextBox().getText()), null,
                        new AsyncCallback<OrganizationResult>() {

                            @Override
                            public void onFailure(Throwable arg0) {
                                Window.alert(I18N.CONSTANTS.organizationManagementWebServiceNotificationError());
                            }

                            @Override
                            public void onSuccess(OrganizationResult result) {

                                fieldValueChanged = false;
                                cache.refreshOrganization(new AsyncCallback<Void>() {

                                    @Override
                                    public void onSuccess(Void arg0) {
                                        Notification.show(
                                            I18N.CONSTANTS.organizationManagementSaveChangesNotificationTitle(),
                                            I18N.CONSTANTS.organizationManagementSaveChangesNotificationMessage());
                                    }

                                    @Override
                                    public void onFailure(Throwable arg0) {
                                        Window.alert(I18N.CONSTANTS.organizationManagementLocalCacheNotificationError());
                                    }
                                });
                            }
                        });
                }
            }
        });
    }

    @Override
    public boolean hasValueChanged() {
        return fieldValueChanged;
    }

    @Override
    public void forgetAllChangedValues() {
        fieldValueChanged = false;
    }

    @Override
    public void setCurrentState() {
        // TODO Auto-generated method stub

    }

    @Override
    public ContentPanel getContentPanel() {
        return view.getContentPanel();
    }

    @Override
    public String getName() {
        return I18N.CONSTANTS.organizationManagementTitle();
    }

}
