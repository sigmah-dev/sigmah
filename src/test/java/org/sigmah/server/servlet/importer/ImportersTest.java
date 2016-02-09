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

import org.junit.Assert;
import org.junit.Test;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;

/**
 * Test class for <code>Importers</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ImportersTest {
	
	/**
	 * Test of createImporterForScheme method, of class Importers.
	 */
	@Test
	public void testCreateImporterForScheme() {
		ImportationSchemeDTO scheme = new ImportationSchemeDTO();
		scheme.setFileFormat(ImportationSchemeFileFormat.CSV);
		Assert.assertEquals(CsvImporter.class, Importers.createImporterForScheme(scheme).getClass());
		
		scheme.setFileFormat(ImportationSchemeFileFormat.MS_EXCEL);
		Assert.assertEquals(ExcelImporter.class, Importers.createImporterForScheme(scheme).getClass());
		
		scheme.setFileFormat(ImportationSchemeFileFormat.ODS);
		Assert.assertEquals(OdsImporter.class, Importers.createImporterForScheme(scheme).getClass());
	}
	
}
