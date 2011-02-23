package org.sigmah.client.page.config.design;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.grid.AbstractGridView;
import org.sigmah.client.page.common.grid.ImprovedCellTreeGridSelectionModel;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
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
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.treegrid.CellTreeGridSelectionModel;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DesignGrid extends AbstractGridView<ModelData, DesignPresenter> {

	private EditorTreeGrid<ModelData> tree;
	private UserDatabaseDTO db;
	
	public void init(UserDatabaseDTO db, EditorTreeGrid<ModelData> tree) {
		this.tree = tree;
		this.db = db;
	}

	private void showForm(ModelData at) {
		// TODO Auto-generated method stub
		
	}
	
    protected Grid<ModelData> createGridAndAddToContainer(Store store) {
        final TreeStore treeStore = (TreeStore) store;
        tree = new EditorTreeGrid<ModelData>(treeStore, createColumnModel());
        tree.setSelectionModel(new ImprovedCellTreeGridSelectionModel<ModelData>());
        tree.setClicksToEdit(EditorGrid.ClicksToEdit.TWO);
        tree.setAutoExpandColumn("name");
        tree.setHideHeaders(true);
        tree.setLoadMask(true);
        //   tree.setContextMenu(createContextMenu());

        tree.setIconProvider(new ModelIconProvider<ModelData>() {
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
        tree.addListener(Events.CellClick, new Listener<GridEvent>() {
            public void handleEvent(GridEvent ge) {
                showForm(tree.getStore().getAt(ge.getRowIndex()));
            }
        });

        add(tree, new BorderLayoutData(Style.LayoutRegion.CENTER));

        TreeGridDragSource source = new TreeGridDragSource(tree);
        source.addDNDListener(new DNDListener() {
            @Override
            public void dragStart(DNDEvent e) {

                ModelData sel = ((CellTreeGridSelectionModel) tree.getSelectionModel()).getSelectCell().model;
                if (!db.isDesignAllowed() || sel == null || sel instanceof Folder) {
                    e.setCancelled(true);
                    e.getStatus().setStatus(false);
                    return;
                }
                super.dragStart(e);
            }
        });

        TreeGridDropTarget target = new TreeGridDropTarget(tree);
        target.setAllowSelfAsSource(true);
        target.setFeedback(DND.Feedback.BOTH);
        target.setAutoExpand(false);
        target.addDNDListener(new DNDListener() {
            @Override
            public void dragMove(DNDEvent e) {
                List<TreeModel> sourceData = e.getData();
                ModelData source = sourceData.get(0).get("model");
                TreeGrid.TreeNode target = tree.findNode(e.getTarget());

                if (treeStore.getParent(target.getModel()) !=
                        treeStore.getParent(source)) {

                    e.setCancelled(true);
                    e.getStatus().setStatus(false);
                }
            }

            @Override
            public void dragDrop(DNDEvent e) {
                List<TreeModel> sourceData = e.getData();
                ModelData source = sourceData.get(0).get("model");
                presenter.onNodeDropped(source);
            }
        });
        return tree;
    }


    @Override
    protected void initToolBar() {
        toolBar.addSaveSplitButton();

        SelectionListener<MenuEvent> listener = new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {

                presenter.onNew(ce.getItem().getItemId());
            }
        };

        Menu newMenu = new Menu();
        initNewMenu(newMenu, listener);

        Button newButtonMenu = new Button(I18N.CONSTANTS.newText(), IconImageBundle.ICONS.add());
        newButtonMenu.setMenu(newMenu);
        // TODO fix this!!
        //newButtonMenu.setEnabled(db.isDesignAllowed());
        newButtonMenu.setEnabled(true);
 
        toolBar.add(newButtonMenu);
        toolBar.addDeleteButton();

    }

    protected void initNewMenu(Menu menu, SelectionListener<MenuEvent> listener) {
        MenuItem newActivity = new MenuItem(I18N.CONSTANTS.newActivity(), IconImageBundle.ICONS.activity(), listener);
        newActivity.setItemId("Activity");
        menu.add(newActivity);

        final MenuItem newAttributeGroup = new MenuItem(I18N.CONSTANTS.newAttributeGroup(), IconImageBundle.ICONS.attributeGroup(), listener);
        newAttributeGroup.setItemId("AttributeGroup");
        menu.add(newAttributeGroup);

        final MenuItem newAttribute = new MenuItem(I18N.CONSTANTS.newAttribute(), IconImageBundle.ICONS.attribute(), listener);
        newAttribute.setItemId("Attribute");
        menu.add(newAttribute);

        final MenuItem newIndicator = new MenuItem(I18N.CONSTANTS.newIndicator(), IconImageBundle.ICONS.indicator(), listener);
        newIndicator.setItemId("Indicator");
        menu.add(newIndicator);

        menu.addListener(Events.BeforeShow, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {

                ModelData sel = getSelection();

                newAttributeGroup.setEnabled(sel != null);
                newAttribute.setEnabled(sel instanceof AttributeGroupDTO || sel instanceof AttributeDTO);
                newIndicator.setEnabled(sel != null);
            }
        });
    }

    protected void initRemoveMenu(Menu menu) {
        final MenuItem removeItem = new MenuItem(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
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

        ColumnConfig nameColumn = new ColumnConfig("name", I18N.CONSTANTS.name(), 150);
        nameColumn.setEditor(new CellEditor(nameField));
        nameColumn.setRenderer(new TreeGridCellRenderer());
        columns.add(nameColumn);
        return new ColumnModel(columns);
    }
}
