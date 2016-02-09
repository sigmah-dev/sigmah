package org.sigmah.server.servlet.importer;

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

import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

/**
 * Utility class to create {@link Importer}s.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public final class Importers {
	
	/**
	 * Creates a new importer suitable for the given scheme.
	 * 
	 * @param scheme
	 *			Scheme to use for an importation.
	 * @return A new importer suitable for the given scheme.
	 * @throws UnsupportedOperationException
	 *			If the file format of the given scheme is unsupported.
	 */
	public static Importer createImporterForScheme(final ImportationSchemeDTO scheme) throws UnsupportedOperationException {
		
		final Importer importer;
		
		switch (scheme.getFileFormat()) {
		case CSV:
			importer = new CsvImporter();
			break;
		case MS_EXCEL:
			importer = new ExcelImporter();
			break;
		case ODS:
			importer = new OdsImporter();
			break;
		default:
			throw new UnsupportedOperationException("File format '" + scheme.getFileFormat() + "' is unsupported.");
		}
		
		importer.setScheme(scheme);
		return importer;
	}
	
	/**
	 * Private constructor.
	 */
	private Importers() {
		// No initialization.
	}
	
}
