package org.sigmah.server.domain.logframe;

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

import java.util.HashMap;

import org.sigmah.server.domain.Activity;
import org.sigmah.server.domain.Project;
import org.sigmah.shared.dto.referential.IndicatorCopyStrategy;

/**
 * Defines the context for copying a LogFrame from one Project to another.
 * 
 * @author alexander
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LogFrameCopyContext {

	private final HashMap<Integer, LogFrameGroup> groupMap = new HashMap<Integer, LogFrameGroup>();
	private final HashMap<Integer, Activity> activityMap = new HashMap<Integer, Activity>();
	private final Project destinationProjection;
	private IndicatorCopyStrategy indicatorStrategy = IndicatorCopyStrategy.DUPLICATE;

	private LogFrameCopyContext(Project destinationProjection) {
		super();
		this.destinationProjection = destinationProjection;
	}

	public void addGroupCopy(LogFrameGroup group, LogFrameGroup groupCopy) {
		groupMap.put(group.getId(), groupCopy);
	}

	public LogFrameGroup getGroupCopy(LogFrameGroup originalGroup) {
		if (originalGroup == null) {
			throw new NullPointerException("LogFrame group element cannot be null");
		}
		return groupMap.get(originalGroup.getId());
	}

	public Project getDestinationProjet() {
		return destinationProjection;
	}

	/**
	 * @param destinationProject
	 *          The project to which the logframe is being copied (required for duplicating indicators).
	 */
	public static LogFrameCopyContext toProject(Project destinationProject) {
		return new LogFrameCopyContext(destinationProject);
	}

	public LogFrameCopyContext withStrategy(IndicatorCopyStrategy strategy) {
		this.indicatorStrategy = strategy;
		return this;
	}

	public IndicatorCopyStrategy getIndicatorStrategy() {
		return indicatorStrategy;
	}

	public Activity getActivityCopy(Activity activity) {
		if (activity == null) {
			return null;
		} else {
			Activity copy = activityMap.get(activity.getId());
			if (copy == null) {
				copy = new Activity();
				copy.setName(activity.getName());
				copy.setDatabase(destinationProjection);
				copy.setReportingFrequency(activity.getReportingFrequency());
				copy.setSortOrder(activity.getSortOrder());
				copy.setLocationType(activity.getLocationType());
				activityMap.put(activity.getId(), copy);
			}
			return copy;
		}
	}

}
