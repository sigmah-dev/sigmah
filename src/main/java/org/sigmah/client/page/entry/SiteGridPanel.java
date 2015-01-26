/*
 * All Sigmah code is released under the GNU General Public License v3 See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.event.SiteEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.Shutdownable;
import org.sigmah.client.page.common.grid.SavingHelper;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.util.state.IStateManager;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.GetSchema;
import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.result.BatchResult;
import org.sigmah.shared.command.result.PagingResult;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionProvider;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * Component that provides for listing and editing Site objects.
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class SiteGridPanel extends ContentPanel implements ActionListener, SelectionProvider<SiteDTO> {

    private Listener<SiteEvent> siteChangedListener;
    private Listener<SiteEvent> siteCreatedListener;
    private List<Shutdownable> subComponents = new ArrayList<Shutdownable>();

    public static final int PAGE_SIZE = 25;

    protected final EventBus eventBus;
    protected final Dispatcher service;

    private PagingLoader<SiteResult> loader;
    private ListStore<SiteDTO> store;
    private EditorGrid<SiteDTO> grid;

    private ActionToolBar toolBar;

    private Filter filter;
    private boolean dragSource;

    private Integer siteIdToSelectOnNextLoad;

    private boolean siteUpdated;

    @Inject
    public SiteGridPanel(EventBus eventBus, Dispatcher service, IStateManager stateMgr) {
        this.eventBus = eventBus;
        this.service = service;
        this.siteUpdated = false;

        setLayout(new FitLayout());
        setHeading("Sites");
        initLoader();

        store = new ListStore<SiteDTO>(loader);

        initToolBar();
        initPagingToolBar();

        eventBus.addListener(SiteEvent.CREATED, new Listener<SiteEvent>() {

            @Override
            public void handleEvent(SiteEvent be) {
                if (be.getEntityName().equals("Site")) {
                    onSiteCreated(be);
                }
            }
        });
        eventBus.addListener(SiteEvent.UPDATED, new Listener<SiteEvent>() {

            @Override
            public void handleEvent(SiteEvent be) {
                if (be.getEntityName().equals("Site")) {
                    onSiteUpdated(be);
                }
            }
        });
    }

    /*
     * If true, the site grid will be configured as a Drag and Drop Source
     */
    public void setDragSource(boolean dragSource) {
        dragSource = true;
    }

    /**
     * Indicates if a site was updated
     * 
     * @return true if a site was updated
     */
    public boolean isSiteUpdated() {
        return siteUpdated;
    }

    /**
     * Set the variable siteUpdated
     * 
     * @param siteUpdated
     *            the new value of siteUpdated
     */
    public void setSiteUpdated(boolean siteUpdated) {
        this.siteUpdated = siteUpdated;
    }

    private void initLoader() {
        loader = new BasePagingLoader<SiteResult>(new SiteProxy());
        loader.addLoadListener(new LoadListener() {

            @Override
            public void loaderBeforeLoad(LoadEvent le) {
                // toolBar.setActionEnabled(UIActions.add, currentActivity.getDatabase().isEditAllowed());
                toolBar.setActionEnabled(UIActions.edit, false);
                toolBar.setActionEnabled(UIActions.delete, false);
                if (grid != null) {
                    grid.mask();
                }
            }

            @Override
            public void loaderLoad(LoadEvent le) {
                // set the first item as selected
                if (store.getCount() > 0) {
                    grid.getSelectionModel().setSelection(Collections.singletonList(store.getAt(0)));
                }
                if (grid != null) {
                    grid.unmask();
                }
            }

            @Override
            public void loaderLoadException(LoadEvent le) {
                Log.debug("SiteGridPanel load failed", le.exception);
                if (grid == null) {
                    removeAll();
                    add(new Label(I18N.CONSTANTS.connectionProblem()));
                    layout();
                } else {
                    grid.getView().setEmptyText(I18N.CONSTANTS.connectionProblem());
                    grid.unmask();
                }
                store.removeAll();
            }
        });
    }

    /**
     * Loads the sites using the given filter. This must be called by the container.
     * 
     * @param filter
     */
    public void load(Filter filter) {
        load(filter, Collections.<IndicatorDTO> emptySet());
    }

    /**
     * Loads the sites using the given filter, and including columns for the supplied indicators. This must be called by
     * the container.
     * 
     * @param filter
     * @param indicators
     *            a list of indicators to include as columns
     */
    public void load(Filter filter, Collection<IndicatorDTO> indicators) {
        this.filter = filter;
        this.siteIdToSelectOnNextLoad = 0;
        loader.setOffset(0);
        loader.load();
        new SiteColumnModelBuilder(service, filter, indicators, new AsyncCallback<ColumnModel>() {

            @Override
            public void onSuccess(ColumnModel result) {
                initGrid(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO: what to do??
            }
        });
    }

    /**
     * Gets this grid's ActionToolBar. Consumers of this widget can add additional buttons.
     * 
     * @return this grid's {@link ActionToolBar}
     */
    public ActionToolBar getToolBar() {
        return toolBar;
    }

    /**
     * Creates the {@link ActionToolBar} used as the top component. This can be overriden by subclasses.
     * 
     * @return the toolbar
     */
    private void initToolBar() {
        toolBar = new ActionToolBar();
        toolBar.setListener(this);
        toolBar.addSaveSplitButton();
        toolBar.add(new SeparatorToolItem());

        toolBar.addButton(UIActions.add, I18N.CONSTANTS.newSite(), IconImageBundle.ICONS.add());
        toolBar.addEditButton();
        toolBar.addDeleteButton(I18N.CONSTANTS.deleteSite());

        toolBar.add(new SeparatorToolItem());

        // toolBar.addExcelExportButton();

        setTopComponent(toolBar);
    }

    private void initPagingToolBar() {
        PagingToolBar bar = new PagingToolBar(PAGE_SIZE);
        bar.bind(loader);
        setBottomComponent(bar);
    }

    @Override
    public List<SiteDTO> getSelection() {
        return grid == null ? Collections.<SiteDTO> emptyList() : grid.getSelectionModel().getSelection();
    }

    @Override
    public void addSelectionChangedListener(SelectionChangedListener<SiteDTO> listener) {
        addListener(Events.SelectionChange, listener);
    }

    @Override
    public void removeSelectionListener(SelectionChangedListener<SiteDTO> listener) {
        removeListener(Events.SelectionChange, listener);
    }

    public void addActionListener(Listener<ComponentEvent> listener) {
        toolBar.addListener(Events.Select, listener);
    }

    @Override
    public void setSelection(List<SiteDTO> selection) {
        if (grid != null) {
            grid.getSelectionModel().setSelection(selection);
        }
    }

    /**
     * A GXT grid can't be created without a column model, and since our column model is a function of the filter
     * applied, we delay creating the control until load() is called above.
     * 
     * @param model
     */
    private void initGrid(ColumnModel model) {

        if (grid == null) {
            grid = new EditorGrid<SiteDTO>(store, model);
            grid.setClicksToEdit(ClicksToEdit.TWO);
            grid.addListener(Events.AfterEdit, new Listener<GridEvent<SiteDTO>>() {

                @Override
                public void handleEvent(GridEvent<SiteDTO> event) {
                    siteUpdated = true;
                    toolBar.setDirty(true);
                }
            });

            if (dragSource) {
                new SiteGridDragSource(grid);
            }

            grid.setSelectionModel(new GridSelectionModel<SiteDTO>());
            grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<SiteDTO>() {

                @Override
                public void selectionChanged(SelectionChangedEvent<SiteDTO> se) {
                    onSelectionChanged(se);
                }
            });
            removeAll();
            add(grid);
            layout();
        } else {
            grid.reconfigure(store, model);
        }
    }

    @Override
    public void onUIAction(String actionId) {
        if (UIActions.save.equals(actionId)) {
            save();
        } else if (UIActions.delete.equals(actionId)) {
            delete();
        }
    }

    private void save() {
        toolBar.setSaving();
        // defensive copy, this may change while we're saving
        final List<Record> dirty = new ArrayList<Record>(store.getModifiedRecords());
        service.execute(SavingHelper.createUpdateCommand(store), null, new AsyncCallback<BatchResult>() {

            @Override
            public void onFailure(Throwable caught) {
                Info.display(I18N.CONSTANTS.serverError(), I18N.CONSTANTS.saveError());
                siteUpdated = true;
                toolBar.setDirty(true);
            }

            @Override
            public void onSuccess(BatchResult result) {
                siteUpdated = false;
                toolBar.setDirty(false);
                for (Record record : dirty) {
                    record.commit(false);
                }
                for (Record record : dirty) {
                    eventBus.fireEvent(new SiteEvent(SiteEvent.UPDATED, SiteGridPanel.this, (SiteDTO) record.getModel()));
                }
            }
        });
    }

    private void delete() {
        final SiteDTO site = grid.getSelectionModel().getSelectedItem();
        MessageBox.confirm(I18N.CONSTANTS.deleteSite(), I18N.MESSAGES.confirmDelete(site.getLocationName()),
            new Listener<MessageBoxEvent>() {

                @Override
                public void handleEvent(MessageBoxEvent be) {
                    if (be.getButtonClicked().getItemId().equals("yes")) {
                        service.execute(new Delete(site), new MaskingAsyncMonitor(grid, I18N.CONSTANTS.deleting()),
                            new AsyncCallback<VoidResult>() {

                                @Override
                                public void onFailure(Throwable caught) {
                                    // handled by monitor
                                }

                                @Override
                                public void onSuccess(VoidResult result) {
                                    store.remove(site);
                                }
                            });
                    }
                }
            });
    }

    private void onSiteCreated(SiteEvent se) {
        if (store.getCount() < PAGE_SIZE) {
            // there is only one page, so we can save some time by justing adding this model to directly to
            // the store
            store.add(se.getEntity());
        } else {
            // there are multiple pages and we don't really know where this site is going
            // to end up, so do a reload and seek to the page with the new site
            siteIdToSelectOnNextLoad = se.getEntity().getId();
            loader.load();
        }
    }

    private void onSiteUpdated(SiteEvent se) {
        SiteDTO site = store.findModel("id", se.getSiteId());
        if (site != null) {
            site.setProperties(se.getEntity().getProperties());
            store.update(site);
        }
    }

    public void shutdown() {

        eventBus.removeListener(SiteEvent.UPDATED, siteChangedListener);
        eventBus.removeListener(SiteEvent.CREATED, siteCreatedListener);

        for (Shutdownable subComponet : subComponents) {
            subComponet.shutdown();
        }
    }

    protected void onLoaded(LoadEvent le) {
        PagingResult result = (PagingResult) le.getData();
        toolBar.setActionEnabled(UIActions.export, result.getTotalLength() != 0);

        if (siteIdToSelectOnNextLoad != null) {
            SiteDTO model = store.findModel("id", siteIdToSelectOnNextLoad);
            if (model != null) {
                grid.getSelectionModel().setSelection(Collections.singletonList(model));
            }
            siteIdToSelectOnNextLoad = null;
        }
    }

    public void onSelectionChanged(SelectionChangedEvent<SiteDTO> event) {

        toolBar.setActionEnabled(UIActions.delete, false);
        toolBar.setActionEnabled(UIActions.edit, false);

        if (!event.getSelection().isEmpty()) {
            isEditable(event.getSelectedItem(), new AsyncCallback<Boolean>() {

                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess(Boolean editable) {
                    toolBar.setActionEnabled(UIActions.delete, editable);
                    toolBar.setActionEnabled(UIActions.edit, editable);
                }
            });
        }

        fireEvent(Events.SelectionChange, event);
    }

    private void isEditable(final SiteDTO selectedSite, final AsyncCallback<Boolean> callback) {
        service.execute(new GetSchema(), null, new AsyncCallback<SchemaDTO>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(SchemaDTO result) {
                UserDatabaseDTO db = result.getDatabaseById(selectedSite.getDatabaseId());
                boolean editable =
                        db.isEditAllAllowed()
                            || (db.isEditAllowed() && db.getMyPartnerId() == selectedSite.getPartner().getId());
                callback.onSuccess(editable);
            }
        });
    }

    // TODO: update export module to work for databases to
    // private void onExport() {
    // String url = GWT.getModuleBaseURL() + "export?auth=#AUTH#&a=" + currentActivity.getId();
    // eventBus.fireEvent(new DownloadRequestEvent("siteExport", url));
    // }

    private class SiteProxy extends RpcProxy<SiteResult> {

        @Override
        protected void load(Object loadConfig, AsyncCallback<SiteResult> callback) {

            PagingLoadConfig config = (PagingLoadConfig) loadConfig;
            GetSites cmd = new GetSites();
            cmd.setLimit(config.getLimit());
            cmd.setOffset(config.getOffset());
            cmd.setFilter(filter);
            cmd.setSeekToSiteId(siteIdToSelectOnNextLoad);

            service.execute(cmd, null, callback);
        }
    }
}
