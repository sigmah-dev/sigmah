package org.sigmah.shared.dto.importation;

import java.util.List;

import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.shared.domain.importation.ImportationSchemeFileFormat;
import org.sigmah.shared.domain.importation.ImportationSchemeImportType;
import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.ui.Image;

public class ImportationSchemeDTO extends BaseModelData implements EntityDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1360714480030529929L;

	@Override
	public int getId() {
		if (get("id") != null)
			return (Integer) get("id");
		else
			return -1;
	}

	public void setId(int id) {
		set("id", id);
	}

	@Override
	public String getEntityName() {
		return "importation.ImportationScheme";
	}

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public ImportationSchemeImportType getImportType() {
		return get("importType");
	}

	public void setImportType(ImportationSchemeImportType importType) {
		set("importType", importType);
	}

	public ImportationSchemeFileFormat getFileFormat() {
		return get("fileFormat");
	}

	public void setFileFormat(ImportationSchemeFileFormat fileFormat) {
		set("fileFormat", fileFormat);
	}

	public List<VariableDTO> getVariablesDTO() {
		return get("variablesDTO");
	}

	public void setVariablesDTO(List<VariableDTO> variablesDTO) {
		set("variablesDTO", variablesDTO);
	}

	public Integer getFirstRow() {
		return get("firstRow");
	}

	public void setFirstRow(Integer firstRow) {
		set("firstRow", firstRow);
	}

	public String getSheetName() {
		return get("sheetName");
	}

	public void setSheetName(String sheetName) {
		set("sheetName", sheetName);
	}

	public Image getFileFormatIcon() {
		Image iconHTML = null;
		switch (getFileFormat()) {
		case CSV:
			iconHTML = IconImageBundle.ICONS.csv().createImage();
			break;
		case MS_EXCEL:
			iconHTML = IconImageBundle.ICONS.excel().createImage();
			break;
		case ODS:
			iconHTML = IconImageBundle.ICONS.ods().createImage();
			break;
		default:
			break;
		}
		return iconHTML;
	}

}
