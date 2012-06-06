/*
 * All Sigmah code is released under the GNU General Public License v3 See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.event.IndicatorEvent;
import org.sigmah.client.event.IndicatorEvent.ChangeType;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogImpl;
import org.sigmah.client.page.common.grid.ImprovedCellTreeGridSelectionModel;
import org.sigmah.client.page.common.grid.SavingHelper;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.entry.IndicatorNumberFormats;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.DND;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.dnd.TreeGridDropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treegrid.CellTreeGridSelectionModel;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class DesignPanel extends DesignPanelBase implements ActionListener {

    private Provider<IndicatorDialog> indicatorDialog;

    private int currentDatabaseId;
    private boolean indicatorUpdated;

    private MappedIndicatorSelection mappedIndicator;

    @Inject
    public DesignPanel(EventBus eventBus, Dispatcher dispatcher, Provider<IndicatorDialog> indicatorDialog) {
        super(eventBus, dispatcher);
        treeStore = new TreeStore<ModelData>();
        treeStore.setKeyProvider(new ModelKeyProvider<ModelData>() {

            @Override
            public String getKey(ModelData model) {
                if (model instanceof IndicatorGroup) {
                    return "group" + model.get("id");
                } else {
                    return "i" + model.get("id");
                }
            }
        });
        this.indicatorDialog = indicatorDialog;

        DesignPanelResources.INSTANCE.css().ensureInjected();

        setLayout(new FitLayout());

        mappedIndicator = new MappedIndicatorSelection();

        // setup grid
        treeGrid = new EditorTreeGrid<ModelData>(treeStore, createColumnModel()) {

            @Override
            protected boolean hasChildren(ModelData model) {
                return model instanceof IndicatorGroup;
            }
        };
        treeGrid.setSelectionModel(new ImprovedCellTreeGridSelectionModel<ModelData>());
        treeGrid.setClicksToEdit(EditorGrid.ClicksToEdit.TWO);
        treeGrid.setAutoExpandColumn("name");
        treeGrid.setHideHeaders(false);
        treeGrid.setLoadMask(true);

        treeGrid.getStyle().setNodeCloseIcon(null);
        treeGrid.getStyle().setNodeOpenIcon(null);
        treeGrid.getStyle().setLeafIcon(null);
        // cell click listener
        treeGrid.addListener(Events.CellClick, new Listener<GridEvent>() {

            public void handleEvent(GridEvent ge) {
                switch (DesignTreeGridCellRenderer.computeTarget(ge)) {
                    case LABEL:
                        showIndicatorForm((IndicatorDTO) ge.getModel());
                        break;
                    case MAP_ICON:
                        mapSelectionChanged((IndicatorDTO) ge.getModel());
                        break;
                    case STAR_ICON:
                        starChanged((IndicatorDTO) ge.getModel());
                        break;
                }
            }
        });
        treeGrid.addListener(Events.BeforeEdit, new Listener<GridEvent>() {

            @Override
            public void handleEvent(GridEvent be) {
                if (be.getModel() instanceof IndicatorGroup && be.getColIndex() > 0) {
                    // only the first cell of an indicator group is editable
                    be.setCancelled(true);
                }
            }
        });
        treeGrid.addListener(Events.AfterEdit, new Listener<GridEvent>() {

            @Override
            public void handleEvent(GridEvent be) {
                indicatorUpdated = true;
                if (be.getColIndex() == 0 && be.getModel() instanceof IndicatorGroup) {
                    onGroupRenamed((IndicatorGroup) be.getModel());
                }
            }
        });

        // Setup context menu

        TreeGridDragSource source = new TreeGridDragSource(treeGrid);
        source.addDNDListener(new DNDListener() {

            @Override
            public void dragStart(DNDEvent e) {
                ModelData sel = ((CellTreeGridSelectionModel) treeGrid.getSelectionModel()).getSelectCell().model;

                if (db != null && (!db.isDesignAllowed() || sel == null || sel instanceof Folder)) {
                    e.setCancelled(true);
                    e.getStatus().setStatus(false);
                    return;
                }
                super.dragStart(e);
            }
        });

        TreeGridDropTarget target = new TreeGridDropTarget(treeGrid);
        target.setAllowSelfAsSource(true);
        target.setFeedback(DND.Feedback.BOTH);
        target.setAutoExpand(false);
        target.addDNDListener(new DNDListener() {

            @Override
            public void dragMove(DNDEvent e) {
                List<TreeModel> sourceData = e.getData();
                ModelData source = sourceData.get(0).get("model");
                TreeGrid.TreeNode target = treeGrid.findNode(e.getTarget());
                ModelData targetParent = treeStore.getParent(target.getModel());

                // Indicator Groups cannot be nested for the moment
                if (source instanceof IndicatorGroup && targetParent != null) {
                    e.setCancelled(true);
                    e.getStatus().setStatus(false);

                    // Indicator cannot be children of other indicators
                } else if (source instanceof IndicatorDTO && targetParent instanceof IndicatorDTO) {
                    e.setCancelled(true);
                    e.getStatus().setStatus(false);
                }

            }

            @Override
            public void dragDrop(DNDEvent e) {
                List<TreeModel> sourceData = e.getData();
                ModelData source = sourceData.get(0).get("model");
                onNodeDropped(source);
            }
        });
        add(treeGrid);

        toolBar.setListener(this);
        toolBar.addButton("newIndicatorGroup", I18N.CONSTANTS.newIndicatorGroup(), null);
        toolBar.addButton("newIndicator", I18N.CONSTANTS.newIndicator(), null);
        toolBar.addButton("delete", I18N.CONSTANTS.delete(), null);
        toolBar.addRefreshButton();

        eventBus.addListener(IndicatorEvent.CHANGED, new Listener<IndicatorEvent>() {

            @Override
            public void handleEvent(IndicatorEvent be) {
                if (be.getSource() != DesignPanel.this) {
                    doLoad();
                }
            }
        });
    }

    public boolean isIndicatorUpdated() {
        return indicatorUpdated;
    }

    public void setIndicatorUpdated(boolean indicatorUpdated) {
        this.indicatorUpdated = indicatorUpdated;
    }

    @Override
    public void onUIAction(String actionId) {
        if (UIActions.save.equals(actionId)) {
            SavingHelper.save(service, treeStore, this);
            indicatorUpdated = false;

        } else if (UIActions.refresh.equals(actionId)) {
            doLoad();

        } else if ("newIndicator".equals(actionId)) {
            onNewIndicator();

        } else if ("newIndicatorGroup".equals(actionId)) {
            onNewIndicatorGroup();

        } else if ("delete".equals(actionId)) {
            onDelete();
        }
    }

    @Override
    protected void onNodeDropped(ModelData source) {
        // update sortOrder
        ModelData newParent = treeStore.getParent(source);
        if (source instanceof IndicatorDTO && newParent instanceof IndicatorGroup) {
            IndicatorDTO indicator = (IndicatorDTO) source;
            IndicatorGroup group = (IndicatorGroup) newParent;
            treeStore.getRecord(indicator).set("groupId", group.getId());
            indicatorUpdated = true;
        }
        renumberRecursively(treeStore.getRootItems(), 1);
    }

    private int renumberRecursively(List<ModelData> list, int index) {
        for (ModelData child : list) {
            if (child instanceof IndicatorDTO) {
                treeStore.getRecord(child).set("sortOrder", index++);
            } else if (child instanceof IndicatorGroup) {
                index = renumberRecursively(treeStore.getChildren(child), index);
            }
        }
        return index;
    }

    protected void starChanged(IndicatorDTO model) {
        GWT.log("Star clicked");
    }

    protected void mapSelectionChanged(IndicatorDTO model) {
        GWT.log("Map clicked");
        if (mappedIndicator.getValue() == null || mappedIndicator.getValue().getId() != model.getId()) {
            mappedIndicator.setValue(model);
            treeGrid.getView().refresh(false);
        }
    }

    /**
     * Loads the indicators for the given databaseId/projectId
     * 
     * @param databaseId
     */
    public void load(int databaseId) {
        this.currentDatabaseId = databaseId;
        treeGrid.setStateful(true);
        treeGrid.setStateId("indicatorDesign" + databaseId);
        doLoad();
    }

    @Override
    protected void doLoad() {
        service.execute(GetIndicators.forDatabase(currentDatabaseId), null, new AsyncCallback<IndicatorListResult>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO
            }

            @Override
            public void onSuccess(IndicatorListResult result) {
                treeStore.removeAll();

                for (IndicatorGroup group : result.getGroups()) {
                    treeStore.add(group, false);
                    for (IndicatorDTO indicator : group.getIndicators()) {
                        treeStore.add(group, indicator, false);
                    }
                }

                if (!result.getUngroupedIndicators().isEmpty()) {
                    treeStore.add((List) result.getUngroupedIndicators(), false);
                }

                onLoaded();
            }

        });
    }

    private void onLoaded() {
        // set the first indicator as the mapped indicator
        IndicatorDTO first = getFirstIndicator();
        if (first != null) {
            mappedIndicator.setValue(first, true);
            treeGrid.getView().refresh(false);
        }
    }

    private IndicatorDTO getFirstIndicator() {
        for (ModelData model : treeStore.getAllItems()) {
            if (model instanceof IndicatorDTO) {
                return (IndicatorDTO) model;
            }
        }
        return null;
    }

    private void onNewIndicator() {
        final IndicatorDTO newIndicator = new IndicatorDTO();
        newIndicator.setCollectIntervention(true);
        newIndicator.setAggregation(IndicatorDTO.AGGREGATE_SUM);
        newIndicator.set("databaseId", currentDatabaseId);
        newIndicator.setDirectDataEntryEnabled(true);

        final IndicatorGroup parent = computeIndicatorParent(newIndicator);
        if (parent != null) {
            newIndicator.setCategory(parent.getName());
        }
        final IndicatorForm form = new IndicatorForm(service);
        form.getBinding().bind(newIndicator);
        form.setIdVisible(false);

        final FormDialogImpl<IndicatorForm> dialog = new FormDialogImpl<IndicatorForm>(form);
        dialog.setHeading(I18N.CONSTANTS.newIndicator());
        dialog.setWidth(form.getPreferredDialogWidth());
        dialog.setHeight(form.getPreferredDialogHeight());
        dialog.setScrollMode(Style.Scroll.AUTOY);
        dialog.show(new FormDialogCallback() {

            @Override
            public void onValidated() {
                service.execute(new CreateEntity(newIndicator), dialog, new AsyncCallback<CreateResult>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        // handled by dialog
                    }

                    @Override
                    public void onSuccess(CreateResult result) {
                        dialog.hide();
                        treeStore.add(parent, newIndicator, false);
                        eventBus.fireEvent(new IndicatorEvent(IndicatorEvent.CHANGED, DesignPanel.this));
                        doLoad();
                    }
                });
            }
        });
    }

    private IndicatorGroup computeIndicatorParent(final IndicatorDTO newIndicator) {
        ModelData sel = getSelection();
        if (sel != null) {
            if (sel instanceof IndicatorGroup) {
                return (IndicatorGroup) sel;

            } else if (sel instanceof IndicatorDTO) {
                return (IndicatorGroup) treeStore.getParent(sel);
            }
        }
        return null;
    }

    private void onNewIndicatorGroup() {
        final IndicatorGroup group = new IndicatorGroup();
        IndicatorGroupForm form = new IndicatorGroupForm();
        form.getBinding().bind(group);

        final FormDialogImpl<IndicatorGroupForm> dialog = new FormDialogImpl<IndicatorGroupForm>(form);
        dialog.setHeading(I18N.CONSTANTS.newIndicatorGroup());
        dialog.setWidth(form.getPreferredDialogWidth());
        dialog.setHeight(form.getPreferredDialogHeight());
        dialog.setScrollMode(Style.Scroll.AUTOY);
        dialog.show(new FormDialogCallback() {

            @Override
            public void onValidated() {

                service.execute(CreateEntity.IndicatorGroup(currentDatabaseId, group), null,
                    new AsyncCallback<CreateResult>() {

                        @Override
                        public void onFailure(Throwable caught) {
                        }

                        @Override
                        public void onSuccess(CreateResult result) {
                            dialog.hide();
                            doLoad();
                        }

                    });
            }
        });
    }

    private void onDelete() {
        final ModelData selected = treeGrid.getSelectionModel().getSelectedItem();
        if (selected instanceof IndicatorDTO) {
            MessageBox.confirm(I18N.CONSTANTS.delete(), I18N.CONSTANTS.confirmDeleteIndicator(),
                new Listener<MessageBoxEvent>() {

                    @Override
                    public void handleEvent(MessageBoxEvent be) {
                        if (be.getButtonClicked().getItemId().equals("yes")) {
                            deleteIndicator((IndicatorDTO) selected);
                        }
                    }
                });
        } else if (selected instanceof IndicatorGroup) {
            deleteIndicatorGroup((IndicatorGroup) selected);
        }

    }

    private void deleteIndicatorGroup(IndicatorGroup selected) {
        List<ModelData> children = treeStore.getChildren(selected);
        treeStore.remove(selected);
        treeStore.getRecord(selected).set("isDeleted", true);
        // we don't delete the indicators, just move them out of the group
        for (ModelData child : children) {
            treeStore.add(child, false);
            treeStore.getRecord(child).set("groupId", null);
        }
    }

    private void deleteIndicator(final IndicatorDTO selected) {
        service.execute(new Delete(selected), new MaskingAsyncMonitor(this, I18N.CONSTANTS.deleting()),
            new AsyncCallback<VoidResult>() {

                @Override
                public void onFailure(Throwable caught) {
                    // handled by monitor
                }

                @Override
                public void onSuccess(VoidResult result) {
                    treeGrid.getTreeStore().remove(selected);

                    IndicatorEvent event = new IndicatorEvent(IndicatorEvent.CHANGED, DesignPanel.this);
                    event.setEntityId(selected.getId());
                    event.setChangeType(ChangeType.DELETED);

                    eventBus.fireEvent(event);
                }
            });
    }

    private void onGroupRenamed(IndicatorGroup model) {
        List<ModelData> children = treeStore.getChildren(model);
        for (ModelData child : children) {
            treeStore.getRecord(child).set("category", model.getName());
        }
    }

    private void showIndicatorForm(IndicatorDTO model) {
        IndicatorDialog dialog = indicatorDialog.get();
        dialog.show(currentDatabaseId, model);
    }

    protected ColumnModel createColumnModel() {
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        TextField<String> nameEditor = new TextField<String>();
        nameEditor.setAllowBlank(false);

        ColumnConfig nameColumn = new ColumnConfig("name", I18N.CONSTANTS.name(), 150);
        nameColumn.setRenderer(new DesignTreeGridCellRenderer(mappedIndicator));
        nameColumn.setEditor(new CellEditor(nameEditor));
        columns.add(nameColumn);

        ColumnConfig codeColumn = new ColumnConfig("code", I18N.CONSTANTS.code(), 75);
        codeColumn.setEditor(new CellEditor(new TextField<String>()));
        columns.add(codeColumn);

        ColumnConfig objectiveColumn = new ColumnConfig("objective", I18N.CONSTANTS.targetValue(), 75);
        objectiveColumn.setRenderer(new IndicatorObjectiveValueRenderer());
        objectiveColumn.setEditor(new CellEditor(new NumberField()));
        objectiveColumn.setAlignment(HorizontalAlignment.RIGHT);
        columns.add(objectiveColumn);

        ColumnConfig valueColumn = new ColumnConfig("currentValue", I18N.CONSTANTS.value(), 75);
        valueColumn.setRenderer(new CurrentIndicatorValueRenderer());
        valueColumn.setAlignment(HorizontalAlignment.RIGHT);
        columns.add(valueColumn);

        return new ColumnModel(columns);
    }

    private String formatIndicatorValue(ModelData model, String property) {
        Double value = model.get(property);
        if (value != null) {
            return IndicatorNumberFormats.forIndicator((IndicatorDTO) model).format(value);
        } else {
            return "";
        }
    }

    private class IndicatorObjectiveValueRenderer implements GridCellRenderer {

        @Override
        public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                ListStore store, Grid grid) {

            if (model instanceof IndicatorDTO) {
                return formatIndicatorValue(model, property);
            } else {
                return "";
            }
        }
    }

    private class CurrentIndicatorValueRenderer extends IndicatorObjectiveValueRenderer {

        @Override
        public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                ListStore store, Grid grid) {
            if (model instanceof IndicatorDTO) {
                IndicatorDTO indicator = (IndicatorDTO) model;
                if (indicator.getLabelCounts() != null) {
                    return indicator.formatMode();
                } else {
                    return formatIndicatorValue(model, property);
                }
            } else {
                return "";
            }
        }
    }

    public MappedIndicatorSelection getMappedIndicator() {
        return mappedIndicator;
    }

}
