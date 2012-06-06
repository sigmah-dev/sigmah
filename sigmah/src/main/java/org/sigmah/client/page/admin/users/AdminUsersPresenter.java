package org.sigmah.client.page.admin.users;

import java.util.List;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.AdminSubPresenter;
import org.sigmah.client.page.admin.users.form.PrivacyGroupSigmahForm;
import org.sigmah.client.page.admin.users.form.ProfileSigmahForm;
import org.sigmah.client.page.admin.users.form.UserSigmahForm;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.shared.command.GetPrivacyGroups;
import org.sigmah.shared.command.GetProfilesWithDetails;
import org.sigmah.shared.command.GetUsersWithProfiles;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.PrivacyGroupsListResult;
import org.sigmah.shared.command.result.ProfileWithDetailsListResult;
import org.sigmah.shared.command.result.UserListResult;
import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.profile.ProfileUtils;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * Loads data for users administration screen.
 * 
 * @author nrebiai
 */
public class AdminUsersPresenter implements AdminSubPresenter {

    private static boolean alert = false;
    private final Dispatcher dispatcher;
    private final UserLocalCache cache;
    private final View view;
    private Authentication authentication;

    @ImplementedBy(AdminUsersView.class)
    public static abstract class View extends ContentPanel {

        public abstract ContentPanel getMainPanel();

        public abstract AdminUsersStore getAdminUsersStore();

        public abstract AdminProfilesStore getAdminProfilesStore();

        public abstract AdminPrivacyGroupsStore getAdminPrivacyGroupsStore();

        public abstract MaskingAsyncMonitor getUsersLoadingMonitor();

        public abstract MaskingAsyncMonitor getProfilesLoadingMonitor();

        public abstract MaskingAsyncMonitor getPrivacyGroupsLoadingMonitor();

        public abstract UserSigmahForm showNewUserForm(Window window, AsyncCallback<CreateResult> callback,
                UserDTO userToUpdate);

        public abstract ProfileSigmahForm showNewProfileForm(Window window, AsyncCallback<CreateResult> asyncCallback,
                ProfileDTO profileToUpdate);

        public abstract PrivacyGroupSigmahForm showNewPrivacyGroupForm(Window window,
                AsyncCallback<CreateResult> asyncCallback, PrivacyGroupDTO privacyGroupToUpdate);

        public abstract List<UserDTO> getUsersSelection();

        public abstract void confirmDeleteSelected(ConfirmCallback confirmCallback);

        public abstract List<ProfileDTO> getProfilesSelection();

        public abstract List<PrivacyGroupDTO> getPrivacyGroupsSelection();

        public abstract void sufficient();

        public abstract void insufficient();
    }

    public static class AdminUsersStore extends ListStore<UserDTO> {
    }

    public static class AdminProfilesStore extends ListStore<ProfileDTO> {
    }

    public static class AdminPrivacyGroupsStore extends ListStore<PrivacyGroupDTO> {
    }

    @Inject
    public AdminUsersPresenter(Dispatcher dispatcher, UserLocalCache cache, final Authentication authentication) {
        this.cache = cache;
        this.dispatcher = dispatcher;
        this.authentication = authentication;
        this.view = new AdminUsersView(dispatcher, cache);
        if (ProfileUtils.isGranted(authentication, GlobalPermissionEnum.MANAGE_USER)) {

            // Getting Users
            refreshUserPanel(dispatcher, view);

            // Getting profiles
            refreshProfilePanel(dispatcher, view);

            // Getting privacy groups
            refreshPrivacyGroupPanel(dispatcher, view);
            view.sufficient();
        } else {
            view.insufficient();
        }
    }

    private static void alertPbmData() {
        if (alert)
            return;
        alert = true;
        MessageBox.alert(I18N.CONSTANTS.adminUsers(), I18N.CONSTANTS.adminProblemLoading(), null);
    }

    @Override
    public Component getView() {
        /*
         * if(!ProfileUtils.isGranted(authentication, GlobalPermissionEnum.MANAGE_USER)){ ContentPanel insufficientView
         * = new ContentPanel(); final HTML insufficient = new HTML(I18N.CONSTANTS.permManageUsersInsufficient());
         * insufficient.addStyleName("important-label"); insufficientView.add(insufficient); return insufficientView;
         * }else{
         */
        return view.getMainPanel();
        // }
    }

    @Override
    public void viewDidAppear() {
        // nothing to do
    }

    @Override
    public boolean hasValueChanged() {
        return false;
    }

    @Override
    public void forgetAllChangedValues() {
    }

    public static void refreshUserPanel(Dispatcher dispatcher, final View view) {
        dispatcher.execute(new GetUsersWithProfiles(), view.getUsersLoadingMonitor(),
            new AsyncCallback<UserListResult>() {

                @Override
                public void onFailure(Throwable arg0) {
                    alertPbmData();
                }

                @Override
                public void onSuccess(UserListResult result) {
                    view.getAdminUsersStore().removeAll();
                    view.getAdminUsersStore().clearFilters();
                    if (result.getList() == null || result.getList().isEmpty()) {
                        alertPbmData();
                        return;
                    }
                    view.getAdminUsersStore().add(result.getList());
                    view.getAdminUsersStore().commitChanges();
                }
            });

    }

    public static void refreshProfilePanel(Dispatcher dispatcher, final View view) {
        dispatcher.execute(new GetProfilesWithDetails(), view.getProfilesLoadingMonitor(),
            new AsyncCallback<ProfileWithDetailsListResult>() {

                @Override
                public void onFailure(Throwable arg0) {
                    alertPbmData();
                }

                @Override
                public void onSuccess(ProfileWithDetailsListResult result) {
                    view.getAdminProfilesStore().removeAll();
                    if (result.getList() == null || result.getList().isEmpty()) {
                        alertPbmData();
                        return;
                    }
                    view.getAdminProfilesStore().add(result.getList());
                    view.getAdminProfilesStore().commitChanges();
                }
            });

    }

    public static void refreshPrivacyGroupPanel(Dispatcher dispatcher, final View view) {
        dispatcher.execute(new GetPrivacyGroups(), view.getPrivacyGroupsLoadingMonitor(),
            new AsyncCallback<PrivacyGroupsListResult>() {

                @Override
                public void onFailure(Throwable arg0) {
                    alertPbmData();
                }

                @Override
                public void onSuccess(PrivacyGroupsListResult result) {
                    view.getAdminPrivacyGroupsStore().removeAll();
                    if (result.getList() == null || result.getList().isEmpty()) {
                        alertPbmData();
                        return;
                    }
                    view.getAdminPrivacyGroupsStore().add(result.getList());
                    view.getAdminPrivacyGroupsStore().commitChanges();
                }
            });

    }

    @Override
    public void discardView() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCurrentState(AdminPageState currentState) {
        // TODO Auto-generated method stub

    }

}
