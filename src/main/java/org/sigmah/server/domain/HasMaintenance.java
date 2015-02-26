package org.sigmah.server.domain;

import java.util.Date;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Parent interface for models that can be put under maintenance.
 * 
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface HasMaintenance {
	
	/**
	 * Identifier of the model.
	 * 
	 * @return The identifier.
	 */
	public Integer getId();
	
	/**
	 * Returns the current status of the model.
	 * 
	 * @return The current status of the model.
	 */
	public ProjectModelStatus getStatus();
	
	/**
	 * Changes the status of the model.
	 * 
	 * @param status The status to set.
	 */
	public void setStatus(ProjectModelStatus status);
	
	/**
	 * Returns the date on which the maintenance started or is going to start.
	 * 
	 * @return The date on which the maintenance started or is going to start or
	 * <code>null</code> if no maintenance is defined.
	 */
	public Date getDateMaintenance();
	
	/**
	 * Define or remove the maintenance date for this model.
	 * 
	 * @param dateMaintenance A date to planify a maintenance or <code>null</code>
	 * to remove the maintenance.
	 */
	public void setDateMaintenance(Date dateMaintenance);
}
