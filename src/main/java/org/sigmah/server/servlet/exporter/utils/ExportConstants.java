package org.sigmah.server.servlet.exporter.utils;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalAlignmentType;

/**
 * Constants used only in exporting classes
 * 
 * @author sherzod (v1.3)
 */
public final class ExportConstants {

	private ExportConstants() {
		// Only provides static methods.
	}

	public static final String DATE_FORMAT_PATTERN = "M/d/yy";

	public final static DateFormat EXPORT_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

	public static class MultiItemText {

		public final String text;
		public final int lineCount;

		public MultiItemText(String text, int lineCount) {
			this.text = text;
			this.lineCount = lineCount;
		}
	}

	/*
	 * Measures
	 */
	public final static float TITLE_ROW_HEIGHT = 16.15f;
	public final static float EMPTY_ROW_HEIGHT = 12.15f;
	public final static float HEADER_ROW_HEIGHT = 20.15f;

	/*
	 * Colors
	 */
	// Table headers GRAY 10 %
	public final static byte[] GRAY_5_RGB = {
																						(byte) 252,
																						(byte) 252,
																						(byte) 252
	}; // RGB
	public final static byte[] GRAY_10_RGB = {
																						(byte) 245,
																						(byte) 245,
																						(byte) 245
	}; // RGB
	public final static String GRAY_5_HEX = "#FCFCFC"; // Hexadecimal color code
	public final static String GRAY_10_HEX = "#F5F5F5"; // Hexadecimal color code

	// Secondary or inner table headers LIGHT ORAGANGE
	public final static byte[] LIGHTORANGE_RGB = {
																								(byte) 251,
																								(byte) 245,
																								(byte) 217
	};
	public final static String LIGHTORANGE_HEX = "#FBF5D9";

	// White
	public final static String WHITE_HEX = "#FFFFFF";

	// Calc
	public static final VerticalAlignmentType ALIGN_VER_MIDDLE = VerticalAlignmentType.MIDDLE;
	public static final HorizontalAlignmentType ALIGH_HOR_CENTER = HorizontalAlignmentType.CENTER;
	public static final HorizontalAlignmentType ALIGH_HOR_LEFT = HorizontalAlignmentType.LEFT;
	public static final HorizontalAlignmentType ALIGH_HOR_RIGHT = HorizontalAlignmentType.RIGHT;

	public static final Color CALC_COL_GRAY5 = Color.valueOf(ExportConstants.GRAY_5_HEX);
	public static final Color CALC_COL_GRAY10 = Color.valueOf(ExportConstants.GRAY_10_HEX);
	public static final Color CALC_COL_ORANGE = Color.valueOf(ExportConstants.LIGHTORANGE_HEX);

	/*
	 * Mix
	 */

	public static final String INDICATOR_SHEET_PREFIX = "IND_";
	public static final String CONTACT_SHEET_PREFIX = "CONT_";
	public static final String GROUP_ITERATIONS_SHEET_PREFIX = "ITER_";
}
