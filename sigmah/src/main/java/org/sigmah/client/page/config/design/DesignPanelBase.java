package org.sigmah.client.page.config.design;

import java.util.List;

import org.sigmah.client.AppEvents;
import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogTether;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;

abstract class DesignPanelBase extends ContentPanel {

	protected Dispatcher service;
	protected EventBus eventBus;
	protected TreeStore<ModelData> treeStore;
	protected UserDatabaseDTO db;
	protected ActionToolBar toolBar;
	protected EditorTreeGrid<ModelData> treeGrid;

	protected DesignFormContainer formContainer;

	protected DesignPanelBase() {
		toolBar = new ActionToolBar();
		toolBar.addSaveSplitButton();
		formContainer = new DesignFormContainer(service, db, treeGrid);
		setTopComponent(toolBar);
	}

	protected abstract ColumnModel createColumnModel();

	protected void initNewMenu(Menu menu,
			SelectionListener<MenuEvent> listener) {}

	protected abstract void fillStore();

	protected ActionToolBar getToolbar() {
		return this.toolBar;
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

	protected void onNew(String entityName) {
		final ModelData newEntity;
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

		} else if ("IndicatorGroup".equals(entityName)) {
			// ActivityDTO activity = findActivityFolder(selected);
			IndicatorGroup newGroup = new IndicatorGroup();
			parent = null;
			newEntity = newGroup;

		} else if ("Indicator".equals(entityName)) {
			// ActivityDTO activity = findActivityFolder(selected);
			IndicatorDTO newIndicator = new IndicatorDTO();
			newIndicator.setCollectIntervention(true);
			newIndicator.setAggregation(IndicatorDTO.AGGREGATE_SUM);
			
			newEntity = newIndicator;
			newEntity.set("databaseId", db.getId() );
			// newEntity.set("activityId", activity.getId());
			ModelData sel = getSelection();
			parent = null;
			if (sel != null) {
				if (sel instanceof IndicatorGroup) {
					parent = sel;
					
				} else if (sel instanceof IndicatorDTO) { 
					parent = treeStore.getParent(sel);
				}
				newIndicator.setCategory(((IndicatorGroup) parent).getName());
			} 
		} else {
			return; // TODO log error
		}
		createEntity(parent, newEntity);
	}

	protected AsyncMonitor getDeletingMonitor() {
		return new MaskingAsyncMonitor(this, I18N.CONSTANTS.deleting());
	}

	protected AsyncMonitor getSavingMonitor() {
		return new MaskingAsyncMonitor(this, I18N.CONSTANTS.saving());
	}

	protected void onNodeDropped(ModelData source) {
		// update sortOrder
		ModelData parent = treeStore.getParent(source);
		List<ModelData> children = parent == null ? treeStore.getRootItems()
				: treeStore.getChildren(parent);

		for (int i = 0; i != children.size(); ++i) {
			Record record = treeStore.getRecord(children.get(i));
			record.set("sortOrder", i);
		}
	}

	private FormDialogTether getFormContainer(ModelData entity,
			FormDialogCallback callback) {
		return formContainer.showNewForm(entity, callback);
	}

	private ActivityDTO findActivityFolder(ModelData selected) {
		while (!(selected instanceof ActivityDTO)) {
			selected = treeStore.getParent(selected);
		}
		return (ActivityDTO) selected;
	}

	private AttributeGroupDTO findAttributeGroupNode(ModelData selected) {
		if (selected instanceof AttributeGroupDTO) {
			return (AttributeGroupDTO) selected;
		}
		if (selected instanceof AttributeDTO) {
			return (AttributeGroupDTO) treeStore.getParent(selected);
		}
		throw new AssertionError("not a valid selection to add an attribute !");
	}
	
	private void createEntity(final ModelData parent, final ModelData newEntity) {
		getFormContainer(newEntity, new FormDialogCallback() {
			@Override
			public void onValidated(final FormDialogTether tether) {
				if (!(newEntity instanceof EntityDTO)) {
					treeStore.add(newEntity, false);
					tether.hide();
				} else {
					service.execute(new CreateEntity((EntityDTO) newEntity),
							tether, new AsyncCallback<CreateResult>() {
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
										treeStore
												.add(newEntity,
														new AttributeGroupFolder(
																(ActivityDTO) newEntity,
																I18N.CONSTANTS
																		.attributes()),
														false);
										
									} else if (newEntity instanceof IndicatorDTO) {	
										treeStore.add(parent, newEntity, false);
									}
									
								
									eventBus.fireEvent(AppEvents.SchemaChanged);
									tether.hide();
								}
							});
				}
			}
		});
	}

}