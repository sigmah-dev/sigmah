package org.sigmah.client.page.admin.management;

import java.util.Map;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.management.AdminManagementPresenter.AdminManagementSubPresenter;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.GetGlobalExportSettings;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.export.GlobalExportFormat;
import org.sigmah.shared.dto.GlobalExportSettingsDTO;
import org.sigmah.shared.dto.UpdateGlobalExportSettings;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * Presenter of the export setting page
 * 
 * @author Aurélien Ponçon
 */
public class AdminExportManagementPresenter implements AdminManagementSubPresenter {

    public interface View {

        ContentPanel getContentPanel();

        Radio getExcelRadioButton();

        Radio getCalcRadioButton();

        Map<Integer, Boolean> getFieldsMap();

        Button getSaveButton();
    }

    private final View view;
    private boolean valueChanged;

    @Inject
    public AdminExportManagementPresenter(final UserLocalCache cache, final View view, final Dispatcher dispatcher) {
        this.view = view;

        final GetGlobalExportSettings settingsCommand =
                new GetGlobalExportSettings(cache.getOrganizationCache().getOrganization().getId());
        dispatcher.execute(settingsCommand, null, new AsyncCallback<GlobalExportSettingsDTO>() {

            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(GlobalExportSettingsDTO result) {
                // set export format
                switch (result.getExportFormat()) {
                    case XLS:
                        view.getExcelRadioButton().setValue(true);
                        break;

                    case ODS:
                        view.getCalcRadioButton().setValue(true);
                        break;
                }
            }
        });

        view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                UpdateGlobalExportSettings settings = new UpdateGlobalExportSettings(view.getFieldsMap());
                if (view.getExcelRadioButton().getValue()) {
                    settings.setExportFormat(GlobalExportFormat.XLS);
                } else {
                    settings.setExportFormat(GlobalExportFormat.ODS);
                }

                settings.setOrganizationId(cache.getOrganizationCache().getOrganization().getId());
                dispatcher.execute(settings, null, new AsyncCallback<VoidResult>() {

                    @Override
                    public void onFailure(Throwable caught) {
                    }

                    @Override
                    public void onSuccess(VoidResult result) {
                        valueChanged = false;
                        Notification.show(I18N.CONSTANTS.exportManagementSaveChangesNotificationTitle(),
                            I18N.CONSTANTS.exportManagementSaveChangesNotificationMessage());
                    }
                });
            }
        });

        Listener<BaseEvent> valueChangedListener = new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                valueChanged = true;

            }
        };

        view.getCalcRadioButton().addListener(Events.OnClick, valueChangedListener);
        view.getExcelRadioButton().addListener(Events.OnClick, valueChangedListener);
    }

    @Override
    public boolean hasValueChanged() {
        return valueChanged;
    }

    @Override
    public void forgetAllChangedValues() {
        valueChanged = false;
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
        return I18N.CONSTANTS.globalExportConfiguration();
    }

}
