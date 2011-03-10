package org.sigmah.client.page.config.design;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sigmah.client.AppEvents;
import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogTether;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.grid.ImprovedCellTreeGridSelectionModel;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.DND;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.dnd.TreeGridDropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.treegrid.CellTreeGridSelectionModel;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.inject.Inject;

public abstract class DesignPanelBase extends ContentPanel {

	protected final Dispatcher service;
	protected final EventBus eventBus;
	protected TreeStore<ModelData> treeStore;
	protected UserDatabaseDTO db;
	protected ActionToolBar toolBar;
	protected EditorTreeGrid<ModelData> treeGrid;
	private DesignFormContainer formContainer;

	@Inject
	protected DesignPanelBase(EventBus eventBus, Dispatcher service){
		this.service = service;
		this.eventBus = eventBus;
		this.toolBar = new ActionToolBar();
		setTopComponent(toolBar);
	}
	
	public AsyncMonitor getDeletingMonitor() {
		return new MaskingAsyncMonitor(this, I18N.CONSTANTS.deleting());
	}

	public AsyncMonitor getSavingMonitor() {
		return new MaskingAsyncMonitor(this, I18N.CONSTANTS.saving());
	}
	
	public ActionToolBar getToolbar() {
		return this.toolBar;
	}

	public void confirmDeleteSelected(ConfirmCallback callback) {
		callback.confirmed();
	}
	

