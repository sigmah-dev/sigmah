/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.sigmah.client.AppEvents;
import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.event.SiteEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.Shutdownable;
import org.sigmah.client.page.common.grid.SavingHelper;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.util.state.IStateManager;
import org.sigmah.shared.command.GetSchema;
import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.result.BatchResult;
import org.sigmah.shared.command.result.SiteResult;
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
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionProvider;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
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
 * Component that provides for listing and
 * editing Site objects.
 * 
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class SiteGridPanel extends ContentPanel implements ActionListener, SelectionProvider<SiteDTO> {

	private Listener<SiteEvent> siteChangedListener;
	private Listener<SiteEvent> siteCreatedListener;
	private Listener<SiteEvent> siteSelectedListner;
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
	
	@Inject
	public SiteGridPanel(EventBus eventBus, Dispatcher service, IStateManager stateMgr) {
		this.eventBus = eventBus;
		this.service = service;

		setLayout(new FitLayout());
		setHeading("Sites");
		initLoader();
		
		store = new ListStore<SiteDTO>(loader);
		
		initToolBar();
		initPagingToolBar();
	}
	
	/*
	 * If true, the site grid will be configured as a Drag and Drop Source
	 */
	public void setDragSource(boolean dragSource) {
		dragSource = true;
	}


	private void initLoader() {
		loader = new BasePagingLoader<SiteResult>(new SiteProxy());
		loader.addLoadListener(new LoadListener() {

			@Override
			public void loaderBeforeLoad(LoadEvent le) {
				//toolBar.setActionEnabled(UIActions.add, currentActivity.getDatabase().isEditAllowed());
				toolBar.setActionEnabled(UIActions.edit, false);
				toolBar.setActionEnabled(UIActions.delete, false);
				if(grid != null) {
					grid.mask();
				}
			}
			@Override
			public void loaderLoad(LoadEvent le) {
				// set the first item as selected
				if(store.getCount() > 0) {
					grid.getSelectionModel().setSelection(Collections.singletonList(store.getAt(0)));
				}
				if(grid != null) {
					grid.unmask();
				}
			}
			@Override
			public void loaderLoadException(LoadEvent le) {
				Log.debug("SiteGridPanel load failed", le.exception);
				grid.getView().setEmptyText(I18N.CONSTANTS.connectionProblem());
				store.removeAll();
				if(grid != null) {
					grid.unmask();
				}
			}
		});
	}
	
	
	/**
	 * Loads the sites using the given filter.
	 * This must be called by the container.
	 * 
	 * @param filter
	 */
	public void load(Filter filter) {
		load(filter, Collections.<IndicatorDTO>emptySet());
	}
	
	/**
	 * Loads the sites using the given filter, 
	 * and including columns for the supplied indicators.
	 * 
	 * This must be called by the container.
	 * 
	 * @param filter
	 * @param indicators a list of indicators to include as columns
	 */
	public void load(Filter filter, Collection<IndicatorDTO> indicators) {
		this.filter = filter;
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
	 * 
	 * Gets this grid's ActionToolBar.
	 * Consumers of this widget can add additional buttons.
	 * 
	 * @return this grid's {@link ActionToolBar}
	 */
	public ActionToolBar getToolBar() {
		return toolBar;
	}
	
	protected void initToolBar() {
		toolBar = createToolBar(); 
		assert toolBar != null : "createToolBar() cannot return null.";
		toolBar.setListener(this);

		setTopComponent(toolBar);
	}

	/**
	 * Creates the {@link ActionToolBar} used as the top component.
	 * This can be overriden by subclasses.
	 * 
	 * @return the toolbar
	 */
	protected ActionToolBar createToolBar() {
		ActionToolBar toolBar = new ActionToolBar();
		toolBar.addSaveSplitButton();
		toolBar.add(new SeparatorToolItem());

		toolBar.addButton(UIActions.add, I18N.CONSTANTS.newSite(), IconImageBundle.ICONS.add());
		toolBar.addEditButton();
		toolBar.addDeleteButton(I18N.CONSTANTS.deleteSite());

		toolBar.add(new SeparatorToolItem());

		toolBar.addExcelExportButton();
		
		return toolBar;
	}
	
	private void initPagingToolBar() {
		PagingToolBar bar = new PagingToolBar(PAGE_SIZE);
		bar.bind(loader);
		setBottomComponent(bar);
	}
	
	@Override
	public List<SiteDTO> getSelection() {
		return grid == null ? Collections.<SiteDTO>emptyList() : grid.getSelectionModel().getSelection();
	}

	@Override
	public void addSelectionChangedListener(
			SelectionChangedListener<SiteDTO> listener) {
		addListener(Events.SelectionChange, listener);
	}

	@Override
	public void removeSelectionListener(
			SelectionChangedListener<SiteDTO> listener) {
		removeListener(Events.SelectionChange, listener);
	}
	
	public void addActionListener(Listener<ComponentEvent> listener) {
		toolBar.addListener(Events.Select, listener);
	}

	@Override
	public void setSelection(List<SiteDTO> selection) {
		if(grid != null) {
			grid.getSelectionModel().setSelection(selection);
		}
	} 

	/**
	 * A GXT grid can't be created without a column model, and since 
	 * our column model is a function of the filter applied, we delay
	 * creating the control until load() is called above.
	 * 
	 * @param model
	 */
	private void initGrid(ColumnModel model) {
		
		if(grid == null) {
			grid = new EditorGrid<SiteDTO>(store, model);
			grid.setClicksToEdit(ClicksToEdit.TWO);
			grid.addListener(Events.AfterEdit, new Listener<GridEvent<SiteDTO>>() {
				@Override
				public void handleEvent(GridEvent<SiteDTO> event) {
					toolBar.setDirty(true);
				}
			});
			
			if(dragSource) {
	            new SiteGridDragSource(grid);
	        }
			
			grid.setSelectionModel(new GridSelectionModel<SiteDTO>());
			grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<SiteDTO>() {
				@Override
				public void selectionChanged(SelectionChangedEvent<SiteDTO> se) {
					onSelectionChanged(se);
				}
			});
			
			add(grid);
			layout();
		} else {
			grid.reconfigure(store, model);
		}
	}
	
	@Override
	public void onUIAction(String actionId) {
		if(UIActions.save.equals(actionId)) {
			save();
		} else if(UIActions.add.equals(actionId)) {
			
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
				toolBar.setDirty(true);
			}

			@Override
			public void onSuccess(BatchResult result) {
				toolBar.setDirty(false);
				for(Record record : dirty) {
					record.commit(false);
				}
			}
		});
	}

	private void onSiteCreated(SiteEvent se) {
//		if (store.getCount() < PAGE_SIZE) {
//			// there is only one page, so we can save some time by justing adding this model to directly to
//			//  the store
//			store.add(se.getSite());
//		} else {
//			// there are multiple pages and we don't really know where this site is going
//			// to end up, so do a reload and seek to the page with the new site
//			GetSites cmd = (GetSites) loader.getCommand();
//			cmd.setSeekToSiteId(se.getSite().getId());
//			siteIdToSelectOnNextLoad = se.getSite().getId();
//			loader.load();
//		}
	}


	public void shutdown() {

		eventBus.removeListener(AppEvents.SiteChanged, siteChangedListener);
		eventBus.removeListener(AppEvents.SiteCreated, siteCreatedListener);
		eventBus.removeListener(AppEvents.SiteSelected, siteSelectedListner);

		for (Shutdownable subComponet : subComponents) {
			subComponet.shutdown();
		}
	}



	protected void onLoaded(LoadEvent le) {
//		PagingResult result = (PagingResult) le.getData();
//		view.setActionEnabled(UIActions.export, result.getTotalLength() != 0);
//
//		/*
//		 * Let everyone else know we have navigated
//		 */
//		firePageEvent(new SiteGridPageState(currentActivity), le);
//
//		/*
//		 * Select a site
//		 */
//
//		if (siteIdToSelectOnNextLoad != null) {
//			view.setSelection(siteIdToSelectOnNextLoad);
//			siteIdToSelectOnNextLoad = null;
//		}
	}

	public void onSelectionChanged(SelectionChangedEvent<SiteDTO> event) {

		toolBar.setActionEnabled(UIActions.delete, false);
		toolBar.setActionEnabled(UIActions.edit, false);

		if(!event.getSelection().isEmpty()) {
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

		if(!event.getSelection().isEmpty()) {
			eventBus.fireEvent(new SiteEvent(AppEvents.SiteSelected, this, event.getSelectedItem()));
		}
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
				boolean editable = db.isEditAllAllowed() ||
				(db.isEditAllowed() && db.getMyPartnerId() == selectedSite.getPartner().getId());
				callback.onSuccess(editable);			
			}
		});
	}

