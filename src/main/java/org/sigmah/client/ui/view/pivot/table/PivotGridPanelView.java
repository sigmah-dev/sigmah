package org.sigmah.client.ui.view.pivot.table;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;

class PivotGridPanelView extends TreeGridView {

	/** 
	 * Overrides the default autoExpand behavior to distribute columns evenly.
	 */
	@Override
	protected void autoExpand(boolean preventUpdate) {
		
		int availableWidth = grid.getWidth(true) - getScrollAdjust();
		int baseWidth = availableWidth / (cm.getColumnCount());
		
		if(baseWidth < 100) {
			baseWidth = 100;
		}
		
		for(int i=0;i!=cm.getColumnCount(); ++i) {
			cm.setColumnWidth(i, baseWidth, true);
		}
		updateAllColumnWidths();
	}

}
