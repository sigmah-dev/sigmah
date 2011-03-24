package org.sigmah.client.page.config.design;

import org.sigmah.client.icon.IconUtil;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.dom.client.Element;


/**
 * Renders the first column of the TreeGrid in the DesignPanel
 * 
 * @author alexander
 *
 */
class DesignTreeGridCellRenderer extends TreeGridCellRenderer {

	private final static DesignPanelResources.Style CSS = DesignPanelResources.INSTANCE.css();
	private final static String EMPTY_MAP_ICON = IconUtil.iconHtml(CSS.emptyMapIcon());
	private final static String MAP_ICON = IconUtil.iconHtml(CSS.mapIcon());

	private final static String EMPTY_STAR_ICON = IconUtil.iconHtml(CSS.emptyStarIcon());
	private final static String STAR_ICON = IconUtil.iconHtml(CSS.emptyStarIcon());
	
	private MappedIndicatorSelection mappedSelection;
	
	
	public DesignTreeGridCellRenderer(MappedIndicatorSelection mappedSelection) {
		super();
		this.mappedSelection = mappedSelection;
	}

	/**
	 * Wraps the actual IndicatorDTO model class so that we can supply our own
	 * html for the contents of the tree grid cell.
	 * 
	 * @author alexander
	 *
	 */
	private class ModelWrapper extends BaseModelData {
		IndicatorDTO indicator;


		@Override
		public <X> X get(String property) {
			StringBuilder html = new StringBuilder();
			//html.append(EMPTY_STAR_ICON); // to be readded once Project Dashboard displays indicators
			html.append(indicator == mappedSelection.getValue() ? MAP_ICON : EMPTY_MAP_ICON);
			html.append("<span class='" + CSS.indicatorLabel() + "' qtip='");
			html.append(Format.htmlEncode(indicator.getName()).replace("'", "&#39;"));
			html.append("'>");
			html.append(indicator.getCode());
			html.append("</span>");
			return (X)html.toString();
		}


		@Override
		public boolean equals(Object obj) {
			return indicator.equals(obj);
		}

		@Override
		public int hashCode() {
			return indicator.hashCode();
		}
	}
	

	private ModelWrapper modelWrapper = new ModelWrapper();

	@Override
	public Object render(ModelData model, String property,
			ColumnData config, int rowIndex, int colIndex, ListStore store,
			Grid grid) {
		if(model instanceof IndicatorDTO) {
			modelWrapper.indicator = (IndicatorDTO) model;
			return super.render(modelWrapper, property, config, rowIndex, colIndex, store, grid);
		} else {
			return super.render(model, "name", config, rowIndex, colIndex, store, grid);
		}
	}

	public enum Target {
		MAP_ICON,
		STAR_ICON,
		LABEL,
		NONE
	}
	
	public static Target computeTarget(GridEvent ge) {
		Element targetElement =  ge.getEvent().getEventTarget().cast();
		String targetClass = targetElement.getClassName();
		if(CSS.indicatorLabel().equals(targetClass)) {
			return Target.LABEL;
		} else if(CSS.mapIcon().equals(targetClass) || CSS.emptyMapIcon().equals(targetClass)) {
			return Target.MAP_ICON;
		} else if(CSS.emptyStarIcon().equals(targetClass) || CSS.starIcon().equals(targetClass)) {
			return Target.STAR_ICON;
		} else {
			return Target.NONE;
		}
	}
}