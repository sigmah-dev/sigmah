/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sigmah.client.AppEvents;
import org.sigmah.client.EventBus;
import org.sigmah.client.event.PivotCellEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconUtil;
import org.sigmah.client.util.DateUtilGWTImpl;
import org.sigmah.shared.command.Month;
import org.sigmah.shared.date.DateUtil;
import org.sigmah.shared.report.content.DimensionCategory;
import org.sigmah.shared.report.content.EntityCategory;
import org.sigmah.shared.report.content.MonthCategory;
import org.sigmah.shared.report.content.PivotTableData;
import org.sigmah.shared.report.content.PivotTableData.Axis;
import org.sigmah.shared.report.model.DateDimension;
import org.sigmah.shared.report.model.DateRange;
import org.sigmah.shared.report.model.DateUnit;
import org.sigmah.shared.report.model.Dimension;
import org.sigmah.shared.report.model.DimensionType;
import org.sigmah.shared.report.model.PivotElement;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;

/**
 * 
 * Independent component to display and edited pivoted site / indicator results.
 * 
 * @author Alex Bertram
 */
public class PivotGridPanel extends ContentPanel implements HasValue<PivotElement> {

    protected EventBus eventBus;

    protected PivotElement element;
    protected EditorTreeGrid<PivotTableRow> grid;
    protected TreeStore<PivotTableRow> store;
    protected ColumnModel columnModel;
    
    private DateUtil dateUtil = new DateUtilGWTImpl();
    
    /**
     * Maps column axes to property names
     */
    protected Map<PivotTableData.Axis, String> propertyMap;
    
    /**
     * Maps grid column indices to the leaf axis
     */
    protected Map<Integer, PivotTableData.Axis> columnMap;
    
    
    private boolean showAxisIcons = true;
    private boolean showSwapIcon = false;

    @Inject
    public PivotGridPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        setLayout(new FitLayout());
        
        store = new TreeStore<PivotTableRow>();

