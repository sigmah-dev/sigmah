package org.sigmah.server.servlet.exporter.data;

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

import org.sigmah.server.servlet.exporter.base.Exporter;

public abstract class ExportData {

	protected final Exporter exporter;
	private final int numbOfCols;

	public ExportData(final Exporter exporter, final int numbOfCols) {
		this.exporter = exporter;
		this.numbOfCols = numbOfCols;
	}

	public String getLocalizedVersion(String key) {
		return exporter.localize(key);
	}

	public int getNumbOfCols() {
		return numbOfCols;
	}

}
