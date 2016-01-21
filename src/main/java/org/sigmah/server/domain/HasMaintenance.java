package org.sigmah.server.domain;

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
