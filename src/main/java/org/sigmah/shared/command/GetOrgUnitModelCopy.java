package org.sigmah.shared.command;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.OrgUnitModelDTO;

/**
 * Duplicates an existing OrgUnit model and returns the copy.
 * 
 * @author Kristela Macaj (kmacaj@ideia.fr) (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetOrgUnitModelCopy extends AbstractCommand<OrgUnitModelDTO> {

	private Integer modelId;
	private String newModelName;
	private OrgUnitModelDTO.Mode mappingMode;

	protected GetOrgUnitModelCopy() {
		// Serialization.
	}

	public GetOrgUnitModelCopy(Integer modelId, String newModelName, OrgUnitModelDTO.Mode mappingMode) {
		this.modelId = modelId;
		this.newModelName = newModelName;
		this.mappingMode = mappingMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("modelId", modelId);
		builder.append("newModelName", newModelName);
		builder.append("mappingMode", mappingMode);
	}

	public Integer getModelId() {
		return modelId;
	}

	public String getNewModelName() {
		return newModelName;
	}

	public OrgUnitModelDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
