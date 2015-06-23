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
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
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
		ImportInformationResult result = null;
		
		if (command.getFileName() != null && command.getScheme() != null) {
			result = doImport(command.getFileName(), command.getScheme(), context);
			
		} else if(command.getFileName() != null && command.isCommit()) {
			try {
				storageProvider.delete(command.getFileName());
			} catch (IOException ex) {
				LOGGER.debug("An error occured while removing the imported file '" + command.getFileName() + "'.", ex);
			}
		}

		return result;
	}
	
	protected ImportInformationResult doImport(String fileName, ImportationSchemeDTO importationScheme, UserDispatch.UserExecutionContext context) {
		try {
			final HashMap<String, Object> properties = new HashMap<String, Object>();
			final InputStream inputStream = storageProvider.open(fileName);
			
			if (inputStream != null) {
				Importer importer = null;
				properties.put("scheme", importationScheme);

				switch (importationScheme.getFileFormat()) {
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
				if (importer != null) {
					ImportInformationResult result = new ImportInformationResult();
					result.setEntitiesToImport(importer.getEntitiesToImport());
					return result;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error while importing file '" + fileName + "'.", e);
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
