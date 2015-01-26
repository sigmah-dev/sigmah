package org.sigmah.client.ui.view.project.indicator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.sigmah.client.event.SiteEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.result.PagingResult;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.command.result.VoidResult;
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
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SelectionProvider;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.handler.SiteHandler;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.project.indicator.SavingHelper;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.button.SplitButton;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.toolbar.ActionToolBar;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;
import org.sigmah.shared.util.Filter;

/**
 * Component that provides for listing and editing Site objects.
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class SiteGridPanel extends ContentPanel implements SelectionProvider<SiteDTO> {

	private HandlerRegistration handlerRegistration;

    public static final int PAGE_SIZE = 25;

	@Inject
    private EventBus eventBus;
	
	@Inject
    private DispatchAsync service;
	
	@Inject
	private AuthenticationProvider authenticationProvider;

    private PagingLoader<SiteResult> loader;
    private final ListStore<SiteDTO> store;
    private EditorGrid<SiteDTO> grid;

    private ToolBar toolBar;
	private SplitButton saveButton;
	private Button manageMainSiteButton;
	private Button newSiteButton;
	private Button editButton;
	private Button deleteButton;

    private Filter filter;

    private Integer siteIdToSelectOnNextLoad;
    private boolean siteUpdated;
    private Integer mainSiteId;
	
	private SchemaDTO schema;

    public SiteGridPanel() {
        setLayout(new FitLayout());
        setHeadingText(I18N.CONSTANTS.sites());
        initLoader();

        store = new ListStore<SiteDTO>(loader);

        initToolBar();
        initPagingToolBar();
    }
	
	public void initialize() {
		handlerRegistration = eventBus.addHandler(SiteEvent.getType(), new SiteHandler() {

			@Override
			public void handleEvent(SiteEvent siteEvent) {
				switch(siteEvent.getAction()) {
					case CREATED:
						onSiteCreated(siteEvent);
						break;
					case UPDATED:
						onSiteUpdated(siteEvent);
						break;
					case DELETED:
						onSiteDeleted(siteEvent);
						break;
					case MAIN_SITE_CREATED:
						onMainSiteCreated(siteEvent);
						break;
					case MAIN_SITE_UPDATED:
						onMainSiteUpdated(siteEvent);
						break;
				}
			}
		});
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

	public void setSchema(SchemaDTO schema) {
		this.schema = schema;
	}

    private void initLoader() {
        loader = new BasePagingLoader<SiteResult>(new SiteProxy());
        loader.addLoadListener(new LoadListener() {

            @Override
            public void loaderBeforeLoad(LoadEvent le) {
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
     * Loads the sites using the given filter and with the given mainSite. 
     * This must be called by the container.
     * 
     * @param filter
	 * @param mainSiteId
     */
    public void load(Filter filter, Integer mainSiteId) {
    	this.mainSiteId = mainSiteId;
    	
		manageMainSiteButton.setEnabled(ProfileUtils.isGranted(authenticationProvider.get(), GlobalPermissionEnum.MANAGE_MAIN_SITE));
		
		// Disable creating sites until a main site is created.
		newSiteButton.setEnabled(canManageSites() && mainSiteId != null);
		
		// By default the label is "Create". If the project has a main site, use
    	// the "Edit" label for this button.
		manageMainSiteButton.setText(mainSiteId != null ?
			I18N.CONSTANTS.editMainSiteButton() : I18N.CONSTANTS.newSite());
		
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
    private void load(Filter filter, Collection<IndicatorDTO> indicators) {
        this.filter = filter;
        this.siteIdToSelectOnNextLoad = 0;
        loader.setOffset(0);
        loader.load();

		final SiteColumnModelBuilder siteColumnModelBuilder = new SiteColumnModelBuilder(schema, filter, this.mainSiteId, indicators, new AsyncCallback<ColumnModel>() {

			@Override
			public void onSuccess(ColumnModel result) {
				initGrid(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO: Write a more specific error message.
				N10N.error(I18N.CONSTANTS.errorOnServer());
			}
		});
		
		siteColumnModelBuilder.buildColumnModel();
    }
	
	/**
	 * Clear the content of the grid.
	 */
	public void clear() {
		store.removeAll();
		editButton.setEnabled(false);
		deleteButton.setEnabled(false);
	}
   

    /**
     * Gets this grid's ActionToolBar. Consumers of this widget can add additional buttons.
     * 
     * @return this grid's {@link ActionToolBar}
     */
    public ToolBar getToolBar() {
        return toolBar;
    }

	public Button getNewSiteButton() {
		return newSiteButton;
	}

	public Button getManageMainSiteButton() {
		return manageMainSiteButton;
	}
	
	public Button getEditButton() {
		return editButton;
	}

    /**
     * Creates the {@link ActionToolBar} used as the top component. This can be overriden by subclasses.
     * 
     * @return the toolbar
     */
    private void initToolBar() {
        toolBar = new ToolBar();

		// Save button.
		saveButton = Forms.saveSplitButton();
		final MenuItem saveItem = (MenuItem) saveButton.getMenu().getItem(0);
		final MenuItem discardChangesItem = (MenuItem) saveButton.getMenu().getItem(1);
		
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				save();
			}
		});
		
		saveItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				save();
			}
		});
		
		discardChangesItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				grid.getStore().rejectChanges();
			}
		});
		
        // Manage main site button.
		manageMainSiteButton = Forms.button(I18N.CONSTANTS.createMainSiteButton(), IconImageBundle.ICONS.mainSite());
		
		manageMainSiteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				autoSelectMainSite();
			}
		});
		
		// New site button.
		newSiteButton = Forms.button(I18N.CONSTANTS.newSite(), IconImageBundle.ICONS.add());
		
		// Edit button.
		editButton = Forms.button(I18N.CONSTANTS.edit(), IconImageBundle.ICONS.editPage());
		
		// Delete button.
		deleteButton = Forms.button(I18N.CONSTANTS.deleteSite(), IconImageBundle.ICONS.delete());

		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				delete();
			}
		});
		
		// Adding the buttons to the toolbar.
		toolBar.add(saveButton);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(manageMainSiteButton);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(newSiteButton);
		toolBar.add(editButton);
		toolBar.add(deleteButton);

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
                    saveButton.setEnabled(true);

                }
            });

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

    private void save() {
        // defensive copy, this may change while we're saving
        final List<Record> dirty = new ArrayList<Record>(store.getModifiedRecords());
		
        service.execute(SavingHelper.createUpdateCommand(store), new CommandResultHandler<ListResult<Result>>() {

			@Override
			protected void onCommandFailure(Throwable caught) {
				siteUpdated = true;    
                saveButton.setEnabled(true);
			}

			@Override
			protected void onCommandSuccess(ListResult<Result> result) {
                saveButton.setEnabled(false);
                
                for (Record record : dirty) {
                    record.commit(false);
                }
                for (Record record : dirty) {
                    eventBus.fireEvent(new SiteEvent(SiteEvent.Action.UPDATED, SiteGridPanel.this, (SiteDTO) record.getModel()));
                }
            }
        });
    }

    private void delete() {
        final SiteDTO site = grid.getSelectionModel().getSelectedItem();
		N10N.confirmation(I18N.CONSTANTS.deleteSite(), I18N.MESSAGES.confirmDelete(site.getLocationName()), new ConfirmCallback() {

			@Override
			public void onAction() {
				service.execute(new Delete(site),
					new AsyncCallback<VoidResult>() {

						@Override
						public void onFailure(Throwable caught) {
							// handled by monitor
						}

						@Override
						public void onSuccess(VoidResult result) {
							store.remove(site);
							eventBus.fireEvent(new SiteEvent(SiteEvent.Action.DELETED, SiteGridPanel.this, site));
						}
				}, new LoadingMask(grid, I18N.CONSTANTS.deleting()));
			}
		});
    }
    
    private void autoSelectMainSite() {
    	if (this.mainSiteId != null) {
    		SiteDTO model = store.findModel("id", this.mainSiteId);
            if (model != null) {
                grid.getSelectionModel().setSelection(Collections.singletonList(model));
            }
    	}
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
    
    private void onSiteDeleted(SiteEvent se) {
        SiteDTO site = store.findModel("id", se.getSiteId());
        if (site != null) {
        	store.remove(site);
        }
    }

    private void onMainSiteCreated(SiteEvent se) {
    	this.mainSiteId = se.getEntity().getId();
    	
    	// reload the grid 
    	load(filter, mainSiteId);
    	
    	// By default the label is "Create". If the user creates a main site, use
    	// the "Edit" label for this button.
    	if (mainSiteId != null) {
			manageMainSiteButton.setText(I18N.CONSTANTS.editMainSiteButton());
			newSiteButton.setEnabled(canManageSites());
    	} 
    	
        onSiteCreated(se);
    }

    private void onMainSiteUpdated(SiteEvent se) {
    	this.mainSiteId = se.getEntity().getId();
    	onSiteUpdated(se);
    }
    
    public void shutdown() {
		handlerRegistration.removeHandler();
    }

    protected void onLoaded(LoadEvent le) {
        PagingResult result = (PagingResult) le.getData();
        

//        toolBar.setActionEnabled(UIActions.export, result.getTotalLength() != 0);

        
        if (siteIdToSelectOnNextLoad != null) {
            SiteDTO model = store.findModel("id", siteIdToSelectOnNextLoad);
            if (model != null) {
                grid.getSelectionModel().setSelection(Collections.singletonList(model));
            }
            siteIdToSelectOnNextLoad = null;
        }
    }

    public void onSelectionChanged(SelectionChangedEvent<SiteDTO> event) {

		// Enable toolbar buttons for selection
		deleteButton.setEnabled(false);
		editButton.setEnabled(false);

    	
		if (!event.getSelection().isEmpty()) {
			// Disable the toolbar for the main site
			// It should be handled only through the Set main site button
			// from project > map

			if (this.mainSiteId == null
					|| !this.mainSiteId.equals(event.getSelectedItem().getId())) {

				// Not the main site, check editable
				if(canManageSites()) {
					deleteButton.setEnabled(isEditable(event.getSelectedItem()));
					editButton.setEnabled(isEditable(event.getSelectedItem()));
				}
			}
		}

		fireEvent(Events.SelectionChange, event);
    }

    private boolean isEditable(final SiteDTO selectedSite) {
		final UserDatabaseDTO db = schema.getDatabaseById(selectedSite.getDatabaseId());
		
		return db.isEditAllAllowed() || 
			(db.isEditAllowed() && db.getMyPartnerId() == selectedSite.getPartner().getId());
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

            service.execute(cmd, callback);
        }
    }
    
    private boolean canManageSites() {
    	return (ProfileUtils.isGranted(authenticationProvider.get(), GlobalPermissionEnum.MANAGE_SITES));
    }
}
