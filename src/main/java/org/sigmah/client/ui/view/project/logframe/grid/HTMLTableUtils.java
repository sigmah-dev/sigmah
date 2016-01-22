package org.sigmah.client.ui.view.project.logframe.grid;

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

import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;

/**
 * An utility class to manage a HTML table with GXT CSS styles.
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public final class HTMLTableUtils {

	/**
	 * Provides only static methods.
	 */
	private HTMLTableUtils() {
	}

	/**
	 * Applies the CSS column-header styles to a cell.
	 * 
	 * @param table
	 *          The GWT table.
	 * @param row
	 *          The row index.
	 * @param column
	 *          The column index.
	 */
	public static void applyColumnHeaderStyles(HTMLTable table, int row, int column) {
		table.getCellFormatter().addStyleName(row, column, "x-grid3-header");
		table.getCellFormatter().addStyleName(row, column, "x-grid3-hd");
		table.getCellFormatter().addStyleName(row, column, "x-grid3-hd-row");
		table.getCellFormatter().addStyleName(row, column, "x-grid3-td-favorite");
		table.getCellFormatter().addStyleName(row, column, "x-grid3-cell");

		final Widget w = table.getWidget(row, column);
		if (w != null) {
			w.addStyleName("x-grid3-hd-inner");
			w.addStyleName("x-grid3-hd-favorite ");
			w.addStyleName("x-component");
		}
	}

	/**
	 * Applies the CSS row-header styles to a cell.
	 * 
	 * @param table
	 *          The GWT table.
	 * @param row
	 *          The row index.
	 * @param column
	 *          The column index.
	 */
	public static void applyRowHeaderStyles(HTMLTable table, int row, int column) {
		applyColumnHeaderStyles(table, row, column);
		table.getCellFormatter().addStyleName(row, column, "html-grid-header-row");
	}

	/**
	 * Applies the CSS header styles to a GWT table.
	 * 
	 * @param table
	 *          The GWT table.
	 * @param applyToRows
	 *          If the first column contains also headers (double entry array).
	 */
	public static void applyHeaderStyles(HTMLTable table, boolean applyToRows) {

		// Rows.
		if (applyToRows) {
			for (int row = 0; row < table.getRowCount(); row++) {
				applyRowStyles(table, row);
				applyRowHeaderStyles(table, row, 0);
				// for (int column = 0; column < table.getCellCount(row); column++) {
				//
				// }
			}
		}

		// Columns.
		for (int column = 0; column < table.getCellCount(0); column++) {
			applyColumnHeaderStyles(table, 0, column);
		}
	}

	/**
	 * Applies the CSS row styles to a row.
	 * 
	 * @param table
	 *          The GWT table.
	 * @param row
	 *          The row index.
	 */
	public static void applyRowStyles(HTMLTable table, int row) {
		table.getRowFormatter().addStyleName(row, "x-grid3-hd-row");
		table.getRowFormatter().addStyleName(row, "x-grid3-row");
	}

	/**
	 * Applies the CSS content style to a cell.
	 * 
	 * @param table
	 *          The GWT table.
	 * @param row
	 *          The row index.
	 * @param column
	 *          The column index.
	 * @param first
	 *          If the cell is the first of its row.
	 * @param last
	 *          If the cell is the last of its row.
	 */
	public static void applyCellStyles(HTMLTable table, int row, int column, boolean first, boolean last) {
		table.getCellFormatter().addStyleName(row, column, "x-grid3-col");
		table.getCellFormatter().addStyleName(row, column, "x-grid3-cell");
		table.getCellFormatter().addStyleName(row, column, "html-table-cell");

		if (first) {
			table.getCellFormatter().addStyleName(row, column, "x-grid3-cell-first");
		}

		if (last) {
			table.getCellFormatter().addStyleName(row, column, "x-grid3-cell-last");
			table.getCellFormatter().addStyleName(row, column, "html-table-cell-last");
		}

		final Widget w = table.getWidget(row, column);
		if (w != null) {
			w.addStyleName("x-grid3-cell-inner");
		}
	}
}
