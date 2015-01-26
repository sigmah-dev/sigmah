package org.sigmah.server.endpoint.export.sigmah.importer;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.sigmah.server.endpoint.export.sigmah.Importer;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

import com.google.inject.Injector;

public class CsvImporter extends Importer {

	private List<String[]> lines;

	@SuppressWarnings("unchecked")
	public CsvImporter(Injector injector,  Map<String, Object> properties, User user) throws Throwable {
		super(injector,properties, user);
		if(properties.get("importedCsvDocument") != null && properties.get("importedCsvDocument") instanceof List<?>){
			lines = (List<String[]>) properties.get("importedCsvDocument");
		}
		
		getCorrespondances(schemeModelList);
	}

	@Override
	protected void getCorrespondances(List<ImportationSchemeModelDTO> schemeModelList) throws Throwable {
		for (ImportationSchemeModelDTO schemeModelDTO : schemeModelList) {
			// GetThe variable and the flexible element for the identification

			switch (scheme.getImportType()) {
			case ROW:
				int firstRow = 0;
				if(scheme.getFirstRow() != null) {
					firstRow = scheme.getFirstRow();
				}
				for (int i = firstRow; i < lines.size(); i++) {
					getCorrespondancePerSheetOrLine(schemeModelDTO, i, null);
				}
				break;
			case SEVERAL:
				logWarnFormatImportTypeIncoherence();
				break;
			case UNIQUE:
				logWarnFormatImportTypeIncoherence();
				break;
			default:
				logWarnFormatImportTypeIncoherence();
				break;

			}
		}

	}

	@Override
	public String getValueFromVariable(String reference, Integer lineNumber, String sheetName) throws ServletException {
	
		String columnValue = "";
		if (reference != null && !reference.isEmpty()) {
			switch (scheme.getImportType()) {
			case ROW:
				// Get First Row and sheet name
				if(reference != null){
					try {
						columnValue = lines.get(lineNumber)[Integer.valueOf(reference)];
					} catch(NumberFormatException nfe){
						throw new ServletException("The variable's reference : " + reference + " is invalid for the Csv file format type");
					}
				}
				break;
			case SEVERAL:
				logWarnFormatImportTypeIncoherence();
				break;
			case UNIQUE:
				logWarnFormatImportTypeIncoherence();
				break;
			default:
				break;

			}
		}
		return columnValue;
	}

}
