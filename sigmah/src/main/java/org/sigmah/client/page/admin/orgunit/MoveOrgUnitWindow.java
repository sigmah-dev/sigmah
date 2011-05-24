package org.sigmah.client.page.admin.orgunit;

import java.util.ArrayList;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.MoveOrgUnit;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.OrgUnitDTOLight;

import com.allen_sauer.gwt.log.client.Log;
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
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MoveOrgUnitWindow {

    /**
     * Listener.
     * 
     * @author tmi
     * 
     */
    public static interface MoveOrgUnitListener {

        /**
         * Called when an new org unit has been moved.
         */
        public void orgUnitMoved();
    }

    private final Dispatcher dispatcher;
    private final UserLocalCache cache;
    private final ArrayList<MoveOrgUnitListener> listeners;

    private final FormPanel formPanel;
    private final Window window;

    private final ComboBox<OrgUnitDTOLight> orgUnitsField;
    private final ListStore<OrgUnitDTOLight> orgUnitsStore;
    private final HiddenField<String> unitField;

    /**
     * Counter to wait that required data are loaded before showing the window.
     */
    private int countBeforeShow;

    /**
     * Flag to display only one alert message.
     */
    private boolean alert = false;

    public MoveOrgUnitWindow(Dispatcher dispatcher, UserLocalCache cache) {

        this.dispatcher = dispatcher;
        this.cache = cache;
        listeners = new ArrayList<MoveOrgUnitWindow.MoveOrgUnitListener>();

        // Models list.
        orgUnitsField = new ComboBox<OrgUnitDTOLight>();
        orgUnitsField.setFieldLabel(I18N.CONSTANTS.orgunit());
        orgUnitsField.setAllowBlank(false);
        orgUnitsField.setValueField("id");
        orgUnitsField.setDisplayField("completeName");
        orgUnitsField.setEditable(true);
        orgUnitsField.setEmptyText(I18N.CONSTANTS.orgunitEmptyChoice());
        orgUnitsField.setTriggerAction(TriggerAction.ALL);

        // Org units list store.
        orgUnitsStore = new ListStore<OrgUnitDTOLight>();
        orgUnitsStore.addListener(Events.Add, new Listener<StoreEvent<OrgUnitDTOLight>>() {

            @Override
            public void handleEvent(StoreEvent<OrgUnitDTOLight> be) {
                orgUnitsField.setEnabled(true);
            }
        });

        orgUnitsStore.addListener(Events.Clear, new Listener<StoreEvent<OrgUnitDTOLight>>() {

            @Override
            public void handleEvent(StoreEvent<OrgUnitDTOLight> be) {
                orgUnitsField.setEnabled(false);
            }
        });

        orgUnitsField.setStore(orgUnitsStore);

        // Parent id.
        unitField = new HiddenField<String>();
        unitField.setName("parentId");

        // Create button.
        final Button createButton = new Button(I18N.CONSTANTS.adminOrgUnitMove());
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

        formPanel.add(orgUnitsField);
        formPanel.add(unitField);
        formPanel.addButton(createButton);

        // Main window panel.
        final VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setLayout(new FitLayout());
        mainPanel.add(formPanel);
        mainPanel.setAutoHeight(true);

        // Window.
        window = new Window();
        window.setHeading(I18N.CONSTANTS.adminOrgUnitMove());
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

        dispatcher.execute(new MoveOrgUnit(Integer.parseInt(unitField.getValue()), orgUnitsField.getValue().getId()),
                null, new AsyncCallback<VoidResult>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        MessageBox.alert(I18N.CONSTANTS.adminOrgUnitMoveFailed(),
                                I18N.CONSTANTS.adminOrgUnitMoveFailedDetails(), null);
                    }

                    @Override
                    public void onSuccess(VoidResult result) {
                        fireOrgUnitMoved();
                        window.hide();
                    }
                });
    }

    public void show(int id) {

        // Reset.
        orgUnitsField.reset();
        orgUnitsStore.removeAll();
        unitField.setValue(String.valueOf(id));

        // There are three remote calls
        countBeforeShow = 1;
        alert = false;

        // Retrieves the units.
        cache.getOrganizationCache().get(new AsyncCallback<OrgUnitDTOLight>() {

            @Override
            public void onSuccess(OrgUnitDTOLight result) {
                fillOrgUnitsList(result);

                if (orgUnitsStore.getCount() == 0) {
                    Log.error("[show] No available org unit.");
                    missingRequiredData(I18N.CONSTANTS.adminOrgUnitMoveMissingUnit());
                    return;
                }

                countBeforeShow();
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("[show] Error while getting the org units.", caught);
                missingRequiredData(I18N.CONSTANTS.adminOrgUnitMoveMissingUnit());
            }
        });
    }

    /**
     * Fills combobox with the children of the given root org units.
     * 
     * @param root
     *            The root org unit.
     */
    private void fillOrgUnitsList(OrgUnitDTOLight root) {

        for (final OrgUnitDTOLight child : root.getChildrenDTO()) {
            recursiveFillOrgUnitsList(child);
        }
    }

    /**
     * Fills recursively the combobox from the given root org unit.
     * 
     * @param root
     *            The root org unit.
     */
    private void recursiveFillOrgUnitsList(OrgUnitDTOLight root) {

        orgUnitsStore.add(root);

        for (final OrgUnitDTOLight child : root.getChildrenDTO()) {
            recursiveFillOrgUnitsList(child);
        }
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

        MessageBox.alert(I18N.CONSTANTS.adminOrgUnitMoveUnavailable(), msg, null);

        window.hide();
    }

    public void addListener(MoveOrgUnitListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Called when an new org unit has been moved.
     */
    protected void fireOrgUnitMoved() {
        for (final MoveOrgUnitListener listener : listeners) {
            listener.orgUnitMoved();
        }
    }
}
