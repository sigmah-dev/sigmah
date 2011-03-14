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
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogImpl;
import org.sigmah.client.page.common.dialog.FormDialogTether;
import org.sigmah.client.page.common.grid.ImprovedCellTreeGridSelectionModel;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.client.page.entry.IndicatorNumberFormats;
import org.sigmah.client.page.entry.SiteGridPageState;
import org.sigmah.client.page.project.ProjectPresenter;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.BaseModelData;
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
import com.extjs.gxt.ui.client.widget.treegrid.CellTreeGridSelectionModel;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.Joint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class DesignPanel extends DesignPanelBase {

	private UserDatabaseDTO db;
	private ProjectPresenter projectPresenter;
	private Provider<IndicatorDialog> indicatorDialog;
	
	private int currentDatabaseId;
	
	@Inject
	public DesignPanel(Dispatcher dispatcher, Provider<IndicatorDialog> indicatorDialog) {
		service=dispatcher;
		treeStore = new TreeStore<ModelData>();
		this.indicatorDialog = indicatorDialog;
		
		setLayout(new FitLayout());
		
		// setup grid
		treeGrid = new EditorTreeGrid<ModelData>(treeStore, createColumnModel());
		treeGrid.setSelectionModel(new ImprovedCellTreeGridSelectionModel<ModelData>());
		treeGrid.setClicksToEdit(EditorGrid.ClicksToEdit.TWO);
		treeGrid.setAutoExpandColumn("code");
		treeGrid.setHideHeaders(false);
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
				if(ge.getColIndex() == 0 && ge.getModel() instanceof IndicatorDTO) {
					showIndicatorForm((IndicatorDTO) ge.getModel());
				}
			}
		});

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
		add(treeGrid);
		
		
		final Button newIndicatorGroup = new Button(
				I18N.CONSTANTS.newIndicatorGroup(), new SelectionListener<ButtonEvent>() {
					
			@Override
			public void componentSelected(ButtonEvent ce) {
				onNewIndicatorGroup();
			}
		});
		toolBar.add(newIndicatorGroup);
		
		final Button newIndicatorButton = new Button(
				I18N.CONSTANTS.newIndicator(), new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				onNewIndicator();
			}
		});
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
	
	public void load(int id) {
		this.currentDatabaseId = id;
		fillStore();
	}
	
	@Override
	protected void fillStore() {
		service.execute(new GetIndicators(currentDatabaseId), null, new AsyncCallback<IndicatorListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO
			}

			@Override
			public void onSuccess(IndicatorListResult result) {
				treeStore.removeAll();
				
				Map<String, TreeModel> categoryNodes = new HashMap<String, TreeModel>();
				for(IndicatorDTO indicator : result.getData()) {
										
					if(indicator.getCategory() != null) {
						TreeModel categoryNode = categoryNodes.get(indicator.getCategory());
						if(categoryNode == null) {
							categoryNode = new BaseTreeModel();
							categoryNode.set("code", indicator.getCategory());
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

	private void onNewIndicator() {
		onNew("Indicator");
				
	}
	
	private void onNewIndicatorGroup() {
		onNew("IndicatorGroup");
	}
	
	private void showIndicatorForm(IndicatorDTO model) {
	    IndicatorDialog dialog = indicatorDialog.get();
	    dialog.bindIndicator(projectPresenter.getCurrentProjectDTO().getId(), model, treeStore);
	    dialog.show();
	}
	
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
		
		ColumnConfig valueColumn = new ColumnConfig("currentValue", I18N.CONSTANTS.value(), 50);
		valueColumn.setRenderer(new IndicatorValueRenderer());
		valueColumn.setEditor(new CellEditor(new NumberField()));
		columns.add(valueColumn);
		
		return new ColumnModel(columns);
	}

	private class MyTreeGridCellRenderer extends TreeGridCellRenderer {

		private class TextProvider extends BaseModelData {
			IndicatorDTO indicator;

			@Override
			public <X> X get(String property) {
				StringBuilder html = new StringBuilder();
				html.append("<div class='" + DesignPanelResources.INSTANCE.getStyle().emptyStarIcon() + "></div>");
				html.append("<div class='" + DesignPanelResources.INSTANCE.getStyle().emptyMapIcon() + "></div>");
				html.append("<div class='" + DesignPanelResources.INSTANCE.getStyle().indicatorCell() + ">" + indicator.getCode() + "</div>");
				return (X)html.toString();
			}

			@Override
			public boolean equals(Object obj) {
				return indicator.equals(obj);
			}

			@Override
			public int hashCode() {
				return indicator.hashCode();
			}
			
			
		}
		
		private TextProvider textProvider = new TextProvider();
		
		@Override
		public Object render(ModelData model, String property,
				ColumnData config, int rowIndex, int colIndex, ListStore store,
				Grid grid) {
			if(model instanceof IndicatorDTO) {
				textProvider.indicator = (IndicatorDTO) model;
				return super.render(textProvider, property, config, rowIndex, colIndex, store, grid);
			} else {
				return super.render(model, property, config, rowIndex, colIndex, store, grid);
			}
		}
	
		
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
