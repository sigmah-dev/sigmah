package org.sigmah.client.event;

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
