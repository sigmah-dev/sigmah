package org.sigmah.shared.command;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

/**
 * AddOrgUnit command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class AddOrgUnit extends AbstractCommand<CreateResult> {

	private Integer parentId;
	private Integer modelId;
	private String calendarName;
	private OrgUnitDTO unit;
	private OrgUnitDTO.Mode mappingMode;

	protected AddOrgUnit() {
		// Serialization.
	}

	public AddOrgUnit(Integer parentId, Integer modelId, String calendarName, OrgUnitDTO unit, OrgUnitDTO.Mode mappingMode) {
		this.parentId = parentId;
		this.modelId = modelId;
		this.calendarName = calendarName;
		this.unit = unit;
		this.mappingMode = mappingMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("parentId", parentId);
		builder.append("modelId", modelId);
		builder.append("calendarName", calendarName);
		builder.append("unit", unit);
		builder.append("mappingMode", mappingMode);
	}

	public Integer getParentId() {
		return parentId;
	}

	public OrgUnitDTO getUnit() {
		return unit;
	}

	public Integer getModelId() {
		return modelId;
	}

	public String getCalendarName() {
		return calendarName;
	}

	public OrgUnitDTO.Mode getMappingMode() {
		return mappingMode;
	}
}