	protected void doLayout(UserDatabaseDTO db) {
		this.db = db;
		toolBar.setDirty(false);
		treeGrid = (EditorTreeGrid<ModelData>) createGridAndAddToContainer(treeStore);
		if (this.isRendered()) {
			this.layout();
		}
		// setLayout(new BorderLayout());
		setIcon(IconImageBundle.ICONS.design());
		formContainer = new DesignFormContainer(service, db, treeGrid);
		setHeading(I18N.CONSTANTS.design() + " - " + db.getFullName());
	}
	
	
	protected ModelData getSelection() {
		GridSelectionModel<ModelData> sm = treeGrid.getSelectionModel();
		if (sm instanceof CellSelectionModel) {
			CellSelectionModel<ModelData>.CellSelection cell = ((CellSelectionModel<ModelData>) sm)
					.getSelectCell();
			return cell == null ? null : cell.model;
		} else {
			return sm.getSelectedItem();
		}
	}

	
	private Grid<ModelData> createGridAndAddToContainer(Store store) {
		final TreeStore treeStore = (TreeStore) store;
		treeGrid = new EditorTreeGrid<ModelData>(treeStore, createColumnModel());
		treeGrid.setSelectionModel(new ImprovedCellTreeGridSelectionModel<ModelData>());
		treeGrid.setClicksToEdit(EditorGrid.ClicksToEdit.TWO);
		treeGrid.setAutoExpandColumn("name");
		treeGrid.setHideHeaders(true);
		treeGrid.setLoadMask(true);
		treeGrid.setContextMenu(createContextMenu());
	
		treeGrid.setIconProvider(new ModelIconProvider<ModelData>() {
			public AbstractImagePrototype getIcon(ModelData model) {
				if (model instanceof ActivityDTO) {
					return IconImageBundle.ICONS.activity();
				} else if (model instanceof Folder) {
					return GXT.IMAGES.tree_folder_closed();
				} else if (model instanceof AttributeGroupDTO) {
					return IconImageBundle.ICONS.attributeGroup();
				} else if (model instanceof AttributeDTO) {
					return IconImageBundle.ICONS.attribute();
				} else if (model instanceof IndicatorDTO) {
					return IconImageBundle.ICONS.indicator();
				} else {
					return null;
				}
			}
		});
		treeGrid.addListener(Events.CellClick, new Listener<GridEvent>() {
			public void handleEvent(GridEvent ge) {
				formContainer.showForm(treeGrid.getStore().getAt(ge.getRowIndex()));
			}
		});
	
		add(treeGrid, new BorderLayoutData(Style.LayoutRegion.CENTER));
	
		TreeGridDragSource source = new TreeGridDragSource(treeGrid);
		source.addDNDListener(new DNDListener() {
			@Override
			public void dragStart(DNDEvent e) {
	
				ModelData sel = ((CellTreeGridSelectionModel) treeGrid
						.getSelectionModel()).getSelectCell().model;
				if (!db.isDesignAllowed() || sel == null
						|| sel instanceof Folder) {
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
	
				if (treeStore.getParent(target.getModel()) != treeStore
						.getParent(source)) {
	
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
		return treeGrid;
	}

	protected void initNewMenu(Menu menu, SelectionListener<MenuEvent> listener) {
		MenuItem newActivity = new MenuItem(I18N.CONSTANTS.newActivity(),
				IconImageBundle.ICONS.activity(), listener);
		newActivity.setItemId("Activity");
		menu.add(newActivity);
	
		final MenuItem newAttributeGroup = new MenuItem(
				I18N.CONSTANTS.newAttributeGroup(),
				IconImageBundle.ICONS.attributeGroup(), listener);
		newAttributeGroup.setItemId("AttributeGroup");
		menu.add(newAttributeGroup);
	
		final MenuItem newAttribute = new MenuItem(
				I18N.CONSTANTS.newAttribute(),
				IconImageBundle.ICONS.attribute(), listener);
		newAttribute.setItemId("Attribute");
		menu.add(newAttribute);
	
		final MenuItem newIndicator = new MenuItem(
				I18N.CONSTANTS.newIndicator(),
				IconImageBundle.ICONS.indicator(), listener);
		newIndicator.setItemId("Indicator");
		menu.add(newIndicator);
	
		menu.addListener(Events.BeforeShow, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
	
				ModelData sel = getSelection();
				newAttributeGroup.setEnabled(sel != null);
				newAttribute.setEnabled(sel instanceof AttributeGroupDTO
						|| sel instanceof AttributeDTO);
				newIndicator.setEnabled(sel != null);
			}
		});
	}

	protected void initRemoveMenu(Menu menu) {
		final MenuItem removeItem = new MenuItem(I18N.CONSTANTS.delete(),
				IconImageBundle.ICONS.delete());
		removeItem.setItemId(UIActions.delete);
		menu.add(removeItem);
	}

	protected Menu createContextMenu() {
		Menu menu = new Menu();
		initNewMenu(menu, null);
		menu.add(new SeparatorMenuItem());
		initRemoveMenu(menu);
		return menu;
	}

	private ColumnModel createColumnModel() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
	
		TextField<String> nameField = new TextField<String>();
		nameField.setAllowBlank(false);
	
		ColumnConfig nameColumn = new ColumnConfig("name",
				I18N.CONSTANTS.name(), 150);
		nameColumn.setEditor(new CellEditor(nameField));
		nameColumn.setRenderer(new TreeGridCellRenderer());
		columns.add(nameColumn);
	
		return new ColumnModel(columns);
	}

	private FormDialogTether showNewForm(EntityDTO entity, FormDialogCallback callback) {
		return formContainer.showNewForm(entity, callback);
	}

	
	public void onNodeDropped(ModelData source) {
		// update sortOrder
		ModelData parent = treeStore.getParent(source);
		List<ModelData> children = parent == null ? treeStore.getRootItems()
				: treeStore.getChildren(parent);
	
		for (int i = 0; i != children.size(); ++i) {
			Record record = treeStore.getRecord(children.get(i));
			record.set("sortOrder", i);
		}
	}

	public void onNew(String entityName) {
	
		final EntityDTO newEntity;
		ModelData parent;
	
		ModelData selected = getSelection();
	
		if ("Activity".equals(entityName)) {
			UserDatabaseDTO d = new UserDatabaseDTO();
			d.setId(db.getId());
			newEntity = new ActivityDTO(d);
			newEntity.set("databaseId", db.getId());
			parent = null;
	
		} else if ("AttributeGroup".equals(entityName)) {
			ActivityDTO activity = findActivityFolder(selected);
	
			newEntity = new AttributeGroupDTO();
			newEntity.set("activityId", activity.getId());
			parent = treeStore.getChild(activity, 0);
	
		} else if ("Attribute".equals(entityName)) {
			AttributeGroupDTO group = findAttributeGroupNode(selected);
	
			newEntity = new AttributeDTO();
			newEntity.set("attributeGroupId", group.getId());
			parent = group;
	
		} else if ("Indicator".equals(entityName)) {
			//ActivityDTO activity = findActivityFolder(selected);
	
			IndicatorDTO newIndicator = new IndicatorDTO();
			newIndicator.setCollectIntervention(true);
			newIndicator.setAggregation(IndicatorDTO.AGGREGATE_SUM);
	
			newEntity = newIndicator;
			//newEntity.set("activityId", activity.getId());
	
			ModelData activity = null;
			parent = treeStore.getChild(activity, 1);
	
		} else if ("IndicatorGroup".equals(entityName)) {
			//ActivityDTO activity = findActivityFolder(selected);
	
			IndicatorDTO newIndicator = new IndicatorDTO();
			newIndicator.setCollectIntervention(true);
			newIndicator.setAggregation(IndicatorDTO.AGGREGATE_SUM);
	
			newEntity = newIndicator;
			//newEntity.set("activityId", activity.getId());
	
			ModelData activity = null;
			parent = treeStore.getChild(activity, 1);
			
		} else {
			return; // TODO log error
		}
	
		createEntity(parent, newEntity);
	}

	private void createEntity(final ModelData parent, final EntityDTO newEntity) {
		showNewForm(newEntity, new FormDialogCallback() {
			@Override
			public void onValidated(final FormDialogTether tether) {
	
				service.execute(new CreateEntity(newEntity), tether,
						new AsyncCallback<CreateResult>() {
							public void onFailure(Throwable caught) {
	
							}
	
							public void onSuccess(CreateResult result) {
								newEntity.set("id", result.getNewId()); 
							
								if (parent == null) {
									treeStore.add(newEntity, false);
								} else {
									treeStore.add(parent, newEntity, false);
								}
	
								if (newEntity instanceof ActivityDTO) {
									treeStore.add(newEntity,
											new AttributeGroupFolder(
													(ActivityDTO) newEntity,
													I18N.CONSTANTS.attributes()),
											false);
									treeStore.add(newEntity,
											new IndicatorFolder(
													(ActivityDTO) newEntity,
													I18N.CONSTANTS.indicators()),
											false);
								}
	
								tether.hide();
	
								eventBus.fireEvent(AppEvents.SchemaChanged);
							}
						});
			}
		});
	}

	protected ActivityDTO findActivityFolder(ModelData selected) {
	
		while (!(selected instanceof ActivityDTO)) {
			selected = treeStore.getParent(selected);
		}
	
		return (ActivityDTO) selected;
	}

	protected AttributeGroupDTO findAttributeGroupNode(ModelData selected) {
		if (selected instanceof AttributeGroupDTO) {
			return (AttributeGroupDTO) selected;
		}
		if (selected instanceof AttributeDTO) {
			return (AttributeGroupDTO) treeStore.getParent(selected);
		}
		throw new AssertionError("not a valid selection to add an attribute !");
	
	}

	protected Command createSaveCommand() {
		BatchCommand batch = new BatchCommand();
	
		for (ModelData model : treeStore.getRootItems()) {
			prepareBatch(batch, model);
		}
		return batch;
	}

	protected void prepareBatch(BatchCommand batch, ModelData model) {
		if (model instanceof EntityDTO) {
			Record record = treeStore.getRecord(model);
			if (record.isDirty()) {
				batch.add(new UpdateEntity((EntityDTO) model, this
						.getChangedProperties(record)));
			}
		}
	
		for (ModelData child : treeStore.getChildren(model)) {
			prepareBatch(batch, child);
		}
	}

	private Map<String, Object> getChangedProperties(Record record) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onSelectionChanged(ModelData selectedItem) {
		setActionEnabled(UIActions.delete, db.isDesignAllowed()
				&& selectedItem instanceof EntityDTO);
	}

	private void setActionEnabled(String delete, boolean b) {
		// TODO Auto-generated method stub
		
	}

	protected abstract void fillStore();

}