package org.sigmah.shared.domain.logframe;

import java.util.HashMap;

import org.sigmah.shared.domain.Project;

/**
 * Defines the context for copying a LogFrame from one Project to another
 * 
 * @author alexander
 *
 */
public class LogFrameCopyContext {

	private final HashMap<Integer, LogFrameGroup> groupMap = new HashMap<Integer, LogFrameGroup>();
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
	
}