        PivotResources.INSTANCE.css().ensureInjected();
    }

    
    public boolean isShowAxisIcons() {
		return showAxisIcons;
	}

	public void setShowAxisIcons(boolean showAxisIcons) {
		this.showAxisIcons = showAxisIcons;
	}

	public boolean isShowSwapIcon() {
		return showSwapIcon;
	}

	public void setShowSwapIcon(boolean showSwapIcon) {
		this.showSwapIcon = showSwapIcon;
	}

	public class PivotTableRow extends BaseTreeModel {

        private PivotTableData.Axis rowAxis;

        public PivotTableRow(PivotTableData.Axis axis) {
            this.rowAxis = axis;
            set("header", decorateHeader(axis.getLabel(), axis));

            updateFromTree();
            
            for(PivotTableData.Axis child : rowAxis.getChildren()) {
                add(new PivotTableRow(child));
            }
        }

        /**
         * Updates this model from the Cell values in the PivotTableData tree. 
         */
		private void updateFromTree() {
			this.setProperties(Collections.EMPTY_MAP);
			for(Map.Entry<PivotTableData.Axis, PivotTableData.Cell> entry : rowAxis.getCells().entrySet()) {
                String property = propertyMap.get(entry.getKey());
				set(property, entry.getValue().getValue());
            }
		}

        public PivotTableData.Axis getRowAxis() {
            return rowAxis;
        }
        
        public PivotTableData.Axis getColAxis(String property) {
        	for(Entry<PivotTableData.Axis, String> entry : propertyMap.entrySet()) {
        		if(entry.getValue().equals(property)) {
        			return entry.getKey();
        		}
        	}
        	throw new IllegalArgumentException("the property '" + property + "' is not linked to a column axis");
        }
        
        public DimensionCategory getCategory(String property, Dimension dimension) {
        	DimensionCategory category = findCategory(rowAxis, dimension);
        	if(category == null) {
        		return findCategory(getColAxis(property), dimension);
        	}
        	return category;
        }
        
        private DimensionCategory findCategory(PivotTableData.Axis leaf, Dimension dim) {
        	while(leaf != null) {
        		if(leaf.getDimension() != null && leaf.getDimension().equals(dim)) {
        			return leaf.getCategory();
        		}
        		leaf = leaf.getParent();
        	}
        	return null;
        }
        
        public int getIndicatorId(String property) {
        	Set<Integer> indicatorRestrictions = element.getFilter().getRestrictions(DimensionType.Indicator);
			if(indicatorRestrictions.size() == 1) {
        		return indicatorRestrictions.iterator().next();
        	}
        	EntityCategory cat = (EntityCategory) getCategory(property, new Dimension(DimensionType.Indicator));
        	if(cat != null) {
        		return cat.getId();
        	}
        	return -1;
        }
        
        public int getSiteId(String property) {
        	Set<Integer> siteRestrictions = element.getFilter().getRestrictions(DimensionType.Site);
        	if(siteRestrictions.size() == 1) {
        		return siteRestrictions.iterator().next();
        	}
        	EntityCategory cat = (EntityCategory) getCategory(property, new Dimension(DimensionType.Site));
        	if(cat != null) {
        		return cat.getId();
        	}
        	return -1;
        }
        
        public Month getMonth(String property) {
    		if(element.getFilter().getDateRange().isClosed()) {
    			// TODO(alex) : this should check to see whether the date range is actually a month range
    			// but dateUtil is behaving weirdly because of conflicting timezones
    			return new Month(dateUtil.getYear(element.getFilter().getMinDate()), dateUtil.getMonth(element.getFilter().getMinDate()));
    		}
    	
        	MonthCategory cat = (MonthCategory) getCategory(property, new DateDimension(DateUnit.MONTH));
        	if(cat != null) {
        		return new Month(cat.getYear(), cat.getMonth());
        	}
        	throw new UnsupportedOperationException("This cell at property '" + property + "' is not constrained by month");
        }
        
    }

    public void setData(final PivotElement element) {
        if(grid != null) {
        	grid.removeAllListeners();
            removeAll();
        }

        this.element = element;

        PivotTableData data = element.getContent().getData();

        propertyMap = new HashMap<PivotTableData.Axis, String>();
        columnMap = new HashMap<Integer, PivotTableData.Axis>();

        columnModel = createColumnModel(data);

        store.removeAll();
        for(PivotTableData.Axis axis : data.getRootRow().getChildren()) {
            store.add(new PivotTableRow(axis), true);
        }

        grid = new EditorTreeGrid<PivotTableRow>(store, columnModel);
        grid.setView(new PivotGridPanelView());
        grid.getStyle().setNodeCloseIcon(null);
        grid.getStyle().setNodeOpenIcon(null);
        grid.setAutoExpandColumn("header");
        grid.setAutoExpandMin(150);
        grid.addListener(Events.CellDoubleClick, new Listener<GridEvent<PivotTableRow>>() {
            public void handleEvent(GridEvent<PivotTableRow> ge) {
                if(ge.getColIndex() != 0) {
                    eventBus.fireEvent(new PivotCellEvent(AppEvents.Drilldown,
                            element,
                            ge.getModel().getRowAxis(),
                            columnMap.get(ge.getColIndex())));
                }
            }
        });
        grid.addListener(Events.HeaderClick, new Listener<GridEvent<PivotTableRow>>() {

			@Override
			public void handleEvent(GridEvent<PivotTableRow> event) {
				fireEvent(Events.HeaderClick, new PivotGridHeaderEvent(event, columnMap.get(event.getColIndex())));				
			}
		});
        grid.addListener(Events.CellClick, new Listener<GridEvent<PivotTableRow>>() {

			@Override
			public void handleEvent(GridEvent<PivotTableRow> event) {
				if(event.getColIndex() == 0) {
					fireEvent(Events.HeaderClick, new PivotGridHeaderEvent(event, event.getModel().getRowAxis()));
				} else {
					fireEvent(Events.CellClick, new PivotGridCellEvent(event, columnMap.get(event.getColIndex())));
				}
			}
        });
        grid.addListener(Events.BeforeEdit, new Listener<GridEvent<PivotTableRow>>() {

			@Override
			public void handleEvent(GridEvent<PivotTableRow> be) {
				if(!be.getModel().getRowAxis().isLeaf()) {
					be.setCancelled(true);
				}
			}
        });
        grid.addListener(Events.AfterEdit, new Listener<GridEvent<PivotTableRow>>() {

			@Override
			public void handleEvent(GridEvent<PivotTableRow> event) {
				PivotGridCellEvent pivotEvent = new PivotGridCellEvent(event, columnMap.get(event.getColIndex()));
				updateTotalsAfterEdit(pivotEvent);
				fireEvent(Events.AfterEdit, pivotEvent);
			}
		});
        grid.addStyleName(PivotResources.INSTANCE.css().pivotTable());
        
        add(grid);

        layout();

        new DelayedTask(new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                for(PivotTableRow row : store.getRootItems()) {
                    grid.setExpanded(row, true, true);
                }

            }
        }).delay(1);

    } 

    private void updateTotalsAfterEdit(PivotGridCellEvent event) {
		// update the PivotTableData.Cell
    	Double newValue = event.getModel().get(event.getProperty());
    	event.getCell().setValue(newValue);
    	
    	// update totals
    	element.getContent().getData().updateTotals();
    	
    	syncGridWithContent();
    	
	}
    
    private void syncGridWithContent() {
    	for(PivotTableRow row : store.getAllItems()) {
    		row.updateFromTree();
    	}
    	grid.getView().refresh(false);
    }


    private int findIndicatorId(PivotTableData.Axis axis) {
        while(axis != null) {
            if(axis.getDimension().getType() == DimensionType.Indicator) {
                return ((EntityCategory)axis.getCategory()).getId();
            }
            axis = axis.getParent();
        }
        return -1;
    }

    private ColumnModel createColumnModel(PivotTableData data) {

        List<ColumnConfig> config = new ArrayList<ColumnConfig>();

        ColumnConfig rowHeader = new ColumnConfig("header", cornerCellHtml(), 150);
        rowHeader.setRenderer(new TreeGridCellRenderer() {

			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore store, Grid grid) {
				Object result = super.render(model, property, config, rowIndex, colIndex, store, grid);
				config.css = config.css + " x-grid3-header";
				return result;
			}
        	
        });
        rowHeader.setSortable(false);
        rowHeader.setMenuDisabled(true);
        config.add(rowHeader);

        int colIndex = 1;

        List<PivotTableData.Axis> leaves = data.getRootColumn().getLeaves();
        for(PivotTableData.Axis axis : leaves) {


            String id = "col" + colIndex;

            String label = axis.getLabel();
            if(label == null) {
                label = I18N.CONSTANTS.value();
            }
            label = decorateHeader(label, axis);
            final NumberFormat numberFormat = NumberFormat.getFormat("#,###");
            ColumnConfig column = new ColumnConfig(id, label, 75);
            column.setRenderer(new GridCellRenderer<PivotTableRow>() {

				@Override
				public Object render(PivotTableRow model, String property,
						ColumnData config, int rowIndex, int colIndex,
						ListStore<PivotTableRow> store, Grid<PivotTableRow> grid) {
					
					Double value = model.get(property);
					if(value == null) {
						return "";
					} else {
						if(model.getRowAxis().isTotal()) {
							config.css = config.css + " " + PivotResources.INSTANCE.css().totalCell();
						}
						return numberFormat.format(value);
					}
				}
		
			});
            column.setAlignment(Style.HorizontalAlignment.RIGHT);
            column.setSortable(false);
            column.setMenuDisabled(true);
            
            NumberField valueField = new NumberField();            
            column.setEditor(new CellEditor(valueField));

            propertyMap.put(axis, id);
            columnMap.put(colIndex, axis);


            config.add(column);
            colIndex++;
        }

        ColumnModel columnModel = new ColumnModel(config);

        int depth = data.getRootColumn().getDepth();
        int row = 0;

        for(int d = 1; d<=depth; ++d) {

            List<PivotTableData.Axis> children = data.getRootColumn().getDescendantsAtDepth(d);

            // first add a group identifying the dimension

            Dimension dim = children.get(0).getDimension();

            // now add child columns

            if(d < depth) {

                int col = 1;
                for(PivotTableData.Axis child : children) {

                    int colSpan = child.getLeaves().size();
                    columnModel.addHeaderGroup(row, col, new HeaderGroupConfig(child.getLabel(), 1, colSpan) );

                    col += colSpan;

                }
                row++;

            }

        }
        return columnModel;
    }

    private String cornerCellHtml() {
    	if(showSwapIcon) {
    		return IconUtil.iconHtml(PivotResources.INSTANCE.css().swapIcon());
    	} else {
    		return "";
    	}
	}

	private String decorateHeader(String header, Axis axis) {
    	if(showAxisIcons && axis.isLeaf()) {
    		StringBuilder sb = new StringBuilder(header);
    		sb.append(IconUtil.iconHtml(PivotResources.INSTANCE.css().zoomIcon()));
    		
    		switch(axis.getDimension().getType()) {
    		case Indicator:
    		case Site:
    			sb.append(IconUtil.iconHtml(PivotResources.INSTANCE.css().editIcon()));
    		}
    		return sb.toString();
    	} else {
    		return header;
    	}
    }
    
	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<PivotElement> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public PivotElement getValue() {
		return element;
	}

	@Override
	public void setValue(PivotElement value) {
		setData(value);
	}

	@Override
	public void setValue(PivotElement value, boolean fireEvents) {
		setData(element);
		if(fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

	public Store<PivotTableRow> getStore() {
		return store;
	}

}
