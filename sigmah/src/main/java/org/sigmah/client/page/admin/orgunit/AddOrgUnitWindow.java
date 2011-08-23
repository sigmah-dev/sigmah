package org.sigmah.client.page.admin.orgunit;

import java.util.ArrayList;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.AddOrgUnit;
import org.sigmah.shared.command.GetOrgUnitModels;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.OrgUnitModelListResult;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.dto.CountryDTO;
import org.sigmah.shared.dto.OrgUnitDTOLight;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AddOrgUnitWindow {

    /**
     * Listener.
     * 
     * @author tmi
     * 
     */
    public static interface CreateOrgUnitListener {

        /**
         * Called when an new org unit has been created.
         */
        public void orgUnitCreated();
    }

    private final Dispatcher dispatcher;
    private final UserLocalCache cache;
    private final ArrayList<CreateOrgUnitListener> listeners;

    private final FormPanel formPanel;
    private final Window window;

    private final TextField<String> nameField;
    private final TextField<String> fullNameField;
    private final ComboBox<OrgUnitModelDTO> modelsField;
    private final ListStore<OrgUnitModelDTO> modelsStore;
    private final ComboBox<CountryDTO> countriesField;
    private final ListStore<CountryDTO> countriesStore;
    private final HiddenField<String> parentField;

    /**
     * Counter to wait that required data are loaded before showing the window.
     */
    private int countBeforeShow;

    /**
     * Flag to display only one alert message.
     */
    private boolean alert = false;

    public AddOrgUnitWindow(Dispatcher dispatcher, UserLocalCache cache) {

        this.dispatcher = dispatcher;
        this.cache = cache;
        listeners = new ArrayList<AddOrgUnitWindow.CreateOrgUnitListener>();

        // Name field.
        nameField = new TextField<String>();
        nameField.setMaxLength(16);
        nameField.setFieldLabel(I18N.CONSTANTS.adminOrgUnitCode());
        nameField.setAllowBlank(false);

        // Full name field.
        fullNameField = new TextField<String>();
        fullNameField.setMaxLength(50);
        fullNameField.setFieldLabel(I18N.CONSTANTS.adminOrgUnitTitle());
        fullNameField.setAllowBlank(false);

        // Countries list.
        countriesField = new ComboBox<CountryDTO>();
        countriesField.setFieldLabel(I18N.CONSTANTS.adminOrgUnitCountry());
        countriesField.setAllowBlank(false);
        countriesField.setValueField("id");
        countriesField.setDisplayField("name");
        countriesField.setEditable(true);
        countriesField.setEmptyText(I18N.CONSTANTS.flexibleElementDefaultSelectCountry());
        countriesField.setTriggerAction(TriggerAction.ALL);

        countriesStore = new ListStore<CountryDTO>();
        countriesStore.addListener(Events.Add, new Listener<StoreEvent<CountryDTO>>() {

            @Override
            public void handleEvent(StoreEvent<CountryDTO> be) {
                countriesField.setEnabled(true);
            }
        });

        countriesStore.addListener(Events.Clear, new Listener<StoreEvent<CountryDTO>>() {

            @Override
            public void handleEvent(StoreEvent<CountryDTO> be) {
                countriesField.setEnabled(false);
            }
        });
        countriesField.setStore(countriesStore);

        // Models list.
        modelsField = new ComboBox<OrgUnitModelDTO>();
        modelsField.setFieldLabel(I18N.CONSTANTS.adminOrgUnitModel());
        modelsField.setAllowBlank(false);
        modelsField.setValueField("id");
        modelsField.setDisplayField("name");
        modelsField.setEditable(true);
        modelsField.setEmptyText(I18N.CONSTANTS.adminOrgUnitModelEmptyChoice());
        modelsField.setTriggerAction(TriggerAction.ALL);

        modelsStore = new ListStore<OrgUnitModelDTO>();
        modelsStore.addListener(Events.Add, new Listener<StoreEvent<ProjectModelDTO>>() {

            @Override
            public void handleEvent(StoreEvent<ProjectModelDTO> be) {
                modelsField.setEnabled(true);
            }
        });

        modelsStore.addListener(Events.Clear, new Listener<StoreEvent<ProjectModelDTO>>() {

            @Override
            public void handleEvent(StoreEvent<ProjectModelDTO> be) {
                modelsField.setEnabled(false);
            }
        });
        modelsField.setStore(modelsStore);

        // Parent id.
        parentField = new HiddenField<String>();
        parentField.setName("parentId");

        // Create button.
        final Button createButton = new Button(I18N.CONSTANTS.adminOrgUnitCreateButton());
        createButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
            @Override
            public void handleEvent(ButtonEvent be) {
                createOrgUnit();
            }
        });

        // Form panel.
        formPanel = new FormPanel();
        formPanel.setBodyBorder(false);
        formPanel.setHeaderVisible(false);
        formPanel.setPadding(5);
        formPanel.setLabelWidth(170);
        formPanel.setFieldWidth(350);

        formPanel.add(nameField);
        formPanel.add(fullNameField);
        formPanel.add(countriesField);
        formPanel.add(modelsField);
        formPanel.add(parentField);
        formPanel.addButton(createButton);

        // Main window panel.
        final VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setLayout(new FitLayout());
        mainPanel.add(formPanel);
        mainPanel.setAutoHeight(true);

        // Window.
        window = new Window();
        window.setHeading(I18N.CONSTANTS.adminOrgUnitAdd());
        window.setWidth(560);
        window.setAutoHeight(true);

        window.setPlain(true);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setLayout(new FitLayout());
        window.add(mainPanel);
    }

    private void createOrgUnit() {

        // Checks the form completion.
        if (!formPanel.isValid()) {
            MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
                    I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.orgunit()), null);
            return;
        }

        final OrgUnitDTOLight unit = new OrgUnitDTOLight();
        unit.setName(nameField.getValue());
        unit.setFullName(fullNameField.getValue());
        unit.setOfficeLocationCountry(countriesField.getValue());

        dispatcher.execute(new AddOrgUnit(Integer.parseInt(parentField.getValue()), modelsField.getValue().getId(),
                I18N.CONSTANTS.calendarDefaultName(), unit), null, new AsyncCallback<CreateResult>() {

            @Override
            public void onFailure(Throwable caught) {
                MessageBox.alert(I18N.CONSTANTS.adminOrgUnitAddFailed(), I18N.CONSTANTS.adminOrgUnitAddFailedDetails(),
                        null);
            }

            @Override
            public void onSuccess(CreateResult result) {
                fireOrgUnitCreated();
                window.hide();
            }
        });
    }

    public void show(Integer parentId) {

        // Reset.
        nameField.reset();
        fullNameField.reset();
        modelsField.reset();
        modelsStore.removeAll();
        countriesField.reset();
        countriesStore.removeAll();
        parentField.setValue(String.valueOf(parentId));

        // There are three remote calls
        countBeforeShow = 1;
        alert = false;

        // Retrieves the models.
        dispatcher.execute(new GetOrgUnitModels(ProjectModelStatus.READY), null,
                new AsyncCallback<OrgUnitModelListResult>() {

                    @Override
                    public void onFailure(Throwable e) {
                        missingRequiredData(I18N.CONSTANTS.adminOrgUnitAddMissingModel());
                    }

                    @Override
                    public void onSuccess(OrgUnitModelListResult result) {

                        if (result.getList() != null && !result.getList().isEmpty()) {
                            modelsStore.add(result.getList());
                            countBeforeShow();
                        } else {
                            missingRequiredData(I18N.CONSTANTS.adminOrgUnitAddMissingModel());
                        }
                    }
                });

        // Retrieves the countries.
        countriesStore.add(cache.getCountryCache().get());
    }

    /**
     * Decrements the local counter before showing the window.
     */
    private void countBeforeShow() {

        countBeforeShow--;

        if (countBeforeShow == 0) {
            window.show();
        }
    }

    /**
     * Informs the user that some required data cannot be recovered. The org
     * unit cannot be created.
     * 
     * @param msg
     *            The alert message.
     */
    private void missingRequiredData(String msg) {

        if (alert) {
            return;
        }

        alert = true;

        MessageBox.alert(I18N.CONSTANTS.adminOrgUnitAddUnavailable(), msg, null);

        window.hide();
    }

    public void addListener(CreateOrgUnitListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Called when an new org unit has been created.
     */
    protected void fireOrgUnitCreated() {
        for (final CreateOrgUnitListener listener : listeners) {
            listener.orgUnitCreated();
        }
    }
}
