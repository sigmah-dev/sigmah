package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetImportationSchemes extends AbstractCommand<ListResult<ImportationSchemeDTO>> {

	private Integer schemaId;

	private Integer projectModelId;

	private Integer orgUnitModelId;

	private Boolean excludeExistent = false;

	public GetImportationSchemes() {
		// Serialization.
	}

	public Integer getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(Integer schemaId) {
		this.schemaId = schemaId;
	}

	/**
	 * @return the excludeExistent
	 */
	public Boolean getExcludeExistent() {
		return excludeExistent;
	}

	/**
	 * @param excludeExistent
	 *          the excludeExistent to set
	 */
	public void setExcludeExistent(Boolean excludeExistent) {
		this.excludeExistent = excludeExistent;
	}

	/**
	 * @return the modelId
	 */
	public Integer getProjectModelId() {
		return projectModelId;
	}

	/**
	 * @param modelId
	 *          the modelId to set
	 */
	public void setProjectModelId(Integer modelId) {
		this.projectModelId = modelId;
	}

	/**
	 * @return the orgUnitModelId
	 */
	public Integer getOrgUnitModelId() {
		return orgUnitModelId;
	}

	/**
	 * @param orgUnitModelId
	 *          the orgUnitModelId to set
	 */
	public void setOrgUnitModelId(Integer orgUnitModelId) {
		this.orgUnitModelId = orgUnitModelId;
	}

}
