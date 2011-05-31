/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command;

import org.sigmah.shared.domain.logframe.IndicatorCopyStrategy;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.dto.logframe.LogFrameDTO;

/**
 * Ask the server to replace a log frame by an other one.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class CopyLogFrame implements Command<LogFrameDTO> {
    /**
     * ID of the log frame to copy.
     */
    private int sourceId;
    /**
     * ID of the project of destination.
     */
    private int destinationId;
    
    /**
     * Strategy for copying referenced indicators
     */
    private IndicatorCopyStrategy indicatorCopyStrategy;

    public CopyLogFrame() {
    }

    /**
     * 
     * @param sourceLogFrameId the id of the logframe to copy
     * @param destinationProjectId the id of the project whose logframe should be replaced
     */
    public CopyLogFrame(int sourceLogFrameId, int destinationProjectId) {
        this.sourceId = sourceLogFrameId;
        this.destinationId = destinationProjectId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }

	public IndicatorCopyStrategy getIndicatorCopyStrategy() {
		return indicatorCopyStrategy;
	}

	public void setIndicatorCopyStrategy(IndicatorCopyStrategy indicatorCopyStrategy) {
		this.indicatorCopyStrategy = indicatorCopyStrategy;
	}
    
    public static CopyLogFrame from(int sourceLogFrameId) {
    	CopyLogFrame command = new CopyLogFrame();
    	command.setSourceId(sourceLogFrameId);
    	return command;
    }
    
    public CopyLogFrame to(ProjectDTO project) {
    	this.destinationId = project.getId();
    	return this;
    }
    
    public CopyLogFrame with(IndicatorCopyStrategy strategy) {
    	this.indicatorCopyStrategy = strategy;
    	return this;
    }
	
}
