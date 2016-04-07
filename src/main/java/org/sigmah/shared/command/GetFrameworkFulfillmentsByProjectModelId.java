package org.sigmah.shared.command;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.FrameworkFulfillmentDTO;

public class GetFrameworkFulfillmentsByProjectModelId extends AbstractCommand<ListResult<FrameworkFulfillmentDTO>> {
	private Integer projectModelId;

	public GetFrameworkFulfillmentsByProjectModelId() {
	}

	public GetFrameworkFulfillmentsByProjectModelId(Integer projectModelId) {
		this.projectModelId = projectModelId;
	}

	public Integer getProjectModelId() {
		return projectModelId;
	}

	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("projectModelId", projectModelId);
	}
}
