package org.sigmah.client.event;

import com.google.gwt.event.shared.GwtEvent;
import org.sigmah.client.event.handler.PivotCellHandler;
import org.sigmah.shared.dto.pivot.model.PivotElement;
import org.sigmah.shared.dto.pivot.content.PivotTableData;

/**
 * Event fired when a pivot cell is double-clicked. Other listening components may 
 * show details, drill-downs, etc.
 */
public class PivotCellEvent extends GwtEvent<PivotCellHandler> {
	
	private static Type<PivotCellHandler> TYPE;

	private final Action action;
    private final PivotElement element;
    private final PivotTableData.Axis row;
    private final PivotTableData.Axis column;
	
	public static enum Action {
		DRILLDOWN,
	}

    /**
     * 
     * @param element the enclosing {@link org.sigmah.shared.report.model.PivotTableElement} or 
     * {@link org.sigmah.shared.report.model.PivotChartElement}
     * @param row  the clicked row
     * @param column  the clicked column
     */
    public PivotCellEvent(Action action, PivotElement element, PivotTableData.Axis row, PivotTableData.Axis column) {
		this.action = action;
        this.element = element;
        this.row = row;
        this.column = column;
    }

	public Action getAction() {
		return action;
	}

    public PivotElement getElement() {
        return element;
    }

    public PivotTableData.Axis getRow() {
        return row;
    }

    public PivotTableData.Axis getColumn() {
        return column;
    }

	// --
	// GWT event method
	// --
	
	public static Type<PivotCellHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<PivotCellHandler>();
		}
		return TYPE;
	}
	
	@Override
	public Type<PivotCellHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(PivotCellHandler handler) {
		handler.handleEvent(this);
	}
}
