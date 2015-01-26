package org.sigmah.client.ui.view.pivot.table;

import org.sigmah.client.ui.res.icon.IconUtil;
import org.sigmah.shared.dto.pivot.content.PivotTableData;

/**
 * {@link HeaderDecorator} displayed when the {@link PivotGridPanel} is not editable.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ReadOnlyHeaderDecorator implements HeaderDecorator {
	private final PivotGridPanel gridPanel;

	ReadOnlyHeaderDecorator(final PivotGridPanel gridPanel) {
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
