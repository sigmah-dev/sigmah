package org.sigmah.client.page.dashboard;

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

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Widget to represents the organizational chart of an org unit.
 * 
 * @author tmi
 */
public class OrgUnitTreeGrid {

    /**
     * The tree grid.
     */
    private final TreeGrid<OrgUnitDTOLight> tree;

    /**
     * The selection model (can be <code>null</code> if the tree doesn't manage a selection model).
     */
    private TreeGridCheckboxSelectionModel<OrgUnitDTOLight> selectionModel;

    /**
     * The actions toolbar.
     */
    private final ToolBar toolbar;

    public OrgUnitTreeGrid(final EventBus eventBus, final boolean hasSelectionModel) {

        // Creates columns

        final ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        final ColumnConfig nameColumn = new ColumnConfig();
        nameColumn.setId("name");
        nameColumn.setHeader(I18N.CONSTANTS.projectName());
        nameColumn.setRenderer(new TreeGridCellRenderer<OrgUnitDTOLight>());
        nameColumn.setWidth(150);

        final ColumnConfig fullNameColumn = new ColumnConfig();
        fullNameColumn.setId("fullName");
        fullNameColumn.setHeader(I18N.CONSTANTS.projectFullName());
        fullNameColumn.setWidth(250);
        fullNameColumn.setRenderer(new GridCellRenderer<OrgUnitDTOLight>() {

            @Override
            public Object render(final OrgUnitDTOLight model, String property, ColumnData config, int rowIndex,
                    int colIndex, ListStore<OrgUnitDTOLight> store, Grid<OrgUnitDTOLight> grid) {

                final com.google.gwt.user.client.ui.Label visitButton =
                        new com.google.gwt.user.client.ui.Label((String) model.get(property));
                visitButton.addStyleName("flexibility-action");
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

        // Adds columns.

        if (hasSelectionModel) {
            // Tree selection model
            selectionModel = new TreeGridCheckboxSelectionModel<OrgUnitDTOLight>();
            columns.add(selectionModel.getColumn());
        }

        columns.add(nameColumn);
        columns.add(fullNameColumn);
        columns.add(countryColumn);

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

        // Tree grid
        tree = new TreeGrid<OrgUnitDTOLight>(store, new ColumnModel(columns));
        tree.setBorders(true);
        tree.getStyle().setLeafIcon(OrgUnitImageBundle.ICONS.orgUnitSmall());
        tree.getStyle().setNodeCloseIcon(OrgUnitImageBundle.ICONS.orgUnitSmall());
        tree.getStyle().setNodeOpenIcon(OrgUnitImageBundle.ICONS.orgUnitSmallTransparent());
        tree.setTrackMouseOver(false);
        tree.addPlugin(createOrgUnitFilters());

        if (hasSelectionModel) {
            tree.setSelectionModel(selectionModel);
            tree.addPlugin(selectionModel);
        }

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

        // Toolbar
        toolbar = new ToolBar();
        toolbar.setAlignment(HorizontalAlignment.LEFT);

        toolbar.add(expandButton);
        toolbar.add(collapseButton);
    }
    
    
    private GridFilters createOrgUnitFilters(){
		 
        final GridFilters filters = new GridFilters();
        filters.setLocal(true);
        // Data index of each filter should be identical with column id in ColumnModel of TreeGrid  
        filters.addFilter(new StringFilter("name"));
        filters.addFilter(new StringFilter("fullName"));
        filters.addFilter(new StringFilter("country"){
			@Override
			@SuppressWarnings("unchecked")
			protected <X> X getModelValue(ModelData model) {
				CountryDTO country=(CountryDTO)super.getModelValue(model);
				return (X)(country == null ? "" : (country.getName() + " (" + country.getCodeISO() + ')'));
			}

			
        	
        });              
        return filters;
   }

    public TreeGrid<OrgUnitDTOLight> getTreeGrid() {
        return tree;
    }

    public TreeStore<OrgUnitDTOLight> getStore() {
        return tree.getTreeStore();
    }

    public ToolBar getToolbar() {
        return toolbar;
    }

    public void addToolbarButton(Button button) {
        toolbar.add(new SeparatorToolItem());
        toolbar.add(button);
    }
}
