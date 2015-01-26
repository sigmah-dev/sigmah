package org.sigmah.client.page.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sigmah.shared.report.content.PivotTableData;
import org.sigmah.shared.report.content.PivotTableData.Axis;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.google.gwt.core.client.GWT;
import com.google.inject.Provider;

/**
 * Maps a PivotTableData structure to a ColumnModel for 
 * GXT Trees.
 * 
 * @author alexander
 *
 */
class ColumnMapping {
	

	private final PivotTableData data;
	
    private ColumnModel columnModel;
    private int maxColumnDepth;
    final HeaderDecorator headerDecorator;

    /**
     * Maps column axes to property names
     */
    private Map<PivotTableData.Axis, String> propertyMap;
    
    /**
     * Maps grid column indices to the leaf axis
     */
    private Map<Integer, PivotTableData.Axis> columnMap;


    private PivotCellRendererProvider rendererProvider;
    

    public ColumnMapping(PivotTableData data, PivotCellRendererProvider rendererProvider, HeaderDecorator headerDecorator) {
    	this.data = data;
    	this.headerDecorator = headerDecorator;
    	this.rendererProvider = rendererProvider;
    	
        propertyMap = new HashMap<PivotTableData.Axis, String>();
        columnMap = new HashMap<Integer, PivotTableData.Axis>();
        maxColumnDepth = data.getRootColumn().getDepth();
        
        createColumnModel();
        
    }
    
    public ColumnModel getColumnModel() {
		return columnModel;
	}

	public void setColumnModel(ColumnModel columnModel) {
		this.columnModel = columnModel;
	}

	private ColumnModel createColumnModel() {

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        ColumnConfig rowHeader = new ColumnConfig("header", headerDecorator.cornerCellHtml(), 150);
        rowHeader.setRenderer(new PivotTreeGridCellRenderer());
        rowHeader.setSortable(false);
        rowHeader.setMenuDisabled(true);
        columns.add(rowHeader);

        addLeafColumns(columns);

        columnModel = new ColumnModel(columns);

        addHeaderGroup(data.getRootColumn(), 0, 1);
        
        return columnModel;
    }
	
	
	private void addLeafColumns(List<ColumnConfig> config) {
        int colIndex = 1;
		List<PivotTableData.Axis> leaves = data.getRootColumn().getLeaves();
        for(PivotTableData.Axis axis : leaves) {
            String id = "col" + colIndex;

            ColumnConfig column = new ColumnConfig(id, headerDecorator.decorateHeader(axis), 75);
            column.setRenderer(rendererProvider.getRendererForColumn(axis));
            column.setAlignment(Style.HorizontalAlignment.RIGHT);
            column.setSortable(false);
            column.setMenuDisabled(true);
            
            if(GWT.isClient()) { // hack to allow unit tests to run
	            column.setEditor(new IndicatorValueEditor());
            }

            propertyMap.put(axis, id);
            columnMap.put(colIndex, axis);

            config.add(column);
            colIndex++;
        }
	}


	/**
	 * Recursively descend thru column tree to add headers
	 * 
	 * @param columnModel
	 * @param node
	 * @param depth
	 * @param maxDepth
	 * @return
	 */
	private int addHeaderGroup(PivotTableData.Axis parent, int depth, int col) {
        for(PivotTableData.Axis child : parent.getChildren()) {
    		if(child.isLeaf()) {
    			col++;
    		} else {
    			int rowSpan = maxColumnDepth - depth - child.getDepth();
    			int colSpan = child.getLeaves().size();    			

    			columnModel.addHeaderGroup(depth, col, new HeaderGroupConfig( child.getLabel(), rowSpan, colSpan) );
    		
    			col = addHeaderGroup(child, depth+1, col);
    		}
        }
        return col;
    }

	public Axis columnAxisForIndex(int colIndex) {
		return columnMap.get(colIndex);
	}
	

	public String propertyNameForAxis(Axis key) {
		return propertyMap.get(key);
	}
	
	public Axis columnAxisForProperty(String propertyName) {
    	for(Entry<PivotTableData.Axis, String> entry : propertyMap.entrySet()) {
    		if(entry.getValue().equals(propertyName)) {
    			return entry.getKey();
    		}
    	}
    	throw new IllegalArgumentException("the property '" + propertyName + "' is not linked to a column axis");
	}
 
}
