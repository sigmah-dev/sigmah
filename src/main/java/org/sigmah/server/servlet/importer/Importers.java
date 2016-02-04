package org.sigmah.server.servlet.importer;

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
