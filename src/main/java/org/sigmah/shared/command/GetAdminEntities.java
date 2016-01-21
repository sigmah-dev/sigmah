package org.sigmah.shared.command;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
