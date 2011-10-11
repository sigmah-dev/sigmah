package org.sigmah.shared.domain.logframe;

import java.util.HashMap;

import org.sigmah.shared.domain.Activity;
import org.sigmah.shared.domain.Project;


/**
 * Defines the context for copying a LogFrame from one Project to another
 * 
 * @author alexander
 *
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
		if(originalGroup == null) {
			throw new NullPointerException("LogFrame group element cannot be null");
		}
		return groupMap.get(originalGroup.getId());
	}
	
	public Project getDestinationProjet() {
		return destinationProjection;
	}
	
    /**
     * 
     * @param destinationProjection the project to which the logframe is being copied (required for 
     * duplicating indicators)
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
		if(activity == null) {
			return null;
		} else {
			Activity copy = activityMap.get(activity.getId());
			if(copy == null) {
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
