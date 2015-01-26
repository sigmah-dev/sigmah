package org.sigmah.shared.command;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.AdminEntityDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetAdminEntities extends GetListCommand<ListResult<AdminEntityDTO>> {

	private int levelId;
	private Integer parentId;
	private Integer activityId;

	protected GetAdminEntities() {
		// Serialization.
	}

	public GetAdminEntities(int levelId) {
		this.levelId = levelId;
	}

	public GetAdminEntities(int levelId, Integer parentId) {
		super();
		this.levelId = levelId;
		this.parentId = parentId;
	}

	public GetAdminEntities(int levelId, Integer parentId, Integer activityId) {
		this.levelId = levelId;
		this.parentId = parentId;
		this.activityId = activityId;
	}

	public int getLevelId() {
		return levelId;
	}

	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("levelId", levelId);
		builder.append("parentId", parentId);
		builder.append("activityId", activityId);
	}

}
