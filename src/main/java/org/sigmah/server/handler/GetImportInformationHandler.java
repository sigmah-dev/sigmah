package org.sigmah.server.handler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.shared.command.GetImportInformation;
import org.sigmah.shared.command.result.ImportInformationResult;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.servlet.exporter.utils.CsvParser;
import org.sigmah.server.servlet.importer.CsvImporter;
import org.sigmah.server.servlet.importer.ExcelImporter;
import org.sigmah.server.servlet.importer.Importer;
import org.sigmah.server.servlet.importer.OdsImporter;
import org.sigmah.shared.dispatch.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link GetImportInformation} command.
 * <p/>
 * Retrieve the file matching the given <code>fileId</code> and parse it with 
 * the given importation scheme. Results are not applied by this command.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class GetImportInformationHandler extends AbstractCommandHandler<GetImportInformation, ImportInformationResult> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateEntityHandler.class);
	
	@Inject
	private Injector injector;
	
	@Inject
	private FileStorageProvider storageProvider;

	@Override
	public ImportInformationResult execute(GetImportInformation command, UserDispatch.UserExecutionContext context) throws CommandException {
		if (command.getFileName() != null) {
			
			try {
				final HashMap<String, Object> properties = new HashMap<String, Object>();
				InputStream inputStream = storageProvider.open(command.getFileName());
				if (inputStream != null && command.getScheme() != null) {
					Importer importer = null;
					properties.put("scheme", command.getScheme());
					
					switch (command.getScheme().getFileFormat()) {
					case CSV:
						String stringFromStream = inputStreamToString(inputStream, "UTF-8");
						List<String[]> csvLines = new CsvParser().parseCsv(stringFromStream);
						properties.put("importedCsvDocument", csvLines);
						importer = new CsvImporter(injector, properties, context);
						break;
						
					case MS_EXCEL:
						try {
							Workbook workbook = WorkbookFactory.create(inputStream);
							properties.put("importedExcelDocument", workbook);
							importer = new ExcelImporter(injector, properties, context);
							
						} catch (InvalidFormatException e) {
							LOGGER.error("The format of the file given is invalid for the file format Excel.", e);
						}
						break;
						
					case ODS:
						try {
							Document doc = SpreadsheetDocument.loadDocument(inputStream);
							properties.put("importedOdsDocument", doc);
							importer = new OdsImporter(injector, properties, context);
							
						} catch (ClassCastException e) {
							LOGGER.error("The format of the file given is invalid for the file format ODS.", e);
						}
						break;
						
					default:
						LOGGER.warn("No file has been received.");
						break;

					}
					inputStream.close();
					storageProvider.delete(command.getFileName());
					if (importer != null) {
						ImportInformationResult result = new ImportInformationResult();
						result.setEntitiesToImport(importer.getEntitiesToImport());
						return result;
					}
				}
			} catch (Exception e) {
				LOGGER.error("Error while importing file '" + command.getFileName() + "'.", e);
			}

		}

		return null;
	}
	
	/**
	 * Read fully the given input stream and return it as a <code>String</code>.
	 * <p/>
	 * The input stream is not closed by this method.
	 * 
	 * @param inputStream Stream to read.
	 * @param encoding Encoding to use.
	 * @return The content of the input stream as a <code>String</code>.
	 * @throws IOException If an error occur while reading the stream.
	 */
	private String inputStreamToString(InputStream inputStream, String encoding) throws IOException {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		final byte[] bytes = new byte[1024];

		int length = inputStream.read(bytes);
		while(length > 0) {
			outputStream.write(bytes, 0, length);
			length = inputStream.read(bytes);
		}
		
		return outputStream.toString(encoding);
	}

}
