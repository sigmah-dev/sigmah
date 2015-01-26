package org.sigmah.shared.dto.importation;

import java.util.List;

import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;

import com.google.gwt.user.client.ui.Image;

/**
 * ImportationSchemeDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ImportationSchemeDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1360714480030529929L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "importation.ImportationScheme";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String IMPORT_TYPE = "importType";
	public static final String FILE_FORMAT = "fileFormat";
	public static final String FIRST_ROW = "firstRow";
	public static final String SHEET_NAME = "sheetName";
	public static final String VARIABLES = "variables";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(NAME, getName());
		builder.append(IMPORT_TYPE, getImportType());
		builder.append(FILE_FORMAT, getFileFormat());
		builder.append(FIRST_ROW, getFirstRow());
		builder.append(SHEET_NAME, getSheetName());
	}

	/**
	 * Returns the given {@code importationScheme} corresponding file format icon.
	 * 
	 * @param importationScheme
	 *          The importation scheme instance.
	 * @return The given {@code importationScheme} corresponding file format icon, or {@code null}.
	 */
	public static Image getFileFormatIcon(final ImportationSchemeDTO importationScheme) {

		if (importationScheme == null || importationScheme.getFileFormat() == null) {
			return null;
		}

		switch (importationScheme.getFileFormat()) {
			case CSV:
				return IconImageBundle.ICONS.csv().createImage();

			case MS_EXCEL:
				return IconImageBundle.ICONS.excel().createImage();

			case ODS:
				return IconImageBundle.ICONS.ods().createImage();

			default:
				return null;
		}
	}

	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	public ImportationSchemeImportType getImportType() {
		return get(IMPORT_TYPE);
	}

	public void setImportType(ImportationSchemeImportType importType) {
		set(IMPORT_TYPE, importType);
	}

	public ImportationSchemeFileFormat getFileFormat() {
		return get(FILE_FORMAT);
	}

	public void setFileFormat(ImportationSchemeFileFormat fileFormat) {
		set(FILE_FORMAT, fileFormat);
	}

	public List<VariableDTO> getVariables() {
		return get(VARIABLES);
	}

	public void setVariables(List<VariableDTO> variables) {
		set(VARIABLES, variables);
	}

	public Integer getFirstRow() {
		return get(FIRST_ROW);
	}

	public void setFirstRow(Integer firstRow) {
		set(FIRST_ROW, firstRow);
	}

	public String getSheetName() {
		return get(SHEET_NAME);
	}

	public void setSheetName(String sheetName) {
		set(SHEET_NAME, sheetName);
	}

}
