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

import java.util.Date;
import org.sigmah.server.servlet.exporter.data.LogFrameExportData;

/**
 * Pair between a label and its value.
 * 
 * @author sherzod
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ValueLabel {
	
	/**
	 * Label of this pair.
	 */
	private String label;
	
	/**
	 * Value of this pair.
	 * 
	 * Can be a <code>String</code>, a <code>Double</code>, a <code>Long</code>
	 * or a <code>Date</code>.
	 */
	private Object value;
	
	/**
	 * Number of lines.
	 */
	private int lines;
	
	/**
	 * <code>true</code> if this entry concern a <code>MessageElement</code>.
	 */
	private boolean message;

	public ValueLabel(final String label, final Object value) {
		this(label, value, 1);
	}

	public ValueLabel(final String label, final Object value, final int lines) {
		this.label = label;
		this.value = value;
		this.lines = lines;
	}
	
	/**
	 * Returns the label of this pair without its HTML formatting.
	 * 
	 * @return The label of this pair.
	 */
	public String getFormattedLabel() {
		return ExporterUtil.clearHtmlFormatting(label);
	}
	
	/**
	 * Convert the value of this pair into a <code>String</code>.
	 * 
	 * @return A new string representing the value of this pair (can be <code>null</code>).
	 */
	public String toValueString() {
		
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Double) {
			final Double d = (Double) value;
			return LogFrameExportData.AGGR_AVG_FORMATTER.format(d.doubleValue());
		} else if (value instanceof Long) {
			final Long l = (Long) value;
			return LogFrameExportData.AGGR_SUM_FORMATTER.format(l.longValue());
		} else if (value instanceof Date) { // date
			return ExportConstants.EXPORT_DATE_FORMAT.format((Date) value);
		} else {
			return null;
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getLines() {
		return lines;
	}

	public boolean isMessage() {
		return message;
	}

	public void setMessage(boolean message) {
		this.message = message;
	}
	
}
