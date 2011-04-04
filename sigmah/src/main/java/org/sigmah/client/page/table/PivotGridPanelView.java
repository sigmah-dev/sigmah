package org.sigmah.client.page.table;

import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;

public class PivotGridPanelView extends TreeGridView {

	
	/** 
	 * Overrides the default autoExpand behavior to distribute columns evenly.
	 */
	@Override
	protected void autoExpand(boolean preventUpdate) {
		
		int availableWidth = grid.getWidth(true) - getScrollAdjust();
		int baseWidth = availableWidth / (cm.getColumnCount());
		
		for(int i=0;i!=cm.getColumnCount(); ++i) {
			cm.setColumnWidth(i, baseWidth, true);
		}
		updateAllColumnWidths();
	}

}
