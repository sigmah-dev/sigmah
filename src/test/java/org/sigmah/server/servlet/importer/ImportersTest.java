package org.sigmah.server.servlet.importer;

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
