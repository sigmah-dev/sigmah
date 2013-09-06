package org.sigmah.server.endpoint.gwtrpc.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.server.endpoint.export.sigmah.Importer;
import org.sigmah.server.endpoint.export.sigmah.importer.CsvImporter;
import org.sigmah.server.endpoint.export.sigmah.importer.ExcelImporter;
import org.sigmah.server.endpoint.export.sigmah.importer.OdsImporter;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.CsvParser;
import org.sigmah.server.endpoint.file.FileStorageProvider;
import org.sigmah.shared.command.GetImportInformation;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ImportInformationResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class GetImportInformationHandler implements CommandHandler<GetImportInformation> {
	private final Injector injector;

	private static final Log log = LogFactory.getLog(CreateEntityHandler.class);

	@Inject
	public GetImportInformationHandler(Injector injector) {
		this.injector = injector;
	}

	@Override
	@Inject
	public CommandResult execute(GetImportInformation cmd, User user) throws CommandException {
		if (cmd.getFileName() != null) {
			FileStorageProvider storageProvider = injector.getInstance(FileStorageProvider.class);
			try {
				final HashMap<String, Object> properties = new HashMap<String, Object>();
				InputStream inputStream = storageProvider.open(cmd.getFileName());
				if (inputStream != null && cmd.getScheme() != null) {
					Importer importer = null;
					properties.put("scheme", cmd.getScheme());
					switch (cmd.getScheme().getFileFormat()) {
					case CSV:
						String stringFromStream = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
						List<String[]> csvLines = new CsvParser().parseCsv(stringFromStream);
						properties.put("importedCsvDocument", csvLines);
						importer = new CsvImporter(injector, properties, user);
						break;
					case MS_EXCEL:
						try {
							Workbook workbook = WorkbookFactory.create(inputStream);
							properties.put("importedExcelDocument", workbook);
							importer = new ExcelImporter(injector, properties, user);
						} catch (InvalidFormatException e) {
							if (log.isDebugEnabled()) {
								log.debug("The format of the file given is invalid for the file format Excel");
							}
						}
						break;
					case ODS:
						try {
							Document doc = SpreadsheetDocument.loadDocument(inputStream);
							properties.put("importedOdsDocument", doc);
							importer = new OdsImporter(injector, properties, user);
						}catch (ClassCastException e) {
							if (log.isDebugEnabled()) {
								log.debug("The format of the file given is invalid for the file format Excel");
							}
						}
						break;
					default:
						log.warn("No file has been received.");
						break;

					}
					inputStream.close();
					storageProvider.delete(cmd.getFileName());
					if (importer != null) {
						ImportInformationResult result = new ImportInformationResult();
						result.setEntitiesToImport(importer.getEntitiesToImport());
						return result;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}

}
