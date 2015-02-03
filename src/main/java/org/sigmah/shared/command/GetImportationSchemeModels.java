package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetImportationSchemeModels extends AbstractCommand<ListResult<ImportationSchemeModelDTO>> {

	private Integer importationSchemeId;
	private Integer projectModelId;
	private Integer orgUnitModelId;

	public GetImportationSchemeModels() {
		// Serialization.
	}
	
	public GetImportationSchemeModels(IsModel model) {
		if(model instanceof ProjectModelDTO) {
			this.projectModelId = model.getId();
			
		} else if(model instanceof OrgUnitModelDTO) {
			this.orgUnitModelId = model.getId();
		}
	}

	/**
	 * @return the schemaId
	 */
	public Integer getImportationSchemeId() {
		return importationSchemeId;
	}

	/**
	 * @param importationSchemeId
	 *          the schemaId to set
	 */
	public void setImportationSchemeId(Integer importationSchemeId) {
		this.importationSchemeId = importationSchemeId;
	}

	/**
	 * @return the projectModelId
	 */
	public Integer getProjectModelId() {
		return projectModelId;
	}

	/**
	 * @param projectModelId
	 *          the projectModelId to set
	 */
	public void setProjectModelId(Integer projectModelId) {
		this.projectModelId = projectModelId;
	}

	public Integer getOrgUnitModelId() {
		return orgUnitModelId;
	}

	public void setOrgUnitModelId(Integer orgUnitModelId) {
		this.orgUnitModelId = orgUnitModelId;
	}

}
