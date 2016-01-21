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

import org.sigmah.client.ui.res.icon.IconUtil;
import org.sigmah.shared.dto.pivot.content.PivotTableData;

/**
 * Default {@link HeaderDecorator} of the {@link PivotGridPanel}.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
class GridHeaderDecorator implements HeaderDecorator {
	private final PivotGridPanel gridPanel;

	GridHeaderDecorator(final PivotGridPanel gridPanel) {
		this.gridPanel = gridPanel;
	}

	@Override
	public String decorateHeader(PivotTableData.Axis axis) {
		if (gridPanel.isShowAxisIcons() && axis.isLeaf()) {
			StringBuilder sb = new StringBuilder();
			sb.append(IconUtil.iconHtml(PivotResources.INSTANCE.css().zoomIcon()));
			if (axis.getDimension() == null) {
				return "";
			}
			switch (axis.getDimension().getType()) {
				case Indicator:
					sb.append(IconUtil.iconHtml(PivotResources.INSTANCE.css().editIcon()));
					break;
			}
			sb.append("<span>");
			sb.append(axis.getLabel());
			sb.append("</span>");
			return sb.toString();
		} else {
			return axis.getLabel();
		}
	}

	@Override
	public String cornerCellHtml() {
		if (gridPanel.isShowSwapIcon()) {
			return IconUtil.iconHtml(PivotResources.INSTANCE.css().swapIcon());
		} else {
			return "";
		}
	}
	
}
