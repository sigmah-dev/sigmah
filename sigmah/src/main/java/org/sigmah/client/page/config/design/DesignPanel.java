/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.AppEvents;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogImpl;
import org.sigmah.client.page.common.dialog.FormDialogTether;
import org.sigmah.client.page.common.grid.ImprovedCellTreeGridSelectionModel;
import org.sigmah.client.page.common.grid.SavingHelper;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.entry.IndicatorNumberFormats;
import org.sigmah.client.page.entry.SiteGridPageState;
import org.sigmah.client.page.project.ProjectPresenter;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.DND;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.dnd.TreeGridDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
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
import com.extjs.gxt.ui.client.widget.tips.QuickTip;
import com.extjs.gxt.ui.client.widget.treegrid.CellTreeGridSelectionModel;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.Joint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class DesignPanel extends DesignPanelBase implements ActionListener {
	
	private ProjectPresenter projectPresenter;
	private Provider<IndicatorDialog> indicatorDialog;
	
	private int currentDatabaseId;
	
	private MappedIndicatorSelection mappedIndicator; 
	
	@Inject
	public DesignPanel(Dispatcher dispatcher, Provider<IndicatorDialog> indicatorDialog) {
		service=dispatcher;
		treeStore = new TreeStore<ModelData>();
		this.indicatorDialog = indicatorDialog;

		DesignPanelResources.INSTANCE.css().ensureInjected();
		
		setLayout(new FitLayout());
		
		mappedIndicator = new MappedIndicatorSelection();
		
		// setup grid
		treeGrid = new EditorTreeGrid<ModelData>(treeStore, createColumnModel());
		treeGrid.setSelectionModel(new ImprovedCellTreeGridSelectionModel<ModelData>());
		treeGrid.setClicksToEdit(EditorGrid.ClicksToEdit.TWO);
		treeGrid.setAutoExpandColumn("code");
		treeGrid.setHideHeaders(false);
		treeGrid.setLoadMask(true);

		treeGrid.getStyle().setNodeCloseIcon(null);
		treeGrid.getStyle().setNodeOpenIcon(null);
		treeGrid.getStyle().setLeafIcon(null);
		// cell click listener
		treeGrid.addListener(Events.CellClick, new Listener<GridEvent>() {
			public void handleEvent(GridEvent ge) {
				switch(DesignTreeGridCellRenderer.computeTarget(ge)) {
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

		// Quick tip to display indicator full name
		QuickTip fullNameTip = new QuickTip(treeGrid);
		
		// Setup context menu
			
		TreeGridDragSource source = new TreeGridDragSource(treeGrid);
		source.addDNDListener(new DNDListener() {
			@Override
			public void dragStart(DNDEvent e) {
				ModelData sel = ((CellTreeGridSelectionModel) treeGrid
						.getSelectionModel()).getSelectCell().model;
				
				if (db != null && ( !db.isDesignAllowed() || sel == null
						|| sel instanceof Folder)) {
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
				if(source instanceof IndicatorGroup && targetParent != null ) {
					e.setCancelled(true);
					e.getStatus().setStatus(false);
					
				// Indicator cannot be children of other indicators
				} else if(source instanceof IndicatorDTO && targetParent instanceof IndicatorDTO) {
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
		toolBar.addRefreshButton();
	}
	
	
	
	@Override
	public void onUIAction(String actionId) {
		if(UIActions.save.equals(actionId)) {
			SavingHelper.save(service, treeStore, this);
			
		} else if(UIActions.refresh.equals(actionId)) {
			doLoad();
			
		} else if("newIndicator".equals(actionId)) {
			onNewIndicator();
			
		} else if("newIndicatorGroup".equals(actionId)) {
			onNewIndicatorGroup();
		}
	}


	@Override
	protected void onNodeDropped(ModelData source) {
		// update sortOrder
		ModelData newParent = treeStore.getParent(source);
		if(source instanceof IndicatorDTO && newParent instanceof IndicatorGroup) {
			IndicatorDTO indicator = (IndicatorDTO) source;
			IndicatorGroup group = (IndicatorGroup) newParent;
			treeStore.getRecord(indicator).set("category", group.getName());
		}
		renumberRecursively(treeStore.getRootItems(), 1);
	}

	private int renumberRecursively(List<ModelData> list, int index) {
		for(ModelData child : list) {
			if(child instanceof IndicatorDTO) {
				treeStore.getRecord(child).set("sortOrder", index++);
			} else if( child instanceof IndicatorGroup ) {
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
		if(mappedIndicator.getValue() == null || mappedIndicator.getValue().getId() != model.getId()) {
			mappedIndicator.setValue(model);
			treeGrid.getView().refresh(false);
		}
	}

	public void setProjectPresenter(ProjectPresenter project) {
		this.projectPresenter = project;
	}
	
	/**
	 * Loads the indicators for the given databaseId/projectId
	 * 
	 * @param databaseId
	 */
	public void load(int databaseId) {
		this.currentDatabaseId = databaseId;
		doLoad();
	}
	
	@Override
	protected void doLoad() {
		service.execute(new GetIndicators(currentDatabaseId), null, new AsyncCallback<IndicatorListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO
			}

			@Override
			public void onSuccess(IndicatorListResult result) {
				treeStore.removeAll();
				
				Map<String, TreeModel> groupNodes = new HashMap<String, TreeModel>();
				for(IndicatorDTO indicator : result.getData()) {
										
					if(indicator.getCategory() != null) {
						TreeModel groupNode = groupNodes.get(indicator.getCategory());
						if(groupNode == null) {
							groupNode = new IndicatorGroup();
							groupNode.set("name", indicator.getCategory());
							treeStore.add(groupNode, false);
							groupNodes.put(indicator.getCategory(), groupNode);
						} 
						treeStore.add(groupNode, indicator, false);
					} else {
						treeStore.add(indicator, false);
					}
				}
			}

		});
	}

	private void onNewIndicator() {
		final IndicatorDTO newIndicator = new IndicatorDTO();
		newIndicator.setCollectIntervention(true);
		newIndicator.setAggregation(IndicatorDTO.AGGREGATE_SUM);
		newIndicator.set("databaseId", currentDatabaseId);
	
		final IndicatorGroup parent = computeIndicatorParent(newIndicator); 
		if(parent != null) {
			newIndicator.setCategory(parent.getName());
		}
		final IndicatorForm form = new IndicatorForm();
		form.getBinding().bind(newIndicator);
		form.setIdVisible(false);
		form.setCategoryVisible(false);
				
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
					}
				});
			}
		});
	}

	private IndicatorGroup computeIndicatorParent(final IndicatorDTO newIndicator) {
		ModelData sel = getSelection();
		if (sel != null) {
			if (sel instanceof IndicatorGroup) {
				return (IndicatorGroup)sel;
				
			} else if (sel instanceof IndicatorDTO) { 
				return (IndicatorGroup)treeStore.getParent(sel);
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
				dialog.hide();
				treeStore.add(group, false);
			}
		});
	}
	
	private void showIndicatorForm(IndicatorDTO model) {
	    IndicatorDialog dialog = indicatorDialog.get();
	    dialog.bindIndicator(projectPresenter.getCurrentProjectDTO().getId(), model, treeStore);
	    dialog.show();
	}
	
	protected ColumnModel createColumnModel() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		ColumnConfig nameColumn = new ColumnConfig("code",
				I18N.CONSTANTS.name(), 150);
		nameColumn.setRenderer(new DesignTreeGridCellRenderer(mappedIndicator));
		columns.add(nameColumn);
		
		ColumnConfig objectiveColumn = new ColumnConfig("objective", I18N.CONSTANTS.objecive(), 75);
		objectiveColumn.setRenderer(new IndicatorValueRenderer());
		objectiveColumn.setEditor(new CellEditor(new NumberField()));
		columns.add(objectiveColumn);
		
		ColumnConfig valueColumn = new ColumnConfig("currentValue", I18N.CONSTANTS.value(), 75);
		valueColumn.setRenderer(new IndicatorValueRenderer());
		valueColumn.setEditor(new CellEditor(new NumberField()));
		columns.add(valueColumn);
		
		return new ColumnModel(columns);
	}

	private class IndicatorValueRenderer implements GridCellRenderer {

		@Override
		public Object render(ModelData model, String property,
				ColumnData config, int rowIndex, int colIndex, ListStore store,
				Grid grid) {
			
			if(model instanceof IndicatorDTO) {
				Double value = model.get(property);
				if(value != null) {
					return IndicatorNumberFormats
						.forIndicator((IndicatorDTO)model)
							.format(value);
				}
			}
			return "";
		}
	}

	public MappedIndicatorSelection getMappedIndicator() {
		return mappedIndicator;
	}

	
}
