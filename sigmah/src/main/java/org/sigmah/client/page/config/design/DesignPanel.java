/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.grid.ImprovedCellTreeGridSelectionModel;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.entry.IndicatorNumberFormats;
import org.sigmah.client.page.entry.SiteGridPageState;
import org.sigmah.client.page.project.ProjectPresenter;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.DND;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.dnd.TreeGridDropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
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

public class DesignPanel extends DesignPanelBase implements SubPresenter {
		
	private ProjectPresenter projectPresenter;
	
	@Inject
	public DesignPanel(EventBus bus, Dispatcher dispatcher) {
		eventBus=bus;
		service=dispatcher;
		treeStore = new TreeStore<ModelData>(new BaseTreeLoader<ModelData>(new Proxy()));
		
		// setup grid
		treeGrid = new EditorTreeGrid<ModelData>(treeStore, createColumnModel());
		treeGrid.setSelectionModel(new ImprovedCellTreeGridSelectionModel<ModelData>());
		treeGrid.setClicksToEdit(EditorGrid.ClicksToEdit.TWO);
		treeGrid.setAutoExpandColumn("name");
		treeGrid.setHideHeaders(true);
		treeGrid.setLoadMask(true);

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
		
		// cell click listener
		treeGrid.addListener(Events.CellClick, new Listener<GridEvent>() {
			public void handleEvent(GridEvent ge) {
				formContainer.showForm(treeGrid.getStore().getAt(
						ge.getRowIndex()));
			}
		});

		// Setup context menu
		Menu menu = new Menu();
		final MenuItem newIndicator = new MenuItem(
				I18N.CONSTANTS.newIndicator(),
				IconImageBundle.ICONS.indicator(), null);
		
		newIndicator.setItemId("Indicator");
		menu.add(newIndicator);	
		menu.addListener(Events.BeforeShow, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				ModelData sel = getSelection();
			//	newAttributeGroup.setEnabled(sel != null);
			//	newAttribute.setEnabled(sel instanceof AttributeGroupDTO
			//			|| sel instanceof AttributeDTO);
				newIndicator.setEnabled(sel != null);
			}
		});
		menu.add(new SeparatorMenuItem());
		
		final MenuItem removeItem = new MenuItem(I18N.CONSTANTS.delete(),
				IconImageBundle.ICONS.delete());
		removeItem.setItemId(UIActions.delete);
		menu.add(removeItem);	
		treeGrid.setContextMenu(menu);
		
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
		add(treeGrid, new BorderLayoutData(Style.LayoutRegion.CENTER));
		
		SelectionListener<ButtonEvent> listener = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				onNew(ce.getButton().getItemId());
			}
		};
		
		final Button newIndicatorGroup = new Button(
				I18N.CONSTANTS.newIndicatorGroup(),
				IconImageBundle.ICONS.indicator(), listener);
		newIndicatorGroup.setItemId("IndicatorGroup");
		toolBar.add(newIndicator);
		
		final Button newIndicatorButton = new Button(
				I18N.CONSTANTS.newIndicator(),
				IconImageBundle.ICONS.indicator(), listener);
		newIndicatorButton.setItemId("Indicator");
		toolBar.add(newIndicatorButton);
		
		Button reloadButtonMenu = new Button(I18N.CONSTANTS.refresh(),
				IconImageBundle.ICONS.refresh(), new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						fillStore();
					}
				});
		reloadButtonMenu.setEnabled(true);
		toolBar.add(reloadButtonMenu);	
		
		SiteGridPageState state = new SiteGridPageState();
		state.setPageNum(1);
	}
		
	public void setProjectPresenter(ProjectPresenter project) {
		this.projectPresenter = project;
	}
	
	
	@Override
	public Component getView() {
		return this;
	}

	@Override
	public void discardView() {
		// TODO Auto-generated method stub	
	}

	@Override
	public void viewDidAppear() {
		// TODO Auto-generated method stub
	}

	private void finishLoad(UserDatabaseDTO db) {
		this.db = db;
		fillStore();	
	}
	
	
	private class Proxy extends RpcProxy<List<ModelData>> {

		@Override
		protected void load(Object parent,
				AsyncCallback<List<ModelData>> callback) {
			
			if(parent == null) {
				
			}
			
		}
		
		
	}

	@Override
	protected void fillStore() {
		service.execute(new GetIndicators(projectPresenter.getCurrentProjectDTO().getId()), null, new AsyncCallback<IndicatorListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO
			}

			@Override
			public void onSuccess(IndicatorListResult result) {
				Map<String, TreeModel> categoryNodes = new HashMap<String, TreeModel>();
				for(IndicatorDTO indicator : result.getData()) {
										
					if(indicator.getCategory() != null) {
						TreeModel categoryNode = categoryNodes.get(indicator.getCategory());
						if(categoryNode == null) {
							categoryNode = new BaseTreeModel();
							categoryNode.set("name", indicator.getCategory());
							treeStore.add(categoryNode, false);
						} 
						treeStore.add(categoryNode, indicator, false);
					} else {
						treeStore.add(indicator, false);
					}
				}
			}

		});
	}

	@Override
	protected void initNewMenu(Menu menu, SelectionListener<MenuEvent> listener) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected ColumnModel createColumnModel() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		TextField<String> nameField = new TextField<String>();
		nameField.setAllowBlank(false);

		ColumnConfig nameColumn = new ColumnConfig("code",
				I18N.CONSTANTS.name(), 150);
		nameColumn.setEditor(new CellEditor(nameField));
		nameColumn.setRenderer(new TreeGridCellRenderer());
		columns.add(nameColumn);
		
		ColumnConfig objectiveColumn = new ColumnConfig("objective", I18N.CONSTANTS.objecive(), 50);
		objectiveColumn.setRenderer(new IndicatorValueRenderer());
		objectiveColumn.setEditor(new CellEditor(new NumberField()));
		columns.add(objectiveColumn);
		
		ColumnConfig valueColumn = new ColumnConfig("currentValue", I18N.CONSTANTS.objecive(), 50);
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
	
}
