package org.sigmah.client.page.admin.orgunit;

import java.util.ArrayList;

import org.sigmah.client.EventBus;
import org.sigmah.client.event.NavigationEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.orgunit.OrgUnitImageBundle;
import org.sigmah.client.page.orgunit.OrgUnitState;
import org.sigmah.client.util.TreeGridCheckboxSelectionModel;
import org.sigmah.shared.dto.CountryDTO;
import org.sigmah.shared.dto.OrgUnitDTOLight;
import org.sigmah.shared.dto.OrgUnitModelDTO;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class AdminOrgUnitView extends AdminOrgUnitPresenter.View {

    private final ContentPanel mainPanel;
    private TreeGrid<OrgUnitDTOLight> tree;
    private TreeGridCheckboxSelectionModel<OrgUnitDTOLight> selectionModel;
    private ToolBar toolbar;
    private Button addButton;
    private Button moveButton;
    private Button removeButton;

    public AdminOrgUnitView(final EventBus eventBus) {

        buildTree(eventBus);
        buildToolbar();

        mainPanel = new ContentPanel(new FitLayout());
        mainPanel.setHeading(I18N.CONSTANTS.orgunitTree());

        mainPanel.setTopComponent(toolbar);
        mainPanel.add(tree);
    }

    private void buildTree(final EventBus eventBus) {

        final ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        // Code
        final ColumnConfig nameColumn = new ColumnConfig();
        nameColumn.setId("name");
        nameColumn.setHeader(I18N.CONSTANTS.projectName());
        nameColumn.setRenderer(new TreeGridCellRenderer<OrgUnitDTOLight>());
        nameColumn.setWidth(150);

        // Title
        final ColumnConfig fullNameColumn = new ColumnConfig();
        fullNameColumn.setId("fullName");
        fullNameColumn.setHeader(I18N.CONSTANTS.projectFullName());
        fullNameColumn.setRenderer(new GridCellRenderer<OrgUnitDTOLight>() {

            @Override
            public Object render(final OrgUnitDTOLight model, String property, ColumnData config, int rowIndex,
                    int colIndex, ListStore<OrgUnitDTOLight> store, Grid<OrgUnitDTOLight> grid) {

                final com.google.gwt.user.client.ui.Label visitButton =
                        new com.google.gwt.user.client.ui.Label((String) model.get(property));
                visitButton.addStyleName("flexibility-action");
                visitButton.setWidth("250px");
                visitButton.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent e) {
                        eventBus.fireEvent(new NavigationEvent(NavigationHandler.NavigationRequested, new OrgUnitState(
                            model.getId()), null));
                    }
                });

                return visitButton;
            }
        });

        // Country
        final ColumnConfig countryColumn = new ColumnConfig();
        countryColumn.setId("country");
        countryColumn.setHeader(I18N.CONSTANTS.projectCountry());
        countryColumn.setWidth(100);
        countryColumn.setRenderer(new GridCellRenderer<OrgUnitDTOLight>() {

            @Override
            public Object render(final OrgUnitDTOLight model, String property, ColumnData config, int rowIndex,
                    int colIndex, ListStore<OrgUnitDTOLight> store, Grid<OrgUnitDTOLight> grid) {

                final CountryDTO country = (CountryDTO) model.get(property);

                if (country == null) {
                    return "";
                } else {
                    return country.getName() + " (" + country.getCodeISO() + ')';
                }
            }
        });

        // OrgUnitModel
        final ColumnConfig modelColumn = new ColumnConfig();
        modelColumn.setId("oum");
        modelColumn.setHeader(I18N.CONSTANTS.projectModel());
        modelColumn.setWidth(200);
        modelColumn.setRenderer(new GridCellRenderer<OrgUnitDTOLight>() {

            @Override
            public Object render(OrgUnitDTOLight model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<OrgUnitDTOLight> store, Grid<OrgUnitDTOLight> grid) {

                final OrgUnitModelDTO orgUnitModel = (OrgUnitModelDTO) model.get(property);

                if (orgUnitModel == null) {
                    return "";
                } else {
                    return orgUnitModel.getName();
                }

            }

        });

        // Tree store
        final TreeStore<OrgUnitDTOLight> store = new TreeStore<OrgUnitDTOLight>();
        store.setSortInfo(new SortInfo("name", SortDir.ASC));

        store.setStoreSorter(new StoreSorter<OrgUnitDTOLight>() {

            @Override
            public int compare(Store<OrgUnitDTOLight> store, OrgUnitDTOLight m1, OrgUnitDTOLight m2, String property) {

                if ("country".equals(property)) {
                    return ((CountryDTO) m1.get(property)).getName().compareToIgnoreCase(
                        ((CountryDTO) m2.get(property)).getName());
                } else {
                    return super.compare(store, m1, m2, property);
                }
            }
        });

        // Tree selection model
        selectionModel = new TreeGridCheckboxSelectionModel<OrgUnitDTOLight>();
        columns.add(selectionModel.getColumn());

        columns.add(nameColumn);
        columns.add(fullNameColumn);
        columns.add(countryColumn);
        columns.add(modelColumn);

        // Tree grid
        tree = new TreeGrid<OrgUnitDTOLight>(store, new ColumnModel(columns));
        tree.setBorders(true);
        tree.getStyle().setLeafIcon(OrgUnitImageBundle.ICONS.orgUnitSmall());
        tree.getStyle().setNodeCloseIcon(OrgUnitImageBundle.ICONS.orgUnitSmall());
        tree.getStyle().setNodeOpenIcon(OrgUnitImageBundle.ICONS.orgUnitSmallTransparent());
        tree.setAutoExpandColumn("fullName");
        tree.setTrackMouseOver(false);
        tree.setSelectionModel(selectionModel);
        tree.addPlugin(selectionModel);
    }

    private void buildToolbar() {

        // Expand all button.
        final Button expandButton =
                new Button(I18N.CONSTANTS.expandAll(), IconImageBundle.ICONS.expand(),
                    new SelectionListener<ButtonEvent>() {

                        @Override
                        public void componentSelected(ButtonEvent ce) {
                            tree.expandAll();
                        }
                    });

        // Collapse all button.
        final Button collapseButton =
                new Button(I18N.CONSTANTS.collapseAll(), IconImageBundle.ICONS.collapse(),
                    new SelectionListener<ButtonEvent>() {

                        @Override
                        public void componentSelected(ButtonEvent ce) {
                            tree.collapseAll();
                        }
                    });

        // Actions buttons.
        addButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
        moveButton = new Button(I18N.CONSTANTS.adminOrgUnitMove(), IconImageBundle.ICONS.up());
        removeButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());

        // Toolbar
        toolbar = new ToolBar();
        toolbar.setAlignment(HorizontalAlignment.LEFT);

        toolbar.add(expandButton);
        toolbar.add(collapseButton);
        toolbar.add(new SeparatorToolItem());
        toolbar.add(addButton);
        toolbar.add(moveButton);
        toolbar.add(removeButton);
    }

    @Override
    public ContentPanel getMainPanel() {
        return mainPanel;
    }

    @Override
    public TreeGrid<OrgUnitDTOLight> getTree() {
        return tree;
    }

    @Override
    public TreeStore<OrgUnitDTOLight> getStore() {
        return tree.getTreeStore();
    }

    @Override
    public Button getAddButton() {
        return addButton;
    }

    @Override
    public Button getMoveButton() {
        return moveButton;
    }

    @Override
    public Button getRemoveButton() {
        return removeButton;
    }
}