////
////	protected void onAdd() {
////
////		SiteDTO newSite = new SiteDTO();
////		newSite.setActivityId(currentActivity.getId());
////
////		if (!currentActivity.getDatabase().isEditAllAllowed()) {
////			newSite.setPartner(currentActivity.getDatabase().getMyPartner());
////		}
////
////		// initialize with defaults
////		SiteDTO sel = view.getSelection();
////		if (sel != null) {
////			for (Map.Entry<String, Object> prop : sel.getProperties().entrySet()) {
////				if (prop.getKey().startsWith(AdminLevelDTO.PROPERTY_PREFIX)) {
////					newSite.set(prop.getKey(), prop.getValue());
////				}
////			}
////		}
////
////		formLoader.edit(currentActivity, newSite, view.getLoadingMonitor());
////
////	}
////
////	protected void onEdit(SiteDTO site) {
////		formLoader.edit(currentActivity, site, view.getLoadingMonitor());
////	}
////
////
////	@Override
////	protected void onDeleteConfirmed(final SiteDTO site) {
////
////		service.execute(new Delete(site), view.getDeletingMonitor(), new AsyncCallback<VoidResult>() {
////
////			public void onFailure(Throwable caught) {
////
////			}
////
////			public void onSuccess(VoidResult result) {
////				store.remove(site);seekToSiteId
////			}
////		});
////	}
//
//	private void onExport() {
//		String url = GWT.getModuleBaseURL() + "export?auth=#AUTH#&a=" + currentActivity.getId();
//		eventBus.fireEvent(new DownloadRequestEvent("siteExport", url));
//	}

	private class SiteProxy extends RpcProxy<SiteResult> {

		@Override
		protected void load(Object loadConfig,
				AsyncCallback<SiteResult> callback) {

			PagingLoadConfig config = (PagingLoadConfig)loadConfig;
			GetSites cmd = new GetSites();
			cmd.setLimit(config.getLimit());
			cmd.setOffset(config.getOffset());
			cmd.setFilter(filter);
			
			service.execute(cmd, null, callback);
		}
	}
}
