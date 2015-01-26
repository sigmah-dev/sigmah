package org.sigmah.client.ui.view.pivot.table;

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
